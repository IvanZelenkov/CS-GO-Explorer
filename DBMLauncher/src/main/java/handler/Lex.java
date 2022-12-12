package handler;

import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.lexmodelsv2.LexModelsV2Client;
import software.amazon.awssdk.services.lexmodelsv2.model.*;

import java.util.ArrayList;
import java.util.List;

public class Lex {
    public void createLexBot(LexModelsV2Client lexModelsV2Client) {
        DataPrivacy dataPrivacy = DataPrivacy
                .builder()
                .childDirected(false)
                .build();

        // Create bot
        CreateBotRequest createBotRequest = CreateBotRequest
                .builder()
                .botName("DBM")
                .description("Database Bot Manager")
                .roleArn("arn:aws:iam::981684844178:role/database-bot-manager-lambda-role")
                .dataPrivacy(dataPrivacy)
                .idleSessionTTLInSeconds(600)
                .build();
        CreateBotResponse createBotResponse = lexModelsV2Client.createBot(createBotRequest);
        DescribeBotRequest describeBotRequest = DescribeBotRequest
                .builder()
                .botId(createBotResponse.botId())
                .build();
        WaiterResponse<DescribeBotResponse> waitUntilBotAvailable = lexModelsV2Client.waiter().waitUntilBotAvailable(describeBotRequest);
        waitUntilBotAvailable.matched().response().ifPresent(System.out::println);

        // Create bot locale
        VoiceSettings voiceSettings = VoiceSettings
                .builder()
                .voiceId("Ivy")
                .engine("standard")
                .build();
        CreateBotLocaleRequest createBotLocaleRequest = CreateBotLocaleRequest
                .builder()
                .botId(createBotResponse.botId())
                .botVersion("DRAFT")
                .localeId("en_US")
                .voiceSettings(voiceSettings)
                .nluIntentConfidenceThreshold(0.4)
                .build();
        CreateBotLocaleResponse createBotLocaleResponse = lexModelsV2Client.createBotLocale(createBotLocaleRequest);
        DescribeBotLocaleRequest describeBotLocaleRequest = DescribeBotLocaleRequest
                .builder()
                .botId(createBotLocaleResponse.botId())
                .botVersion("DRAFT")
                .localeId("en_US")
                .build();
        WaiterResponse<DescribeBotLocaleResponse> waitUntilBotLocaleAvailable = lexModelsV2Client.waiter().waitUntilBotLocaleCreated(describeBotLocaleRequest);
        waitUntilBotLocaleAvailable.matched().response().ifPresent(System.out::println);

        // Greeting intent
        CreateIntentResponse greetingIntentResponse = createIntent(lexModelsV2Client,
                createBotResponse,
                "Greeting",
                "Bot greeting",
                getGreetingIntentSampleUtterances(),
                true,
                false
        );

        // GetStudent intent
        CreateIntentResponse getStudentIntentResponse = createIntent(lexModelsV2Client,
                createBotResponse,
                "GetStudent",
                "GET student data",
                getGetStudentIntentSampleUtterances(),
                true,
                false
        );
        createSlot(lexModelsV2Client,
                createBotResponse,
                getStudentIntentResponse,
                "StudentID",
                "The student ID slot is required to search for and retrieve student data.",
                "Please enter a student ID to search.",
                "AMAZON.Number"
        );

        // RemoveStudent intent
        CreateIntentResponse removeStudentIntentResponse = createIntent(lexModelsV2Client,
                createBotResponse,
                "RemoveStudent",
                "REMOVE student data",
                getRemoveStudentIntentSampleUtterances(),
                true,
                false
        );
        createSlot(lexModelsV2Client,
                createBotResponse,
                removeStudentIntentResponse,
                "StudentID",
                "The student ID slot is required to search and remove a student.",
                "Please enter a student ID.",
                "AMAZON.Number"
        );

        // AnotherActionRejected intent
        CreateIntentResponse anotherActionRejectedIntentResponse = createIntent(lexModelsV2Client,
                createBotResponse,
                "AnotherActionRejected",
                "End the conversation if the user no longer wants to manage the database",
                getAnotherActionRejectedIntentSampleUtterances(),
                false,
                true
        );

        // UpdateStudent intent
        CreateIntentResponse updateStudentIntentResponse = createIntent(lexModelsV2Client,
                createBotResponse,
                "UpdateStudent",
                "UPDATE student data",
                getUpdateStudentIntentSampleUtterances(),
                true,
                false
        );
        CreateSlotResponse updateStudentIntentStudentIdSlot = createSlot(lexModelsV2Client,
                createBotResponse,
                updateStudentIntentResponse,
                "StudentID",
                "The student ID slot is required to search for a student.",
                "Please enter a student ID to find a person.",
                "AMAZON.Number"
        );
        CreateSlotResponse updateStudentIntentAttributeNameSlot = createSlot(lexModelsV2Client,
                createBotResponse,
                updateStudentIntentResponse,
                "AttributeName",
                "The entered attribute that needs to be updated in the Students table.",
                "Which student attribute do you need to update?",
                "AMAZON.FreeFormInput"
        );
        CreateSlotResponse updateStudentIntentNewAttributeValueSlot = createSlot(lexModelsV2Client,
                createBotResponse,
                updateStudentIntentResponse,
                "NewAttributeValue",
                "New attribute value that will update the old one.",
                "Enter a new attribute value.",
                "AMAZON.FreeFormInput"
        );
        List<CreateSlotResponse> updateStudentIntentSlotResponsesList = new ArrayList<>();
        updateStudentIntentSlotResponsesList.add(updateStudentIntentStudentIdSlot);
        updateStudentIntentSlotResponsesList.add(updateStudentIntentAttributeNameSlot);
        updateStudentIntentSlotResponsesList.add(updateStudentIntentNewAttributeValueSlot);
        List<Integer> updateStudentIntentSlotPrioritiesList = new ArrayList<>();
        updateStudentIntentSlotPrioritiesList.add(1);
        updateStudentIntentSlotPrioritiesList.add(2);
        updateStudentIntentSlotPrioritiesList.add(3);
        slotPriorities(lexModelsV2Client, updateStudentIntentSlotResponsesList, updateStudentIntentSlotPrioritiesList);
    }

