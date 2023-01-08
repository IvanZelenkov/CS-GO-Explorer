package services.api;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.apigateway.ApiGatewayClient;
import software.amazon.awssdk.services.apigateway.model.*;

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
     * Requests API Gateway to create a resource.
     * @param apiGatewayClient Client for accessing Amazon API Gateway.
     * @param apiId The string identifier of the associated RestApi.
     * @param parentResourceIndex Index of the created resource in API (from 0 to n)
     * @param pathPart The last path segment for this resource.
     * @return The parent resource's identifier.
     */
    public static String createResource(ApiGatewayClient apiGatewayClient,
                                        String apiId,
                                        int parentResourceIndex,
                                        String pathPart) {
        try {
            GetResourcesRequest getResourcesRequest = GetResourcesRequest
                    .builder()
                    .restApiId(apiId)
                    .build();

            GetResourcesResponse getResourcesResponse = apiGatewayClient.getResources(getResourcesRequest);

            CreateResourceRequest createResourceRequest = CreateResourceRequest
                    .builder()
                    .restApiId(apiId)
                    .parentId(getResourcesResponse.items().get(parentResourceIndex).id())
                    .pathPart(pathPart)
                    .build();

            CreateResourceResponse createResourceResponse = apiGatewayClient.createResource(createResourceRequest);
            return createResourceResponse.id();
        } catch (ApiGatewayException error) {
            System.err.println(error.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    /**
     * Requests API Gateway to create a method.
     * @param apiGatewayClient Client for accessing Amazon API Gateway.
     * @param apiId The string identifier of the associated RestApi.
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
    public static String createMethodRequest(ApiGatewayClient apiGatewayClient,
                                             String apiId,
                                             String resourceId,
                                             String httpMethod,
                                             String authorizationType,
                                             boolean isApiKeyRequired) {
        try {
            PutMethodRequest putMethodRequest = PutMethodRequest
                    .builder()
                    .restApiId(apiId)
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
    public static String createIntegrationRequest(ApiGatewayClient apiGatewayClient,
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
    public static String createIntegrationResponse(ApiGatewayClient apiGatewayClient,
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
    public static String createMethodResponse(ApiGatewayClient apiGatewayClient,
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
}