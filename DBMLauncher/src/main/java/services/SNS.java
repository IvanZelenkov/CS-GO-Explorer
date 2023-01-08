package services;

import handler.BotLogic;

import java.util.ArrayList;
import java.util.List;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.CreateTopicResponse;
import software.amazon.awssdk.services.sns.model.SnsException;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;
import software.amazon.awssdk.services.sns.model.SubscribeResponse;
import software.amazon.awssdk.services.sns.model.ListSubscriptionsRequest;
import software.amazon.awssdk.services.sns.model.ListSubscriptionsResponse;
import software.amazon.awssdk.services.sns.model.Subscription;
import software.amazon.awssdk.services.sns.model.PublishRequest;

/**
 * Amazon Simple Notification Service (Amazon SNS) is a web service that enables applications, end-users,
 * and devices to instantly send and receive notifications from the cloud.
 */
public class SNS {

    /**
     * Authenticate to the SNS client using the AWS user's credentials.
     * @param awsBasicCredentials The AWS Access Key ID and Secret Access Key are credentials that are used to securely sign requests to AWS services.
     * @return Service client for accessing Amazon SNS.
     */
    public static SnsClient authenticateSNS(AwsBasicCredentials awsBasicCredentials) {
        return SnsClient
                .builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .region(Region.of(System.getenv("AWS_REGION")))
                .build();
    }

    /**
     * Creates a topic to which notifications can be published.
     * @param snsClient Service client for accessing SNS.
     * @param snsTopicName The name of the topic you want to create.
     * @return The Amazon Resource Name (ARN) assigned to the created topic.
     */
    public static String createSNSTopic(SnsClient snsClient, String snsTopicName) {
        try {
            CreateTopicRequest request = CreateTopicRequest.builder()
                    .name(snsTopicName)
                    .build();

            CreateTopicResponse result = snsClient.createTopic(request);
            return result.topicArn();
        } catch (SnsException error) {
            System.err.println(error.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    /**
     * Subscribes an endpoint to an Amazon SNS topic.
     * @param snsClient Service client for accessing SNS.
     * @param topicArn The ARN of the topic you want to subscribe to.
     * @param email The endpoint that you want to receive notifications. Endpoints vary by protocol:
     *              For the http protocol, the (public) endpoint is a URL beginning with http://.
     *              For the https protocol, the (public) endpoint is a URL beginning with https://.
     *              For the email protocol, the endpoint is an email address.
     *              For the email-json protocol, the endpoint is an email address.
     *              For the sms protocol, the endpoint is a phone number of an SMS-enabled device.
     *              For the sqs protocol, the endpoint is the ARN of an Amazon SQS queue.
     *              For the application protocol, the endpoint is the EndpointArn of a mobile app and device.
     *              For the lambda protocol, the endpoint is the ARN of an AWS Lambda function.
     *              For the firehose protocol, the endpoint is the ARN of an Amazon Kinesis Data Firehose delivery stream.
     */
    public static void emailSubscriber(SnsClient snsClient, String topicArn, String email) {
        try {
            SubscribeRequest request = SubscribeRequest.builder()
                    .protocol("email")
                    .endpoint(email)
                    .returnSubscriptionArn(true)
                    .topicArn(topicArn)
                    .build();

            SubscribeResponse result = snsClient.subscribe(request);
            System.out.println("Subscription ARN is " + result.subscriptionArn() + "\n\nStatus is " + result.sdkHttpResponse().statusCode());
        } catch (SnsException error) {
            System.err.println(error.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    /**
     * Returns a list of the requester's subscriptions.
     * @param snsClient Service client for accessing SNS.
     * @return List of the requester's subscriptions.
     */
    public static List<Subscription> listSNSSubscriptions(SnsClient snsClient) {
        try {
            ListSubscriptionsRequest request = ListSubscriptionsRequest.builder().build();
            ListSubscriptionsResponse result = snsClient.listSubscriptions(request);
            return result.subscriptions();
        } catch (SnsException error) {
            System.err.println(error.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return new ArrayList<>();
    }

    /**
     * Sends a message to an Amazon SNS topic, a text message (SMS message) directly to a phone number,
     * or a message to a mobile platform endpoint (when you specify the TargetArn).
     * @param snsClient Service client for accessing SNS.
     * @param snsTopicName The name of the topic you want to create.
     * @param messages List of messages that will be displayed to the user in the lex-bot
     * @param message The message you want to send.
     * @param type Type of action that was taken on the DynamoDB table (GET, INSERT, REMOVE, or UPDATE).
     */
    public static void publishMessage(SnsClient snsClient, String snsTopicName, List<String> messages, String message, String type) {
        try {
            // TODO
            String topicArn = createSNSTopic(snsClient, snsTopicName);
            List<String> subscribersList = new ArrayList<>();
            subscribersList.add(System.getenv("ADMIN_EMAIL"));

            for (String subscriber : subscribersList)
                emailSubscriber(snsClient, topicArn, subscriber);

            PublishRequest request = PublishRequest.builder()
                    .message(message)
                    .topicArn(topicArn)
                    .build();
            snsClient.publish(request);

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Confirmation sent to ");
            for (Subscription subscription : listSNSSubscriptions(snsClient)) {
                if (listSNSSubscriptions(snsClient).get(listSNSSubscriptions(snsClient).size() - 1).equals(subscription)) {
                    stringBuilder.append(subscription.endpoint());
                }
                stringBuilder.append(subscription.endpoint()).append(", ");
            }

            messages.add(String.valueOf(stringBuilder));
            messages.add("Do you want to perform another operation on the \"Students\" table?");
        } catch (SnsException error) {
            System.err.println(error.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        BotLogic.multipleMessages(messages, "PlainText");

        S3Client s3Client = S3.authenticateS3(BotLogic.getAwsBasicCredentials());
        String bucketName = S3.createBucket(s3Client, System.getenv("S3_BUCKET_NAME"));

        String putObjectResponse = S3.putObject(s3Client, bucketName, type, message);
        System.out.println("Successfully put object to the S3 bucket " + bucketName + ": " + putObjectResponse);
    }
}