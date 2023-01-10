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
 * Launches Database Bot Manager application to the user's AWS account using a single command.
 * @author Ivan Zelenkov
 */
public class AppLauncher {
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

        // Command line arguments
        final String accessKey = args[0];
        final String secretAccessKey = args[1];
        final String awsAppDeploymentRegion = args[2];
        final String adminEmail = args[3];

        // Predefined configuration variables. The creation of services follows this order
        final String roleName = "DatabaseBotManagerRole"; // IAM
        final String permissionsPolicyName = "DatabaseBotManagerFullAccess"; // IAM
        final String restApiName = "database-manager-rest-api"; // API Gateway
        final String s3BucketName = "dynamo-db-students-table-actions"; // S3
        final String snsTopicName = "DynamoStudentsDBTableChanges"; // SNS
        final String lambdaFunctionName = "database-bot-manager-lambda"; // Lambda
        final String botName = "DatabaseManagerBot"; // Lex
        final String tableName = "Students"; // DynamoDB
        final String codeCommitRepositoryName = "DatabaseManagerRepository"; // CodeCommit
        final String appName = "DatabaseManager"; // Amplify

        // IAM region and the region where the AWS application will be deployed
        final Region globalRegion = Region.AWS_GLOBAL;
        final Region appRegion = Region.of(awsAppDeploymentRegion);

        // Create an AWS account credentials instance
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(accessKey, secretAccessKey);

        // Authenticate and create an IAM client
        IamClient iamClient = IAM.authenticateIAM(awsBasicCredentials, globalRegion);

        // Create an IAM Lex V2 role
        String lexRoleArn = IAM.createServiceLinkedRole(
                iamClient,
                "lexv2.amazonaws.com",
                Lex.lexRoleCustomSuffixGenerator(),
                "Database Manager Lex V2 Bot Role"
        );

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

        // Create REST API
        String restApiId = ApiGateway.createAPI(
                apiGatewayClient,
                restApiName,
                "REST API for Database Manager application",
                ApiKeySourceType.AUTHORIZER,
                EndpointConfiguration.builder().types(EndpointType.REGIONAL).build()
        );
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
        String cloneUrlHttp = CodeCommit.createRepository(
                codeCommitClient,
                codeCommitRepositoryName,
                "UI of the DBM application"
        );
        System.out.println("Successfully created repository with clone URL Http: " + cloneUrlHttp);

        // Close CodeCommit client
        codeCommitClient.close();

        // Authenticate and create an Amplify client
        AmplifyClient amplifyClient = Amplify.authenticateAmplify(awsBasicCredentials, appRegion);

        Map<String, String> environmentalVariables = new HashMap<>();
        environmentalVariables.put("BUILD_ENV", "prod");
        environmentalVariables.put("REACT_APP_REST_API_ID", restApiId);

        // Create Amplify application
        String appId = Amplify.createApp(
                amplifyClient,
                appName,
                "Database manager application",
                Platform.WEB,
                "arn:aws:iam::981684844178:role/DatabaseBotManagerRole",
                cloneUrlHttp,
                true,
                true,
                Stage.DEVELOPMENT,
                true,
                true,
                environmentalVariables
        );
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
            put("ADMIN_EMAIL", adminEmail);
            put("S3_BUCKET_NAME", s3BucketName);
            put("APP_URL", "https://main." + appDefaultDomain);
        }}).build();

        // Create a lambda function and attach a role
        String lambdaArn = Lambda.createLambdaFunction(
                lambdaClient,
                lambdaFunctionName,
                "Database Bot Manager Application Logic",
                roleArn,
                "handler.AppHandler::handleRequest",
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

        // 'get-all-table-items' configuration of resources and its methods
        String resourceId = ApiGateway.createResource(apiGatewayClient, restApiId, 0, "get-all-table-items");

        // Create method request for the get-all-table-items/POST
        String methodRequestPOST = ApiGateway.createMethodRequest(
                apiGatewayClient,
                restApiId,
                resourceId,
                "POST",
                "NONE",
                false
        );
        System.out.println("Successfully created API method request: " + methodRequestPOST);

        // Create integration request for the get-all-table-items/POST
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

        // Create integration response for the get-all-table-items/POST
        String integrationResponsePOST = ApiGateway.createIntegrationResponse(
                apiGatewayClient,
                restApiId,
                resourceId,
                "POST",
                "200"
        );
        System.out.println("Successfully created API integration response: " + integrationResponsePOST);

        // Create method response for the get-all-table-items/POST
        String methodResponsePOST = ApiGateway.createMethodResponse(
                apiGatewayClient,
                restApiId,
                resourceId,
                "POST",
                "200",
                new HashMap<>(){{put("application/json", "Empty");}}
        );
        System.out.println("Successfully created API method response: " + methodResponsePOST);

        // Create method request for the get-all-table-items/OPTIONS
        String methodRequestOPTIONS = ApiGateway.createMethodRequest(
                apiGatewayClient,
                restApiId,
                resourceId,
                "OPTIONS",
                "NONE",
                false
        );
        System.out.println("Successfully created API method request: " + methodRequestOPTIONS);

        // Create integration request for the get-all-table-items/OPTIONS
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

        // Create integration response for the get-all-table-items/OPTIONS
        String integrationResponseOPTIONS = ApiGateway.createIntegrationResponse(
                apiGatewayClient,
                restApiId,
                resourceId,
                "OPTIONS",
                "200"
        );
        System.out.println("Successfully created API integration response: " + integrationResponseOPTIONS);

        // Create method response for the get-all-table-items/OPTIONS
        String methodResponseOPTIONS = ApiGateway.createMethodResponse(
                apiGatewayClient,
                restApiId,
                resourceId,
                "OPTIONS",
                "200",
                new HashMap<>(){{put("application/json", "Empty");}}
        );
        System.out.println("Successfully created API method response: " + methodResponseOPTIONS);

        // Create a deployment stage
        String stageName = "DevelopmentStage";
        String deploymentId = ApiGateway.createNewDeployment(
                apiGatewayClient,
                restApiId,
                "Created using Java AWS SDK",
                stageName,
                "Test Deployment"
        );
        System.out.println("The id of the REST API deployment: " + deploymentId);

        // Configure and create a usage plan
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

        // Create API key
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
        System.out.println("Successfully created " + tableName + " table.");

        // Close DynamoDB client
        dynamoDbClient.close();

        // Output the website to the user that can be used after
        System.out.println("The database manager website will be available at https://main." + appDefaultDomain
                + " after the code is committed through the Git control system. Please follow the documentation on how to accomplish it.");
        System.out.println("Use the following HTTPS link to push the code:: " + cloneUrlHttp);
    }
}