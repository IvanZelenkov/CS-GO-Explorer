package handler;

import java.io.IOException;
import java.util.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import services.DynamoDB;

/**
 * The BotHandler program implements the bot application logic that
 * manages an Amazon DynamoDB Students table.
 * @author Ivan Zelenkov
 * @version 2.0.0
 */
@SuppressWarnings("unchecked")
public class BotHandler implements RequestHandler<Map<String, Object>, Object> {

    private static Map<String, Object> currentEvent;
    private static Map<String, Object> sessionState;
    private static Map<String, Object> response;
    private static List<String> greetingUtteranceList;

    /**
     * Handles a Lambda Function request
     * @param event The Lambda Function event
     * @param context The Lambda execution environment context object
     * @return The Lambda Function output
     */
    @Override
    public Object handleRequest(Map<String, Object> event, Context context) {
        currentEvent = event;
        String username = System.getenv("ADMIN_USERNAME");
        String intentName = getIntentName(event);
        String studentIDValue;
        DynamoDB dynamoDB = new DynamoDB();
        DynamoDbClient dynamoDbClient = dynamoDB.authenticateDynamoDB();
        String createTableResponse = dynamoDB.createTable(System.getenv("DYNAMO_DB_TABLE_NAME"), "studentID");
        System.out.print("TABLE: " + createTableResponse);
        switch (intentName) {
            case "Greeting":
                String greetingUserInputText = ((String) event.get("inputTranscript"));
                if (!"nova".equalsIgnoreCase(greetingUserInputText))
                    greetingUserInputText = ((String) event.get("inputTranscript")).replaceAll(",*\\s*[n|N]ova.*", "").trim();

                fillGreetingsList();
                dialogAction("ElicitIntent");
                String message;
                if (greetingUtteranceList.contains(greetingUserInputText.toLowerCase()))
                    message = greetingUserInputText.substring(0, 1).toUpperCase() + greetingUserInputText.substring(1) +
                            ", " + username + ". What action do you want to perform on the \"Students\" table?";
                else
                    message = "What action do you want to perform on the \"Students\" table?";
                responseCard(message);
                return response;
            case "GetStudent":
                dialogAction("ElicitIntent");
                studentIDValue = getSlotValue("StudentID");
                try {
                    dynamoDB.getRecord(dynamoDbClient, "studentID", studentIDValue);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                dynamoDbClient.close();
                return response;
            case "InsertStudent":
                DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient
                        .builder()
                        .dynamoDbClient(dynamoDbClient)
                        .build();
                dialogAction("ElicitIntent");
                try {
                    dynamoDB.putRecord(enhancedClient);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                dynamoDbClient.close();
                return response;
            case "RemoveStudent":
                dialogAction("ElicitIntent");
                studentIDValue = getSlotValue("StudentID");
                try {
                    dynamoDB.removeRecord(dynamoDbClient, "studentID", studentIDValue);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                dynamoDbClient.close();
                return response;
            case "UpdateStudent":
                dialogAction("ElicitIntent");
                studentIDValue = getSlotValue("StudentID");
                String attributeName = getSlotValue("AttributeName");
                String newAttributeValue = getSlotValue("NewAttributeValue");
                try {
                    dynamoDB.updateRecord(dynamoDbClient, "studentID", studentIDValue, attributeName, newAttributeValue);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                dynamoDbClient.close();
                return response;
            case "AnotherActionRejected":
                dialogAction("ElicitIntent");
                messages("Okay, see you next time, " + username + "!", "PlainText");
                return response;
        }
        return event;
    }

    /**
     * Populate the list with greetings.
     */
    private static void fillGreetingsList() {
        greetingUtteranceList = new ArrayList<>();
        greetingUtteranceList.add("hi");
        greetingUtteranceList.add("hello");
        greetingUtteranceList.add("hey");
        greetingUtteranceList.add("good morning");
        greetingUtteranceList.add("good afternoon");
        greetingUtteranceList.add("good evening");
    }

    /**
     * Get intent name.
     * @param event The Lambda Function event.
     * @return Intent name.
     */
    private static String getIntentName(Map<String, Object> event) {
        return (String)
                ((Map<String, Object>)
                        ((Map<String, Object>)
                                event.get("sessionState"))
                                .get("intent"))
                        .get("name");
    }

    /**
     * Get slot name.
     * @param slotName Slot name.
     * @return Map instance containing slot data.
     */
    private static Map<String, Object> getSlotName(String slotName) {
        return (Map<String, Object>)
                ((Map<String, Object>)
                        ((Map<String, Object>)
                                ((Map<String, Object>)
                                        currentEvent.get("sessionState"))
                                        .get("intent"))
                                .get("slots"))
                        .get(slotName);
    }

    /**
     * Get slot value.
     * @param slotName Slot name.
     * @return Value in a slot.
     */
    public static String getSlotValue(String slotName) {
        return (String)
                ((Map<String, Object>)
                        getSlotName(slotName)
                                .get("value"))
                        .get("originalValue");
    }

    private static String getSessionAttribute(Map<String, Object> event, String attribute) {
        return (String)
                ((Map<String, Object>)
                        ((Map<String, Object>)
                                event.get("sessionState"))
                                .get("sessionAttributes"))
                        .get(attribute);
    }

    private static void sessionAttributes(String attribute) {
        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put("tableName", attribute);
        sessionState.put("sessionAttributes", sessionAttributes);
        response.put("sessionState", sessionState);
    }

    /**
     * The next action that Amazon Lex V2 should take.
     * @param type The next action that the bot should take in its interaction with the user.
     */
    private static void dialogAction(String type) {
        Map<String, Object> dialogAction = new HashMap<>();
        sessionState = new HashMap<>();
        response = new HashMap<>();
        dialogAction.put("type", type);
        sessionState.put("dialogAction", dialogAction);
        response.put("sessionState", sessionState);
    }

    /**
     * Bot will be able to display a message in response.
     * @param content Message to display.
     * @param contentType The type of message to use.
     */
    private static void messages(String content, String contentType) {
        Map<String, Object> messages = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();
        messages.put("content", content);
        messages.put("contentType", contentType);
        list.add(messages);
        response.put("messages", list);
    }

    /**
     * Bot will be able to display multiple messages in one response.
     * @param messages List of messages to display.
     */
    public static void multipleMessages(List<String> messages, String contentType) {
        List<Map<String, Object>> maps = new ArrayList<>(messages.size());
        for (int i = 0; i < messages.size(); i++) {
            maps.add((new HashMap<>()));
            maps.get(i).put("contentType", contentType);
            maps.get(i).put("content", messages.get(i));
        }
        response.put("messages", maps);
    }

    /**
     * A response card is shown to the user in a chat box.
     * When we use a response card, the response from the user
     * is constrained to the text associated with a button on the card.
     * @param message The text of the message.
     */
    private static void responseCard(String message) {
        Map<String, Object> content = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();
        content.put("content", message);
        content.put("contentType", "PlainText");
        list.add(content);

        List<Map<String, Object>> buttonList = new ArrayList<>();
        Map<String, Object> buttonOne = new HashMap<>();
        Map<String, Object> buttonTwo = new HashMap<>();
        Map<String, Object> buttonThree = new HashMap<>();
        Map<String, Object> buttonFour = new HashMap<>();
        buttonOne.put("text", "Get");
        buttonOne.put("value", "Get");
        buttonTwo.put("text", "Insert");
        buttonTwo.put("value", "Insert");
        buttonThree.put("text", "Remove");
        buttonThree.put("value", "Remove");
        buttonFour.put("text", "Update");
        buttonFour.put("value", "Update");
        buttonList.add(buttonOne);
        buttonList.add(buttonTwo);
        buttonList.add(buttonThree);
        buttonList.add(buttonFour);

        Map<String, Object> imageResponseCard = new HashMap<>();
        imageResponseCard.put("title", " ");
        imageResponseCard.put("buttons", buttonList);

        Map<String, Object> responseCard = new HashMap<>();
        responseCard.put("contentType", "ImageResponseCard");
        responseCard.put("imageResponseCard", imageResponseCard);

        list.add(responseCard);
        response.put("messages", list);
    }
}