package services.api;

import org.json.simple.JSONObject;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.internal.waiters.DefaultWaiter;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.apigateway.ApiGatewayClient;
import software.amazon.awssdk.services.apigateway.model.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Amazon API Gateway is an AWS service for creating, publishing, maintaining, monitoring, and securing REST, HTTP,
 * and WebSocket APIs at any scale. API developers can create APIs that access AWS or other web services, as well as
 * data stored in the AWS Cloud.
 */
public class ApiGateway {

    /**
     * Authenticate to the API Gateway client using the AWS user's credentials.
     * @param awsCredentials The AWS Access Key ID and Secret Access Key are credentials that are used to securely sign requests to AWS services.
     * @param appRegion The AWS Region where the service will be hosted.
     * @return Service client for accessing API Gateway.
     */
    public static ApiGatewayClient authenticateApiGateway(AwsBasicCredentials awsCredentials, Region appRegion) {
        return ApiGatewayClient
                .builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .region(appRegion)
                .build();
    }

    /**
     * Create an API served by API Gateway.
     * @param apiGatewayClient Client for accessing Amazon API Gateway.
     * @param apiName The name of the API.
     * @param apiDescription The description of the API.
     * @param apiKeySourceType The source of the API key for metering requests according to a usage plan.
     *                         Valid values are: HEADER to read the API key from the X-API-Key header of a request.
     *                         AUTHORIZER to read the API key from the UsageIdentifierKey from a custom authorizer.
     * @param endpointConfiguration The EndpointConfiguration property type specifies the endpoint types of a REST API.
     *                              A list of endpoint types of an API or its custom domain name. Valid values include:
     *                              EDGE: For an edge-optimized API and its custom domain name.
     *                              REGIONAL: For a regional API and its custom domain name.
     *                              PRIVATE: For a private API.
     * @return The API's identifier.
     */
    public static String createAPI(ApiGatewayClient apiGatewayClient,
                                   String apiName,
                                   String apiDescription,
                                   ApiKeySourceType apiKeySourceType,
                                   EndpointConfiguration endpointConfiguration) {
        try {
            CreateRestApiRequest request = CreateRestApiRequest.builder()
                    .name(apiName)
                    .description(apiDescription)
                    .apiKeySource(apiKeySourceType)
                    .endpointConfiguration(endpointConfiguration)
                    .build();

            CreateRestApiResponse response = apiGatewayClient.createRestApi(request);
            return response.id();
        } catch (ApiGatewayException error) {
            System.err.println(error.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    /**
     * Creates and configures a REST API resource.
     * @param apiGatewayClient Client for accessing Amazon API Gateway.
     * @param restApiId The string identifier of the associated RestApi.
     * @param roleArn Role ARN.
     * @param lambdaArn Lambda function ARN.
     * @param awsAppDeploymentRegion The app deployment region.
     * @param parentId The parent resource's identifier.
     * @param resourceName The name of the resource.
     * @param isPreflightRequest Is the use of the HTTP method needs a preflight request.
     * @param methodName Specifies the method request's HTTP method type.
     * @param authorizationType The method's authorization type. Valid values are
     *                          NONE for open access,
     *                          AWS_IAM for using AWS IAM permissions,
     *                          CUSTOM for using a custom authorizer, or
     *                          COGNITO_USER_POOLS for using a Cognito user pool.
     * @return Parent ID.
     */
    public static String createAndConfigureRestApiResource(ApiGatewayClient apiGatewayClient,
                                                         String restApiId,
                                                         String roleArn,
                                                         String lambdaArn,
                                                         String awsAppDeploymentRegion,
                                                         String parentId,
                                                         String resourceName,
                                                         boolean isPreflightRequest,
                                                         String methodName,
                                                         String authorizationType) {
        // Create a resource
        Map<String, String> createResourceResponse = ApiGateway.createResource(apiGatewayClient, restApiId, parentId, resourceName);

        if (isPreflightRequest) {
            // Create method request for the resourceName/OPTIONS
            String methodRequestOPTIONS = ApiGateway.createMethodRequest(apiGatewayClient, restApiId, createResourceResponse.get("resourceId"), "OPTIONS", authorizationType, false);
            System.out.println("Successfully created API method request: " + methodRequestOPTIONS);

            // Create integration request for the resourceName/OPTIONS
            String integrationRequestOPTIONS = ApiGateway.createIntegrationRequest(apiGatewayClient, restApiId, createResourceResponse.get("resourceId"), "OPTIONS", roleArn,
                    "arn:aws:apigateway:" + awsAppDeploymentRegion + ":lambda:path/2015-03-31/functions/" + lambdaArn + "/invocations", IntegrationType.AWS_PROXY, "POST");
            System.out.println("Successfully created API integration request: " + integrationRequestOPTIONS);

            // Create integration response for the resourceName/OPTIONS
            String integrationResponseOPTIONS = ApiGateway.createIntegrationResponse(apiGatewayClient, restApiId, createResourceResponse.get("resourceId"), "OPTIONS", "200");
            System.out.println("Successfully created API integration response: " + integrationResponseOPTIONS);

            // Create method response for the resourceName/OPTIONS
            String methodResponseOPTIONS = ApiGateway.createMethodResponse(apiGatewayClient, restApiId, createResourceResponse.get("resourceId"), "OPTIONS", "200", new HashMap<>(){{put("application/json", "Empty");}});
            System.out.println("Successfully created API method response: " + methodResponseOPTIONS);
        }

        // Create method request for the resourceName/POST
        String methodRequest = ApiGateway.createMethodRequest(apiGatewayClient, restApiId, createResourceResponse.get("resourceId"), methodName, authorizationType, false);
        System.out.println("Successfully created API method request: " + methodRequest);

        // Create integration request for the resourceName/POST
        String integrationRequest = ApiGateway.createIntegrationRequest(apiGatewayClient, restApiId, createResourceResponse.get("resourceId"), methodName, roleArn,
                "arn:aws:apigateway:" + awsAppDeploymentRegion + ":lambda:path/2015-03-31/functions/" + lambdaArn + "/invocations", IntegrationType.AWS_PROXY, "POST");
        System.out.println("Successfully created API integration request: " + integrationRequest);

        // Create integration response for the resourceName/POST
        String integrationResponse = ApiGateway.createIntegrationResponse(apiGatewayClient, restApiId, createResourceResponse.get("resourceId"), methodName, "200");
        System.out.println("Successfully created API integration response: " + integrationResponse);

        // Create method response for the resourceName/POST
        String methodResponse = ApiGateway.createMethodResponse(apiGatewayClient, restApiId, createResourceResponse.get("resourceId"), methodName, "200", new HashMap<>(){{put("application/json", "Empty");}});
        System.out.println("Successfully created API method response: " + methodResponse);

        return createResourceResponse.get("parentId");
    }

    /**
     * Requests API Gateway to create a resource.
     * @param apiGatewayClient Client for accessing Amazon API Gateway.
     * @param restApiId The string identifier of the associated RestApi.
     * @param parentId The parent resource's identifier.
     * @param pathPart The last path segment for this resource.
     * @return Resource ID and parent ID.
     */
    private static HashMap<String, String> createResource(ApiGatewayClient apiGatewayClient,
                                        String restApiId,
                                        String parentId,
                                        String pathPart) {
        try {
            GetResourcesRequest getResourcesRequest = GetResourcesRequest
                    .builder()
                    .restApiId(restApiId)
                    .build();

            GetResourcesResponse getResourcesResponse;
            CreateResourceRequest createResourceRequest;
            if (parentId.equals("root")) {
                getResourcesResponse = apiGatewayClient.getResources(getResourcesRequest);
                createResourceRequest = CreateResourceRequest
                        .builder()
                        .restApiId(restApiId)
                        .parentId(getResourcesResponse.items().get(0).id())
                        .pathPart(pathPart)
                        .build();
            } else {
                createResourceRequest = CreateResourceRequest
                        .builder()
                        .restApiId(restApiId)
                        .parentId(parentId)
                        .pathPart(pathPart)
                        .build();
            }

            CreateResourceResponse createResourceResponse = apiGatewayClient.createResource(createResourceRequest);
            return new HashMap<>(){{
                put("resourceId", createResourceResponse.id());
                put("parentId", createResourceResponse.parentId());
            }};
        } catch (ApiGatewayException error) {
            System.err.println(error.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return new HashMap<>();
    }

    /**
     * Requests API Gateway to create a method.
     * @param apiGatewayClient Client for accessing Amazon API Gateway.
     * @param restApiId The string identifier of the associated RestApi.
     * @param resourceId The Resource identifier for the new Method resource.
     * @param httpMethod Specifies the method request's HTTP method type.
     * @param authorizationType The method's authorization type. Valid values are
     *                          NONE for open access,
     *                          AWS_IAM for using AWS IAM permissions,
     *                          CUSTOM for using a custom authorizer, or
     *                          COGNITO_USER_POOLS for using a Cognito user pool.
     * @param isApiKeyRequired Specifies whether the method required a valid ApiKey.
     * @return The method request's HTTP method type.
     */
    private static String createMethodRequest(ApiGatewayClient apiGatewayClient,
                                             String restApiId,
                                             String resourceId,
                                             String httpMethod,
                                             String authorizationType,
                                             boolean isApiKeyRequired) {
        try {
            PutMethodRequest putMethodRequest = PutMethodRequest
                    .builder()
                    .restApiId(restApiId)
                    .resourceId(resourceId)
                    .httpMethod(httpMethod)
                    .authorizationType(authorizationType)
                    .apiKeyRequired(isApiKeyRequired)
                    .build();

            PutMethodResponse putMethodResponse = apiGatewayClient.putMethod(putMethodRequest);
            return putMethodResponse.toString();
        } catch (ApiGatewayException error) {
            System.err.println(error.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    /**
     * Sets up a method's integration.
     * @param apiGatewayClient Client for accessing Amazon API Gateway.
     * @param restApiId The string identifier of the associated RestApi.
     * @param resourceId Specifies a put integration request's resource ID.
     * @param httpMethod Specifies the HTTP method for the integration.
     * @param roleArn Specifies whether credentials are required for a put integration.
     * @param uri Specifies Uniform Resource Identifier (URI) of the integration endpoint.
     * @param integrationType Specifies an API method integration type. The valid value is one of the following:
     *                        HTTP | AWS | MOCK | HTTP_PROXY | AWS_PROXY
     * @param integrationHttpMethod Integration HTTP method. Required if type is AWS, AWS_PROXY, HTTP or HTTP_PROXY.
     *                              Not all methods are compatible with all AWS integrations. e.g., Lambda function
     *                              can only be invoked via POST.
     * @return Information representing a put integration request.
     */
    private static String createIntegrationRequest(ApiGatewayClient apiGatewayClient,
                                                  String restApiId,
                                                  String resourceId,
                                                  String httpMethod,
                                                  String roleArn,
                                                  String uri,
                                                  IntegrationType integrationType,
                                                  String integrationHttpMethod) {
        try {
            PutIntegrationRequest putIntegrationRequest = PutIntegrationRequest
                    .builder()
                    .restApiId(restApiId)
                    .resourceId(resourceId)
                    .httpMethod(httpMethod)
                    .credentials(roleArn)
                    .uri(uri)
                    .type(integrationType)
                    .integrationHttpMethod(integrationHttpMethod)
                    .build();

            PutIntegrationResponse putIntegrationResponse = apiGatewayClient.putIntegration(putIntegrationRequest);
            return putIntegrationResponse.toString();
        } catch (ApiGatewayException error) {
            System.err.println(error.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    /**
     *
     * @param apiGatewayClient Client for accessing Amazon API Gateway.
     * @param restApiId The string identifier of the associated RestApi.
     * @param resourceId Specifies a put integration response request's resource identifier.
     * @param httpMethod Specifies a put integration response request's HTTP method.
     * @param statusCode Specifies the status code that is used to map the integration response to an existing MethodResponse.
     * @return Information representing a put integration response.
     */
    private static String createIntegrationResponse(ApiGatewayClient apiGatewayClient,
                                                   String restApiId,
                                                   String resourceId,
                                                   String httpMethod,
                                                   String statusCode) {
        try {
            PutIntegrationResponseRequest putIntegrationResponseRequest = PutIntegrationResponseRequest
                    .builder()
                    .restApiId(restApiId)
                    .resourceId(resourceId)
                    .httpMethod(httpMethod)
                    .statusCode(statusCode)
                    .build();

            PutIntegrationResponseResponse putIntegrationResponseResponse = apiGatewayClient.putIntegrationResponse(putIntegrationResponseRequest);
            return putIntegrationResponseResponse.toString();
        } catch (ApiGatewayException error) {
            System.err.println(error.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    /**
     * Adds a MethodResponse to an existing Method resource.
     * @param apiGatewayClient Client for accessing Amazon API Gateway.
     * @param restApiId The string identifier of the associated RestApi.
     * @param resourceId The Resource identifier for the Method resource.
     * @param httpMethod The HTTP verb of the Method resource.
     * @param statusCode The method response's status code.
     * @param responseModels Specifies the Model resources used for the response's content type.
     *                       Response models are represented as a key/value map, with a content
     *                       type as the key and a Model name as the value.
     * @return Information representing a put method request.
     */
    private static String createMethodResponse(ApiGatewayClient apiGatewayClient,
                                              String restApiId,
                                              String resourceId,
                                              String httpMethod,
                                              String statusCode,
                                              Map<String, String> responseModels) {
        try {
            PutMethodResponseRequest putMethodResponseRequest = PutMethodResponseRequest
                    .builder()
                    .restApiId(restApiId)
                    .resourceId(resourceId)
                    .httpMethod(httpMethod)
                    .statusCode(statusCode)
                    .responseModels(responseModels)
                    .build();

            PutMethodResponseResponse putMethodResponseResponse = apiGatewayClient.putMethodResponse(putMethodResponseRequest);
            return putMethodResponseResponse.toString();
        } catch (ApiGatewayException error) {
            System.err.println(error.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    /**
     * Creates a Deployment resource, which makes a specified RestApi callable over the internet.
     * @param apiGatewayClient Client for accessing Amazon API Gateway.
     * @param restApiId The string identifier of the associated RestApi.
     * @param description The description for the Deployment resource to create.
     * @param stageName The name of the Stage resource for the Deployment resource to create.
     * @param stageDescription The description of the Stage resource for the Deployment resource to create.
     * @return The identifier for the deployment resource.
     */
    public static String createNewDeployment(ApiGatewayClient apiGatewayClient,
                                             String restApiId,
                                             String description,
                                             String stageName,
                                             String stageDescription) {
        try {
            CreateDeploymentRequest request = CreateDeploymentRequest.builder()
                    .restApiId(restApiId)
                    .description(description)
                    .stageName(stageName)
                    .stageDescription(stageDescription)
                    .build();

            CreateDeploymentResponse response = apiGatewayClient.createDeployment(request);
            return response.id();
        } catch (ApiGatewayException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    /**
     * Gets information about a Deployment resource.
     * @param apiGatewayClient Client for accessing Amazon API Gateway.
     * @param restApiId The string identifier of the associated RestApi.
     * @param deploymentId The identifier of the Deployment resource to get information about.
     * @return The identifier for the deployment resource.
     */
    private static String getDeployment(ApiGatewayClient apiGatewayClient, String restApiId, String deploymentId) {
        try {
            GetDeploymentRequest getDeploymentRequest = GetDeploymentRequest
                    .builder()
                    .restApiId(restApiId)
                    .deploymentId(deploymentId)
                    .build();

            GetDeploymentResponse getDeploymentResponse = apiGatewayClient.getDeployment(getDeploymentRequest);
            return getDeploymentResponse.id();
        } catch (ApiGatewayException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    /**
     * Creates a usage plan with the throttle and quota limits, as well as the associated API stages, specified in the payload.
     * @param apiGatewayClient Client for accessing Amazon API Gateway.
     * @param restApiId API id of the associated API stage in a usage plan.
     * @param stageName API stage name of the associated API stage in a usage plan.
     * @param throttleSettings The API request rate limits.
     * @param throttle The throttling limits of the usage plan.
     * @param usagePlanName The name of the usage plan.
     * @param usagePlanDescription The description of the usage plan.
     * @param quotaPeriod The time period in which the limit applies. Valid values are "DAY", "WEEK" or "MONTH".
     * @param quotaLimit The target maximum number of requests that can be made in a given time period.
     * @return The identifier of a UsagePlan resource.
     */
    public static String createUsagePlan(ApiGatewayClient apiGatewayClient,
                                         String restApiId,
                                         String stageName,
                                         ThrottleSettings throttleSettings,
                                         Map<String, ThrottleSettings> throttle,
                                         String usagePlanName,
                                         String usagePlanDescription,
                                         String quotaPeriod,
                                         int quotaLimit) {
        try {
            QuotaSettings quotaSettings = QuotaSettings
                    .builder()
                    .period(quotaPeriod)
                    .limit(quotaLimit)
                    .build();

            ApiStage apiStage = ApiStage
                    .builder()
                    .apiId(restApiId)
                    .stage(stageName)
                    .throttle(throttle)
                    .build();

             CreateUsagePlanRequest createUsagePlanRequest = CreateUsagePlanRequest
                     .builder()
                     .name(usagePlanName)
                     .description(usagePlanDescription)
                     .throttle(throttleSettings)
                     .quota(quotaSettings)
                     .apiStages(apiStage)
                     .build();

             CreateUsagePlanResponse createUsagePlanResponse = apiGatewayClient.createUsagePlan(createUsagePlanRequest);
             return createUsagePlanResponse.id();
        } catch (ApiGatewayException error) {
            System.out.println(error.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    /**
     * Create an ApiKey resource.
     * @param apiGatewayClient Client for accessing Amazon API Gateway.
     * @param keyName The name of the ApiKey.
     * @param description The description of the ApiKey.
     * @param isEnabled Specifies whether the ApiKey can be used by callers.
     * @param usagePlanId The id of the UsagePlan resource representing the usage plan containing
     *                    the to-be-created UsagePlanKey resource representing a plan customer.
     * @param keyType The type of the UsagePlanKey resource for a plan customer.
     */
    public static void createApiKey(ApiGatewayClient apiGatewayClient,
                                    String keyName,
                                    String description,
                                    boolean isEnabled,
                                    String usagePlanId,
                                    String keyType) {
        try {
            CreateApiKeyRequest createApiKeyRequest = CreateApiKeyRequest.builder()
                    .name(keyName)
                    .description(description)
                    .enabled(isEnabled)
                    .build();

            // Creating a api key
            CreateApiKeyResponse createApiKeyResponse = apiGatewayClient.createApiKey(createApiKeyRequest);

            // If we have a plan for the api keys, we can set it for the created api key
            CreateUsagePlanKeyRequest createUsagePlanKeyRequest = CreateUsagePlanKeyRequest.builder()
                    .usagePlanId(usagePlanId)
                    .keyId(createApiKeyResponse.id())
                    .keyType(keyType)
                    .build();

            apiGatewayClient.createUsagePlanKey(createUsagePlanKeyRequest);
        } catch (ApiGatewayException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    /**
     * Formats an API Gateway proxy response that will be sent to the front-end by adding a response body from the called OPTIONS request.
     * @return API Gateway proxy response containing status code, headers, body, and base64 encoding enabled.
     */
    public static ApiGatewayProxyResponse generateResponseForOptionsRequest() {
        // Response headers
        JSONObject responseHeaders = new JSONObject();
        responseHeaders.put("Access-Control-Allow-Headers", "Content-Type");
        responseHeaders.put("Access-Control-Allow-Origin", System.getenv("APP_URL"));
//        responseHeaders.put("Access-Control-Allow-Origin", "http://localhost:3000");
        responseHeaders.put("Access-Control-Allow-Methods", "OPTIONS");
        responseHeaders.put("Access-Control-Allow-Credentials", "true");

        // Response
        ApiGatewayProxyResponse apiGatewayProxyOptionsResponse = new ApiGatewayProxyResponse
                .ApiGatewayProxyResponseBuilder()
                .withStatusCode(200)
                .withHeaders(responseHeaders)
                .withBody("CORS preflight request")
                .withBase64Encoded(true)
                .build();

        return apiGatewayProxyOptionsResponse;
    }

    /**
     * Formats an API Gateway proxy response that will be sent to the front-end by adding a response body from the called POST or GET request.
     * @param httpResponse An HTTP Response contains a response status code, headers, and body that have been received.
     * @return API Gateway proxy response containing status code, headers, body, and base64 encoding enabled.
     */
    public static ApiGatewayProxyResponse generateResponseForPostOrGetRequest(String httpResponse) {
        // Response headers
        JSONObject responseHeaders = new JSONObject();
        responseHeaders.put("Access-Control-Allow-Headers", "Content-Type");
        responseHeaders.put("Access-Control-Allow-Origin", System.getenv("APP_URL"));
//        responseHeaders.put("Access-Control-Allow-Origin", "http://localhost:3000");
        responseHeaders.put("Access-Control-Allow-Methods", "OPTIONS, POST, GET");
        responseHeaders.put("Access-Control-Allow-Credentials", "true");

        // Response body
        JSONObject responseBody = new JSONObject();
        responseBody.put("body", httpResponse);

        ApiGatewayProxyResponse apiGatewayProxyPostResponse = new ApiGatewayProxyResponse
                .ApiGatewayProxyResponseBuilder()
                .withStatusCode(200)
                .withHeaders(responseHeaders)
                .withBody(responseBody.toJSONString())
                .withBase64Encoded(true)
                .build();

        return apiGatewayProxyPostResponse;
    }
}