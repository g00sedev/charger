package com.wingmate.charger;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class UserLookup
{
    private final DynamoDB dynamoDb;
    private final LambdaLogger lambdaLogger;

    public UserLookup(LambdaLogger lambdaLogger)
    {
        this.lambdaLogger = lambdaLogger;
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_WEST_2).build();
        dynamoDb = new DynamoDB(client);
    }

    private Item lookup(String userId)
    {
        Table table = dynamoDb.getTable("tahoe-users");
        return table.getItem("userId", userId);
    }

    public UserInfo getUserInfo(String driverUserId)
    {
        Item driver = lookup(driverUserId);
        String parentUserId = driver.getString("contactId");
        Item parent = lookup(parentUserId);

        lambdaLogger.log("driverUser " + driver.toJSON() + ", parent " + parent.toJSON());

        String driverName = driver.getString("firstName") + " " + driver.getString("lastName");
        String parentPhoneNumber = parent.getString("phoneNumber");

        return new UserInfo(driverName, parentPhoneNumber);
    }
}
