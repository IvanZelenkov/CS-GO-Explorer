package services.database;

import handler.AppHandler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import services.SNS;
import services.api.ApiGateway;
import services.api.ApiGatewayProxyResponse;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.paginators.ScanIterable;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;
import software.amazon.awssdk.services.sns.SnsClient;

/**
 * Amazon DynamoDB is a fully managed NoSQL database service that provides fast and predictable performance with
 * seamless scalability. You can use Amazon DynamoDB to create a database table that can store and retrieve any
 * amount of data, and serve any level of request traffic.
 */
public class DynamoDB {

    /**
     * Authenticate to the DynamoDB client using the AWS user's credentials.
     * @param awsBasicCredentials The AWS Access Key ID and Secret Access Key are credentials that are used to securely sign requests to AWS services.
     * @param appRegion The AWS Region where the service will be hosted.
     * @return Service client for accessing Amazon DynamoDB.
     */
    public static DynamoDbClient authenticateDynamoDB(AwsBasicCredentials awsBasicCredentials, Region appRegion) {
        return DynamoDbClient
                .builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .region(appRegion)
                .build();
    }

    /**
     * Creates a new DynamoDB.
     * @param dynamoDbClient Client for accessing DynamoDB.
     * @param tableName The name of the table to create.
     * @param attributeName A name for the attribute.
     * @param attributeType The data type for the attribute, where:
     *                      S - the attribute is of type String,
     *                      N - the attribute is of type Number,
     *                      B - the attribute is of type Binary.
     * @param keyType The role that this key attribute will assume: HASH - partition key or RANGE - sort key
     * @return The id of the table.
     */
    public static String createTable(DynamoDbClient dynamoDbClient,
                                     String tableName,
                                     String attributeName,
                                     ScalarAttributeType attributeType,
                                     KeyType keyType) {
        try {
            ListTablesResponse listTables = dynamoDbClient.listTables();

            for (String table : listTables.tableNames())
                if (table.equals(System.getenv("DYNAMO_DB_TABLE_NAME")))
                    return table + " table already exists.";

            CreateTableRequest request = CreateTableRequest
                    .builder()
                    .attributeDefinitions(AttributeDefinition
                            .builder()
                            .attributeName(attributeName)
                            .attributeType(attributeType)
                            .build())
                    .keySchema(KeySchemaElement
                            .builder()
                            .attributeName(attributeName)
                            .keyType(keyType)
                            .build())
                    .provisionedThroughput(ProvisionedThroughput
                            .builder()
                            .readCapacityUnits(10L)
                            .writeCapacityUnits(10L)
                            .build())
                    .tableName(tableName)
                    .build();

            CreateTableResponse createTableResponse = dynamoDbClient.createTable(request);

            DescribeTableRequest tableRequest = DescribeTableRequest.builder()
                    .tableName(createTableResponse.tableDescription().tableName())
                    .build();

            // Wait until the Amazon DynamoDB table is created
            DynamoDbWaiter dbWaiter = dynamoDbClient.waiter();
            WaiterResponse<DescribeTableResponse> waiterResponse =  dbWaiter.waitUntilTableExists(tableRequest);
            waiterResponse.matched().response().ifPresent(System.out::println);

            return createTableResponse.tableDescription().tableId();
        } catch (DynamoDbException error) {
            System.err.println(error.getMessage());
            System.exit(1);
        }
        return "";
    }

    /**
     * Gets a set of attributes for the item with the given primary key.
     * If there is no matching item, it does not return any data and
     * there will be no Item element in the response.
     * @param dynamoDbClient Client for accessing DynamoDB.
     * @param primaryKeyName The name of the primary key attribute.
     * @param primaryKeyValue The value of the primary key attribute.
     */
    public static void getRecord(DynamoDbClient dynamoDbClient, String primaryKeyName, String primaryKeyValue) {
        List<String> messages = new ArrayList<>();
        Map<String,AttributeValue> keyToGet = new HashMap<>();
        keyToGet.put(primaryKeyName, AttributeValue.builder().n(primaryKeyValue).build());

        try {
            GetItemRequest getItemRequest = GetItemRequest
                    .builder()
                    .key(keyToGet)
                    .tableName(System.getenv("DYNAMO_DB_TABLE_NAME"))
                    .build();

            Map<String, AttributeValue> returnedItem = dynamoDbClient.getItem(getItemRequest).item();

            if (!returnedItem.isEmpty()) {
                Set<String> keys = returnedItem.keySet();
                messages.add("Amazon DynamoDB table attributes: \n");

                for (String key : keys) {
                    messages.add(String.format("%s: %s\n", key, returnedItem.get(key).toString()));
                }
                messages.add("Student with ID " + primaryKeyValue + " was successfully retrieved.");
            } else {
                messages.add(String.format("No item found with the key %s!\n", primaryKeyName));
            }
        } catch (DynamoDbException error) {
            System.err.println(error.getMessage());
            System.exit(1);
        }

        // Publish the message to the SNS topic
        String mainMessage;
        if (messages.size() > 1) {
            mainMessage = messages.get(messages.size() - 1);
        } else {
            mainMessage = messages.get(0);
        }
        SnsClient snsClient = SNS.authenticateSNS(AppHandler.getAwsBasicCredentials(), Region.of(System.getenv("AWS_REGION")));
        SNS.publishMessage(snsClient, System.getenv("SNS_TOPIC_ARN"), messages, mainMessage, "GET");
        snsClient.close();
    }

