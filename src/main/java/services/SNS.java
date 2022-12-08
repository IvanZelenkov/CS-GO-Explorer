package services;

import handler.BotHandler;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;

import java.util.List;
import java.util.UUID;

public class SNS {

    public SnsClient authenticateSNS() {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials
                .create(System.getenv("ACCESS_KEY_ID"),
                        System.getenv("SECRET_ACCESS_KEY"));

        return SnsClient
                .builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .region(Region.of(System.getenv("AWS_REGION")))
                .build();
    }

    public static String createSNSTopic(SnsClient snsClient) {
        CreateTopicResponse result;
        try {
            CreateTopicRequest request = CreateTopicRequest.builder()
                    .name(System.getenv("SNS_TOPIC_NAME"))
                    .build();

            result = snsClient.createTopic(request);
            return result.topicArn();
        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    public static void emailSubscriber(SnsClient snsClient, String topicArn, String email) {
        try {
            SubscribeRequest request = SubscribeRequest.builder()
                    .protocol("email")
                    .endpoint(email)
                    .returnSubscriptionArn(true)
                    .topicArn(topicArn)
                    .build();

            SubscribeResponse result = snsClient.subscribe(request);
            System.out.println("Subscription ARN is " + result.subscriptionArn() + "\n\n Status is " + result.sdkHttpResponse().statusCode());
        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }


    public List<Subscription> listSNSSubscriptions(SnsClient snsClient) {
        try {
            ListSubscriptionsRequest request = ListSubscriptionsRequest.builder().build();
            ListSubscriptionsResponse result = snsClient.listSubscriptions(request);
            return result.subscriptions();
        } catch (SnsException error) {
            System.err.println(error.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
    }

    public void publishMessage(SnsClient snsClient, List<String> messages, String mainMessage, String type) {
        try {
            String topicArn = createSNSTopic(snsClient);
            emailSubscriber(snsClient, topicArn, "itproger.ivan@gmail.com");
            PublishRequest request = PublishRequest.builder()
                    .message(mainMessage)
                    .topicArn(topicArn)
                    .build();

            snsClient.publish(request);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Confirmation sent to ");
            for (Subscription subscription : listSNSSubscriptions(snsClient))
                stringBuilder.append(subscription.endpoint()).append(" ");

            messages.add(String.valueOf(stringBuilder));
            messages.add("Do you want to perform another operation on the \"Students\" table?");
        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        BotHandler.multipleMessages(messages, "PlainText");

        S3 s3 = new S3();
        S3Client s3Client = s3.authenticateS3();
        String createBucketResponse = s3.createBucket(s3Client);
        System.out.print("BUCKET: " + createBucketResponse);
        PutObjectRequest objectRequest = PutObjectRequest
                .builder()
                .bucket(System.getenv("S3_BUCKET_NAME"))
                .key(type + "-" + UUID.randomUUID() + ".txt")
                .build();
        s3Client.putObject(objectRequest, RequestBody.fromString(mainMessage));
    }
}