    public void slotPriorities(LexModelsV2Client lexModelsV2Client,
                               List<CreateSlotResponse> intentSlotResponsesList,
                               List<Integer> intentSlotPrioritiesList) {
        for (int i = 0; i < intentSlotResponsesList.size(); i++) {
            SlotPriority slotPriority = SlotPriority
                    .builder()
                    .slotId(intentSlotResponsesList.get(i).slotId())
                    .priority(intentSlotPrioritiesList.get(i))
                    .build();
        }
    }

    public CreateIntentResponse createIntent(LexModelsV2Client lexModelsV2Client,
                                    CreateBotResponse createBotResponse,
                                    String intentName,
                                    String intentDescription,
                                    List<String> sampleUtterances,
                                    boolean isFulfillmentCodeHook,
                                    boolean isDialogCodeHook) {

        CreateIntentRequest createIntentRequest;
        if (isFulfillmentCodeHook && !isDialogCodeHook) {
            FulfillmentCodeHookSettings fulfillmentCodeHookSettings = FulfillmentCodeHookSettings
                    .builder()
                    .enabled(true)
                    .build();

            createIntentRequest = CreateIntentRequest
                    .builder()
                    .intentName(intentName)
                    .botId(createBotResponse.botId())
                    .botVersion("DRAFT")
                    .localeId("en_US")
                    .description(intentDescription)
                    .sampleUtterances(createSampleUtterances(sampleUtterances))
                    .fulfillmentCodeHook(fulfillmentCodeHookSettings)
                    .build();
        } else if (!isFulfillmentCodeHook && isDialogCodeHook) {
            DialogCodeHookSettings dialogCodeHookSettings = DialogCodeHookSettings
                    .builder()
                    .enabled(true)
                    .build();

            createIntentRequest = CreateIntentRequest
                    .builder()
                    .botId(createBotResponse.botId())
                    .botVersion("DRAFT")
                    .localeId("en_US")
                    .intentName(intentName)
                    .description(intentDescription)
                    .sampleUtterances(createSampleUtterances(sampleUtterances))
                    .dialogCodeHook(dialogCodeHookSettings)
                    .build();
        } else if (isFulfillmentCodeHook && isDialogCodeHook) {
            FulfillmentCodeHookSettings fulfillmentCodeHookSettings = FulfillmentCodeHookSettings
                    .builder()
                    .enabled(true)
                    .build();

            DialogCodeHookSettings dialogCodeHookSettings = DialogCodeHookSettings
                    .builder()
                    .enabled(true)
                    .build();

            createIntentRequest = CreateIntentRequest
                    .builder()
                    .botId(createBotResponse.botId())
                    .botVersion("DRAFT")
                    .localeId("en_US")
                    .intentName(intentName)
                    .description(intentDescription)
                    .sampleUtterances(createSampleUtterances(sampleUtterances))
                    .fulfillmentCodeHook(fulfillmentCodeHookSettings)
                    .dialogCodeHook(dialogCodeHookSettings)
                    .build();
        }
        else {
            createIntentRequest = CreateIntentRequest
                    .builder()
                    .botId(createBotResponse.botId())
                    .botVersion("DRAFT")
                    .localeId("en_US")
                    .intentName(intentName)
                    .description(intentDescription)
                    .sampleUtterances(createSampleUtterances(sampleUtterances))
                    .build();
        }

        CreateIntentResponse createIntentResponse = lexModelsV2Client.createIntent(createIntentRequest);
        return createIntentResponse;
    }

