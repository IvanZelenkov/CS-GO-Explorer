package handler;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.*;
import software.amazon.awssdk.services.lambda.model.Runtime;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class Creator {
    public static void main(String[] args) throws Exception {

        final String usage = "\n" +
                "Usage:\n" +
                "    java -jar <adminName> <roleName> <trustPolicyFileLocation> <permissionsPolicyName> " +
                "    <permissionsPolicyFileLocation> <lambdaFunctionName> <lambdaFilePath> <handler>\n\n" +
                "Where:\n" +
                "    adminName - The name of the administrator who will manage the table.\n" +
                "    roleName - The name of the IAM role to be created.\n" +
                "    trustPolicyFileLocation - The path to the JSON where the trust policy is located.\n" +
                "    permissionsPolicyName - The name of the permissions policy.\n" +
                "    permissionsPolicyFileLocation - The path to the JSON where the permissions policy is located.\n" +
                "    lambdaFunctionName - The name of the Lambda function.\n" +
                "    lambdaFilePath - The path to the ZIP or JAR where the code is located.\n" +
                "    handler - The fully qualified method name (for example, example.Handler::handleRequest).\n";

        if (args.length != 8) {
            System.out.println(usage);
            System.exit(1);
        }

        String adminName = args[0];
        String roleName = args[1];
        String trustPolicyFileLocation = args[2];
        String permissionsPolicyName = args[3];
        String permissionsPolicyFileLocation = args[4];
        String lambdaFunctionName = args[5];
        String lambdaFilePath = args[6];
        String handler = args[7];

        Region appRegion = Region.US_EAST_1;
        Region iamRegion = Region.AWS_GLOBAL;

        IamClient iamClient = IamClient.builder()
                .region(iamRegion)
                .credentialsProvider(ProfileCredentialsProvider.create())
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
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        createLambdaFunction(awsLambda, lambdaFunctionName, lambdaFilePath, roleArn, handler, adminName);
        awsLambda.close();
    }

    public static void createLambdaFunction(LambdaClient awsLambda, String functionName, String filePath, String role, String handler, String adminName) {
        try {
            InputStream is = new FileInputStream(filePath);
            SdkBytes fileToUpload = SdkBytes.fromInputStream(is);

            FunctionCode code = FunctionCode.builder()
                    .zipFile(fileToUpload)
                    .build();

            Environment.Builder environment = Environment.builder().variables(new HashMap<>(){{put("ADMIN_NAME", adminName);}});
            CreateFunctionRequest functionRequest = CreateFunctionRequest.builder()
                    .functionName(functionName)
                    .description("Database Bot Manager Application Logic")
                    .code(code)
                    .handler(handler)
                    .runtime(Runtime.JAVA11)
                    .role(role)
                    .environment(environment.build())
                    .build();

            awsLambda.createFunction(functionRequest);
            System.out.println("Successfully created lambda function.");
        } catch (FileNotFoundException error) {
            System.err.println(error.getMessage());
            System.exit(1);
        }
    }
}