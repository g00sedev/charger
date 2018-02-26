package com.wingmate.charger;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.sns.model.PublishResult;
import java.util.Map;

public class DyanmoEventContextHandler
{
    private static final String INSERT_EVENT = "INSERT";

    private final DynamodbEvent ddbEvent;
    private final ChargerSmsSender smsSender;
    private final LambdaLogger lambdaLogger;
    private final UserLookup userLookup;

    public DyanmoEventContextHandler(DynamodbEvent ddbEvent, Context context, ChargerSmsSender smsSender)
    {
        this.ddbEvent = ddbEvent;
        this.smsSender = smsSender;
        lambdaLogger = context.getLogger();
        userLookup = new UserLookup(lambdaLogger);
    }

    public void handleDbEvent()
    {
        for (DynamodbEvent.DynamodbStreamRecord record : ddbEvent.getRecords())
        {
            if(INSERT_EVENT.equalsIgnoreCase(record.getEventName()))
            {
                handleNewWingmateEvent(record);
            }
            else
            {
                lambdaLogger.log("Skipping event type = " + record.getEventName());
            }
        }
    }

    private void handleNewWingmateEvent(DynamodbEvent.DynamodbStreamRecord record)
    {
        lambdaLogger.log("record = " + record.getDynamodb().toString());

        Boolean sendText = Boolean.valueOf(System.getenv("SEND_TEXT"));
        if(!sendText)
        {
            lambdaLogger.log("Sending SMS disabled by SEND_TEXT environment variable");
            return;
        }

        Map<String, AttributeValue> wingmateEventData = record.getDynamodb().getNewImage();

        WingmateEvent wingmateEvent = WingmateEvent.fromAttributeValueMap(wingmateEventData);
        lambdaLogger.log("wingmateEvent " + wingmateEvent);

        Boolean threshold = wingmateEvent.threshold();
        if(!threshold)
        {
            lambdaLogger.log("Sending is skipped due to threshold = false");
            return;
        }

        lambdaLogger.log("sender configuration = " + smsSender);

        String driverUserId = wingmateEventData.get("userId").getS();
        UserInfo userInfo = userLookup.getUserInfo(driverUserId);
        String message = createMessage(wingmateEvent, userInfo);
        lambdaLogger.log("Sending to parent phone number = [ " + userInfo.getParentPhoneNumber()
            + " ], message = " + message);

        PublishResult publishResult = smsSender.send(message, userInfo.getParentPhoneNumber());

        lambdaLogger.log("SMS send result = " + publishResult.toString());
    }

    private String createMessage(WingmateEvent wingmateEvent, UserInfo userInfo)
    {
        return  "Driver " + userInfo.getDriverName()
            + " was driving inattentively at " + wingmateEvent.eventDate()
            + " for reference ID = " + wingmateEvent.eventId();
    }
}
