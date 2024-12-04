package com.example.rescuecats.Controller;

import com.example.rescuecats.Model.Player;
import com.example.rescuecats.Service.LeaderBoardService;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class LeaderBoardController implements Initializable {


    public VBox leaderboardContainer;
    public Button leaderBoardBackBtn;

    private ArrayList<Player> leaderboardPlayers= new ArrayList<>(); //array containing all players on the leaderboard



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addListeners();
        setupLeaderboard();

    }
    // attaching event listeners to the button
    private void addListeners() {
        leaderBoardBackBtn.setOnAction(actionEvent -> {
            try {
                SceneController.control(leaderBoardBackBtn,"menu.fxml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            leaderboardPlayers.clear();

        });
    }

    /** setupLeaderboard() responsible for building the leaderboard UI using the populated leaderboardPlayers list**/
    private void setupLeaderboard()
    {
        leaderboardPlayers=LeaderBoardService.getInstance().fetchPlayers(); // making call to leaderboard API to fetch players according to their highscore and populate the list
        leaderboardContainer.getChildren().clear(); // Clear previous entries

        // for every player in the leaderboardPlayers list create a horizontal box with their name and high score
        for (Player player : leaderboardPlayers) {
            HBox row = new HBox(10);
            row.setStyle("-fx-background-color:#063de3;-fx-background-radius:20;-fx-border-radius:15;-fx-border-color: white;-fx-border-width:3px;-fx-pref-height:30;-fx-pref-width:100;-fx-padding:10");
            Label username = new Label(player.getUsername());
            username.setStyle("-fx-text-fill: white; -fx-font-size: 16;-fx-font-weight:bold;-fx-font-family:Verdana;");

            Label score = new Label(String.valueOf(player.getHighscore()));
            score.setStyle("-fx-text-fill: white; -fx-font-size: 16;-fx-font-weight:bold;-fx-font-family:Verdana;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            row.getChildren().addAll(username,spacer,score);
            leaderboardContainer.getChildren().add(row);

        }


    }
}
