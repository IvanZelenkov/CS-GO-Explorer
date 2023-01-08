package services;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codecommit.CodeCommitClient;
import software.amazon.awssdk.services.codecommit.model.*;

/**
 * AWS CodeCommit is a version control service that enables you to privately store and manage Git repositories in the AWS Cloud.
 */
public class CodeCommit {

    /**
     * Authenticate to the CodeCommit client using the AWS user's credentials.
     * @param awsBasicCredentials The AWS Access Key ID and Secret Access Key are credentials that are used to securely sign requests to AWS services.
     * @param appRegion The AWS Region where the service will be hosted.
     * @return Service client for accessing AWS CodeCommit.
     */
    public static CodeCommitClient authenticateCodeCommit(AwsBasicCredentials awsBasicCredentials, Region appRegion) {
        return CodeCommitClient
                .builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .region(appRegion)
                .build();
    }

    /**
     * Creates a new, empty repository.
     * @param codeCommitClient Service client for accessing CodeCommit.
     * @param repositoryName The name of the new repository to be created.
     * @param repositoryDescription A comment or description about the new repository.
     * @return The URL to use for cloning the repository over HTTPS.
     */
    public static String createRepository(CodeCommitClient codeCommitClient,
                                          String repositoryName,
                                          String repositoryDescription) {
        try {
            CreateRepositoryRequest createRepositoryRequest = CreateRepositoryRequest
                    .builder()
                    .repositoryName(repositoryName)
                    .repositoryDescription(repositoryDescription)
                    .build();

            CreateRepositoryResponse createRepositoryResponse = codeCommitClient.createRepository(createRepositoryRequest);
            return createRepositoryResponse.repositoryMetadata().cloneUrlHttp();
        } catch (CodeCommitException error) {
            System.err.println(error.getMessage());
            System.exit(1);
        }
        return "";
    }
}