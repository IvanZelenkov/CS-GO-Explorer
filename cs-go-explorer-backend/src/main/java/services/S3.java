package services;

import org.json.simple.JSONArray;
import services.api.ApiGateway;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import services.api.ApiGatewayProxyResponse;
import services.api.ApiGateway;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.UUID;

/**
 * Amazon S3 provides storage for the Internet, and is designed to make web-scale computing easier for developers.
 */
public class S3 {

    /**
     * Authenticate to the S3 client using the AWS user's credentials.
     * @param awsBasicCredentials The AWS Access Key ID and Secret Access Key are credentials that are used to securely sign requests to AWS services.
     * @param appRegion The AWS Region where the service will be hosted.
     * @return Service client for accessing AWS Lambda.
     */
    public static S3Client authenticateS3(AwsBasicCredentials awsBasicCredentials, Region appRegion) {
        return S3Client
                .builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .region(appRegion)
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
            ListBucketsResponse listBucketsResponse = s3Client.listBuckets();

            for (Bucket bucket : listBucketsResponse.buckets())
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

    /**
     * Returns some or all (up to 1,000) of the objects in a bucket.
     * @param s3Client Service client for accessing Amazon S3.
     * @param bucketName The bucket name to which the PUT action was initiated.
     * @return Keys of all (up to 1,000) objects in a bucket.
     */
    public static List<String> listBucketObjectsKeys(S3Client s3Client, String bucketName) {
        List<String> s3ObjectKeyList = new ArrayList<>();
        try {
            ListObjectsRequest listObjects = ListObjectsRequest
                    .builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsResponse listObjectsResponse = s3Client.listObjects(listObjects);
            List<S3Object> objects = listObjectsResponse.contents();
            for (S3Object image : objects)
                s3ObjectKeyList.add(image.key());

            return s3ObjectKeyList;
        } catch (S3Exception error) {
            System.err.println(error.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return new ArrayList<>();
    }

    /**
     * Retrieves URLs of objects from Amazon S3.
     * @param s3Client Service client for accessing Amazon S3.
     * @param bucketName The bucket name to which the PUT action was initiated.
     * @param s3ObjectKeyList Keys of all (up to 1,000) objects in a bucket.
     * @param event The Lambda Function event.
     * @return API Gateway proxy response containing status code, headers, body, and base64 encoding enabled.
     */
    public static ApiGatewayProxyResponse getImageUrls(S3Client s3Client, String bucketName, List<String> s3ObjectKeyList, Map<String, Object> event) {
        // Handles CORS preflight request
        if (event.get("httpMethod").equals("OPTIONS"))
            return ApiGateway.generateResponseForOptionsRequest();

        try {
            JSONArray jsonArray = new JSONArray();
            for (String s3ObjectKey : s3ObjectKeyList) {
                String imageUrl = s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(s3ObjectKey)).toExternalForm();
                jsonArray.add(imageUrl);
            }
            return ApiGateway.generateResponseForPostOrGetRequest(jsonArray.toString());
        } catch (S3Exception error) {
            System.err.println(error.getMessage());
            System.exit(1);
        }
        return ApiGateway.generateResponseForPostOrGetRequest("");
    }
}