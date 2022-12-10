package handler;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.*;
import software.amazon.awssdk.services.lambda.model.Runtime;
import software.amazon.awssdk.services.lambda.waiters.LambdaWaiter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class Launcher {
    public static void main(String[] args) throws Exception {

        final String usage = "\n" +
                "Usage:\n" +
                "    java -jar <adminName> <roleName> <trustPolicyFileLocation> <permissionsPolicyName> " +
                "    <permissionsPolicyFileLocation> <lambdaFunctionName> <lambdaFilePath> <handler>\n\n" +
                "Where:\n" +
                "    accessKey - used to sign programmatic requests that you make to AWS.\n" +
                "    secretAccessKey - used to sign programmatic requests that you make to AWS.\n" +
                "    adminName - The name of the administrator who will manage the table.\n" +
                "    roleName - The name of the IAM role to be created.\n" +
                "    trustPolicyFileLocation - The path to the JSON where the trust policy is located.\n" +
                "    permissionsPolicyName - The name of the permissions policy.\n" +
                "    permissionsPolicyFileLocation - The path to the JSON where the permissions policy is located.\n" +
                "    lambdaFunctionName - The name of the Lambda function.\n" +
                "    lambdaFilePath - The path to the ZIP or JAR where the code is located.\n" +
                "    handler - The fully qualified method name (for example, example.Handler::handleRequest).\n";

        if (args.length != 10) {
            System.out.println(usage);
            System.exit(1);
        }

        String accessKey = args[0];
        String secretAccessKey = args[1];
        String adminName = args[2];
        String roleName = args[3];
        String trustPolicyFileLocation = args[4];
        String permissionsPolicyName = args[5];
        String permissionsPolicyFileLocation = args[6];
        String lambdaFunctionName = args[7];
        String lambdaFilePath = args[8];
        String handler = args[9];

        Region appRegion = Region.US_EAST_1;
        Region iamRegion = Region.AWS_GLOBAL;

        AwsBasicCredentials awsCredentials = AwsBasicCredentials
                .create(accessKey, secretAccessKey);

        IamClient iamClient = IamClient.builder()
                .region(iamRegion)
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();

        // Create an IAM role and attach trust policy
        String roleArn = IAM.createIAMRole(iamClient, roleName, trustPolicyFileLocation);
        System.out.println("Successfully created user: " + roleArn + ".");

        // Create an IAM permissions policy
        String permissionsPolicyArn = IAM.createIAMPermissionsPolicy(iamClient, permissionsPolicyName, permissionsPolicyFileLocation);
        System.out.println("Successfully created permissions policy.");

        // Attach an IAM permission policy to the role
        IAM.attachIAMRolePermissionsPolicy(iamClient, roleName, permissionsPolicyArn);
        iamClient.close();

        LambdaClient awsLambda = LambdaClient.builder()
                .region(appRegion)
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();

        createLambdaFunction(awsLambda, lambdaFunctionName, lambdaFilePath, roleArn, handler, adminName, accessKey, secretAccessKey);
        awsLambda.close();
    }

    public static void createLambdaFunction(LambdaClient lambdaClient, String functionName, String filePath,
                                            String role, String handler, String adminName,
                                            String accessKey, String secretAccessKey) {
        try {
            LambdaWaiter lambdaWaiter = lambdaClient.waiter();
            InputStream is = new FileInputStream(filePath);
            SdkBytes fileToUpload = SdkBytes.fromInputStream(is);

            FunctionCode code = FunctionCode.builder()
                    .zipFile(fileToUpload)
                    .build();

            Environment environment = Environment.builder().variables(new HashMap<>(){{
                put("ADMIN_NAME", adminName);
                put("ACCESS_KEY_ID", accessKey);
                put("SECRET_ACCESS_KEY", secretAccessKey);
            }}).build();

            if (environment.hasVariables()) {
                CreateFunctionRequest functionRequest = CreateFunctionRequest.builder()
                        .functionName(functionName)
                        .description("Database Bot Manager Application Logic")
                        .code(code)
                        .handler(handler)
                        .runtime(Runtime.JAVA11)
                        .role(role)
                        .timeout(60)
                        .memorySize(512)
                        .environment(environment)
                        .build();
                CreateFunctionResponse functionResponse = lambdaClient.createFunction(functionRequest);
                GetFunctionRequest getFunctionRequest = GetFunctionRequest.builder()
                        .functionName(functionResponse.functionName())
                        .build();

                WaiterResponse<GetFunctionResponse> waiterResponse = lambdaWaiter.waitUntilFunctionExists(getFunctionRequest);
                waiterResponse.matched().response().ifPresent(System.out::println);

                System.out.println("Successfully created lambda function.");
            } else {
                System.out.println("ERROR: Environment is not set up.");
            }
        } catch (FileNotFoundException error) {
            System.err.println(error.getMessage());
            System.exit(1);
        }
    }
}