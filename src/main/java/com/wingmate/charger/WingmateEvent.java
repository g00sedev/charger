package com.wingmate.charger;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import java.util.Map;


public class WingmateEvent
{
    private final Map<String, AttributeValue> attributeValues;

    public WingmateEvent(Map<String, AttributeValue> attributeValues)
    {
        this.attributeValues = attributeValues;
    }

    public static WingmateEvent fromAttributeValueMap(Map<String, AttributeValue> attributeValues)
    {
        return new WingmateEvent(attributeValues);
    }

    public Boolean threshold()
    {
        AttributeValue threshold = attributeValues.get("threshold");
        if(threshold == null)
        {
            return Boolean.FALSE;
        }
        return threshold.getBOOL();
    }

    public String eventId()
    {
        return attributeValues.get("eventId").getS();
    }

    public String eventType()
    {
        AttributeValue eventType = attributeValues.get("eventType");
        if(eventType == null)
        {
            return "";
        }
        return eventType.getS();
    }

    public String eventDate()
    {
        return attributeValues.get("at").getS();
    }


    @Override
    public String toString()
    {
        return "WingmateEvent{" +
            "attributeValues=" + attributeValues +
            ", eventId=" + eventId() +
            ", eventType=" + eventType() +
            ", eventDate=" + eventDate() +
            ", threshold=" + threshold() +
            "}";
    }
}
