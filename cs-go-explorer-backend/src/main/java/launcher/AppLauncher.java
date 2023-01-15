package launcher;

import java.util.HashMap;
import java.util.Map;

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
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sns.SnsClient;

import services.*;
import services.bot.Lex;
import services.api.ApiGateway;
import services.database.DynamoDB;

/**
 * Launches CS:GO Explorer application to the user's AWS account using a single command.
 * @author Ivan Zelenkov
 * @version 1.0.0
 */
public class AppLauncher {
    public static void main(String[] args) throws Exception {
        final String usage = "\n" +
                "Usage:\n" +
                "    java -jar <accessKey> <secretAccessKey> <awsAppDeploymentRegion> <userEmail> <steamId> <steamApiKey>\n\n" +
                "Where:\n" +
                "    accessKey - used to sign programmatic requests that you make to AWS.\n" +
                "    secretAccessKey - used to sign programmatic requests that you make to AWS.\n" +
                "    awsAppDeploymentRegion - The AWS Region where the application will be deployed.\n" +
                "    userEmail - user's email address to which notifications about changes in the database will be sent.\n" +
                "    steamId - unique identifier of your Steam account.\n" +
                "    steamApiKey - API key is a unique identifier used to connect to, or perform, an API call.";

        if (args.length != 6) {
            System.out.println(usage);
            System.exit(1);
        }

        // Command line arguments
        final String accessKey = args[0];
        final String secretAccessKey = args[1];
        final String awsAppDeploymentRegion = args[2];
        final String userEmail = args[3];
        final String steamId = args[4];
        final String steamApiKey = args[5];

        // Predefined configuration variables. The creation of services follows this order
        final String roleName = "CsGoExplorerRole"; // IAM
        final String permissionsPolicyName = "CsGoExplorerRoleFullAccess"; // IAM
        final String restApiName = "cs-go-explorer-rest-api"; // API Gateway
        final String s3BucketName = "cs-go-explorer-s3-bucket"; // S3
        final String snsTopicName = "cs-go-explorer-sns-topic"; // SNS
        final String lambdaFunctionName = "cs-go-explorer-lambda-function"; // Lambda
        final String botName = "CsGoExplorerBot"; // Lex
        final String tableName = "cs-go-explorer-table"; // DynamoDB
        final String codeCommitRepositoryName = "cs-go-explorer-repository"; // CodeCommit
        final String appName = "CsGoExplorer"; // Amplify

        // IAM region and the region where the AWS application will be deployed
        final Region globalRegion = Region.AWS_GLOBAL;
        final Region appRegion = Region.of(awsAppDeploymentRegion);

        // Create an AWS account credentials instance
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(accessKey, secretAccessKey);

        // Authenticate and create an IAM client
        IamClient iamClient = IAM.authenticateIAM(awsBasicCredentials, globalRegion);

        // Create an IAM Lex V2 role
        String lexRoleArn = IAM.createServiceLinkedRole(iamClient, "lexv2.amazonaws.com", Lex.lexRoleCustomSuffixGenerator(), "CS:GO Explorer Lex V2 bot role");

        // Create an IAM Lambda role and attach trust policy
        String roleArn = IAM.createRole(iamClient, roleName, "CS:GO Explorer Trust Policy");
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

        // Create REST API
        String restApiId = ApiGateway.createAPI(apiGatewayClient, restApiName, "REST API for CS:GO Explorer application",
                ApiKeySourceType.AUTHORIZER, EndpointConfiguration.builder().types(EndpointType.REGIONAL).build());
        System.out.println("Successfully created api with id: " + restApiId);

        // Authenticate and create an S3 client
        S3Client s3Client = S3.authenticateS3(awsBasicCredentials, appRegion);

        // Create S3 bucket
        String bucketName = S3.createBucket(s3Client, s3BucketName);
        System.out.println("S3 bucket " + bucketName + " has been created.");

        // Close S3 client
        s3Client.close();

        // Authenticate and create an SNS client
        SnsClient snsClient = SNS.authenticateSNS(awsBasicCredentials, appRegion);

        // Create an SNS topic
        String topicArn = SNS.createSNSTopic(snsClient, snsTopicName);
        System.out.println("Successfully created an SNS topic: " + topicArn);

        // Close SNS client
        snsClient.close();

        // Authenticate and create a CodeCommit client
        CodeCommitClient codeCommitClient = CodeCommit.authenticateCodeCommit(awsBasicCredentials, appRegion);

        // Create a CodeCommit repository
        String cloneUrlHttp = CodeCommit.createRepository(codeCommitClient, codeCommitRepositoryName, "CS:GO Explorer UI");
        System.out.println("Successfully created repository with clone URL Http: " + cloneUrlHttp);

        // Close CodeCommit client
        codeCommitClient.close();

        // Authenticate and create an Amplify client
        AmplifyClient amplifyClient = Amplify.authenticateAmplify(awsBasicCredentials, appRegion);

        // Create a Map with environmental variables for an Amplify
        Map<String, String> amplifyEnvironmentalVariables = new HashMap<>();
        amplifyEnvironmentalVariables.put("BUILD_ENV", "prod");
        amplifyEnvironmentalVariables.put("REACT_APP_REST_API_ID", restApiId);

        // Create Amplify application
        String appId = Amplify.createApp(amplifyClient, appName, "CS:GO Explorer application", Platform.WEB, roleArn,
                cloneUrlHttp, true, true, Stage.PRODUCTION, true, true, amplifyEnvironmentalVariables);
        System.out.println("Successfully created app with id: " + appId);

        // Get app default domain
        String appDefaultDomain = Amplify.getApp(amplifyClient, appId);

        // Close Amplify client
        amplifyClient.close();

        // Authenticate and create a Lambda client
        LambdaClient lambdaClient = Lambda.authenticateLambda(awsBasicCredentials, appRegion);

        // Configure environment variables, so they can be accessible from function code during execution
        Environment environment = Environment.builder().variables(new HashMap<>(){{
            put("ACCESS_KEY_ID", accessKey);
            put("SECRET_ACCESS_KEY", secretAccessKey);
            put("AWS_APP_REGION", appRegion.toString());
            put("DYNAMO_DB_TABLE_NAME", tableName);
            put("SNS_TOPIC_ARN", topicArn);
            put("USER_EMAIL", userEmail);
            put("S3_BUCKET_NAME", s3BucketName);
            put("APP_URL", "https://main." + appDefaultDomain);
            put("STEAM_ID", steamId);
            put("STEAM_API_KEY", steamApiKey);
            put("CS_GO_APP_ID", "730");
        }}).build();

        // Create a lambda function and attach a role
        String lambdaArn = Lambda.createLambdaFunction(lambdaClient, lambdaFunctionName, "CS:GO Explorer application logic",
                roleArn, "handler.AppHandler::handleRequest", Runtime.JAVA11, 180, 512, environment);
        System.out.println("Successfully created lambda function: " + lambdaArn);

        // Create a resource policy and add a resource-based policy statement
        Lambda.createResourcePolicy(lambdaClient, lambdaFunctionName, "chatbot-fulfillment", "lambda:InvokeFunction", "lex.amazonaws.com");

        // Close Lambda client
        lambdaClient.close();

        // Create 'GetAllTableItems' resource
        String getAllTableItemsResourceParentId = ApiGateway.createAndConfigureRestApiResource(apiGatewayClient, restApiId, roleArn, lambdaArn, awsAppDeploymentRegion, "root", "GetAllTableItems", true, "POST", "NONE");

        // Create 'GetPlayerSummaries' resource
        String getPlayerSummariesResourceParentId = ApiGateway.createAndConfigureRestApiResource(apiGatewayClient, restApiId, roleArn, lambdaArn, awsAppDeploymentRegion, getAllTableItemsResourceParentId, "GetPlayerSummaries", true, "GET", "NONE");

        // Create 'GetFriendList' resource
        String getFriendListResourceParentId = ApiGateway.createAndConfigureRestApiResource(apiGatewayClient, restApiId, roleArn, lambdaArn, awsAppDeploymentRegion, getAllTableItemsResourceParentId, "GetFriendList", true, "GET", "NONE");

        // Create 'GetUserStatsForGame' resource
        String getUserStatsForGameResourceParentId = ApiGateway.createAndConfigureRestApiResource(apiGatewayClient, restApiId, roleArn, lambdaArn, awsAppDeploymentRegion, getAllTableItemsResourceParentId, "GetUserStatsForGame", true, "GET", "NONE");

        // Create a deployment stage
        String stageName = "ProductionStage";
        String deploymentId = ApiGateway.createNewDeployment(apiGatewayClient, restApiId, "Created using Java AWS SDK", stageName, "Production deployment stage");
        System.out.println("The id of the REST API deployment: " + deploymentId);

          // Configure and create a usage plan
        ThrottleSettings throttleSettings = ThrottleSettings.builder().rateLimit(100.0).burstLimit(100).build();

        // Create usage plan
        String usagePlanId = ApiGateway.createUsagePlan(apiGatewayClient, restApiId, stageName, throttleSettings, new HashMap<>(){{put("/GetAllTableItems/OPTIONS", throttleSettings);
                    put("/GetAllTableItems/POST", throttleSettings);}}, "test-plan", "DBM test usage plan", "DAY", 1200);

        // Create API key
        ApiGateway.createApiKey(apiGatewayClient, "DBM_key", "Test key", true, usagePlanId, "API_KEY");

        // Close API Gateway client
        apiGatewayClient.close();

        // Authenticate and create a Lex V2 client
        LexModelsV2Client lexModelsV2Client = Lex.authenticateLexV2(awsBasicCredentials, appRegion);

        // Create Lex V2 bot
        String botId = Lex.botConfiguration(lexModelsV2Client, lexRoleArn, lambdaArn, botName, "Helps manage DynamoDB table");
        System.out.println("Successfully created lex bot with ID: " + botId);

        // Close Lex V2 client
        lexModelsV2Client.close();

        // Authenticate and create a DynamoDB client
        DynamoDbClient dynamoDbClient = DynamoDB.authenticateDynamoDB(awsBasicCredentials, appRegion);

        // Create DynamoDB table
        String tableId = DynamoDB.createTable(dynamoDbClient, tableName, "studentId", ScalarAttributeType.N, KeyType.HASH);
        System.out.println("Successfully created " + tableName + " table with id: " + tableId);

        // Close DynamoDB client
        dynamoDbClient.close();

        // Output the website to the user that can be used after
        System.out.println("CS:GO Explorer will be available at https://main." + appDefaultDomain
                + " after the code is committed to the AWS CodeCommit service. Please follow the documentation on how to accomplish it.");
        System.out.println("Use the following HTTPS link to push the code: " + cloneUrlHttp);
    }
}