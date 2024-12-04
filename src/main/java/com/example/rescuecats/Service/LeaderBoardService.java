package com.example.rescuecats.Service;

import com.example.rescuecats.Database.DatabaseManager;
import com.example.rescuecats.Model.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class LeaderBoardService {

    private final String privateCode = "OEIQkNZxPkSw4oEA8-tHAgDfXYAjMEAkaJ67R9iB_gow";
    private final String publicCode = "6744323f8f40bb0e142abe46";
    private String username = AuthenticationService.player.getUsername();
    private String privateURL = "http://dreamlo.com/lb/OEIQkNZxPkSw4oEA8-tHAgDfXYAjMEAkaJ67R9iB_gow";  // API used to add and delete players from the leaderboard
    private String publicURL = "http://dreamlo.com/lb/6744323f8f40bb0e142abe46/json";  // API used to get the leaderboard
    CompletableFuture<HttpResponse<String>> response = null;
    private ArrayList<Player> leaderboardPlayers = new ArrayList<>();
    private static LeaderBoardService instance;

    /** this is a singleton class. getInstance() method is used to create a single instance of this class and allow access to it.
     * singleton pattern is used here because this object is required to be used globally in other classes like LeaderBoardController and LoginController
     * @return instance
     */
    public static LeaderBoardService getInstance() {
        if (instance == null) {
            instance = new LeaderBoardService();
        }
        return instance;
    }

    /** addPlayersToLeaderBoard() retrieves a resultset with records containing the username and highscore of all players from the database.
     * And loops through the resultset's records, where for every record the username and highscore is extracted and passed via the API to be added
     * to the leaderboard. **/
    public void addPlayersToLeaderBoard ()
    {
        String sql="SELECT username,highscore FROM players";

        try(PreparedStatement pstmt=DatabaseManager.prepareStatement(sql))
        {
            ResultSet resultSet= pstmt.executeQuery();
            while (resultSet.next())
            {
                HttpClient client= HttpClient.newHttpClient();
                HttpRequest request=HttpRequest.newBuilder().
                        uri(URI.create(privateURL+"/add/"+resultSet.getString("username")+"/"+resultSet.getString("highscore"))).
                        build();
                client.sendAsync(request,HttpResponse.BodyHandlers.ofString());
            }
            resultSet.close();

        }
        catch (Exception e)
        {
            System.out.println("could not add to leaderboard");
            e.printStackTrace();
        }

    }

    /** fetchPlayers() will make a call to the leaderboard api to return all player arranged according to their highscores**/
    public ArrayList<Player> fetchPlayers ()
    {
        HttpClient client= HttpClient.newHttpClient();
        HttpRequest request=HttpRequest.newBuilder().
                uri(URI.create(publicURL)).
                build();
        try{
            response= client.sendAsync(request,HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> JSON_response = response.join();
            JSONObject response = new JSONObject(JSON_response.body());
            JSONArray entries=response.getJSONObject("dreamlo").getJSONObject("leaderboard").getJSONArray("entry");

            for (int i = 0; i < entries.length(); i++) {
                JSONObject entry = entries.getJSONObject(i);
                Player player=new Player(entry.getString("name"), entry.getInt("score"));
                leaderboardPlayers.add(player);
            }
        }
        catch (Exception e)
        {
            System.out.println("could not create array for leaderboard");
            e.printStackTrace();
        }
        return leaderboardPlayers;

    }


}

