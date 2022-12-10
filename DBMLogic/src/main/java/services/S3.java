package services;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

public class S3 {

    public S3Client authenticateS3() {
        return S3Client
                .builder()
                .credentialsProvider(ProfileCredentialsProvider.create())
                .region(Region.of(System.getenv("AWS_REGION")))
                .build();
    }

    public String createBucket(S3Client s3Client) {
        ListBucketsResponse s3Response = s3Client.listBuckets();
        for (Bucket bucket : s3Response.buckets()) {
            if (bucket.name().equals("dynamo-db-students-table-actions")) {
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