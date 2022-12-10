package services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import handler.BotLogic;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.sns.SnsClient;

import handler.Students;

public class DynamoDB {

    public DynamoDbClient authenticateDynamoDB() {
        return DynamoDbClient
                .builder()
                .credentialsProvider(ProfileCredentialsProvider.create())
                .region(Region.of(System.getenv("AWS_REGION")))
                .build();
    }

    public String createTable(String tableName, String key) {
        DynamoDbClient ddb = authenticateDynamoDB();
        ListTablesResponse listTables = ddb.listTables();
        for (String table : listTables.tableNames()) {
            if (table.equals("Students")) {
                return table + " table already exists.";
            }
        }

        try {
            CreateTableRequest request = CreateTableRequest.builder()
                    .attributeDefinitions(AttributeDefinition.builder()
                            .attributeName(key)
                            .attributeType(ScalarAttributeType.N)
                            .build())
                    .keySchema(KeySchemaElement.builder()
                            .attributeName(key)
                            .keyType(KeyType.HASH)
                            .build())
                    .provisionedThroughput(ProvisionedThroughput.builder()
                            .readCapacityUnits(10L)
                            .writeCapacityUnits(10L)
                            .build())
                    .tableName(tableName)
                    .build();

            ddb.createTable(request);
        } catch (DynamoDbException error) {
            System.err.println(error.getMessage());
            System.exit(1);
        }
        return "Table " + System.getenv("DYNAMO_DB_TABLE_NAME") + " created.";
    }

    public void getRecord(DynamoDbClient dynamoDbClient, String keyName, String keyValue) throws IOException {
        Map<String,AttributeValue> keyToGet = new HashMap<>();
        List<String> messages = new ArrayList<>();

        keyToGet.put(keyName, AttributeValue.builder().n(keyValue).build());

        GetItemRequest request = GetItemRequest.builder()
                .key(keyToGet)
                .tableName(System.getenv("DYNAMO_DB_TABLE_NAME"))
                .build();

        try {
            Map<String, AttributeValue> returnedItem = dynamoDbClient.getItem(request).item();

            if (returnedItem != null) {
                Set<String> keys = returnedItem.keySet();
                messages.add("Amazon DynamoDB table attributes: \n");

                for (String key : keys) {
                    messages.add(String.format("%s: %s\n", key, returnedItem.get(key).toString()));
                }
            } else {
                messages.add(String.format("No item found with the key %s!\n", keyName));
            }
        } catch (DynamoDbException error) {
            System.err.println(error.getMessage());
            System.exit(1);
        }

        String mainMessage = "Student with ID " + keyValue + " was successfully retrieved.";
        messages.add(mainMessage);
        BotLogic.multipleMessages(messages, "PlainText");

        SNS sns = new SNS();
        SnsClient snsClient = sns.authenticateSNS();
        sns.publishMessage(snsClient, messages, mainMessage, "GET");
    }

    public void putRecord(DynamoDbEnhancedClient enhancedClient) throws IOException {
        Students studentRecord = new Students();
        try {
            DynamoDbTable<Students> table = enhancedClient.table(
                    System.getenv("DYNAMO_DB_TABLE_NAME"),
                    TableSchema.fromBean(Students.class));

            // Populate the Table.
            studentRecord.setStudentID(Integer.parseInt(BotLogic.getSlotValue("StudentID")));
            studentRecord.setFirstName(BotLogic.getSlotValue("FirstName"));
            studentRecord.setLastName(BotLogic.getSlotValue("LastName"));
            studentRecord.setDateOfBirth(BotLogic.getSlotValue("DateOfBirth"));
            studentRecord.setClassification(BotLogic.getSlotValue("Classification"));
            studentRecord.setEmail(BotLogic.getSlotValue("Email"));

            // Put the student data into an Amazon DynamoDB table.
            table.putItem(studentRecord);
        } catch (DynamoDbException error) {
            System.err.println(error.getMessage());
            System.exit(1);
        }

        List<String> messages = new ArrayList<>();
        String mainMessage = "Student with ID: " + studentRecord.getStudentID() + " has been successfully added.";
        messages.add(mainMessage);
        BotLogic.multipleMessages(messages, "PlainText");

        SNS sns = new SNS();
        SnsClient snsClient = sns.authenticateSNS();
        sns.publishMessage(snsClient, messages, mainMessage, "INSERT");
    }

    public void removeRecord(DynamoDbClient dynamoDbClient, String keyName, String keyValue) throws IOException {
        Map<String, AttributeValue> keyToGet = new HashMap<>();
        keyToGet.put(keyName, AttributeValue.builder().n(keyValue).build());

        DeleteItemRequest deleteRequest = DeleteItemRequest.builder()
                .tableName(System.getenv("DYNAMO_DB_TABLE_NAME"))
                .key(keyToGet)
                .build();
        try {
            dynamoDbClient.deleteItem(deleteRequest);
        } catch (DynamoDbException error) {
            System.err.println(error.getMessage());
            System.exit(1);
        }

        List<String> messages = new ArrayList<>();
        String mainMessage = "Student with ID: " + keyValue + " has been successfully removed.";
        messages.add(mainMessage);
        BotLogic.multipleMessages(messages, "PlainText");

        SNS sns = new SNS();
        SnsClient snsClient = sns.authenticateSNS();
        sns.publishMessage(snsClient, messages, mainMessage, "REMOVE");
    }

    public void updateRecord(DynamoDbClient dynamoDbClient, String keyName, String keyValue, String nameOfRecordToUpdate, String newValue) throws IOException {
        Map<String,AttributeValue> itemKey = new HashMap<>();
        itemKey.put(keyName, AttributeValue.builder().n(keyValue).build());
        Map<String, AttributeValueUpdate> updatedValues = new HashMap<>();

        // Update the column specified by nameOfRecordToUpdate with newValue
        updatedValues.put(nameOfRecordToUpdate, AttributeValueUpdate.builder()
                .value(AttributeValue.builder().s(newValue).build())
                .action(AttributeAction.PUT)
                .build());

        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName(System.getenv("DYNAMO_DB_TABLE_NAME"))
                .key(itemKey)
                .attributeUpdates(updatedValues)
                .build();

        try {
            dynamoDbClient.updateItem(request);
        } catch (DynamoDbException error) {
            System.err.println(error.getMessage());
            System.exit(1);
        }

        List<String> messages = new ArrayList<>();
        String mainMessage = "The " + nameOfRecordToUpdate + " attribute's value of the student with id " + keyValue + " has been successfully updated.";
        messages.add(mainMessage);
        BotLogic.multipleMessages(messages, "PlainText");

        SNS sns = new SNS();
        SnsClient snsClient = sns.authenticateSNS();
        sns.publishMessage(snsClient, messages, mainMessage, "UPDATE");
    }
}