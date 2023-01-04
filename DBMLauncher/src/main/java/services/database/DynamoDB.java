package services.database;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.amazonaws.services.lambda.runtime.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import services.SNS;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.Response;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.paginators.ScanIterable;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;
import software.amazon.awssdk.services.sns.SnsClient;

import handler.BotLogic;

public class DynamoDB {

    private static final String DYNAMO_DB_TABLE_NAME = "Students";

    public static DynamoDbClient authenticateDynamoDB(AwsBasicCredentials awsBasicCredentials, Region appRegion) {
        return DynamoDbClient
                .builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .region(appRegion)
                .build();
    }

    public static String createTable(DynamoDbClient dynamoDbClient, String tableName, String key) {
        ListTablesResponse listTables = dynamoDbClient.listTables();
        for (String table : listTables.tableNames()) {
            if (table.equals(DYNAMO_DB_TABLE_NAME)) {
                return table + " table already exists.";
            }
        }

        try {
            DynamoDbWaiter dbWaiter = dynamoDbClient.waiter();
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

            CreateTableResponse createTableResponse = dynamoDbClient.createTable(request);

            DescribeTableRequest tableRequest = DescribeTableRequest.builder()
                    .tableName(createTableResponse.tableDescription().tableName())
                    .build();

            // Wait until the Amazon DynamoDB table is created
            WaiterResponse<DescribeTableResponse> waiterResponse =  dbWaiter.waitUntilTableExists(tableRequest);
            waiterResponse.matched().response().ifPresent(System.out::println);
        } catch (DynamoDbException error) {
            System.err.println(error.getMessage());
            System.exit(1);
        }
        return "Table " + DYNAMO_DB_TABLE_NAME + " created.";
    }

    public static void getRecord(DynamoDbClient dynamoDbClient, String keyName, String keyValue) throws IOException {
        Map<String,AttributeValue> keyToGet = new HashMap<>();
        List<String> messages = new ArrayList<>();

        keyToGet.put(keyName, AttributeValue.builder().n(keyValue).build());

        GetItemRequest request = GetItemRequest.builder()
                .key(keyToGet)
                .tableName(DYNAMO_DB_TABLE_NAME)
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
        SnsClient snsClient = sns.authenticateSNS(BotLogic.getAwsBasicCredentials());
        sns.publishMessage(snsClient, messages, mainMessage, "GET");
    }

    public static void putRecord(DynamoDbEnhancedClient enhancedClient) throws IOException {
        Students studentRecord = new Students();
        try {
            DynamoDbTable<Students> table = enhancedClient.table(
                    DYNAMO_DB_TABLE_NAME, TableSchema.fromBean(Students.class));

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
        SnsClient snsClient = sns.authenticateSNS(BotLogic.getAwsBasicCredentials());
        sns.publishMessage(snsClient, messages, mainMessage, "INSERT");
    }

    public static void removeRecord(DynamoDbClient dynamoDbClient, String keyName, String keyValue) throws IOException {
        Map<String, AttributeValue> keyToGet = new HashMap<>();
        keyToGet.put(keyName, AttributeValue.builder().n(keyValue).build());

        DeleteItemRequest deleteRequest = DeleteItemRequest.builder()
                .tableName(DYNAMO_DB_TABLE_NAME)
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
        SnsClient snsClient = sns.authenticateSNS(BotLogic.getAwsBasicCredentials());
        sns.publishMessage(snsClient, messages, mainMessage, "REMOVE");
    }

    public static void updateRecord(DynamoDbClient dynamoDbClient, String keyName, String keyValue, String nameOfRecordToUpdate, String newValue) throws IOException {
        Map<String,AttributeValue> itemKey = new HashMap<>();
        itemKey.put(keyName, AttributeValue.builder().n(keyValue).build());
        Map<String, AttributeValueUpdate> updatedValues = new HashMap<>();

        // Update the column specified by nameOfRecordToUpdate with newValue
        updatedValues.put(nameOfRecordToUpdate, AttributeValueUpdate.builder()
                .value(AttributeValue.builder().s(newValue).build())
                .action(AttributeAction.PUT)
                .build());

        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName(DYNAMO_DB_TABLE_NAME)
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
        SnsClient snsClient = sns.authenticateSNS(BotLogic.getAwsBasicCredentials());
        sns.publishMessage(snsClient, messages, mainMessage, "UPDATE");
    }

    public static Object scanTable(DynamoDbClient dynamoDbClient, Map<String, Object> event, Context context) {
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName("Students")
                .consistentRead(false)
                .attributesToGet("studentID", "classification", "dateOfBirth", "email", "firstName", "lastName")
                .limit(Integer.MAX_VALUE)
                .build();

        ScanIterable scanIterable = dynamoDbClient.scanPaginator(scanRequest);
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
        JSONObject responseBody = new JSONObject();
        responseBody.put("students", jsonArray);

        // Response headers.
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Headers", new ArrayList<>(){{add("Content-Type");}});
        headers.put("Access-Control-Allow-Origin", new ArrayList<>(){{add("https://" + System.getenv("REST_API_ID") + ".execute-api.us-east-1.amazonaws.com");}});
        headers.put("Access-Control-Allow-Methods", new ArrayList<>(){{add("OPTIONS, POST");}});
        headers.put("Access-Control-Allow-Credentials", new ArrayList<>(){{add("true");}});

        InputStream inputStream = new ByteArrayInputStream(responseBody.toJSONString().getBytes(StandardCharsets.UTF_8));
        AbortableInputStream abortableInputStream = AbortableInputStream.create(inputStream);

        SdkHttpFullResponse sdkHttpFullResponse = SdkHttpFullResponse
                .builder()
                .headers(headers)
                .statusCode(200)
                .content(abortableInputStream)
                .build();

        Response response = Response
                .builder()
                .httpResponse(sdkHttpFullResponse)
                .build();

        // HAVE TO BE MODIFIED (INCORRECT)

        return response.response();
    }
}