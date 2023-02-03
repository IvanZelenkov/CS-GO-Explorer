package services.api.steam;

import java.util.Map;

import java.io.IOException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import services.api.ApiGateway;
import services.api.ApiGatewayProxyResponse;

public class SteamApi {

    /**
     * The Steam API router is responsible for calling the correct URI.
     * @param event The Lambda Function event.
     * @param resourceName The name of the resource.
     * @return API Gateway proxy response containing status code, headers, body, and base64 encoding enabled.
     */
    public static ApiGatewayProxyResponse steamApiRouter(Map<String, Object> event, String resourceName) {
        Map<String, Object> queryStringParameters = (Map<String, Object>) event.get("queryStringParameters");
        System.out.println("Query String Parameters: " + queryStringParameters);
        String steamId;
        switch (resourceName) {
            case "/GetPlayerSummaries":
                steamId = (String) queryStringParameters.get("steamid");
                // Returns basic profile information for a list of 64-bit Steam IDs.
                return getSteamApiData(event, "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?" +
                        "key=" + System.getenv("STEAM_API_KEY") + "&" +
                        "steamids=" + steamId,
                        "/GetPlayerSummaries");
            case "/GetFriendList":
                steamId = (String) queryStringParameters.get("steamid");
                // Returns the friend list of any Steam user, provided their Steam Community profile visibility is set to "Public".
                return getSteamApiData(event, "http://api.steampowered.com/ISteamUser/GetFriendList/v0001/?" +
                        "key=" + System.getenv("STEAM_API_KEY") + "&" +
                        "steamid=" + steamId + "&" +
                        "relationship=friend", "/GetFriendList");
            case "/GetUserStatsForGame":
                steamId = (String) queryStringParameters.get("steamid");
                // Returns a list of achievements for this user by app id.
                return getSteamApiData(event, "http://api.steampowered.com/ISteamUserStats/GetUserStatsForGame/v0002/?" +
                        "appid=" + System.getenv("CS_GO_APP_ID") + "&key=" + System.getenv("STEAM_API_KEY") + "&" +
                        "steamid=" + steamId, "/GetUserStatsForGame");
            case "/GetCsGoNews":
                // Returns a list of achievements for this user by app id.
                return getSteamApiData(event, "https://api.steampowered.com/ISteamNews/GetNewsForApp/v2?" +
                        "appid=" + System.getenv("CS_GO_APP_ID") + "&key=" + System.getenv("STEAM_API_KEY"), "/GetCsGoNews");
        }
        return new ApiGatewayProxyResponse();
    }

    /**
     * Get response data from the specified Steam API URI.
     * @param event The Lambda Function event.
     * @param resourceName The name of the resource.
     * @return API Gateway proxy response containing status code, headers, body, and base64 encoding enabled.
     */
    public static ApiGatewayProxyResponse getSteamApiData(Map<String, Object> event, String uri, String resourceName) {
        // Handles CORS preflight OPTIONS request
        if (event.get("httpMethod").equals("OPTIONS")) {
            return ApiGateway.generateResponseForOptionsRequest();
        }

        HttpResponse<String> httpResponse;
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
            httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (resourceName.equals("/GetUserStatsForGame")) {
                return ApiGateway.generateResponseForPostOrGetRequest(httpResponse.body());
            } else if (resourceName.equals("/GetPlayerSummaries")) {
                return ApiGateway.generateResponseForPostOrGetRequest(httpResponse.body());
            } else if (resourceName.equals("/GetCsGoNews")) {
                return ApiGateway.generateResponseForPostOrGetRequest(httpResponse.body());
            }else if (resourceName.equals("/GetFriendList")) {
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonResponse = (JSONObject) jsonParser.parse(httpResponse.body());
                JSONObject friendsList = (JSONObject) jsonParser.parse(jsonResponse.get("friendslist").toString());
                JSONArray friends = (JSONArray) jsonParser.parse(friendsList.get("friends").toString());

                StringBuilder profileIds = new StringBuilder();
                for (Object friend : friends) {
                    JSONObject friendSteamId = (JSONObject) jsonParser.parse(friend.toString());
                    if (friend.equals(friends.get(friends.size() - 1))) {
                        profileIds.append(friendSteamId.get("steamid").toString());
                        break;
                    }
                    profileIds.append(friendSteamId.get("steamid").toString()).append(",");
                }

                HttpRequest friendProfileRequest = HttpRequest
                        .newBuilder()
                        .uri(URI.create("http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=" + System.getenv("STEAM_API_KEY") + "&steamids=" + profileIds))
                        .GET()
                        .header("Accept", "application/json")
                        .build();

                HttpResponse<String> friendProfileResponse = client.send(friendProfileRequest, HttpResponse.BodyHandlers.ofString());
                return ApiGateway.generateResponseForPostOrGetRequest(friendProfileResponse.body());
            }
        } catch (IOException | InterruptedException | ParseException error) {
            error.printStackTrace();
        }
        return new ApiGatewayProxyResponse();
    }
}