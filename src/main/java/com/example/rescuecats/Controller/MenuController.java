package com.example.rescuecats.Controller;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {
    public Button startButton;
    public Button profileButton;
    public Button leaderboardButton;
    public Button exitButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addListeners();
    }

    // attaching event listeners to the buttons
    private void addListeners() {

        startButton.setOnAction(actionEvent -> {
            try {
                start();                            // call start() when clicked
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        profileButton.setOnAction(actionEvent -> {
            try {
                profile();                          // call profile() when clicked
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        leaderboardButton.setOnAction(actionEvent -> {
            try {
                leaderboard();                      // call leaderboard() when clicked
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        exitButton.setOnAction(actionEvent -> quit());

    }


    public void start() throws IOException
    {
        SceneController.control(startButton,"main-game.fxml");          // move/start to the main game
    }

    public void profile() throws IOException
    {
        SceneController.control(profileButton,"profile.fxml");          // move to profile screen
    }

    public void leaderboard() throws IOException
    {
        SceneController.control(leaderboardButton,"leaderboard.fxml");  // move to leaderboard screen
    }

    //terminate program
    public void quit()
    {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

}
