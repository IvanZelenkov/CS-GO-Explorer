package launcher;

import java.util.HashMap;
import java.util.Random;

import services.CodeCommit;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.amplify.AmplifyClient;
import software.amazon.awssdk.services.amplify.model.Platform;
import software.amazon.awssdk.services.amplify.model.Stage;
import software.amazon.awssdk.services.apigateway.ApiGatewayClient;
import software.amazon.awssdk.services.apigateway.model.*;
import software.amazon.awssdk.services.codecommit.CodeCommitClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.Environment;
import software.amazon.awssdk.services.lambda.model.Runtime;
import software.amazon.awssdk.services.lexmodelsv2.LexModelsV2Client;

import services.IAM;
import services.Lambda;
import services.bot.Lex;
import services.Amplify;
import services.api.ApiGateway;
import services.database.DynamoDB;

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

        // Create an AWS account credentials instance
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(accessKey, secretAccessKey);

        // Authenticate and create an IAM client
        IamClient iamClient = IAM.authenticateIAM(awsBasicCredentials, iamRegion);

        // Create an IAM Lex V2 role
        String lexRoleArn = IAM.createServiceLinkedRole(iamClient, "lexv2.amazonaws.com", Lex.lexRoleCustomSuffixGenerator(), "DBM Lex V2 Bot Role");

        // Create an IAM Lambda role and attach trust policy
        String roleArn = IAM.createRole(iamClient, roleName, "Database Bot Manager Trust Policy");
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

        // Authenticate and create an API Gateway client
        ApiGatewayClient apiGatewayClient = ApiGateway.authenticateApiGateway(awsBasicCredentials, appRegion);

        String restApiId = ApiGateway.createAPI(
                apiGatewayClient,
                "database-manager-rest-api",
                "REST API for DBM application",
                ApiKeySourceType.AUTHORIZER,
                EndpointConfiguration.builder().types(EndpointType.REGIONAL).build()
        );
        System.out.println("Successfully created api with id: " + restApiId);

        // Authenticate and create a Lambda client
        LambdaClient lambdaClient = Lambda.authenticateLambda(awsBasicCredentials, appRegion);

        // Configure environment variables, so they can be accessible from function code during execution
        Environment environment = Environment.builder().variables(new HashMap<>(){{
            put("ACCESS_KEY_ID", accessKey);
            put("SECRET_ACCESS_KEY", secretAccessKey);
            put("AWS_APP_REGION", appRegion.toString());
            put("REST_API_ID", restApiId);
            put("DYNAMO_DB_TABLE_NAME", "Students");
            put("S3_BUCKET_NAME", "dynamo-db-students-table-actions");
            put("SNS_TOPIC_NAME", "DynamoStudentsDBTableChanges");
            put("ADMIN_EMAIL", adminEmail);
        }}).build();

        // Create a lambda function and attach a role
        String lambdaArn = Lambda.createLambdaFunction(
                lambdaClient,
                lambdaFunctionName,
                "Database Bot Manager Application Logic",
                roleArn,
                "handler.BotLogic::handleRequest",
                Runtime.JAVA11,
                60,
                512,
                environment
        );
        System.out.println("Successfully created lambda function: " + lambdaArn);

        // Create a resource policy and add a resource-based policy statement
        Lambda.createResourcePolicy(lambdaClient, lambdaFunctionName, "chatbot-fulfillment", "lambda:InvokeFunction", "lex.amazonaws.com");

        // Close Lambda client
        lambdaClient.close();

        // Authenticate and create a Lex V2 client
        LexModelsV2Client lexModelsV2Client = Lex.authenticateLexV2(awsBasicCredentials, appRegion);

        String botId = Lex.botConfiguration(lexModelsV2Client, lexRoleArn, lambdaArn);
        System.out.println("Successfully created lex bot with ID: " + lambdaArn);

        // Close Lex V2 client
        lexModelsV2Client.close();

        DynamoDbClient dynamoDbClient = DynamoDB.authenticateDynamoDB(awsBasicCredentials, appRegion);

        String tableName = DynamoDB.createTable(dynamoDbClient, "Students", "studentId", ScalarAttributeType.N, KeyType.HASH);
        System.out.print("Successfully created " + tableName + " table.");

        // Close DynamoDB client
        dynamoDbClient.close();

        // 'get-all-table-items'  configuration of resources and its methods
        String resourceId = ApiGateway.createResource(apiGatewayClient, restApiId, 0, "get-all-table-items");

        String methodRequestPOST = ApiGateway.createMethodRequest(
                apiGatewayClient,
                restApiId,
                resourceId,
                "POST",
                "NONE",
                false
        );
        System.out.println("Successfully created API method request: " + methodRequestPOST);

        String integrationRequestPOST = ApiGateway.createIntegrationRequest(
                apiGatewayClient,
                restApiId,
                resourceId,
                "POST",
                roleArn,
                "arn:aws:apigateway:" + awsAppDeploymentRegion + ":lambda:path/2015-03-31/functions/" + lambdaArn + "/invocations",
                IntegrationType.AWS_PROXY,
                "POST"
        );
        System.out.println("Successfully created API integration request: " + integrationRequestPOST);

        String integrationResponsePOST = ApiGateway.createIntegrationResponse(
                apiGatewayClient,
                restApiId,
                resourceId,
                "POST",
                "200"
        );
        System.out.println("Successfully created API integration response: " + integrationResponsePOST);

        String methodResponsePOST = ApiGateway.createMethodResponse(
                apiGatewayClient,
                restApiId,
                resourceId,
                "POST",
                "200",
                new HashMap<>(){{put("application/json", "Empty");}}
        );
        System.out.println("Successfully created API method response: " + methodResponsePOST);

        String methodRequestOPTIONS = ApiGateway.createMethodRequest(
                apiGatewayClient,
                restApiId,
                resourceId,
                "OPTIONS",
                "NONE",
                false
        );
        System.out.println("Successfully created API method request: " + methodRequestOPTIONS);

        String integrationRequestOPTIONS = ApiGateway.createIntegrationRequest(
                apiGatewayClient,
                restApiId,
                resourceId,
                "OPTIONS",
                roleArn,
                "arn:aws:apigateway:" + awsAppDeploymentRegion + ":lambda:path/2015-03-31/functions/" + lambdaArn + "/invocations",
                IntegrationType.AWS_PROXY,
                "POST"
        );
        System.out.println("Successfully created API integration request: " + integrationRequestOPTIONS);

        String integrationResponseOPTIONS = ApiGateway.createIntegrationResponse(
                apiGatewayClient,
                restApiId,
                resourceId,
                "OPTIONS",
                "200"
        );
        System.out.println("Successfully created API integration response: " + integrationResponseOPTIONS);

        String methodResponseOPTIONS = ApiGateway.createMethodResponse(
                apiGatewayClient,
                restApiId,
                resourceId,
                "OPTIONS",
                "200",
                new HashMap<>(){{put("application/json", "Empty");}}
        );
        System.out.println("Successfully created API method response: " + methodResponseOPTIONS);

        String stageName = "DevelopmentStage";
        String id = ApiGateway.createNewDeployment(
                apiGatewayClient,
                restApiId,
                "Created using Java AWS SDK",
                stageName,
                "Test Deployment"
        );
        System.out.println("The id of the REST API deployment: " + id);

        ThrottleSettings throttleSettings = ThrottleSettings
                .builder()
                .rateLimit(100.0)
                .burstLimit(100)
                .build();

        String usagePlanId = ApiGateway.createUsagePlan(
                apiGatewayClient,
                restApiId,
                stageName,
                throttleSettings,
                new HashMap<>(){{
                    put("/get-all-table-items/OPTIONS", throttleSettings);
                    put("/get-all-table-items/POST", throttleSettings);
                }},
                "test-plan",
                "DBM test usage plan",
                "DAY",
                1200
        );

        ApiGateway.createApiKey(
                apiGatewayClient,
                "DBM_key",
                "Test key",
                true,
                usagePlanId,
                "API_KEY"
        );

        // Close API Gateway client
        apiGatewayClient.close();

        CodeCommitClient codeCommitClient = CodeCommit.authenticateCodeCommit(awsBasicCredentials, appRegion);

        String cloneUrlHttp = CodeCommit.createRepository(
                codeCommitClient,
                "DBM-Repository",
                "UI of the DBM application"
        );
        System.out.println("Successfully created repository with clone URL Http: " + cloneUrlHttp);

        // Close CodeCommit client
        codeCommitClient.close();

        AmplifyClient amplifyClient = Amplify.authenticateAmplify(awsBasicCredentials, appRegion);

        String appId = Amplify.createApp(
                amplifyClient,
                "DBM",
                "Database manager application",
                Platform.WEB,
                roleArn,
                cloneUrlHttp,
                true,
                true,
                Stage.DEVELOPMENT,
                true,
                true
        );
        System.out.println("Successfully created app with id: " + appId);

        // Close Amplify client
        amplifyClient.close();
    }
}