package services.lexBotConfiguration;

import java.util.HashMap;
import java.util.Map;

import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.lexmodelsv2.LexModelsV2Client;
import software.amazon.awssdk.services.lexmodelsv2.model.*;

/**
 * Amazon Lex supports publishing versions of bots, intents, and slot types so that you can
 * control the implementation that your client applications use. A version is a numbered
 * snapshot of your work that you can publish for use in different parts of your workflow,
 * such as development, beta deployment, and production.
 */
public class BotVersion {

    /**
     * Creates a new version of the bot based on the DRAFT version. If the DRAFT version of this
     * resource hasn't changed since you created the last version, Amazon Lex doesn't create
     * a new version, it returns the last created version.
     * @param lexModelsV2Client LexModelsV2Client lexModelsV2Client.
     * @param createBotResponse The response object contains the metadata of the created bot.
     * @param localeId The identifier of the language and locale that the bot will be used in.
     *                 The string must match one of the supported locales.
     *                 All the intents, slot types, and slots used in the bot must have the same locale.
     * @param botVersion The version number assigned to the version.
     * @return The response object contains the metadata of the created bot version.
     */
    public static CreateBotVersionResponse createBotVersion(LexModelsV2Client lexModelsV2Client,
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
                .description("Release version")
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
}
