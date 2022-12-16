package services.lexBotConfiguration;

import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.lexmodelsv2.LexModelsV2Client;
import software.amazon.awssdk.services.lexmodelsv2.model.*;

/**
 * The locale contains the intents and slot types that the bot uses in
 * conversations with users in the specified language and locale.
 * You can create and build a locale.
 */
public class BotLocale {

    /**
     * Creates a locale for the bot.
     * @param lexModelsV2Client LexModelsV2Client lexModelsV2Client.
     * @param createBotResponse The response object contains the metadata of the created bot.
     * @param localeId The identifier of the language and locale that the bot will be used in.
     *                 The string must match one of the supported locales.
     *                 All the intents, slot types, and slots used in the bot must have the same locale.
     * @param botVersion The specified bot version.
     * @param voiceId The identifier of the Amazon Polly voice to use.
     * @return The response object contains the metadata of the created bot locale.
     */
    public static CreateBotLocaleResponse createBotLocale(LexModelsV2Client lexModelsV2Client,
                                                          CreateBotResponse createBotResponse,
                                                          String localeId,
                                                          String botVersion,
                                                          String voiceId) {
        VoiceSettings voiceSettings = VoiceSettings
                .builder()
                .engine("standard")
                .voiceId(voiceId)
                .build();

        CreateBotLocaleRequest createBotLocaleRequest = CreateBotLocaleRequest
                .builder()
                .botId(createBotResponse.botId())
                .botVersion(botVersion)
                .localeId(localeId)
                .voiceSettings(voiceSettings)
                .nluIntentConfidenceThreshold(0.5)
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

    /**
     * Builds a bot, its intents, and its slot types into a specific locale.
     * A bot can be built into multiple locales.
     * At runtime the locale is used to choose a specific build of the bot.
     * @param lexModelsV2Client LexModelsV2Client lexModelsV2Client.
     * @param createBotResponse The response object contains the metadata of the created bot.
     * @param localeId The identifier of the language and locale that the bot will be used in.
     *                 The string must match one of the supported locales.
     *                 All the intents, slot types, and slots used in the bot must have the same locale.
     * @param botVersion The version of the bot that was built. This is only the DRAFT version of the bot.
     * @return The response object contains the metadata of the build bot locale.
     */
    public static BuildBotLocaleResponse buildBotLocale(LexModelsV2Client lexModelsV2Client,
                                                        CreateBotResponse createBotResponse,
                                                        String localeId,
                                                        String botVersion) {
        BuildBotLocaleRequest buildBotLocaleRequest = BuildBotLocaleRequest
                .builder()
                .botId(createBotResponse.botId())
                .botVersion(botVersion)
                .localeId(localeId)
                .build();

        BuildBotLocaleResponse buildBotLocaleResponse = lexModelsV2Client.buildBotLocale(buildBotLocaleRequest);

        DescribeBotLocaleRequest describeBotLocaleRequest = DescribeBotLocaleRequest
                .builder()
                .botId(buildBotLocaleResponse.botId())
                .botVersion(buildBotLocaleResponse.botVersion())
                .localeId(buildBotLocaleResponse.localeId())
                .build();

        WaiterResponse<DescribeBotLocaleResponse> waitUntilBotLocaleBuilt = lexModelsV2Client.waiter().waitUntilBotLocaleBuilt(describeBotLocaleRequest);
        waitUntilBotLocaleBuilt.matched().response().ifPresent(System.out::println);

        return buildBotLocaleResponse;
    }
}