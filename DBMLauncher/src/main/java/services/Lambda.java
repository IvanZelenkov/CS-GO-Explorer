package services;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.*;
import software.amazon.awssdk.services.lambda.model.Runtime;
import software.amazon.awssdk.services.lambda.waiters.LambdaWaiter;

/**
 * AWS Lambda is a serverless, event-driven compute service that lets you run code for
 * virtually any type of application or backend service without provisioning or managing servers.
 */
public class Lambda {

    /**
     * Authenticate to the Lambda client using the AWS user's credentials.
     * @param awsCredentials The AWS Access Key ID and Secret Access Key are credentials that are used to securely sign requests to AWS services.
     * @return Service client for accessing AWS Lambda.
     */
    public static LambdaClient authenticateLambda(AwsBasicCredentials awsCredentials, Region appRegion) {
        return LambdaClient
                .builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .region(appRegion)
                .build();
    }

    /**
     * Creates lambda function.
     * @param lambdaClient Service client for accessing AWS Lambda.
     * @param functionName The name of the Lambda function.
     * @param roleArn The Amazon Resource Name (ARN) of the function's execution role.
     * @param accessKey User's access key.
     * @param secretAccessKey User's secret access key.
     * @return Lambda function ARN.
     */
    public static String createLambdaFunction(LambdaClient lambdaClient,
                                              String functionName,
                                              String roleArn,
                                              String accessKey,
                                              String secretAccessKey,
                                              String adminEmail,
                                              Region appRegion,
                                              String restApiId) {
        try {
            LambdaWaiter lambdaWaiter = lambdaClient.waiter();
            Path path = Paths.get("");
            InputStream inputStream = new FileInputStream(path.toAbsolutePath() + "/DBMLauncher-1.0.0.jar");
            SdkBytes fileToUpload = SdkBytes.fromInputStream(inputStream);

            // Lambda function code
            FunctionCode code = FunctionCode.builder()
                    .zipFile(fileToUpload)
                    .build();

            // Configure environment variables, so they can be accessible from function code during execution
            Environment environment = Environment.builder().variables(new HashMap<>(){{
                put("ACCESS_KEY_ID", accessKey);
                put("SECRET_ACCESS_KEY", secretAccessKey);
                put("ADMIN_EMAIL", adminEmail);
                put("AWS_APP_REGION", appRegion.toString());
                put("REST_API_ID", restApiId);
            }}).build();

            CreateFunctionRequest functionRequest = CreateFunctionRequest.builder()
                    .functionName(functionName)
                    .description("Database Bot Manager Application Logic")
                    .code(code)
                    .handler("handler.BotLogic::handleRequest")
                    .runtime(Runtime.JAVA11)
                    .role(roleArn)
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

            return functionResponse.functionArn();
        } catch (LambdaException | FileNotFoundException error) {
            System.err.println(error.getMessage());
            System.exit(1);
        }
        return "";
    }

    /**
     * Grants an AWS Lex service permission to use a lambda function.
     * @param lambdaClient Service client for accessing AWS Lambda.
     * @param lambdaFunctionName The name of the Lambda function, version, or alias.
     */
    public static void createResourcePolicy(LambdaClient lambdaClient, String lambdaFunctionName) {
        AddPermissionRequest addPermissionRequest = AddPermissionRequest
                .builder()
                .functionName(lambdaFunctionName)
                .statementId("chatbot-fulfillment")
                .action("lambda:InvokeFunction")
                .principal("lex.amazonaws.com")
                .build();

        lambdaClient.addPermission(addPermissionRequest);
    }
}