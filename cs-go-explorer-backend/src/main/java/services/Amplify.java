package services;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.amplify.AmplifyClient;
import software.amazon.awssdk.services.amplify.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * The Amplify Framework is a comprehensive set of SDKs, libraries, tools, and documentation for client app development.
 * Amplify provides a continuous delivery and hosting service for web applications.
 */
public class Amplify {

    /**
     * Authenticate to the Amplify client using the AWS user's credentials.
     * @param awsBasicCredentials The AWS Access Key ID and Secret Access Key are credentials that are used to securely sign requests to AWS services.
     * @param appRegion The AWS Region where the service will be hosted.
     * @return Service client for accessing AWS Amplify.
     */
    public static AmplifyClient authenticateAmplify(AwsBasicCredentials awsBasicCredentials, Region appRegion) {
        return AmplifyClient
                .builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .region(appRegion)
                .build();
    }

    /**
     * Creates a new Amplify app.
     * @param amplifyClient Service client for accessing Amplify.
     * @param appName The name for an Amplify app.
     * @param appDescription The description for the Amplify app.
     * @param platform The platform for the Amplify app. For a static app, set the platform type to WEB.
     *                 For a dynamic server-side rendered (SSR) app, set the platform type to WEB_COMPUTE.
     *                 For an app requiring Amplify Hosting's original SSR support only, set the platform
     *                 type to WEB_DYNAMIC.
     * @param roleArn The AWS Identity and Access Management (IAM) service role for an Amplify app.
     * @param cloneUrlHttp The URL to use for cloning the repository over HTTPS.
     * @param enableAutoBranchCreation Enables automated branch creation for the Amplify app.
     * @param enableBranchAutoBuild Enables the auto-building of branches for the Amplify app.
     * @param enableAutoBuild Enables auto building for the auto-created branch.
     * @param enablePerformanceMode Enables performance mode for the branch.
     * @param stage Describes the current stage for the auto-created branch. Valid Values:
     *              PRODUCTION | BETA | DEVELOPMENT | EXPERIMENTAL | PULL_REQUEST
     * @param environmentalVariables The environment variables for the auto-created branch.
     * @return The unique ID of the Amplify app.
     */
    public static String createApp(AmplifyClient amplifyClient,
                                   String appName,
                                   String appDescription,
                                   Platform platform,
                                   String roleArn,
                                   String cloneUrlHttp,
                                   boolean enableAutoBranchCreation,
                                   boolean enableBranchAutoBuild,
                                   Stage stage,
                                   boolean enableAutoBuild,
                                   boolean enablePerformanceMode,
                                   Map<String, String> environmentalVariables) {
        try {
            AutoBranchCreationConfig autoBranchCreationConfig = AutoBranchCreationConfig
                    .builder()
                    .stage(stage)
                    .enableAutoBuild(enableAutoBuild)
//                    .enablePerformanceMode(enablePerformanceMode)
                    .build();

            String buildSpec = readYmlFile("amplify-build-spec", "amplify-build-spec.yml");

            CustomRule customRule = CustomRule
                    .builder()
                    .status("200")
                    .source("</^[^.]+$|\\.(?!(css|gif|ico|jpg|jpeg|webp|js|png|txt|svg|woff|woff2|ttf|map|json)$)([^.]+$)/>")
                    .target("/")
                    .build();

            CreateAppRequest createAppRequest = CreateAppRequest
                    .builder()
                    .name(appName)
                    .description(appDescription)
                    .platform(platform)
                    .iamServiceRoleArn(roleArn)
                    .repository(cloneUrlHttp)
                    .enableAutoBranchCreation(enableAutoBranchCreation)
                    .enableBranchAutoBuild(enableBranchAutoBuild)
                    .autoBranchCreationConfig(autoBranchCreationConfig)
                    .environmentVariables(environmentalVariables)
                    .buildSpec(buildSpec)
                    .customRules(customRule)
                    .build();

            CreateAppResponse createAppResponse = amplifyClient.createApp(createAppRequest);
            return createAppResponse.app().appId();
        } catch (AmplifyException | IOException error) {
            System.err.println(error.getMessage());
            System.exit(1);
        }
        return "";
    }

    /**
     * Returns an existing Amplify app by appID.
     * @param amplifyClient Service client for accessing Amplify.
     * @param appId The unique ID for an Amplify app.
     * @return The default domain for the Amplify app.
     */
    public static String getApp(AmplifyClient amplifyClient, String appId) {
        try {
            GetAppRequest getAppRequest = GetAppRequest
                    .builder()
                    .appId(appId)
                    .build();

            GetAppResponse getAppResponse = amplifyClient.getApp(getAppRequest);
            return getAppResponse.app().defaultDomain();
        } catch (AmplifyException error) {
            System.err.println(error.getMessage());
            System.exit(1);
        }
        return "";
    }

    /**
     * Reads YML file content then converts it to the InputStream and returns as a string.
     * @param filename The name of the YML file to read.
     * @return Returns the YML contents converted to a string.
     * @throws IOException Signals that an I/O exception has occurred. This class is the general class of exceptions produced by failed or interrupted I/O operations.
     */
    private static String readYmlFile(String folder, String filename) throws IOException {
        InputStream inputStream = IAM.class.getResourceAsStream("/" + folder + "/" + filename);
        assert inputStream != null;
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }
}