    /**
     * Creates a new item, or replaces an old item with a new item. If an item that has
     * the same primary key as the new item already exists in the specified table,
     * the new item completely replaces the existing item.
     * @param dynamoDbClient Client for accessing DynamoDB.
     */
    public static void putRecord(DynamoDbClient dynamoDbClient) {
        Students studentRecord = new Students();
        try {
            DynamoDbEnhancedClient dynamoDbEnhancedClient = DynamoDbEnhancedClient
                    .builder()
                    .dynamoDbClient(dynamoDbClient)
                    .build();

            DynamoDbTable<Students> table = dynamoDbEnhancedClient.table(
                    System.getenv("DYNAMO_DB_TABLE_NAME"), TableSchema.fromBean(Students.class));

            // Populate the table
            studentRecord.setStudentId(Integer.parseInt(AppHandler.getSlotValue("StudentId")));
            studentRecord.setFirstName(AppHandler.getSlotValue("FirstName"));
            studentRecord.setLastName(AppHandler.getSlotValue("LastName"));
            studentRecord.setDateOfBirth(AppHandler.getSlotValue("DateOfBirth"));
            studentRecord.setClassification(AppHandler.getSlotValue("Classification"));
            studentRecord.setEmail(AppHandler.getSlotValue("Email"));

            // Put the student data into an Amazon DynamoDB table
            table.putItem(studentRecord);
        } catch (DynamoDbException error) {
            System.err.println(error.getMessage());
            System.exit(1);
        }

        // Publish the message to the SNS topic
        List<String> messages = new ArrayList<>();
        String mainMessage = "Student with ID: " + studentRecord.getStudentId() + " has been successfully added.";
        messages.add(mainMessage);
        SnsClient snsClient = SNS.authenticateSNS(AppHandler.getAwsBasicCredentials(), Region.of(System.getenv("AWS_REGION")));
        SNS.publishMessage(snsClient, System.getenv("SNS_TOPIC_ARN"), messages, mainMessage, "INSERT");
        snsClient.close();
    }

    /**
     * Deletes a single item in a table by primary key.
     * @param dynamoDbClient Client for accessing DynamoDB.
     * @param primaryKeyName The name of the primary key attribute.
     * @param primaryKeyValue The value of the primary key attribute.
     */
    public static void removeRecord(DynamoDbClient dynamoDbClient, String primaryKeyName, String primaryKeyValue) {
        List<String> messages = new ArrayList<>();
        Map<String, AttributeValue> keyToGet = new HashMap<>();
        keyToGet.put(primaryKeyName, AttributeValue.builder().n(primaryKeyValue).build());

        try {
            GetItemRequest getItemRequest = GetItemRequest
                    .builder()
                    .key(keyToGet)
                    .tableName(System.getenv("DYNAMO_DB_TABLE_NAME"))
                    .build();

            GetItemResponse getItemResponse = dynamoDbClient.getItem(getItemRequest);
            if (getItemResponse.hasItem()) {
                DeleteItemRequest deleteRequest = DeleteItemRequest.builder()
                        .tableName(System.getenv("DYNAMO_DB_TABLE_NAME"))
                        .key(keyToGet)
                        .build();
                dynamoDbClient.deleteItem(deleteRequest);
                messages.add("Student with ID: " + primaryKeyValue + " has been successfully removed.");
            } else {
                messages.add("Student with ID: " + primaryKeyValue + " has not been found.");
            }
        } catch (DynamoDbException error) {
            System.err.println(error.getMessage());
            System.exit(1);
        }

        // Publish the message to the SNS topic
        String mainMessage = messages.get(0);
        SnsClient snsClient = SNS.authenticateSNS(AppHandler.getAwsBasicCredentials(), Region.of(System.getenv("AWS_REGION")));
        SNS.publishMessage(snsClient, System.getenv("SNS_TOPIC_ARN"), messages, mainMessage, "REMOVE");
        snsClient.close();
    }

