package launcher;

import java.util.HashMap;
import java.util.Random;

import services.ApiGateway;
import services.database.DynamoDB;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.apigateway.ApiGatewayClient;
import software.amazon.awssdk.services.apigateway.model.IntegrationType;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lexmodelsv2.LexModelsV2Client;

import services.IAM;
import services.Lambda;
import services.lexBotConfiguration.Lex;

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
        String lexRoleArn = IAM.createServiceLinkedRole(
                iamClient,
                "lexv2.amazonaws.com",
                lexRoleCustomSuffixGenerator(),
                "DBM Lex V2 Bot Role"
        );

        // Create an IAM Lambda role and attach trust policy
        String roleArn = IAM.createRole(iamClient, roleName);
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
                "Created using Java AWS SDK"
        );

        System.out.println("Successfully created api with id: " + restApiId);

        // Authenticate and create a Lambda client
        LambdaClient lambdaClient = Lambda.authenticateLambda(awsBasicCredentials, appRegion);

        // Create a lambda function and attach a role
        String lambdaArn = Lambda.createLambdaFunction(lambdaClient, lambdaFunctionName, roleArn, accessKey, secretAccessKey, adminEmail, appRegion, restApiId);
        System.out.println("Successfully created lambda function: " + lambdaArn);

        // Create a resource policy and add a resource-based policy statement
        Lambda.createResourcePolicy(lambdaClient, lambdaFunctionName);

        // Close Lambda client
        lambdaClient.close();

        // Authenticate and create a Lex V2 client
        LexModelsV2Client lexModelsV2Client = Lex.authenticateLexV2(awsBasicCredentials, appRegion);

        String botId = Lex.botConfiguration(lexModelsV2Client, lexRoleArn, lambdaArn);
        System.out.println("Successfully created lex bot with ID: " + lambdaArn);

        // Close Lex V2 client
        lexModelsV2Client.close();

        DynamoDbClient dynamoDbClient = DynamoDB.authenticateDynamoDB(awsBasicCredentials, appRegion);

        String createTableResponse = DynamoDB.createTable(dynamoDbClient, "Students", "studentID");
        System.out.println(createTableResponse);

        String resourceId = ApiGateway.createResource(apiGatewayClient, restApiId, 0, "get-all-table-items");

        String methodRequestPOST = ApiGateway.createMethodRequest(apiGatewayClient, restApiId, resourceId, "POST", "NONE", true);
        System.out.println("Successfully created API method request: " + methodRequestPOST);

        String methodRequestOPTIONS = ApiGateway.createMethodRequest(apiGatewayClient, restApiId, resourceId, "OPTIONS", "NONE", true);
        System.out.println("Successfully created API method request: " + methodRequestOPTIONS);

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

        String integrationRequestOPTIONS = ApiGateway.createIntegrationRequest(
                apiGatewayClient,
                restApiId,
                resourceId,
                "OPTIONS",
                roleArn,
                "arn:aws:apigateway:" + awsAppDeploymentRegion + ":lambda:path/2015-03-31/functions/" + lambdaArn + "/invocations",
                IntegrationType.AWS_PROXY,
                "OPTIONS"
        );
        System.out.println("Successfully created API integration request: " + integrationRequestOPTIONS);

        String integrationResponsePOST = ApiGateway.createIntegrationResponse(
                apiGatewayClient,
                restApiId,
                resourceId,
                "POST",
                "200"
        );
        System.out.println("Successfully created API integration response: " + integrationResponsePOST);

        String integrationResponseOPTIONS = ApiGateway.createIntegrationResponse(
                apiGatewayClient,
                restApiId,
                resourceId,
                "OPTIONS",
                "200"
        );
        System.out.println("Successfully created API integration response: " + integrationResponseOPTIONS);

        String methodResponsePOST = ApiGateway.createMethodResponse(
                apiGatewayClient,
                restApiId,
                resourceId,
                "POST",
                "200",
                new HashMap<>(){{put("application/json", "Empty");}}
        );
        System.out.println("Successfully created API method response: " + methodResponsePOST);

        String methodResponseOPTIONS = ApiGateway.createMethodResponse(
                apiGatewayClient,
                restApiId,
                resourceId,
                "OPTIONS",
                "200",
                new HashMap<>(){{put("application/json", "Empty");}}
        );
        System.out.println("Successfully created API method response: " + methodResponseOPTIONS);

        String stageName = "Test";
        String id = ApiGateway.createNewDeployment(
                apiGatewayClient,
                restApiId,
                "Created using Java AWS SDK",
                stageName,
                "Test Deployment"
        );

        System.out.println("The id of the REST API deployment: " + id);

        String usagePlanId = ApiGateway.createUsagePlan(
                apiGatewayClient,
                100.0,
                100,
                "DAY",
                1200,
                restApiId,
                stageName,
                "test-plan",
                "DBM test usage plan"
        );

        ApiGateway.createApiKey(
                apiGatewayClient,
                "DBM_key",
                "Test key",
                true,
                true,
                usagePlanId,
                "API_KEY"
        );

        apiGatewayClient.close();
    }

    /**
     * Creates custom suffix for the service-linked role.
     * @return Custom suffix string.
     */
    private static String lexRoleCustomSuffixGenerator() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 11;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int) (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString().toUpperCase();
    }
}