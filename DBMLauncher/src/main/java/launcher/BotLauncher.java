package launcher;

import java.util.Random;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lexmodelsv2.LexModelsV2Client;

import services.IAM;
import services.Lambda;
import services.lexBotConfiguration.Lex;

/**
 * Launches Database Bot Manager application to the user's AWS account using a single command.
 * @author Ivan Zelenkov
 */
public class BotLauncher {
    public static void main(String[] args) throws Exception {
        final String usage = "\n" +
                "Usage:\n" +
                "    java -jar <accessKey> <secretAccessKey> <awsAppDeploymentRegion> <adminEmail>\n\n" +
                "Where:\n" +
                "    accessKey - used to sign programmatic requests that you make to AWS.\n" +
                "    secretAccessKey - used to sign programmatic requests that you make to AWS.\n" +
                "    awsAppDeploymentRegion - The AWS Region where the application will be deployed.\n" +
                "    adminEmail - administrator's email address to which notifications about changes in the database will be sent.";

        if (args.length != 4) {
            System.out.println(usage);
            System.exit(1);
        }

        // Predefined configuration variables
        final String roleName = "DatabaseBotManagerRole";
        final String permissionsPolicyName = "DatabaseBotManagerFullAccess";
        final String lambdaFunctionName = "database-bot-manager-lambda";

        // Command line arguments
        final String accessKey = args[0];
        final String secretAccessKey = args[1];
        final String awsAppDeploymentRegion = args[2];
        final String adminEmail = args[3];

        // IAM region and the region where the AWS application will be deployed
        Region iamRegion = Region.AWS_GLOBAL;
        Region appRegion = Region.of(awsAppDeploymentRegion);

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
                lexRoleCustomSuffixGenerator(),
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
        String lambdaArn = Lambda.createLambdaFunction(lambdaClient, lambdaFunctionName, roleArn, adminEmail, accessKey, secretAccessKey);
        System.out.println("Successfully created lambda function: " + lambdaArn);

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

        String botId = Lex.botConfiguration(lexModelsV2Client, lexRoleArn, lambdaArn);
        System.out.println("Successfully created lex bot with ID: " + lambdaArn);

        // Close Lambda V2 client
        lexModelsV2Client.close();
    }

    /**
     * Creates custom suffix for the service-linked role.
     * @return Custom suffix string.
     */
    private static String lexRoleCustomSuffixGenerator() {
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