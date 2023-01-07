package services;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.amplify.AmplifyClient;
import software.amazon.awssdk.services.amplify.model.*;

public class Amplify {

    public static AmplifyClient authenticateAmplify(AwsBasicCredentials awsCredentials, Region appRegion) {
        return AmplifyClient
                .builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .region(appRegion)
                .build();
    }

    public static String createApp(AmplifyClient amplifyClient,
                                   String appName,
                                   String appDescription,
                                   Platform platform,
                                   String cloneUrlHttp) {
        AutoBranchCreationConfig autoBranchCreationConfig = AutoBranchCreationConfig
                .builder()
                .enableAutoBuild(true)
                .stage(Stage.PRODUCTION)
                .build();

        CreateAppRequest createAppRequest = CreateAppRequest
                .builder()
                .name(appName)
                .description(appDescription)
                .platform(platform)
                .iamServiceRoleArn("arn:aws:iam::981684844178:role/DatabaseBotManagerRole")
                .repository(cloneUrlHttp)
                .enableAutoBranchCreation(true)
                .enableBranchAutoBuild(true)
                .autoBranchCreationConfig(autoBranchCreationConfig)
                .build();

        CreateAppResponse createAppResponse = amplifyClient.createApp(createAppRequest);
        return createAppResponse.app().appId();
    }
}