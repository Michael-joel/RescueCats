package com.example.rescuecats.Service;

import com.example.rescuecats.Model.Puzzle;
import javafx.scene.image.Image;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class PuzzleService {

    private Image puzzleImage;
    private int solution;
    CompletableFuture<HttpResponse<String>> response = null;


    /** fetchNewPuzzle() will build and call an banana game api request and pass it over the network to
     *  to obtain an json response which will then be parsed using the org.json library
     *  to extract the link to the next puzzle image**/
    public Puzzle fetchNewPuzzle()
    {
        /** code for making API calls was referenced from https://www.tutorialspoint.com/java/java_standard_httpclient.htm */
        HttpClient client= HttpClient.newHttpClient();
        HttpRequest request=HttpRequest.newBuilder().
                uri(URI.create("https://marcconrad.com/uob/banana/api.php?out=json")).
                build();
        try{
            response= client.sendAsync(request,HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> JSON_response = response.join();
            JSONObject response = new JSONObject(JSON_response.body());
            puzzleImage= new Image(response.getString("question"));
            System.out.println(response.getString("question"));
            System.out.println(response.getInt("solution"));
            solution= response.getInt("solution");
        }
        catch (Exception e)
        {
            System.out.println("could not retrieve puzzle");
            e.printStackTrace();
        }

        return new Puzzle(puzzleImage,solution);
    }


}
