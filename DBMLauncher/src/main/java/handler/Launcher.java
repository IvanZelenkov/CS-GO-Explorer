package handler;

import java.util.Random;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lexmodelsv2.LexModelsV2Client;

import handler.services.IAM;
import handler.services.Lambda;
import handler.services.Lex;

/**
 * Launches Database Bot Manager application to the user's AWS account.
 * @author Ivan Zelenkov
 */
public class Launcher {
    public static void main(String[] args) throws Exception {
        final String usage = "\n" +
                "Usage:\n" +
                "    java -jar <accessKey> <secretAccessKey> <adminName> <roleName> <permissionsPolicyName> " +
                "    <lambdaFunctionName> <lambdaFilePath> <handler> <awsAppRegion>\n\n" +
                "Where:\n" +
                "    accessKey - used to sign programmatic requests that you make to AWS.\n" +
                "    secretAccessKey - used to sign programmatic requests that you make to AWS.\n" +
                "    adminName - The name of the administrator who will manage the table.\n" +
                "    roleName - The name of the IAM role to be created.\n" +
                "    permissionsPolicyName - The name of the permissions policy.\n" +
                "    lambdaFunctionName - The name of the Lambda function.\n" +
                "    lambdaFilePath - The path to the ZIP or JAR where the code is located.\n" +
                "    handler - The fully qualified method name (for example, example.Handler::handleRequest).\n" +
                "    awsAppRegion - The AWS Region where the application will be deployed.";

        if (args.length != 9) {
            System.out.println(usage);
            System.exit(1);
        }

        // Command line arguments
        String accessKey = args[0];
        String secretAccessKey = args[1];
        String adminName = args[2];
        String roleName = args[3];
        String permissionsPolicyName = args[4];
        String lambdaFunctionName = args[5];
        String lambdaFilePath = args[6];
        String handler = args[7];
        String awsAppRegion = args[8];

        // IAM region and the region where the AWS application will be deployed
        Region iamRegion = Region.AWS_GLOBAL;
        Region appRegion = Region.of(awsAppRegion);

        // Authenticate in AWS account using user input credentials
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretAccessKey);

        // Create IAM client
        IamClient iamClient = IamClient.builder()
                .region(iamRegion)
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();

        // Create an IAM Lex V2 role
        String lexRoleArn = IAM.createServiceLinkedRole(
                iamClient,
                "lexv2.amazonaws.com",
                "4A9B9C4D5E6",
                "DBM Lex V2 Bot Role"
        );

        // Create an IAM Lambda role and attach trust policy
        String roleArn = IAM.createRole(iamClient, roleName);
        System.out.println("Successfully created role: " + roleArn);

        // Create an IAM permissions policy
        String permissionsPolicyArn = IAM.createPermissionsPolicy(iamClient, permissionsPolicyName);
        System.out.println("Successfully created permissions policy: " + permissionsPolicyArn);

        // Attach an IAM permission policy to the role
        IAM.attachRolePermissionsPolicy(iamClient, roleName, permissionsPolicyArn);

        // Close IAM client
        iamClient.close();

        // 10 seconds delay avoids a race condition between attaching the IAM permissions policy and creating a Lambda function.
        Thread.sleep(10000);

        // Create Lambda client
        LambdaClient lambdaClient = LambdaClient
                .builder()
                .region(appRegion)
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();

        // Create a lambda function and attach a role
        String lambdaArn = Lambda.createLambdaFunction(lambdaClient, lambdaFunctionName, lambdaFilePath, roleArn, handler, adminName, accessKey, secretAccessKey);

        // Create a resource policy and add a resource-based policy statement
        Lambda.createResourcePolicy(lambdaClient, lambdaFunctionName);

        // Close Lambda client
        lambdaClient.close();

        // Create LexV2 client
        LexModelsV2Client lexModelsV2Client = LexModelsV2Client
                .builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .region(appRegion)
                .build();

        Lex lex = new Lex();
        lex.botSetUp(lexModelsV2Client, lexRoleArn, lambdaArn);
    }

    /**
     * Creates custom suffix for the service-linked role.
     * @return Custom suffix string.
     */
    public String lexRoleCustomSuffixGenerator() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 11;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int) (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString().toUpperCase();
    }
}