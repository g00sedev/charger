package com.wingmate.charger;

public class UserInfo
{
    private static final String PHONE_PREFIX = "+1";
    private final String driverName;
    private final String parentPhoneNumber;

    public UserInfo(String driverName, String parentPhoneNumber)
    {
        this.driverName = driverName;

        if(parentPhoneNumber.startsWith(PHONE_PREFIX))
        {
            this.parentPhoneNumber = parentPhoneNumber;
        }
        else
        {
            this.parentPhoneNumber = PHONE_PREFIX + parentPhoneNumber;
        }
    }

    public String getDriverName()
    {
        return driverName;
    }

    public String getParentPhoneNumber()
    {
        return parentPhoneNumber;
    }
}
