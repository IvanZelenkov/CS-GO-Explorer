package services;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codecommit.CodeCommitClient;
import software.amazon.awssdk.services.codecommit.model.CreateRepositoryRequest;
import software.amazon.awssdk.services.codecommit.model.CreateRepositoryResponse;

public class CodeCommit {

    public static CodeCommitClient authenticateCodeCommit(AwsBasicCredentials awsCredentials, Region appRegion) {
        return CodeCommitClient
                .builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .region(appRegion)
                .build();
    }

    public static String createRepository(CodeCommitClient codeCommitClient,
                                          String repositoryName,
                                          String repositoryDescription) {
        CreateRepositoryRequest createRepositoryRequest = CreateRepositoryRequest
                .builder()
                .repositoryName(repositoryName)
                .repositoryDescription(repositoryDescription)
                .build();

        CreateRepositoryResponse createRepositoryResponse = codeCommitClient.createRepository(createRepositoryRequest);
        return createRepositoryResponse.repositoryMetadata().repositoryId();
    }
}
