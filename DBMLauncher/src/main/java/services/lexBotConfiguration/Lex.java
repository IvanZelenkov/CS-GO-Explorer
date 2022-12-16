package services.lexBotConfiguration;

import java.util.ArrayList;
import java.util.List;

import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.lexmodelsv2.LexModelsV2Client;
import software.amazon.awssdk.services.lexmodelsv2.model.*;

/**
 * Amazon Lex is a fully managed artificial intelligence (AI) service with advanced natural
 * language models to design, build, test, and deploy conversational interfaces in applications.
 */
public class Lex {

    /**
     * Configure the Lex bot creating a DRAFT version for testing and the release version that is used in the application.
     * @param lexModelsV2Client Service client for accessing Lex Models V2.
     * @param lexRoleArn Service-linked role (ARN) for the Lex bot.
     * @param lambdaArn Lambda ARN defines a function hook that will be called during the conversation.
     */
    public static String botConfiguration(LexModelsV2Client lexModelsV2Client, String lexRoleArn, String lambdaArn) {
        final String botName = "DBM";
        final String botDescription = "Database Bot Manager";
        final String botVersion = "DRAFT";
        final String botAliasName = "DatabaseBotManager";
        final String localeId = "en_US";
        final String voiceId = "Salli";
        final String testBotAliasId = "TSTALIASID";
        final String testBotAliasName = "TestBotAlias";

        // Create bot
        CreateBotResponse createBotResponse = createBot(
                lexModelsV2Client,
                lexRoleArn,
                botName,
                botDescription
        );

        // Create bot locale
        CreateBotLocaleResponse createBotLocaleResponse = BotLocale.createBotLocale(
                lexModelsV2Client,
                createBotResponse,
                localeId,
                botVersion,
                voiceId
        );

        // Create "Greeting" intent
        CreateIntentResponse greetingIntentResponse = Intent.createIntent(
                lexModelsV2Client,
                createBotResponse,
                localeId,
                botVersion,
                "Greeting",
                "Bot greeting",
                Utterance.getGreetingIntentSampleUtterances(),
                true,
                false
        );

        // Create "GetStudent" intent
        CreateIntentResponse getStudentIntentResponse = Intent.createIntent(
                lexModelsV2Client,
                createBotResponse,
                localeId,
                botVersion,
                "GetStudent",
                "GET student data",
                Utterance.getGetStudentIntentSampleUtterances(),
                true,
                false
        );

        // Create "StudentID" slot of "GetStudent" intent
        CreateSlotResponse getStudentIntentStudentIdSlotResponse = Slot.createSlotWithPlainTextMessage(lexModelsV2Client,
                createBotResponse,
                getStudentIntentResponse,
                localeId,
                botVersion,
                "StudentID",
                "StudentID slot is required to search for and retrieve student data.",
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
        UpdateIntentResponse updateGetStudentIntentResponse = Intent.updateIntent(
                lexModelsV2Client,
                createBotResponse,
                getStudentIntentResponse,
                updateGetStudentIntentSlotResponsesList,
                updateGetStudentIntentSlotPrioritiesList,
                localeId,
                botVersion,
                "GetStudent",
                "GET student data",
                Utterance.getGetStudentIntentSampleUtterances(),
                true,
                false
        );

        // Create "RemoveStudent" intent
        CreateIntentResponse removeStudentIntentResponse = Intent.createIntent(
                lexModelsV2Client,
                createBotResponse,
                localeId,
                botVersion,
                "RemoveStudent",
                "REMOVE student data",
                Utterance.getRemoveStudentIntentSampleUtterances(),
                true,
                false
        );

        // Create "StudentID" slot of "RemoveStudent" intent
        CreateSlotResponse removeStudentIntentStudentIdSlotResponse = Slot.createSlotWithPlainTextMessage(lexModelsV2Client,
                createBotResponse,
                removeStudentIntentResponse,
                localeId,
                botVersion,
                "StudentID",
                "StudentID slot is required to search and remove a student.",
                "Please enter a student ID.",
                "AMAZON.Number"
        );

        // Prioritize the slots of "RemoveStudent" intent in the order they created
        List<CreateSlotResponse> updateRemoveStudentIntentSlotResponsesList = new ArrayList<>();
        updateRemoveStudentIntentSlotResponsesList.add(removeStudentIntentStudentIdSlotResponse);
        List<Integer> updateRemoveStudentIntentSlotPrioritiesList = new ArrayList<>();
        updateRemoveStudentIntentSlotPrioritiesList.add(0);

        // RemoveStudent intent should be updated because the slotPriorities method is missing at CreateIntentRequest in the AWS Java SDK.
        // I assume that adding this method was forgotten by the developers.
        // Intent file structure: https://docs.aws.amazon.com/lexv2/latest/dg/import-export-format.html#json-intent
        UpdateIntentResponse updateRemoveStudentIntentResponse = Intent.updateIntent(
                lexModelsV2Client,
                createBotResponse,
                removeStudentIntentResponse,
                updateRemoveStudentIntentSlotResponsesList,
                updateRemoveStudentIntentSlotPrioritiesList,
                localeId,
                botVersion,
                "RemoveStudent",
                "REMOVE student data",
                Utterance.getRemoveStudentIntentSampleUtterances(),
                true,
                false
        );

        // Create "UpdateStudent" intent
        CreateIntentResponse updateStudentIntentResponse = Intent.createIntent(
                lexModelsV2Client,
                createBotResponse,
                localeId,
                botVersion,
                "UpdateStudent",
                "UPDATE student data",
                Utterance.getUpdateStudentIntentSampleUtterances(),
                true,
                false
        );

        // Create "StudentID" slot of "UpdateStudent" intent
        CreateSlotResponse updateStudentIntentStudentIdSlotResponse = Slot.createSlotWithPlainTextMessage(
                lexModelsV2Client,
                createBotResponse,
                updateStudentIntentResponse,
                localeId,
                botVersion,
                "StudentID",
                "StudentID slot is required to search for a student.",
                "Please enter a student ID to find a person.",
                "AMAZON.Number"
        );

        // Create "AttributeName" slot of "UpdateStudent" intent
        CreateSlotResponse updateStudentIntentAttributeNameSlotResponse = Slot.createSlotWithPlainTextMessage(
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
        CreateSlotResponse updateStudentIntentNewAttributeValueSlotResponse = Slot.createSlotWithPlainTextMessage(
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
        UpdateIntentResponse updateUpdateStudentIntentResponse = Intent.updateIntent(
                lexModelsV2Client,
                createBotResponse,
                updateStudentIntentResponse,
                updateUpdateStudentIntentSlotResponsesList,
                updateUpdateStudentIntentSlotPrioritiesList,
                localeId,
                botVersion,
                "UpdateStudent",
                "UPDATE student data",
                Utterance.getUpdateStudentIntentSampleUtterances(),
                true,
                false);

        List<String> stringSampleValuesClassificationList = new ArrayList<>();
        stringSampleValuesClassificationList.add("Freshman");
        stringSampleValuesClassificationList.add("Sophomore");
        stringSampleValuesClassificationList.add("Junior");
        stringSampleValuesClassificationList.add("Senior");

        // Create "Classification" custom slot type
        CreateSlotTypeResponse createClassificationSlotTypeResponse = Slot.createSlotType(
                lexModelsV2Client,
                createBotResponse,
                localeId,
                botVersion,
                "Classification",
                "Classification level of the student at the university",
                Slot.getSlotTypeValues(stringSampleValuesClassificationList)
        );

        // Create "InsertStudent" intent
        CreateIntentResponse insertStudentIntentResponse = Intent.createIntent(
                lexModelsV2Client,
                createBotResponse,
                localeId,
                botVersion,
                "InsertStudent",
                "Insert student data",
                Utterance.getInsertStudentIntentSampleUtterances(),
                true,
                false
        );

        // Create "StudentID" slot of "InsertStudent" intent
        CreateSlotResponse insertStudentIntentStudentIdSlotResponse = Slot.createSlotWithPlainTextMessage(
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

        // Create "FirstName" slot of "InsertStudent" intent
        CreateSlotResponse insertStudentIntentFirstNameSlotResponse = Slot.createSlotWithPlainTextMessage(
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

        // Create "LastName" slot of "InsertStudent" intent
        CreateSlotResponse insertStudentIntentLastNameSlotResponse = Slot.createSlotWithPlainTextMessage(
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

        // Create "DateOfBirth" slot of "InsertStudent" intent
        CreateSlotResponse insertStudentIntentDateOfBirthSlotResponse = Slot.createSlotWithPlainTextMessage(
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

        // List of button names for the Classification slot
        List<String> buttonNamesList = new ArrayList<>();
        buttonNamesList.add("Freshman");
        buttonNamesList.add("Sophomore");
        buttonNamesList.add("Junior");
        buttonNamesList.add("Senior");

        // Create "Classification" slot of "InsertStudent" intent
        CreateSlotResponse insertStudentIntentClassificationSlotResponse = Slot.createSlotWithImageResponseCard(
                lexModelsV2Client,
                createBotResponse,
                insertStudentIntentResponse,
                localeId,
                botVersion,
                createClassificationSlotTypeResponse.slotTypeName(),
                "Classification is a required attribute that needs to be inserted into the Students table.",
                "Student Classification",
                Buttons.getButtons(buttonNamesList),
                createClassificationSlotTypeResponse.slotTypeId()
        );

        // Create "Email" slot of "InsertStudent" intent
        CreateSlotResponse insertStudentIntentEmailSlotResponse = Slot.createSlotWithPlainTextMessage(
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
        UpdateIntentResponse updateInsertStudentIntentResponse = Intent.updateIntent(
                lexModelsV2Client,
                createBotResponse,
                insertStudentIntentResponse,
                updateInsertStudentIntentSlotResponsesList,
                updateInsertStudentIntentSlotPrioritiesList,
                localeId,
                botVersion,
                "InsertStudent",
                "INSERT student data",
                Utterance.getInsertStudentIntentSampleUtterances(),
                true,
                false
        );

        // EndOfConversation intent
        CreateIntentResponse endOfConversationIntentResponse = Intent.createIntent(
                lexModelsV2Client,
                createBotResponse,
                localeId,
                botVersion,
                "EndOfConversation",
                "End the conversation if the user no longer wants to manage the database",
                Utterance.getEndOfConversationIntentSampleUtterances(),
                false,
                true
        );

        // Update TestBotAlias by enabling the lambda hook
        UpdateBotAliasResponse updateBotAliasResponse = BotAlias.updateBotAlias(
                lexModelsV2Client,
                createBotResponse,
                lambdaArn,
                localeId,
                botVersion,
                testBotAliasId,
                testBotAliasName
        );

        // Build bot
        BuildBotLocaleResponse buildBotLocaleResponse = BotLocale.buildBotLocale(
                lexModelsV2Client,
                createBotResponse,
                localeId,
                botVersion
        );

        // Create bot version
        CreateBotVersionResponse createVersion1BotResponse = BotVersion.createBotVersion(
                lexModelsV2Client,
                createBotResponse,
                localeId,
                botVersion
        );

        // Create bot alias
        CreateBotAliasResponse createBotAliasResponse = BotAlias.createBotAlias(
                lexModelsV2Client,
                createBotResponse,
                lambdaArn,
                localeId,
                createVersion1BotResponse,
                botAliasName
        );

        return createBotResponse.botId();
    }

    /**
     * Creates an Amazon Lex conversational bot.
     * @param lexModelsV2Client LexModelsV2Client lexModelsV2Client.
     * @param roleArn The Amazon Resource Name (ARN) of an IAM role that has permission to access the bot.
     * @param botName The name of the bot. The bot name must be unique in the account that creates the bot.
     * @param description A description of the bot. It appears in lists to help you identify a particular bot.
     * @return If the action is successful, returns a response object with metadata.
     */
    private static CreateBotResponse createBot(LexModelsV2Client lexModelsV2Client,
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
}