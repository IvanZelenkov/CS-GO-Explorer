package handler;

import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.lexmodelsv2.LexModelsV2Client;
import software.amazon.awssdk.services.lexmodelsv2.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lex {
    public void botSetUp(LexModelsV2Client lexModelsV2Client, String roleArn, String lambdaArn) {
        String botName = "DBM";
        String botDescription = "Database Bot Manager";
        String botVersion = "DRAFT";
        String botAliasName = "DatabaseBotManager";
        String localeId = "en_US";
        String voiceId = "Salli";

        // Create bot
        CreateBotResponse createBotResponse = createBot(
                lexModelsV2Client,
                roleArn,
                botName,
                botDescription
        );

        // Create bot locale
        CreateBotLocaleResponse createDRAFTBotLocaleResponse = createBotLocale(
                lexModelsV2Client,
                createBotResponse,
                localeId,
                botVersion,
                voiceId
        );

        // Create "Greeting" intent
        CreateIntentResponse greetingIntentResponse = createIntent(
                lexModelsV2Client,
                createBotResponse,
                localeId,
                botVersion,
                "Greeting",
                "Bot greeting",
                getGreetingIntentSampleUtterances(),
                true,
                false
        );

        // Create "GetStudent" intent
        CreateIntentResponse getStudentIntentResponse = createIntent(
                lexModelsV2Client,
                createBotResponse,
                localeId,
                botVersion,
                "GetStudent",
                "GET student data",
                getGetStudentIntentSampleUtterances(),
                true,
                false
        );

        // Create "StudentID" slot of "GetStudent" intent
        CreateSlotResponse getStudentIntentStudentIdSlotResponse = createSlotWithPlainTextPrompt(lexModelsV2Client,
                createBotResponse,
                getStudentIntentResponse,
                localeId,
                botVersion,
                "StudentID",
                "The student ID slot is required to search for and retrieve student data.",
                "Please enter a student ID to search.",
                "AMAZON.Number"
        );

        // Prioritize the slots of "GetStudent" intent in the order they created
        List<CreateSlotResponse> updateGetStudentIntentSlotResponsesList = new ArrayList<>();
        updateGetStudentIntentSlotResponsesList.add(getStudentIntentStudentIdSlotResponse);
        List<Integer> updateGetStudentIntentSlotPrioritiesList = new ArrayList<>();
        updateGetStudentIntentSlotPrioritiesList.add(0);

        // GetStudent intent should be updated because the slotPriorities method is missing at CreateIntentRequest in the AWS Java SDK.
        // I assume that adding this method was forgotten by the developers.
        // Intent file structure: https://docs.aws.amazon.com/lexv2/latest/dg/import-export-format.html#json-intent
        UpdateIntentResponse updateGetStudentIntentResponse = updateIntent(
                lexModelsV2Client,
                createBotResponse,
                getStudentIntentResponse,
                updateGetStudentIntentSlotResponsesList,
                updateGetStudentIntentSlotPrioritiesList,
                localeId,
                botVersion,
                "GetStudent",
                "GET student data",
                getGetStudentIntentSampleUtterances(),
                true,
                false);

        // Create "RemoveStudent" intent
        CreateIntentResponse removeStudentIntentResponse = createIntent(
                lexModelsV2Client,
                createBotResponse,
                localeId,
                botVersion,
                "RemoveStudent",
                "REMOVE student data",
                getRemoveStudentIntentSampleUtterances(),
                true,
                false
        );

        // Create "StudentID" slot of "RemoveStudent" intent
        CreateSlotResponse removeStudentIntentStudentIdSlotResponse = createSlotWithPlainTextPrompt(lexModelsV2Client,
                createBotResponse,
                removeStudentIntentResponse,
                localeId,
                botVersion,
                "StudentID",
                "The student ID slot is required to search and remove a student.",
                "Please enter a student ID.",
                "AMAZON.Number"
        );

        // Prioritize the slots of "GetStudent" intent in the order they created
        List<CreateSlotResponse> updateRemoveStudentIntentSlotResponsesList = new ArrayList<>();
        updateRemoveStudentIntentSlotResponsesList.add(removeStudentIntentStudentIdSlotResponse);
        List<Integer> updateRemoveStudentIntentSlotPrioritiesList = new ArrayList<>();
        updateRemoveStudentIntentSlotPrioritiesList.add(0);

        // RemoveStudent intent should be updated because the slotPriorities method is missing at CreateIntentRequest in the AWS Java SDK.
        // I assume that adding this method was forgotten by the developers.
        // Intent file structure: https://docs.aws.amazon.com/lexv2/latest/dg/import-export-format.html#json-intent
        UpdateIntentResponse updateRemoveStudentIntentResponse = updateIntent(
                lexModelsV2Client,
                createBotResponse,
                removeStudentIntentResponse,
                updateRemoveStudentIntentSlotResponsesList,
                updateRemoveStudentIntentSlotPrioritiesList,
                localeId,
                botVersion,
                "RemoveStudent",
                "REMOVE student data",
                getRemoveStudentIntentSampleUtterances(),
                true,
                false);

        // Create "UpdateStudent" intent
        CreateIntentResponse updateStudentIntentResponse = createIntent(
                lexModelsV2Client,
                createBotResponse,
                localeId,
                botVersion,
                "UpdateStudent",
                "UPDATE student data",
                getUpdateStudentIntentSampleUtterances(),
                true,
                false
        );

        // Create "StudentID" slot of "UpdateStudent" intent
        CreateSlotResponse updateStudentIntentStudentIdSlotResponse = createSlotWithPlainTextPrompt(
                lexModelsV2Client,
                createBotResponse,
                updateStudentIntentResponse,
                localeId,
                botVersion,
                "StudentID",
                "The student ID slot is required to search for a student.",
                "Please enter a student ID to find a person.",
                "AMAZON.Number"
        );

        // Create "AttributeName" slot of "UpdateStudent" intent
        CreateSlotResponse updateStudentIntentAttributeNameSlotResponse = createSlotWithPlainTextPrompt(
                lexModelsV2Client,
                createBotResponse,
                updateStudentIntentResponse,
                localeId,
                botVersion,
                "AttributeName",
                "The entered attribute that needs to be updated in the Students table.",
                "Which student attribute do you need to update?",
                "AMAZON.FreeFormInput"
        );

        // Create "NewAttributeValue" slot of "UpdateStudent" intent
        CreateSlotResponse updateStudentIntentNewAttributeValueSlotResponse = createSlotWithPlainTextPrompt(
                lexModelsV2Client,
                createBotResponse,
                updateStudentIntentResponse,
                localeId,
                botVersion,
                "NewAttributeValue",
                "New attribute value that will update the old one.",
                "Enter a new attribute value.",
                "AMAZON.FreeFormInput"
        );

        // Prioritize the slots of "UpdateStudent" intent in the order they created
        List<CreateSlotResponse> updateUpdateStudentIntentSlotResponsesList = new ArrayList<>();
        updateUpdateStudentIntentSlotResponsesList.add(updateStudentIntentStudentIdSlotResponse);
        updateUpdateStudentIntentSlotResponsesList.add(updateStudentIntentAttributeNameSlotResponse);
        updateUpdateStudentIntentSlotResponsesList.add(updateStudentIntentNewAttributeValueSlotResponse);
        List<Integer> updateUpdateStudentIntentSlotPrioritiesList = new ArrayList<>();
        updateUpdateStudentIntentSlotPrioritiesList.add(0);
        updateUpdateStudentIntentSlotPrioritiesList.add(1);
        updateUpdateStudentIntentSlotPrioritiesList.add(2);

        // UpdateStudent intent should be updated because the slotPriorities method is missing at CreateIntentRequest in the AWS Java SDK.
        // I assume that adding this method was forgotten by the developers.
        // Intent file structure: https://docs.aws.amazon.com/lexv2/latest/dg/import-export-format.html#json-intent
        UpdateIntentResponse updateUpdateStudentIntentResponse = updateIntent(
                lexModelsV2Client,
                createBotResponse,
                updateStudentIntentResponse,
                updateUpdateStudentIntentSlotResponsesList,
                updateUpdateStudentIntentSlotPrioritiesList,
                localeId,
                botVersion,
                "UpdateStudent",
                "UPDATE student data",
                getUpdateStudentIntentSampleUtterances(),
                true,
                false);

        // Create "Classification" custom slot
        CreateSlotTypeResponse createSlotTypeResponse = createCustomSlotType(
                lexModelsV2Client,
                createBotResponse,
                localeId,
                botVersion,
                "Classification",
                "Classification level of the student at the university",
                getClassificationSlotTypeValues()
        );

        // Create "InsertStudent" intent
        CreateIntentResponse insertStudentIntentResponse = createIntent(
                lexModelsV2Client,
                createBotResponse,
                localeId,
                botVersion,
                "InsertStudent",
                "Insert student data",
                getInsertStudentIntentSampleUtterances(),
                true,
                false
        );

        // Create "StudentID" slot of "InsertStudent" intent
        CreateSlotResponse insertStudentIntentStudentIdSlotResponse = createSlotWithPlainTextPrompt(
                lexModelsV2Client,
                createBotResponse,
                insertStudentIntentResponse,
                localeId,
                botVersion,
                "StudentID",
                "StudentID is a required attribute that needs to be inserted into the Students table.",
                "Please provide me a student ID.",
                "AMAZON.Number"
        );

        // Create "StudentID" slot of "InsertStudent" intent
        CreateSlotResponse insertStudentIntentFirstNameSlotResponse = createSlotWithPlainTextPrompt(
                lexModelsV2Client,
                createBotResponse,
                insertStudentIntentResponse,
                localeId,
                botVersion,
                "FirstName",
                "FirstName is a required attribute that needs to be inserted into the Students table.",
                "Student's first name.",
                "AMAZON.FirstName"
        );

        // Create "StudentID" slot of "InsertStudent" intent
        CreateSlotResponse insertStudentIntentLastNameSlotResponse = createSlotWithPlainTextPrompt(
                lexModelsV2Client,
                createBotResponse,
                insertStudentIntentResponse,
                localeId,
                botVersion,
                "LastName",
                "LastName is a required attribute that needs to be inserted into the Students table.",
                "Last name.",
                "AMAZON.LastName"
        );

        // Create "StudentID" slot of "InsertStudent" intent
        CreateSlotResponse insertStudentIntentDateOfBirthSlotResponse = createSlotWithPlainTextPrompt(
                lexModelsV2Client,
                createBotResponse,
                insertStudentIntentResponse,
                localeId,
                botVersion,
                "DateOfBirth",
                "DateOfBirth is a required attribute that needs to be inserted into the Students table.",
                "Date of birth (MM/DD/YYYY).",
                "AMAZON.Date"
        );

        // Create "StudentID" slot of "InsertStudent" intent
        CreateSlotResponse insertStudentIntentClassificationSlotResponse = createSlotWithImageResponseCard(
                lexModelsV2Client,
                createBotResponse,
                insertStudentIntentResponse,
                localeId,
                botVersion,
                createSlotTypeResponse.slotTypeName(),
                "Classification is a required attribute that needs to be inserted into the Students table.",
                "Student Classification",
                getClassificationImageResponseCardButtons(),
                createSlotTypeResponse.slotTypeId()
        );

        // Create "StudentID" slot of "InsertStudent" intent
        CreateSlotResponse insertStudentIntentEmailSlotResponse = createSlotWithPlainTextPrompt(
                lexModelsV2Client,
                createBotResponse,
                insertStudentIntentResponse,
                localeId,
                botVersion,
                "Email",
                "Classification is a required attribute that needs to be inserted into the Students table.",
                "Student's email address.",
                "AMAZON.EmailAddress"
        );

        // Prioritize the slots of "InsertStudent" intent in the order they created
        List<CreateSlotResponse> updateInsertStudentIntentSlotResponsesList = new ArrayList<>();
        updateInsertStudentIntentSlotResponsesList.add(insertStudentIntentStudentIdSlotResponse);
        updateInsertStudentIntentSlotResponsesList.add(insertStudentIntentFirstNameSlotResponse);
        updateInsertStudentIntentSlotResponsesList.add(insertStudentIntentLastNameSlotResponse);
        updateInsertStudentIntentSlotResponsesList.add(insertStudentIntentDateOfBirthSlotResponse);
        updateInsertStudentIntentSlotResponsesList.add(insertStudentIntentClassificationSlotResponse);
        updateInsertStudentIntentSlotResponsesList.add(insertStudentIntentEmailSlotResponse);
        List<Integer> updateInsertStudentIntentSlotPrioritiesList = new ArrayList<>();
        updateInsertStudentIntentSlotPrioritiesList.add(0);
        updateInsertStudentIntentSlotPrioritiesList.add(1);
        updateInsertStudentIntentSlotPrioritiesList.add(2);
        updateInsertStudentIntentSlotPrioritiesList.add(3);
        updateInsertStudentIntentSlotPrioritiesList.add(4);
        updateInsertStudentIntentSlotPrioritiesList.add(5);

        // InsertStudent intent should be updated because the slotPriorities method is missing at CreateIntentRequest in the AWS Java SDK.
        // I assume that adding this method was forgotten by the developers.
        // Intent file structure: https://docs.aws.amazon.com/lexv2/latest/dg/import-export-format.html#json-intent
        UpdateIntentResponse updateInsertStudentIntentResponse = updateIntent(
                lexModelsV2Client,
                createBotResponse,
                insertStudentIntentResponse,
                updateInsertStudentIntentSlotResponsesList,
                updateInsertStudentIntentSlotPrioritiesList,
                localeId,
                botVersion,
                "InsertStudent",
                "INSERT student data",
                getInsertStudentIntentSampleUtterances(),
                true,
                false);

        // AnotherActionRejected intent
        CreateIntentResponse anotherActionRejectedIntentResponse = createIntent(lexModelsV2Client,
                createBotResponse,
                localeId,
                botVersion,
                "AnotherActionRejected",
                "End the conversation if the user no longer wants to manage the database",
                getAnotherActionRejectedIntentSampleUtterances(),
                false,
                true
        );

        // Create bot version
        CreateBotVersionResponse createVersion1BotResponse = createBotVersion(
                lexModelsV2Client,
                createBotResponse,
                localeId,
                botVersion
        );

        // Create bot alias
        CreateBotAliasResponse createBotAliasResponse = createBotAlias(
                lexModelsV2Client,
                createBotResponse,
                lambdaArn,
                localeId,
                createVersion1BotResponse,
                botAliasName
        );

        UpdateBotAliasResponse updateBotAliasResponse = updateBotAlias(
                lexModelsV2Client,
                createBotResponse,
                lambdaArn,
                localeId,
                "DRAFT",
                "TestBotAlias"
        );
    }

    private CreateBotResponse createBot(LexModelsV2Client lexModelsV2Client,
                                        String roleArn,
                                        String botName,
                                        String description) {
        DataPrivacy dataPrivacy = DataPrivacy
                .builder()
                .childDirected(false)
                .build();

        CreateBotRequest createBotRequest = CreateBotRequest
                .builder()
                .botName(botName)
                .description(description)
                .roleArn(roleArn)
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

        return createBotResponse;
    }

    private CreateBotLocaleResponse createBotLocale(LexModelsV2Client lexModelsV2Client,
                                                    CreateBotResponse createBotResponse,
                                                    String localeId,
                                                    String botVersion,
                                                    String voiceId) {
        VoiceSettings voiceSettings = VoiceSettings
                .builder()
                .voiceId(voiceId)
                .engine("standard")
                .build();

        CreateBotLocaleRequest createBotLocaleRequest = CreateBotLocaleRequest
                .builder()
                .botId(createBotResponse.botId())
                .botVersion(botVersion)
                .localeId(localeId)
                .voiceSettings(voiceSettings)
                .nluIntentConfidenceThreshold(0.4)
                .build();

        CreateBotLocaleResponse createBotLocaleResponse = lexModelsV2Client.createBotLocale(createBotLocaleRequest);

        DescribeBotLocaleRequest describeBotLocaleRequest = DescribeBotLocaleRequest
                .builder()
                .botId(createBotLocaleResponse.botId())
                .botVersion(botVersion)
                .localeId(localeId)
                .build();

        WaiterResponse<DescribeBotLocaleResponse> waitUntilBotLocaleAvailable = lexModelsV2Client.waiter().waitUntilBotLocaleCreated(describeBotLocaleRequest);
        waitUntilBotLocaleAvailable.matched().response().ifPresent(System.out::println);

        return createBotLocaleResponse;
    }

    private CreateBotVersionResponse createBotVersion(LexModelsV2Client lexModelsV2Client,
                                                      CreateBotResponse createBotResponse,
                                                      String localeId,
                                                      String botVersion) {
        Map<String, BotVersionLocaleDetails> botVersionLocaleDetailsMap = new HashMap<>();

        BotVersionLocaleDetails botVersionLocaleDetails = BotVersionLocaleDetails
                .builder()
                .sourceBotVersion(botVersion)
                .build();

        botVersionLocaleDetailsMap.put(localeId, botVersionLocaleDetails);

        CreateBotVersionRequest createBotVersionRequest = CreateBotVersionRequest
                .builder()
                .botId(createBotResponse.botId())
                .botVersionLocaleSpecification(botVersionLocaleDetailsMap)
                .description("Main version")
                .build();

        CreateBotVersionResponse createBotVersionResponse = lexModelsV2Client.createBotVersion(createBotVersionRequest);

        DescribeBotVersionRequest describeBotVersionRequest = DescribeBotVersionRequest
                .builder()
                .botId(createBotVersionResponse.botId())
                .botVersion(createBotVersionResponse.botVersion())
                .build();

        WaiterResponse<DescribeBotVersionResponse> waitUntilBotVersionAvailable = lexModelsV2Client.waiter().waitUntilBotVersionAvailable(describeBotVersionRequest);
        waitUntilBotVersionAvailable.matched().response().ifPresent(System.out::println);

        return createBotVersionResponse;
    }

    private CreateBotAliasResponse createBotAlias(LexModelsV2Client lexModelsV2Client,
                                                  CreateBotResponse createBotResponse,
                                                  String lambdaArn,
                                                  String localeId,
                                                  CreateBotVersionResponse createBotVersionResponse,
                                                  String botAliasName) {
        Map<String, BotAliasLocaleSettings> botAliasLocaleSettingsMap = new HashMap<>();

        LambdaCodeHook lambdaCodeHook = LambdaCodeHook
                .builder()
                .lambdaARN(lambdaArn)
                .codeHookInterfaceVersion("1.0")
                .build();

        CodeHookSpecification codeHookSpecification = CodeHookSpecification
                .builder()
                .lambdaCodeHook(lambdaCodeHook)
                .build();

        BotAliasLocaleSettings botAliasLocaleSettings = BotAliasLocaleSettings
                .builder()
                .enabled(true)
                .codeHookSpecification(codeHookSpecification)
                .build();

        botAliasLocaleSettingsMap.put(localeId, botAliasLocaleSettings);

        CreateBotAliasRequest createBotAliasRequest = CreateBotAliasRequest
                .builder()
                .botAliasLocaleSettings(botAliasLocaleSettingsMap)
                .botId(createBotResponse.botId())
                .botAliasName(botAliasName)
                .botVersion(createBotVersionResponse.botVersion())
                .description("Lambda function fires on user input (DBM).")
                .build();

        CreateBotAliasResponse createBotAliasResponse = lexModelsV2Client.createBotAlias(createBotAliasRequest);

        DescribeBotAliasRequest describeBotAliasRequest = DescribeBotAliasRequest
                .builder()
                .botId(createBotAliasResponse.botId())
                .botAliasId(createBotAliasResponse.botAliasId())
                .build();

        WaiterResponse<DescribeBotAliasResponse> waitUntilBotAliasAvailable = lexModelsV2Client.waiter().waitUntilBotAliasAvailable(describeBotAliasRequest);
        waitUntilBotAliasAvailable.matched().response().ifPresent(System.out::println);

        return createBotAliasResponse;
    }

    private UpdateBotAliasResponse updateBotAlias(LexModelsV2Client lexModelsV2Client,
                                                  CreateBotResponse createBotResponse,
                                                  String lambdaArn,
                                                  String localeId,
                                                  String botVersion,
                                                  String botAliasName) {
        Map<String, BotAliasLocaleSettings> botAliasLocaleSettingsMap = new HashMap<>();

        LambdaCodeHook lambdaCodeHook = LambdaCodeHook
                .builder()
                .codeHookInterfaceVersion("1.0")
                .lambdaARN(lambdaArn)
                .build();

        CodeHookSpecification codeHookSpecification = CodeHookSpecification
                .builder()
                .lambdaCodeHook(lambdaCodeHook)
                .build();

        BotAliasLocaleSettings botAliasLocaleSettings = BotAliasLocaleSettings
                .builder()
                .enabled(true)
                .codeHookSpecification(codeHookSpecification)
                .build();

        botAliasLocaleSettingsMap.put(localeId, botAliasLocaleSettings);

        UpdateBotAliasRequest updateBotAliasRequest = UpdateBotAliasRequest
                .builder()
                .botAliasLocaleSettings(botAliasLocaleSettingsMap)
                .botId(createBotResponse.botId())
                .botAliasName(botAliasName)
                .botVersion(botVersion)
                .botAliasId("TSTALIASID")
                .description("Lambda function fires on user input (TestBotAlias).")
                .build();

        UpdateBotAliasResponse updateBotAliasResponse = lexModelsV2Client.updateBotAlias(updateBotAliasRequest);

        DescribeBotAliasRequest describeBotAliasRequest = DescribeBotAliasRequest
                .builder()
                .botId(updateBotAliasResponse.botId())
                .botAliasId(updateBotAliasResponse.botAliasId())
                .build();

        WaiterResponse<DescribeBotAliasResponse> waitUntilBotAliasAvailable = lexModelsV2Client.waiter().waitUntilBotAliasAvailable(describeBotAliasRequest);
        waitUntilBotAliasAvailable.matched().response().ifPresent(System.out::println);

        return updateBotAliasResponse;
    }

    private List<SlotPriority> slotPriorities(List<CreateSlotResponse> intentSlotResponsesList, List<Integer> intentSlotPrioritiesList) {
        List<SlotPriority> slotPriorityList = new ArrayList<>();

        for (int i = 0; i < intentSlotResponsesList.size(); i++) {
            SlotPriority slotPriority = SlotPriority
                    .builder()
                    .slotId(intentSlotResponsesList.get(i).slotId())
                    .priority(intentSlotPrioritiesList.get(i))
                    .build();
            slotPriorityList.add(slotPriority);
        }
        return slotPriorityList;
    }

    private CreateIntentResponse createIntent(LexModelsV2Client lexModelsV2Client,
                                              CreateBotResponse createBotResponse,
                                              String localeId,
                                              String botVersion,
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
                    .botVersion(botVersion)
                    .localeId(localeId)
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
                    .botVersion(botVersion)
                    .localeId(localeId)
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
                    .botVersion(botVersion)
                    .localeId(localeId)
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
                    .botVersion(botVersion)
                    .localeId(localeId)
                    .intentName(intentName)
                    .description(intentDescription)
                    .sampleUtterances(createSampleUtterances(sampleUtterances))
                    .build();
        }
        CreateIntentResponse createIntentResponse = lexModelsV2Client.createIntent(createIntentRequest);
        return createIntentResponse;
    }

    private UpdateIntentResponse updateIntent(LexModelsV2Client lexModelsV2Client,
                                              CreateBotResponse createBotResponse,
                                              CreateIntentResponse createIntentResponse,
                                              List<CreateSlotResponse> updateStudentIntentSlotResponsesList,
                                              List<Integer> updateStudentIntentSlotPrioritiesList,
                                              String localeId,
                                              String botVersion,
                                              String intentName,
                                              String intentDescription,
                                              List<String> sampleUtterances,
                                              boolean isFulfillmentCodeHook,
                                              boolean isDialogCodeHook) {
        UpdateIntentRequest updateIntentRequest;

        if (isFulfillmentCodeHook && !isDialogCodeHook) {
            FulfillmentCodeHookSettings fulfillmentCodeHookSettings = FulfillmentCodeHookSettings
                    .builder()
                    .enabled(true)
                    .build();

            updateIntentRequest = UpdateIntentRequest
                    .builder()
                    .botId(createBotResponse.botId())
                    .botVersion(botVersion)
                    .localeId(localeId)
                    .intentId(createIntentResponse.intentId())
                    .intentName(intentName)
                    .description(intentDescription)
                    .sampleUtterances(createSampleUtterances(sampleUtterances))
                    .slotPriorities(slotPriorities(updateStudentIntentSlotResponsesList, updateStudentIntentSlotPrioritiesList))
                    .fulfillmentCodeHook(fulfillmentCodeHookSettings)
                    .build();
        } else if (!isFulfillmentCodeHook && isDialogCodeHook) {
            DialogCodeHookSettings dialogCodeHookSettings = DialogCodeHookSettings
                    .builder()
                    .enabled(true)
                    .build();

            updateIntentRequest = UpdateIntentRequest
                    .builder()
                    .botId(createBotResponse.botId())
                    .botVersion(botVersion)
                    .localeId(localeId)
                    .intentId(createIntentResponse.intentId())
                    .intentName(intentName)
                    .description(intentDescription)
                    .sampleUtterances(createSampleUtterances(sampleUtterances))
                    .slotPriorities(slotPriorities(updateStudentIntentSlotResponsesList, updateStudentIntentSlotPrioritiesList))
                    .dialogCodeHook(dialogCodeHookSettings)
                    .build();
        } else {
            FulfillmentCodeHookSettings fulfillmentCodeHookSettings = FulfillmentCodeHookSettings
                    .builder()
                    .enabled(true)
                    .build();

            DialogCodeHookSettings dialogCodeHookSettings = DialogCodeHookSettings
                    .builder()
                    .enabled(true)
                    .build();

            updateIntentRequest = UpdateIntentRequest
                    .builder()
                    .botId(createBotResponse.botId())
                    .botVersion(botVersion)
                    .localeId(localeId)
                    .intentId(createIntentResponse.intentId())
                    .intentName(intentName)
                    .description(intentDescription)
                    .sampleUtterances(createSampleUtterances(sampleUtterances))
                    .slotPriorities(slotPriorities(updateStudentIntentSlotResponsesList, updateStudentIntentSlotPrioritiesList))
                    .dialogCodeHook(dialogCodeHookSettings)
                    .fulfillmentCodeHook(fulfillmentCodeHookSettings)
                    .dialogCodeHook(dialogCodeHookSettings)
                    .build();
        }
        UpdateIntentResponse updateIntentResponse = lexModelsV2Client.updateIntent(updateIntentRequest);
        return updateIntentResponse;
    }

    private List<SampleUtterance> createSampleUtterances(List<String> sampleUtterances) {
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

    private CreateSlotResponse createSlotWithPlainTextPrompt(LexModelsV2Client lexModelsV2Client,
                                                             CreateBotResponse createBotResponse,
                                                             CreateIntentResponse createIntentResponse,
                                                             String localeId,
                                                             String botVersion,
                                                             String slotName,
                                                             String slotDescription,
                                                             String promptMessage,
                                                             String slotType) {
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

//        SlotCaptureSetting slotCaptureSetting = SlotCaptureSetting
//                .builder()
//                .build();

        SlotValueElicitationSetting slotValueElicitationSetting = SlotValueElicitationSetting
                .builder()
                .promptSpecification(promptSpecification)
                .slotConstraint("Required")
                .build();

        CreateSlotRequest createSlotRequest = CreateSlotRequest
                .builder()
                .botId(createBotResponse.botId())
                .botVersion(botVersion)
                .localeId(localeId)
                .slotName(slotName)
                .description(slotDescription)
                .intentId(createIntentResponse.intentId())
                .slotTypeId(slotType)
                .valueElicitationSetting(slotValueElicitationSetting)
                .build();

        CreateSlotResponse createSlotResponse = lexModelsV2Client.createSlot(createSlotRequest);
        return createSlotResponse;
    }

    private CreateSlotResponse createSlotWithImageResponseCard(LexModelsV2Client lexModelsV2Client,
                                                               CreateBotResponse createBotResponse,
                                                               CreateIntentResponse createIntentResponse,
                                                               String localeId,
                                                               String botVersion,
                                                               String slotName,
                                                               String slotDescription,
                                                               String title,
                                                               List<Button> buttons,
                                                               String slotType) {
        ImageResponseCard imageResponseCard = ImageResponseCard
                .builder()
                .title(title)
                .buttons(buttons)
                .build();

        Message message = Message
                .builder()
                .imageResponseCard(imageResponseCard)
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

        CreateSlotRequest createSlotRequest = CreateSlotRequest
                .builder()
                .botId(createBotResponse.botId())
                .botVersion(botVersion)
                .localeId(localeId)
                .slotName(slotName)
                .description(slotDescription)
                .intentId(createIntentResponse.intentId())
                .slotTypeId(slotType)
                .valueElicitationSetting(slotValueElicitationSetting)
                .build();

        CreateSlotResponse createSlotResponse = lexModelsV2Client.createSlot(createSlotRequest);
        return createSlotResponse;
    }

    private CreateSlotTypeResponse createCustomSlotType(LexModelsV2Client lexModelsV2Client,
                                                        CreateBotResponse createBotResponse,
                                                        String localeId,
                                                        String botVersion,
                                                        String slotTypeName,
                                                        String description,
                                                        List<SlotTypeValue> slotTypeValuesList) {
        SlotValueResolutionStrategy slotValueResolutionStrategy = SlotValueResolutionStrategy.ORIGINAL_VALUE;

        SlotValueSelectionSetting slotValueSelectionSetting = SlotValueSelectionSetting
                .builder()
                .resolutionStrategy(slotValueResolutionStrategy)
                .build();

        CreateSlotTypeRequest createSlotTypeRequest = CreateSlotTypeRequest
                .builder()
                .botId(createBotResponse.botId())
                .botVersion(botVersion)
                .localeId(localeId)
                .slotTypeName(slotTypeName)
                .description(description)
                .slotTypeValues(slotTypeValuesList)
                .valueSelectionSetting(slotValueSelectionSetting)
                .build();

        CreateSlotTypeResponse createSlotTypeResponse = lexModelsV2Client.createSlotType(createSlotTypeRequest);
        return createSlotTypeResponse;
    }

    private List<String> getGreetingIntentSampleUtterances() {
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

    private List<String> getGetStudentIntentSampleUtterances() {
        List<String> sampleUtterances = new ArrayList<>();
        sampleUtterances.add("Get");
        return sampleUtterances;
    }

    private List<String> getRemoveStudentIntentSampleUtterances() {
        List<String> sampleUtterances = new ArrayList<>();
        sampleUtterances.add("Remove");
        return sampleUtterances;
    }

    private List<String> getUpdateStudentIntentSampleUtterances() {
        List<String> sampleUtterances = new ArrayList<>();
        sampleUtterances.add("Update");
        return sampleUtterances;
    }

    private List<String> getInsertStudentIntentSampleUtterances() {
        List<String> sampleUtterances = new ArrayList<>();
        sampleUtterances.add("Insert");
        return sampleUtterances;
    }

    private List<String> getAnotherActionRejectedIntentSampleUtterances() {
        List<String> sampleUtterances = new ArrayList<>();
        sampleUtterances.add("No");
        sampleUtterances.add("No action");
        sampleUtterances.add("Bye");
        sampleUtterances.add("No, thank you");
        return sampleUtterances;
    }

    private List<SlotTypeValue> getClassificationSlotTypeValues() {
        List<String> stringSampleValuesList = new ArrayList<>();
        List<SlotTypeValue> slotTypeValuesList = new ArrayList<>();

        stringSampleValuesList.add("Freshman");
        stringSampleValuesList.add("Sophomore");
        stringSampleValuesList.add("Junior");
        stringSampleValuesList.add("Senior");

        for (String stringSampleValue : stringSampleValuesList) {
            SampleValue sampleValue = SampleValue.builder()
                    .value(stringSampleValue)
                    .build();

            SlotTypeValue slotTypeValue = SlotTypeValue
                    .builder()
                    .sampleValue(sampleValue)
                    .build();

            slotTypeValuesList.add(slotTypeValue);
        }
        return slotTypeValuesList;
    }

    private List<Button> getClassificationImageResponseCardButtons() {
        List<String> buttonNamesList = new ArrayList<>();
        List<Button> buttonList = new ArrayList<>();

        buttonNamesList.add("Freshman");
        buttonNamesList.add("Sophomore");
        buttonNamesList.add("Junior");
        buttonNamesList.add("Senior");

        for (String buttonName: buttonNamesList) {
            Button button = Button
                    .builder()
                    .text(buttonName)
                    .value(buttonName)
                    .build();

            buttonList.add(button);
        }
        return buttonList;
    }
}
