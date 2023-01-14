package services.bot;

import java.util.ArrayList;
import java.util.List;

import software.amazon.awssdk.services.lexmodelsv2.LexModelsV2Client;
import software.amazon.awssdk.services.lexmodelsv2.model.*;

/**
 * A slot is a variable needed to fulfill an intent. or each slot, you define one or more
 * utterances that Amazon Lex uses to elicit a response from the user.
 */
public class Slot {

    /**
     * Creates a slot in an intent with a prompt in a form of image response card.
     * @param lexModelsV2Client LexModelsV2Client lexModelsV2Client.
     * @param createBotResponse The response object contains the metadata of the created bot.
     * @param createIntentResponse The response object contains the metadata of the created intent.
     * @param localeId The identifier of the language and locale that the slot will be used in.
     *                 The string must match one of the supported locales.
     *                 All the bots, intents, slot types used by the slot must have the same locale.
     * @param botVersion The version of the bot associated with the slot.
     * @param slotName The name of the slot. Slot names must be unique within the bot that contains the slot.
     * @param slotDescription A description of the slot. Use this to help identify the slot in lists.
     * @param title The title to display on the response card. The format of the title is determined by the platform displaying the response card.
     * @param buttons A list of buttons that should be displayed on the response card.
     *                The arrangement of the buttons is determined by the platform that displays the button.
     * @param slotType The name specified for the slot type.
     * @return The response object contains the metadata of the created image response card slot.
     */
    public static CreateSlotResponse createSlotWithImageResponseCard(LexModelsV2Client lexModelsV2Client,
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

    /**
     * Creates a slot in an intent with a prompt in a form of plain text message.
     * @param lexModelsV2Client LexModelsV2Client lexModelsV2Client.
     * @param createBotResponse The response object contains the metadata of the created bot.
     * @param createIntentResponse The response object contains the metadata of the created intent.
     * @param localeId The identifier of the language and locale that the slot will be used in.
     *                 The string must match one of the supported locales.
     *                 All the bots, intents, slot types used by the slot must have the same locale.
     * @param botVersion The version of the bot associated with the slot.
     * @param slotName The name of the slot. Slot names must be unique within the bot that contains the slot.
     * @param slotDescription A description of the slot. Use this to help identify the slot in lists.
     * @param promptMessage The message to send to the user.
     * @param slotType The name specified for the slot type.
     * @return The response object contains the metadata of the created plain text message slot.
     */
    public static CreateSlotResponse createSlotWithPlainTextMessage(LexModelsV2Client lexModelsV2Client,
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

    /**
     * Creates a custom slot type. To create a custom slot type, specify a name for the slot type
     * and a set of enumeration values, the values that a slot of this type can assume.
     * @param lexModelsV2Client LexModelsV2Client lexModelsV2Client.
     * @param createBotResponse The response object contains the metadata of the created bot.
     * @param localeId The identifier of the language and locale that the slot will be used in.
     *                 The string must match one of the supported locales.
     *                 All the bots, intents, slot types used by the slot must have the same locale.
     * @param botVersion The identifier of the bot version associated with this slot type.
     * @param slotTypeName The name for the slot. A slot type name must be unique within the intent.
     * @param description A description of the slot type. Use the description to help identify the slot type in lists.
     * @param slotTypeValuesList A list of SlotTypeValue objects that defines the values that the slot type can take.
     *                           Each value can have a list of synonyms, additional values that help train the machine
     *                           learning model about the values that it resolves for a slot.
     * @return The response object contains the metadata of the created slot type.
     */
    public static CreateSlotTypeResponse createSlotType(LexModelsV2Client lexModelsV2Client,
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

    /**
     * Creates a slot priority list where each slot ordered by priority number.
     * @param intentSlotResponsesList List of responses containing metadata of already created slots.
     * @param intentSlotPrioritiesList List of slot priorities.
     * @return List of slot priority objects.
     */
    public static List<SlotPriority> slotPriorities(List<CreateSlotResponse> intentSlotResponsesList, List<Integer> intentSlotPrioritiesList) {
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

    /**
     * Each slot type can have a set of values. Each SlotTypeValue represents a value that the slot type can take.
     * Method wraps each String value into a SlotTypeValue object and returns a list of SlotTypeValue objects.
     * @param stringSampleValuesList List of String slot type values.
     * @return List of SlotTypeValue objects.
     */
    public static List<SlotTypeValue> getSlotTypeValues(List<String> stringSampleValuesList) {
        List<SlotTypeValue> slotTypeValuesList = new ArrayList<>();

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
}