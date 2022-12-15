package handler.services;

import java.util.HashMap;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.*;
import software.amazon.awssdk.services.lambda.model.Runtime;
import software.amazon.awssdk.services.lambda.waiters.LambdaWaiter;

public class Lambda {

    /**
     * Creates lambda function.
     * @param lambdaClient Service client for accessing AWS Lambda.
     * @param functionName The name of the Lambda function.
     * @param filePath Absolute path to the lambda function JAR file on the user's PC.
     * @param roleArn The Amazon Resource Name (ARN) of the function's execution role.
     * @param handler The name of the method within your code that Lambda calls to run your function.
     * @param adminName The name of the administrator who will manage the database.
     * @param accessKey User's access key.
     * @param secretAccessKey User's secret access key.
     * @return Lambda function ARN.
     */
    public static String createLambdaFunction(LambdaClient lambdaClient, String functionName, String filePath,
                                              String roleArn, String handler, String adminName,
                                              String accessKey, String secretAccessKey) {
        try {
            LambdaWaiter lambdaWaiter = lambdaClient.waiter();
            InputStream inputStream = new FileInputStream(filePath);
            SdkBytes fileToUpload = SdkBytes.fromInputStream(inputStream);

            // Lambda function code
            FunctionCode code = FunctionCode.builder()
                    .zipFile(fileToUpload)
                    .build();

            // Configure environment variables, so they can be accessible from function code during execution
            Environment environment = Environment.builder().variables(new HashMap<>(){{
                put("ADMIN_USERNAME", adminName);
                put("ACCESS_KEY_ID", accessKey);
                put("SECRET_ACCESS_KEY", secretAccessKey);
            }}).build();

            CreateFunctionRequest functionRequest = CreateFunctionRequest.builder()
                    .functionName(functionName)
                    .description("Database Bot Manager Application Logic")
                    .code(code)
                    .handler(handler)
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

            System.out.println("Successfully created lambda function: " + functionResponse.functionArn());
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