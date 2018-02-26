package com.wingmate.charger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ChargerRequestHandler implements RequestHandler<DynamodbEvent, String>
{
    private static final String FROM_NUMBER = "2065551212";
    private static final String MAX_PRICE = "0.05";

    private final ChargerSmsSender smsSender;

    public ChargerRequestHandler()
    {
        String fromNumber = System.getenv("TEXT_FROM_NUMBER");
        if(fromNumber == null || fromNumber.length() != 10)
        {
            fromNumber = FROM_NUMBER;
        }

        String smsType = System.getenv("SMS_TYPE");
        if(smsType == null || smsType.isEmpty())
        {
            smsType = SmsType.Promotional.toString();
        }

        smsSender = new ChargerSmsSender(fromNumber, MAX_PRICE, smsType);
    }

    public String handleRequest(DynamodbEvent ddbEvent, Context context)
    {
        try
        {
            new DyanmoEventContextHandler(ddbEvent, context, smsSender).handleDbEvent();
        }
        catch (RuntimeException e)
        {
            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            e.printStackTrace(printWriter);

            context.getLogger().log("Unexpected exception during handle: " + e.getMessage() + " " + writer.toString());
            printWriter.close();
        }

        String message = "Event processing complete. Record count = " + ddbEvent.getRecords().size();
        context.getLogger().log(message);
        return message;
    }
}