    /**
     * Edits an existing item's attributes, or adds a new item to the table if it does not already exist.
     * @param dynamoDbClient Client for accessing DynamoDB.
     * @param primaryKeyName The name of the primary key attribute.
     * @param primaryKeyValue The value of the primary key attribute.
     * @param attributeName The name of the attribute to update its value.
     * @param newValue The new value of the attribute.
     */
    public static void updateRecord(DynamoDbClient dynamoDbClient,
                                    String primaryKeyName,
                                    String primaryKeyValue,
                                    String attributeName,
                                    String newValue) {
        List<String> messages = new ArrayList<>();
        Map<String,AttributeValue> itemKey = new HashMap<>();
        itemKey.put(primaryKeyName, AttributeValue.builder().n(primaryKeyValue).build());

        List<String> dynamoDbAttributes = new ArrayList<>();
        Field[] fields = Students.class.getDeclaredFields();
        for (Field field : fields)
            dynamoDbAttributes.add(field.getName().toLowerCase());

        AttributeValueUpdate attributeValue;
        String mainMessage;
        if (dynamoDbAttributes.contains(attributeName.toLowerCase())) {
            // Update the column specified by attributeName with newValue
            attributeValue = AttributeValueUpdate
                    .builder()
                    .value(AttributeValue.builder().s(newValue).build())
                    .action(AttributeAction.PUT)
                    .build();

            Map<String, AttributeValueUpdate> updatedValues = new HashMap<>();
            updatedValues.put(attributeName, attributeValue);

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
            mainMessage = "The " + attributeName + " attribute's value of the student with id " + primaryKeyValue + " has been successfully updated.";
        } else {
            mainMessage = "An invalid student ID or attribute name was entered. Please try again.";
        }

        // Publish the message to the SNS topic
        messages.add(mainMessage);
        SnsClient snsClient = SNS.authenticateSNS(AppHandler.getAwsBasicCredentials(), Region.of(System.getenv("AWS_REGION")));
        SNS.publishMessage(snsClient, System.getenv("SNS_TOPIC_ARN"), messages, mainMessage, "UPDATE");
    }

    /**
     * Scans table items and returns an API Gateway proxy response containing all table items
     * that will be displayed to the user on the website.
     * @param dynamoDbClient Client for accessing DynamoDB.
     * @param event The Lambda Function event.
     * @return API Gateway proxy response containing status code, headers, body, and base64 encoding enabled.
     */
    public static ApiGatewayProxyResponse scanTable(DynamoDbClient dynamoDbClient, Map<String, Object> event) {
        // Handles CORS preflight request
        if (event.get("httpMethod").equals("OPTIONS")) {
            return ApiGateway.generateResponseForOptionsRequest();
        }

        ScanRequest scanRequest = ScanRequest.builder()
                .tableName("CsGoExplorerTable")
                .consistentRead(false)
                .attributesToGet("studentID", "classification", "dateOfBirth", "email", "firstName", "lastName")
                .limit(Integer.MAX_VALUE)
                .build();

        ScanIterable scanIterable = dynamoDbClient.scanPaginator(scanRequest);

        if (scanIterable.items().stream().count() != 0) {
            JSONArray jsonArray = new JSONArray();
            for (ScanResponse page : scanIterable) {
                for (Map<String, AttributeValue> item : page.items()) {
                    JSONObject jsonObject = new JSONObject();
                    for (Map.Entry<String, AttributeValue> attribute : item.entrySet()) {
                        if (attribute.getValue().type().equals(AttributeValue.Type.N)) {
                            jsonObject.put(attribute.getKey(), attribute.getValue().n());
                        } else if (attribute.getValue().type().equals(AttributeValue.Type.S)) {
                            jsonObject.put(attribute.getKey(), attribute.getValue().s());
                        }
                    }
                    jsonArray.add(jsonObject);
                }
            }
            return ApiGateway.generateResponseForPostOrGetRequest(jsonArray.toString());
        } else {
            return ApiGateway.generateResponseForPostOrGetRequest("");
        }
    }
}