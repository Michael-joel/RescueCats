package com.example.rescuecats.Service;

import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class CounterService {

    private int solvedPuzzles;
    CompletableFuture<HttpResponse<String>> response = null;
    private String baseURL="https://letscountapi.com/rescuecats/";
    private String jsonBody= """
            {
                "current_value":0
            }
    
            """;
    private String puzzleCounterName="puzzleCounter"+ AuthenticationService.player.getPlayerId();

    /** incrementCounter() wiil call the counter api to increment the existing counter once the player solve a puzzle**/
    public int incrementCounter()
    {
        HttpClient client= HttpClient.newHttpClient();
        HttpRequest request=HttpRequest.newBuilder().
                uri(URI.create(baseURL+puzzleCounterName+"/increment"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        try{
            response= client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> JSON_response = response.join();
            JSONObject response = new JSONObject(JSON_response.body());
            solvedPuzzles= response.getInt("current_value");
        }
        catch (Exception e)
        {
            System.out.println("could not increment counter");
            e.printStackTrace();
        }
        return solvedPuzzles;
    }

    /** ResetCounter() will simply make a call to counter api to update the counter value to value passed in the request body**/
    public int ResetCounter()
    {
        HttpClient client= HttpClient.newHttpClient();
        HttpRequest request=HttpRequest.newBuilder().
                 uri(URI.create(baseURL+puzzleCounterName+"/update"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        try{
            response=client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> JSON_response = response.join();
            JSONObject response = new JSONObject(JSON_response.body());
            solvedPuzzles= response.getInt("current_value");
        }
        catch (Exception e)
        {
            System.out.println("could not reset counter");
            e.printStackTrace();
        }
        return solvedPuzzles;
    }

    /**code for creating a JSON body and sending in the request was referred from
     *  https://kodejava.org/how-do-i-send-post-request-with-a-json-body-using-the-httpclient/
     * and https://www.javaguides.net/2023/03/java-httpclient-post-request-example.html **/

    /** make a new counter for a new player else if c counter exist for the given player then get that counter**/
    public int createCounter()
    {
        HttpClient client= HttpClient.newHttpClient();
        HttpRequest request=HttpRequest.newBuilder().
                uri(URI.create(baseURL+puzzleCounterName))
               .header("Content-Type", "application/json")
               .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
               .build();
        try{
            response=client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> JSON_response = response.join();
            JSONObject response = new JSONObject(JSON_response.body());
            solvedPuzzles= response.getInt("current_value");
        }
        catch (Exception e)
        {
            System.out.println("could not create counter");
            e.printStackTrace();
        }
        return solvedPuzzles;
    }

}
