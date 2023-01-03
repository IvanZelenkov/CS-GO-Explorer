package services;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.apigateway.ApiGatewayClient;
import software.amazon.awssdk.services.apigateway.model.*;

import java.util.Map;

public class ApiGateway {

    /**
     * Authenticate to the API Gateway client using the AWS user's credentials.
     * @param awsCredentials The AWS Access Key ID and Secret Access Key are credentials that are used to securely sign requests to AWS services.
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
     * Create a REST API served by API Gateway.
     * @param apiGatewayClient Client for accessing Amazon API Gateway.
     * @return The API's identifier.
     */
    public static String createAPI(ApiGatewayClient apiGatewayClient) {
        EndpointType endpointType = EndpointType.REGIONAL;

        EndpointConfiguration endpointConfiguration = EndpointConfiguration
                .builder()
                .types(endpointType)
                .build();

        try {
            CreateRestApiRequest request = CreateRestApiRequest.builder()
                    .name("database-manager-rest-api")
                    .description("Created using the Gateway Java API")
                    .endpointConfiguration(endpointConfiguration)
                    .build();

            CreateRestApiResponse response = apiGatewayClient.createRestApi(request);
            System.out.println("The id of the new api is " + response.id());
            return response.id();
        } catch (ApiGatewayException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    /**
     * Requests API Gateway to create a resource.
     * @param apiGatewayClient Client for accessing Amazon API Gateway.
     * @param apiId The string identifier of the associated RestApi.
     * @return The parent resource's identifier.
     */
    public static String createResource(ApiGatewayClient apiGatewayClient,
                                        String apiId,
                                        int parentResourceIndex,
                                        String pathPart) {
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
    }

    /**
     * Requests API Gateway to create a method.
     * @param apiGatewayClient Client for accessing Amazon API Gateway.
     * @param apiId The string identifier of the associated RestApi.
     * @param resourceId The Resource identifier for the new Method resource.
     * @return The method request's HTTP method type.
     */
    public static String createMethodRequest(ApiGatewayClient apiGatewayClient,
                                             String apiId,
                                             String resourceId,
                                             String httpMethod,
                                             String authorizationType) {
        PutMethodRequest putMethodRequest = PutMethodRequest
                .builder()
                .restApiId(apiId)
                .resourceId(resourceId)
                .httpMethod(httpMethod)
                .authorizationType(authorizationType)
                .build();

        PutMethodResponse putMethodResponse = apiGatewayClient.putMethod(putMethodRequest);
        return putMethodResponse.toString();
    }

    public static String createIntegrationRequest(ApiGatewayClient apiGatewayClient,
                                                  String restApiId,
                                                  String resourceId,
                                                  String httpMethod,
                                                  String roleArn,
                                                  String uri,
                                                  IntegrationType integrationType,
                                                  String integrationHttpMethod) {
        // Lambda function can only be invoked via POST.
        PutIntegrationRequest putIntegrationRequest = PutIntegrationRequest
                .builder()
                .restApiId(restApiId)
                .resourceId(resourceId)
                .httpMethod(httpMethod)
                .credentials(roleArn)
                .type(integrationType)
                .uri(uri)
                .integrationHttpMethod(integrationHttpMethod)
                .build();

        PutIntegrationResponse putIntegrationResponse = apiGatewayClient.putIntegration(putIntegrationRequest);
        return putIntegrationResponse.toString();
    }

    public static String createIntegrationResponse(ApiGatewayClient apiGatewayClient,
                                                   String restApiId,
                                                   String resourceId,
                                                   String httpMethod,
                                                   String statusCode) {
        PutIntegrationResponseRequest putIntegrationResponseRequest = PutIntegrationResponseRequest
                .builder()
                .restApiId(restApiId)
                .resourceId(resourceId)
                .httpMethod(httpMethod)
                .statusCode(statusCode)
                .build();

        PutIntegrationResponseResponse putIntegrationResponseResponse = apiGatewayClient.putIntegrationResponse(putIntegrationResponseRequest);
        return putIntegrationResponseResponse.toString();
    }

    public static String createMethodResponse(ApiGatewayClient apiGatewayClient,
                                              String restApiId,
                                              String resourceId,
                                              String httpMethod,
                                              String statusCode,
                                              Map<String, String> responseModels) {
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
    }

    public static String createNewDeployment(ApiGatewayClient apiGateway, String restApiId, String stageName) {
        try {
            CreateDeploymentRequest request = CreateDeploymentRequest.builder()
                    .restApiId(restApiId)
                    .description("Created using the AWS API Gateway Java API")
                    .stageName(stageName)
                    .stageDescription("Test Deployment")
                    .build();

            CreateDeploymentResponse response = apiGateway.createDeployment(request);
            return response.id();
        } catch (ApiGatewayException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }
}