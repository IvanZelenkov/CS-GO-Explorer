package services;

import java.util.UUID;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;

/**
 * Amazon S3 provides storage for the Internet, and is designed to make web-scale computing easier for developers.
 */
public class S3 {

    /**
     * Authenticate to the S3 client using the AWS user's credentials.
     * @param awsBasicCredentials The AWS Access Key ID and Secret Access Key are credentials that are used to securely sign requests to AWS services.
     * @return Service client for accessing AWS Lambda.
     */
    public static S3Client authenticateS3(AwsBasicCredentials awsBasicCredentials) {
        return S3Client
                .builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .region(Region.of(System.getenv("AWS_REGION")))
                .build();
    }

    /**
     * Creates an S3 bucket.
     * @param s3Client Service client for accessing Amazon S3.
     * @param bucketName The name of the bucket to create.
     * @return The name of the created S3 bucket.
     */
    public static String createBucket(S3Client s3Client, String bucketName) {
        try {
            ListBucketsResponse s3Response = s3Client.listBuckets();

            for (Bucket bucket : s3Response.buckets())
                if (bucket.name().equals(bucketName))
                    return bucket.name() + " bucket already exists.";

            CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            s3Client.createBucket(bucketRequest);

            HeadBucketRequest headBucketRequest = HeadBucketRequest
                    .builder()
                    .bucket(bucketRequest.bucket())
                    .build();

            // Wait until the S3 bucket is created
            WaiterResponse<HeadBucketResponse> waitUntilBucketExists = s3Client.waiter().waitUntilBucketExists(headBucketRequest);
            waitUntilBucketExists.matched().response().ifPresent(System.out::println);

            System.out.print("S3 bucket " + headBucketRequest.bucket() + " has been created.");
            return headBucketRequest.bucket();
        } catch (S3Exception error) {
            System.err.println(error.getMessage());
            System.exit(1);
        }
        return "";
    }

    /**
     * Adds an object to a bucket. You must have WRITE permissions on a bucket to add an object to it.
     * @param s3Client Service client for accessing Amazon S3.
     * @param bucketName The bucket name to which the PUT action was initiated.
     * @param type Type of action that was taken on the DynamoDB table (GET, INSERT, REMOVE, or UPDATE).
     * @param message The message that was sent to the SNS.
     * @return Information representing the placed object.
     */
    public static String putObject(S3Client s3Client, String bucketName, String type, String message) {
        try {
            PutObjectRequest objectRequest = PutObjectRequest
                    .builder()
                    .bucket(bucketName)
                    .key(type + "-" + UUID.randomUUID() + ".txt")
                    .build();

            PutObjectResponse putObjectResponse = s3Client.putObject(objectRequest, RequestBody.fromString(message));
            return putObjectResponse.toString();
        } catch (S3Exception error) {
            System.err.println(error.getMessage());
            System.exit(1);
        }
        return "";
    }
}