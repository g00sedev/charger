package com.wingmate.charger;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// https://docs.aws.amazon.com/sns/latest/dg/sms_publish-to-phone.html

public class ChargerSmsSender
{
    private final Map<String, MessageAttributeValue> smsAttributes;
    private final AmazonSNS snsClient = AmazonSNSClientBuilder.defaultClient();

    private final String info;

    public ChargerSmsSender(String fromNumber, String maxPrice, String smsType)
    {
        Map<String, MessageAttributeValue> smsAttributesTemp = new HashMap<>();

        smsAttributesTemp.put("AWS.SNS.SMS.SenderID", new MessageAttributeValue()
            .withStringValue(fromNumber)
            .withDataType("String"));

        smsAttributesTemp.put("AWS.SNS.SMS.MaxPrice", new MessageAttributeValue()
            .withStringValue(maxPrice)
            .withDataType("Number"));

        smsAttributesTemp.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue()
            .withStringValue(smsType)
            .withDataType("String"));

        smsAttributes = Collections.unmodifiableMap(smsAttributesTemp);

        info = "from number = " + fromNumber + ", max price = " + maxPrice + ", sms type = " + smsType;
    }

    public PublishResult send(String message, String phoneNumber)
    {
        return snsClient.publish(new PublishRequest()
//            .withMessageAttributes(smsAttributes)
            .withMessage(message)
            .withPhoneNumber(phoneNumber));
    }

    @Override
    public String toString()
    {
        return "ChargerSmsSender{" +
            "info='" + info + '\'' +
            '}';
    }
}
