package handler;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.*;
import software.amazon.awssdk.services.lambda.model.Runtime;
import software.amazon.awssdk.services.lambda.waiters.LambdaWaiter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class Lambda {
    public String createLambdaFunction(LambdaClient lambdaClient, String functionName, String filePath,
                                     String roleArn, String handler, String adminName,
                                     String accessKey, String secretAccessKey) {
        try {
            LambdaWaiter lambdaWaiter = lambdaClient.waiter();
            InputStream is = new FileInputStream(filePath);
            SdkBytes fileToUpload = SdkBytes.fromInputStream(is);

            FunctionCode code = FunctionCode.builder()
                    .zipFile(fileToUpload)
                    .build();

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
                    .memorySize(256)
                    .environment(environment)
                    .build();

            CreateFunctionResponse functionResponse = lambdaClient.createFunction(functionRequest);
            GetFunctionRequest getFunctionRequest = GetFunctionRequest.builder()
                    .functionName(functionResponse.functionName())
                    .build();

            WaiterResponse<GetFunctionResponse> waiterResponse = lambdaWaiter.waitUntilFunctionExists(getFunctionRequest);
            waiterResponse.matched().response().ifPresent(System.out::println);

            System.out.println("Successfully created lambda function.");
            return functionResponse.functionArn();
        } catch (LambdaException | FileNotFoundException error) {
            System.err.println(error.getMessage());
            System.exit(1);
        }
        return "";
    }
}