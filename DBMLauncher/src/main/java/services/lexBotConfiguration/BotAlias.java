package services.lexBotConfiguration;

import java.util.HashMap;
import java.util.Map;

import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.lexmodelsv2.LexModelsV2Client;
import software.amazon.awssdk.services.lexmodelsv2.model.*;

/**
 * Allows you to create and update an alias.
 */
public class BotAlias {

    /**
     * Creates an alias for the specified version of a bot. Use an alias to enable you to change
     * the version of a bot without updating applications that use the bot.
     * @param lexModelsV2Client LexModelsV2Client lexModelsV2Client.
     * @param createBotResponse The response object contains the metadata of the created bot.
     * @param lambdaArn The Amazon Resource Name (ARN) of the Lambda function.
     * @param localeId The identifier of the language and locale that the bot will be used in.
     *                 The string must match one of the supported locales.
     *                 All the intents, slot types, and slots used in the bot must have the same locale.
     * @param createBotVersionResponse The response object contains the metadata of the created bot version.
     * @param botAliasName The alias to create. The name must be unique for the bot.
     * @return The response object contains the metadata of the created bot alias.
     */
    public static CreateBotAliasResponse createBotAlias(LexModelsV2Client lexModelsV2Client,
                                                        CreateBotResponse createBotResponse,
                                                        String lambdaArn,
                                                        String localeId,
                                                        CreateBotVersionResponse createBotVersionResponse,
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
                .codeHookSpecification(codeHookSpecification)
                .enabled(true)
                .build();

        botAliasLocaleSettingsMap.put(localeId, botAliasLocaleSettings);

        CreateBotAliasRequest createBotAliasRequest = CreateBotAliasRequest
                .builder()
                .botAliasLocaleSettings(botAliasLocaleSettingsMap)
                .botId(createBotResponse.botId())
                .botAliasName(botAliasName)
                .botVersion(createBotVersionResponse.botVersion())
                .description("database bot manager alias")
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

    /**
     * Updates the configuration of an existing bot alias.
     * @param lexModelsV2Client LexModelsV2Client lexModelsV2Client.
     * @param createBotResponse The response object contains the metadata of the created bot.
     * @param lambdaArn The Amazon Resource Name (ARN) of the Lambda function.
     * @param localeId The identifier of the language and locale that the bot will be used in.
     *                 The string must match one of the supported locales.
     *                 All the intents, slot types, and slots used in the bot must have the same locale.
     * @param botVersion The new bot version to assign to the bot alias.
     * @param botAliasId The unique identifier of the bot alias.
     * @param botAliasName The new name to assign to the bot alias.
     * @return The response object contains the metadata of the updated bot alias.
     */
    public static UpdateBotAliasResponse updateBotAlias(LexModelsV2Client lexModelsV2Client,
                                                        CreateBotResponse createBotResponse,
                                                        String lambdaArn,
                                                        String localeId,
                                                        String botVersion,
                                                        String botAliasId,
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
                .codeHookSpecification(codeHookSpecification)
                .enabled(true)
                .build();

        botAliasLocaleSettingsMap.put(localeId, botAliasLocaleSettings);

        UpdateBotAliasRequest updateBotAliasRequest = UpdateBotAliasRequest
                .builder()
                .botAliasLocaleSettings(botAliasLocaleSettingsMap)
                .botId(createBotResponse.botId())
                .botAliasId(botAliasId)
                .botAliasName(botAliasName)
                .botVersion(botVersion)
                .description("test bot alias")
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
}