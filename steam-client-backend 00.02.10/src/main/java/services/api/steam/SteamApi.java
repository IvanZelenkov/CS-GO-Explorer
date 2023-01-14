package services.api.steam;

import java.util.Map;

import org.json.simple.JSONObject;

import java.io.IOException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import services.api.ApiGatewayProxyResponse;

public class SteamApi {

    /**
     * The Steam API router is responsible for calling the correct URI.
     * @param event The Lambda Function event.
     * @param resourceName The name of the resource.
     * @return API Gateway proxy response containing status code, headers, body, and base64 encoding enabled.
     */
    public static ApiGatewayProxyResponse steamApiRouter(Map<String, Object> event, String resourceName) {
        switch (resourceName) {
            case "/GetPlayerSummaries":
                // Returns basic profile information for a list of 64-bit Steam IDs.
                return getSteamApiData(event, "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=" + System.getenv("STEAM_API_KEY") + "&steamids=" + System.getenv("STEAM_ID"));
            case "/GetFriendList":
                // Returns the friend list of any Steam user, provided their Steam Community profile visibility is set to "Public".
                return getSteamApiData(event, "http://api.steampowered.com/ISteamUser/GetFriendList/v0001/?key=" + System.getenv("STEAM_API_KEY") + "&steamid=" + System.getenv("STEAM_ID") + "&relationship=friend");
        }
        return new ApiGatewayProxyResponse();
    }

    /**
     * Get response data from the specified Steam API URI.
     * @param event The Lambda Function event.
     * @return API Gateway proxy response containing status code, headers, body, and base64 encoding enabled.
     */
    public static ApiGatewayProxyResponse getSteamApiData(Map<String, Object> event, String uri) {
        // Handles CORS preflight request
        if (event.get("httpMethod").equals("OPTIONS")) {
            // Response headers
            JSONObject responseHeaders = new JSONObject();
            responseHeaders.put("Access-Control-Allow-Headers", "Content-Type");
//            responseHeaders.put("Access-Control-Allow-Origin", System.getenv("APP_URL"));
            responseHeaders.put("Access-Control-Allow-Origin", "http://localhost:3000");
            responseHeaders.put("Access-Control-Allow-Methods", "OPTIONS, GET");
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

        HttpResponse<String> response = null;
        try {
            // Initiate request
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .uri(URI.create(uri))
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            // Get response
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException error) {
            error.printStackTrace();
        }

        assert response != null;

        // Response headers
        JSONObject responseHeaders = new JSONObject();
        responseHeaders.put("Access-Control-Allow-Headers", "Content-Type");
//        responseHeaders.put("Access-Control-Allow-Origin", System.getenv("APP_URL"));
        responseHeaders.put("Access-Control-Allow-Origin", "http://localhost:3000");
        responseHeaders.put("Access-Control-Allow-Methods", "OPTIONS, GET");
        responseHeaders.put("Access-Control-Allow-Credentials", "true");

        // Response body
        JSONObject responseBody = new JSONObject();
        responseBody.put("body", response.body());

        ApiGatewayProxyResponse apiGatewayProxyPostResponse = new ApiGatewayProxyResponse.ApiGatewayProxyResponseBuilder()
                .withStatusCode(200)
                .withHeaders(responseHeaders)
                .withBody(responseBody.toJSONString())
                .withBase64Encoded(true)
                .build();

        return apiGatewayProxyPostResponse;
    }
}