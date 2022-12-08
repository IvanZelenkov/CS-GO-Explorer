package services;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

public class S3 {

    public S3Client authenticateS3() {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials
                .create(System.getenv("ACCESS_KEY_ID"),
                        System.getenv("SECRET_ACCESS_KEY"));

        return S3Client
                .builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .region(Region.of(System.getenv("AWS_REGION")))
                .build();
    }

    public String createBucket(S3Client s3Client) {
        ListBucketsResponse s3Response = s3Client.listBuckets();
        for (Bucket bucket : s3Response.buckets()) {
            if (bucket.name().equals(System.getenv("S3_BUCKET_NAME"))) {
                return bucket.name() + " table already exists.";
            }
        }

        try {
            CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                    .bucket(System.getenv("S3_BUCKET_NAME"))
                    .build();

            s3Client.createBucket(bucketRequest);
        } catch (S3Exception error) {
            System.err.println(error.getMessage());
            System.exit(1);
        }
        return "Bucket " + System.getenv("S3_BUCKET_NAME") + " created.";
    }
}