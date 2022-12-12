package handler;

import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.lexmodelsv2.LexModelsV2Client;
import software.amazon.awssdk.services.lexmodelsv2.model.*;
import software.amazon.awssdk.services.lexruntimev2.model.Slot;

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

        CreateIntentResponse greetingIntentResponse = createIntent(lexModelsV2Client,
                createBotResponse,
                "Greeting",
                "Greeting the admin",
                getGreetingIntentSampleUtterances(),
                true
        );

        CreateIntentResponse removeStudentIntentResponse = createIntent(lexModelsV2Client,
                createBotResponse,
                "RemoveStudent",
                "REMOVE a student",
                getRemoveStudentIntentSampleUtterances(),
                true
        );

        createSlots(lexModelsV2Client,
                createBotResponse,
                removeStudentIntentResponse,
                getRemoveStudentIntentSlotNames(),
                "Please enter the student ID.",
                "AMAZON.Number"
        );
    }

    public void createSlots(LexModelsV2Client lexModelsV2Client,
                           CreateBotResponse createBotResponse,
                           CreateIntentResponse createIntentResponse,
                           List<String> slotNamesList,
                            String promptMessage,
                            String slotType
                            ) {

        for (int i = 0; i < slotNamesList.size(); i++) {
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

//            CreateSlotTypeRequest createSlotTypeRequest = CreateSlotTypeRequest
//                    .builder()
//                    .botId(createBotResponse.botId())
//                    .botVersion("DRAFT")
//                    .localeId("en_US")
//                    .slotTypeName(slotType)
//                    .parentSlotTypeSignature("AMAZON.Number")
//                    .build();

            CreateSlotRequest createSlotRequest = CreateSlotRequest
                    .builder()
                    .botId(createBotResponse.botId())
                    .botVersion("DRAFT")
                    .localeId("en_US")
                    .slotName(slotNamesList.get(i))
                    .intentId(createIntentResponse.intentId())
                    .description("ANY DESCRIPTION")
                    .slotTypeId(slotType)
                    .valueElicitationSetting(slotValueElicitationSetting)
                    .build();

            CreateSlotResponse createSlotResponse = lexModelsV2Client.createSlot(createSlotRequest);
        }
    }

    public CreateIntentResponse createIntent(LexModelsV2Client lexModelsV2Client,
                                    CreateBotResponse createBotResponse,
                                    String intentName,
                                    String intentDescription,
                                    List<String> sampleUtterances,
                                    boolean isFulfillmentCodeHook
                                    ) {

        CreateIntentRequest createIntentRequest;
        if (isFulfillmentCodeHook) {
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
                    .build();
        } else {
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

    public List<String> getRemoveStudentIntentSlotNames() {
        List<String> slotNamesList = new ArrayList<>();
        slotNamesList.add("StudentID");
        return slotNamesList;
    }
}