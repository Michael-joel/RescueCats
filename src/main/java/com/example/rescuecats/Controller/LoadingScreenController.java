package com.example.rescuecats.Controller;

import com.example.rescuecats.Main;
import com.example.rescuecats.Model.Authentication;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class LoadingScreenController implements Initializable {

    public VBox loadingScreen;
    public ProgressBar progressBar;
    public Label loadingPercent;
    public Label loadLabel;
    public Button logInToPlayBtn;
    public Button exitBtn;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        increaseProgress();
        addListeners();
    }

    public void increaseProgress()
    {
        // Timeline for animating progress
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.1), event -> {
                    if (progressBar.getProgress() < 1) {
                        progressBar.setProgress(progressBar.getProgress() + 0.04);
                        loadingPercent.setText(String.valueOf((int)Math.round(progressBar.getProgress()*100))+"%");
                    }
                    else {
                        progressBar.setVisible(false);
                        loadLabel.setVisible(false);
                        loadingPercent.setVisible(false);

                        logInToPlayBtn.setVisible(true);
                        exitBtn.setVisible(true);
                    }
                })
        );
        timeline.setCycleCount(100); // 100 increments of 1%
        timeline.play();
    }

    public void addListeners()
    {
        exitBtn.setOnAction(actionEvent -> quit());
        logInToPlayBtn.setOnAction(actionEvent -> {
            try {
                login();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void quit()
    {
        Stage stage = (Stage) exitBtn.getScene().getWindow();
        stage.close();
    }

    public void login() throws IOException
    {
        SceneController.control(logInToPlayBtn,"login-screen.fxml");
    }

}