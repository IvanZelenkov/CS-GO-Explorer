package services.lexBotConfiguration;

import java.util.List;

import software.amazon.awssdk.services.lexmodelsv2.LexModelsV2Client;
import software.amazon.awssdk.services.lexmodelsv2.model.*;

/**
 * To define the interaction between the user and the bot, you define one or more intents.
 * You can create and update an intent.
 */
public class Intent {

    /**
     * To define the interaction between the user and the bot, you define one or more intents.
     * @param lexModelsV2Client LexModelsV2Client lexModelsV2Client.
     * @param createBotResponse The response object contains the metadata of the created bot.
     * @param localeId The locale that the intent is specified to use.
     * @param botVersion The version of the bot associated with the intent.
     * @param intentName The name specified for the intent.
     * @param intentDescription The description of the intent.
     * @param sampleUtterances The sample utterances specified for the intent.
     * @param isFulfillmentCodeHook If fulfillmentCodeHook is true, then set the fulfillment code hook for the specified intent. Otherwise, ignore it.
     * @param isDialogCodeHook If isDialogCodeHook is true, then set the dialog code hook for the specified intent. Otherwise, ignore it.
     * @return The response object contains the metadata of the created intent.
     */
    public static CreateIntentResponse createIntent(LexModelsV2Client lexModelsV2Client,
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
                    .active(true)
                    .enabled(true)
                    .build();

            createIntentRequest = CreateIntentRequest
                    .builder()
                    .botId(createBotResponse.botId())
                    .botVersion(botVersion)
                    .localeId(localeId)
                    .intentName(intentName)
                    .description(intentDescription)
                    .sampleUtterances(Utterance.createSampleUtterances(sampleUtterances))
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
                    .sampleUtterances(Utterance.createSampleUtterances(sampleUtterances))
                    .dialogCodeHook(dialogCodeHookSettings)
                    .build();
        } else if (isFulfillmentCodeHook && isDialogCodeHook) {
            FulfillmentCodeHookSettings fulfillmentCodeHookSettings = FulfillmentCodeHookSettings
                    .builder()
                    .active(true)
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
                    .sampleUtterances(Utterance.createSampleUtterances(sampleUtterances))
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
                    .sampleUtterances(Utterance.createSampleUtterances(sampleUtterances))
                    .build();
        }
        return lexModelsV2Client.createIntent(createIntentRequest);
    }

    /**
     * Updates the settings for an intent.
     * @param lexModelsV2Client LexModelsV2Client lexModelsV2Client.
     * @param createBotResponse The response object contains the metadata of the created bot.
     * @param createIntentResponse The response object contains the metadata of already created intent.
     * @param intentSlotResponsesList List of responses containing metadata of already created slots.
     * @param intentSlotPrioritiesList List of slot priorities.
     * @param localeId The identifier of the language and locale where this intent is used.
     *                 The string must match one of the supported locales.
     * @param botVersion The version of the bot that contains the intent. Must be DRAFT.
     * @param intentName The name specified for the intent.
     * @param intentDescription The description of the intent.
     * @param sampleUtterances The sample utterances specified for the intent.
     * @param isFulfillmentCodeHook If fulfillmentCodeHook is true, then set the fulfillment code hook for the specified intent. Otherwise, ignore it.
     * @param isDialogCodeHook If isDialogCodeHook is true, then set the dialog code hook for the specified intent. Otherwise, ignore it.
     * @return The response object contains the metadata of the updated intent.
     */
    public static UpdateIntentResponse updateIntent(LexModelsV2Client lexModelsV2Client,
                                                    CreateBotResponse createBotResponse,
                                                    CreateIntentResponse createIntentResponse,
                                                    List<CreateSlotResponse> intentSlotResponsesList,
                                                    List<Integer> intentSlotPrioritiesList,
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
                    .active(true)
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
                    .sampleUtterances(Utterance.createSampleUtterances(sampleUtterances))
                    .slotPriorities(Slot.slotPriorities(intentSlotResponsesList, intentSlotPrioritiesList))
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
                    .sampleUtterances(Utterance.createSampleUtterances(sampleUtterances))
                    .slotPriorities(Slot.slotPriorities(intentSlotResponsesList, intentSlotPrioritiesList))
                    .dialogCodeHook(dialogCodeHookSettings)
                    .build();
        } else {
            FulfillmentCodeHookSettings fulfillmentCodeHookSettings = FulfillmentCodeHookSettings
                    .builder()
                    .active(true)
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
                    .sampleUtterances(Utterance.createSampleUtterances(sampleUtterances))
                    .slotPriorities(Slot.slotPriorities(intentSlotResponsesList, intentSlotPrioritiesList))
                    .fulfillmentCodeHook(fulfillmentCodeHookSettings)
                    .dialogCodeHook(dialogCodeHookSettings)
                    .build();
        }
        return lexModelsV2Client.updateIntent(updateIntentRequest);
    }
}