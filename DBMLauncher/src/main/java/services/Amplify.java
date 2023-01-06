package services;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.amplify.AmplifyClient;
import software.amazon.awssdk.services.amplify.model.CreateAppRequest;
import software.amazon.awssdk.services.amplify.model.CreateAppResponse;
import software.amazon.awssdk.services.amplify.model.Platform;

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
                                 Platform platform) {

        CreateAppRequest createAppRequest = CreateAppRequest
                .builder()
                .name(appName)
                .description(appDescription)
                .platform(platform)
                .build();

        CreateAppResponse createAppResponse = amplifyClient.createApp(createAppRequest);
        return createAppResponse.app().appId();
    }
}
