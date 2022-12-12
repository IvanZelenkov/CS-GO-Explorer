package handler;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lexmodelsv2.LexModelsV2Client;
import software.amazon.awssdk.services.lexruntimev2.LexRuntimeV2Client;

public class Launcher {
    public static void main(String[] args) throws Exception {

//        final String usage = "\n" +
//                "Usage:\n" +
//                "    java -jar <adminName> <roleName> <trustPolicyFileLocation> <permissionsPolicyName> " +
//                "    <permissionsPolicyFileLocation> <lambdaFunctionName> <lambdaFilePath> <handler>\n\n" +
//                "Where:\n" +
//                "    accessKey - used to sign programmatic requests that you make to AWS.\n" +
//                "    secretAccessKey - used to sign programmatic requests that you make to AWS.\n" +
//                "    adminName - The name of the administrator who will manage the table.\n" +
//                "    roleName - The name of the IAM role to be created.\n" +
//                "    trustPolicyFileLocation - The path to the JSON where the trust policy is located.\n" +
//                "    permissionsPolicyName - The name of the permissions policy.\n" +
//                "    permissionsPolicyFileLocation - The path to the JSON where the permissions policy is located.\n" +
//                "    lambdaFunctionName - The name of the Lambda function.\n" +
//                "    lambdaFilePath - The path to the ZIP or JAR where the code is located.\n" +
//                "    handler - The fully qualified method name (for example, example.Handler::handleRequest).\n";
//
//        if (args.length != 10) {
//            System.out.println(usage);
//            System.exit(1);
//        }
//
//        String accessKey = args[0];
//        String secretAccessKey = args[1];
//        String adminName = args[2];
//        String roleName = args[3];
//        String trustPolicyFileLocation = args[4];
//        String permissionsPolicyName = args[5];
//        String permissionsPolicyFileLocation = args[6];
//        String lambdaFunctionName = args[7];
//        String lambdaFilePath = args[8];
//        String handler = args[9];
//
//        Region appRegion = Region.US_EAST_1;
//        Region iamRegion = Region.AWS_GLOBAL;
//
        AwsBasicCredentials awsCredentials = AwsBasicCredentials
                .create("AKIA6JEHZ62JE3BZ5OO6", "/BfL9OfzQu16nXiaYAxVg7E7sM0fXU0lo0qYU0uZ");
//
//        IamClient iamClient = IamClient.builder()
//                .region(iamRegion)
//                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
//                .build();
//
//        // Create an IAM role and attach trust policy
//        String roleArn = IAM.createIAMRole(iamClient, roleName, trustPolicyFileLocation);
//        System.out.println("Successfully created user: " + roleArn + ".");
//
//        // Create an IAM permissions policy
//        String permissionsPolicyArn = IAM.createIAMPermissionsPolicy(iamClient, permissionsPolicyName, permissionsPolicyFileLocation);
//        System.out.println("Successfully created permissions policy.");
//
//        // Attach an IAM permission policy to the role
//        IAM.attachIAMRolePermissionsPolicy(iamClient, roleName, permissionsPolicyArn);
//        iamClient.close();
//
//        // 5 sec delay avoids race condition between IAM and Lambda
//        Thread.sleep(5000);
//
//        LambdaClient lambdaClient = LambdaClient
//                .builder()
//                .region(appRegion)
//                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
//                .build();
//
//        Lambda lambda = new Lambda();
//        lambda.createLambdaFunction(lambdaClient, lambdaFunctionName, lambdaFilePath, roleArn, handler, adminName, accessKey, secretAccessKey);
//        lambdaClient.close();


        Region appRegion = Region.US_EAST_1;

        LexRuntimeV2Client lexV2Client = LexRuntimeV2Client
                .builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .region(appRegion)
                .build();

        LexModelsV2Client lexModelsV2Client = LexModelsV2Client
                .builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .region(appRegion)
                .build();

        Lex lex = new Lex();
        lex.createLexBot(lexModelsV2Client);
        lexV2Client.close();
    }
}