    public List<SampleUtterance> createSampleUtterances(List<String> sampleUtterances) {
        List<SampleUtterance> sampleUtterancesListObjects = new ArrayList<>();

        for (String utterance : sampleUtterances) {
            sampleUtterancesListObjects.add(SampleUtterance
                    .builder()
                    .utterance(utterance)
                    .build()
            );
        }
        return sampleUtterancesListObjects;
    }

    public CreateSlotResponse createSlot(LexModelsV2Client lexModelsV2Client,
                           CreateBotResponse createBotResponse,
                           CreateIntentResponse createIntentResponse,
                           String slotName,
                           String slotDescription,
                           String promptMessage,
                           String slotType
    ) {

        PlainTextMessage plainTextMessage = PlainTextMessage
                .builder()
                .value(promptMessage)
                .build();

        Message message = Message
                .builder()
                .plainTextMessage(plainTextMessage)
                .build();

        MessageGroup messageGroup = MessageGroup
                .builder()
                .message(message)
                .build();

        PromptSpecification promptSpecification = PromptSpecification
                .builder()
                .messageGroups(messageGroup)
                .maxRetries(2)
                .build();

        SlotValueElicitationSetting slotValueElicitationSetting = SlotValueElicitationSetting
                .builder()
                .promptSpecification(promptSpecification)
                .slotConstraint("Required")
                .build();

//        CreateSlotTypeRequest createSlotTypeRequest = CreateSlotTypeRequest
//                .builder()
//                .botId(createBotResponse.botId())
//                .botVersion("DRAFT")
//                .localeId("en_US")
//                .slotTypeName(slotType)
//                .parentSlotTypeSignature("AMAZON.Number")
//                .build();

        CreateSlotRequest createSlotRequest = CreateSlotRequest
                .builder()
                .botId(createBotResponse.botId())
                .botVersion("DRAFT")
                .localeId("en_US")
                .slotName(slotName)
                .description(slotDescription)
                .intentId(createIntentResponse.intentId())
                .slotTypeId(slotType)
                .valueElicitationSetting(slotValueElicitationSetting)
                .build();

        CreateSlotResponse createSlotResponse = lexModelsV2Client.createSlot(createSlotRequest);
        return createSlotResponse;
    }

    public List<String> getGreetingIntentSampleUtterances() {
        List<String> sampleUtterances = new ArrayList<>();
        sampleUtterances.add("Hi");
        sampleUtterances.add("Hello");
        sampleUtterances.add("Good morning");
        sampleUtterances.add("Good afternoon");
        sampleUtterances.add("Good evening");
        sampleUtterances.add("Hey");
        sampleUtterances.add("Nova");
        sampleUtterances.add("Yes");
        sampleUtterances.add("Sure");
        sampleUtterances.add("Yeah");
        sampleUtterances.add("Restart");
        sampleUtterances.add("Start over");
        return sampleUtterances;
    }

    public List<String> getRemoveStudentIntentSampleUtterances() {
        List<String> sampleUtterances = new ArrayList<>();
        sampleUtterances.add("Remove");
        return sampleUtterances;
    }

    public List<String> getGetStudentIntentSampleUtterances() {
        List<String> sampleUtterances = new ArrayList<>();
        sampleUtterances.add("Get");
        return sampleUtterances;
    }

    public List<String> getUpdateStudentIntentSampleUtterances() {
        List<String> sampleUtterances = new ArrayList<>();
        sampleUtterances.add("Update");
        return sampleUtterances;
    }

    public List<String> getAnotherActionRejectedIntentSampleUtterances() {
        List<String> sampleUtterances = new ArrayList<>();
        sampleUtterances.add("No");
        sampleUtterances.add("No action");
        sampleUtterances.add("Bye");
        sampleUtterances.add("No, thank you");
        return sampleUtterances;
    }
}
