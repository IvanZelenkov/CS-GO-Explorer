package services.api.steam;

import org.json.simple.JSONObject;
import services.api.ApiGatewayProxyResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class SteamApi {

    /**
     * Returns basic profile information for a list of 64-bit Steam IDs.
     * @param event The Lambda Function event.
     * @return API Gateway proxy response containing status code, headers, body, and base64 encoding enabled.
     */
    public static ApiGatewayProxyResponse getPlayerSummaries(Map<String, Object> event) {
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
            ApiGatewayProxyResponse apiGatewayProxyOptionsResponse = new ApiGatewayProxyResponse.ApiGatewayProxyResponseBuilder()
                    .withStatusCode(200)
                    .withHeaders(responseHeaders)
                    .withBody("CORS preflight request")
                    .withBase64Encoded(true)
                    .build();

            return apiGatewayProxyOptionsResponse;
        }

        String uri = "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=" +
                System.getenv("STEAM_API_KEY") + "&steamids=" + System.getenv("STEAM_ID");

        HttpResponse<String> response = null;
        try {
            // Initiate request.
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .uri(URI.create(uri))
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            // Get response.
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
        responseBody.put("getPlayerSummariesBody", response.body());

        ApiGatewayProxyResponse apiGatewayProxyPostResponse = new ApiGatewayProxyResponse.ApiGatewayProxyResponseBuilder()
                .withStatusCode(200)
                .withHeaders(responseHeaders)
                .withBody(responseBody.toJSONString())
                .withBase64Encoded(true)
                .build();

        return apiGatewayProxyPostResponse;
    }
}
