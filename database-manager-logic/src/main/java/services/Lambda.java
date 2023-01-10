package services;

import java.nio.file.Path;
import java.nio.file.Paths;

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
     * @param awsBasicCredentials The AWS Access Key ID and Secret Access Key are credentials that are used to securely sign requests to AWS services.
     * @param appRegion The AWS Region where the service will be hosted.
     * @return Service client for accessing AWS Lambda.
     */
    public static LambdaClient authenticateLambda(AwsBasicCredentials awsBasicCredentials, Region appRegion) {
        return LambdaClient
                .builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .region(appRegion)
                .build();
    }

    /**
     * Creates lambda function.
     * @param lambdaClient Service client for accessing AWS Lambda.
     * @param functionName The name of the Lambda function.
     * @param functionDescription The description of the Lambda function.
     * @param roleArn The Amazon Resource Name (ARN) of the function's execution role.
     * @param handlerPath The path to the handler method to invoke the function in the uploaded package.
     * @param runtime The runtime in which the lambda function will be executed.
     * @param timeout Lambda function's maximum invocation timeout limit.
     * @param memorySizeMB The amount of memory allocated to a Lambda function.
     * @param environment A function's environment variable settings. You can use environment
     *                    variables to adjust your function's behavior without updating code.
     *                    An environment variable is a pair of strings that are stored in a
     *                    function's version-specific configuration.
     * @return Lambda function ARN.
     */
    public static String createLambdaFunction(LambdaClient lambdaClient,
                                              String functionName,
                                              String functionDescription,
                                              String roleArn,
                                              String handlerPath,
                                              Runtime runtime,
                                              int timeout,
                                              int memorySizeMB,
                                              Environment environment) {
        try {
            Path path = Paths.get("");
            InputStream inputStream = new FileInputStream(path.toAbsolutePath() + "/database-manager-logic-1.0.0.jar");
            SdkBytes fileToUpload = SdkBytes.fromInputStream(inputStream);

            // Lambda function code
            FunctionCode code = FunctionCode
                    .builder()
                    .zipFile(fileToUpload)
                    .build();

            CreateFunctionRequest functionRequest = CreateFunctionRequest
                    .builder()
                    .functionName(functionName)
                    .description(functionDescription)
                    .code(code)
                    .handler(handlerPath)
                    .runtime(runtime)
                    .role(roleArn)
                    .timeout(timeout)
                    .memorySize(memorySizeMB)
                    .environment(environment)
                    .build();

            CreateFunctionResponse functionResponse = lambdaClient.createFunction(functionRequest);
            GetFunctionRequest getFunctionRequest = GetFunctionRequest
                    .builder()
                    .functionName(functionResponse.functionName())
                    .build();

            // Wait until the lambda function is created
            WaiterResponse<GetFunctionResponse> waiterResponse = lambdaClient.waiter().waitUntilFunctionExists(getFunctionRequest);
            waiterResponse.matched().response().ifPresent(System.out::println);

            return functionResponse.functionArn();
        } catch (LambdaException | FileNotFoundException error) {
            System.err.println(error.getMessage());
            System.exit(1);
        }
        return "";
    }

    public static String updateLambdaFunctionConfiguration(LambdaClient lambdaClient, String functionName, Environment environment) {
        try {
            UpdateFunctionConfigurationRequest updateFunctionConfigurationRequest = UpdateFunctionConfigurationRequest
                    .builder()
                    .functionName(functionName)
                    .environment(environment)
                    .build();

            UpdateFunctionConfigurationResponse updateFunctionConfigurationResponse = lambdaClient.updateFunctionConfiguration(updateFunctionConfigurationRequest);
            return updateFunctionConfigurationResponse.toString();
        } catch (LambdaException error) {
            System.err.println(error.getMessage());
            System.exit(1);
        }
        return "";
    }

    /**
     * Grants an AWS Lex service permission to use a lambda function.
     * @param lambdaClient Service client for accessing AWS Lambda.
     * @param lambdaFunctionName The name of the Lambda function, version, or alias.
     * @param statementId A statement identifier that differentiates the statement from others in the same policy.
     * @param action The action that the principal can use on the function.
     * @param principal The AWS service or AWS account that invokes the function.
     */
    public static void createResourcePolicy(LambdaClient lambdaClient,
                                            String lambdaFunctionName,
                                            String statementId,
                                            String action,
                                            String principal) {
        try {
            AddPermissionRequest addPermissionRequest = AddPermissionRequest
                    .builder()
                    .functionName(lambdaFunctionName)
                    .statementId(statementId)
                    .action(action)
                    .principal(principal)
                    .build();

            lambdaClient.addPermission(addPermissionRequest);
        } catch (LambdaException error) {
            System.err.println(error.getMessage());
            System.exit(1);
        }
    }
}