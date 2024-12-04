package com.example.rescuecats.Controller;

import com.example.rescuecats.Service.AuthenticationService;
import com.example.rescuecats.Service.LeaderBoardService;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    public VBox loginScreen;
    public TextField loginPassword;
    public TextField loginEmail;;
    public Button loginBtn;
    public Button signupBtn;
    public Button backBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addlisteners();
    }

    // attaching event listeners to the buttons
    private void addlisteners() {
        loginBtn.setOnAction(actionEvent -> {
            try {
                login();                    // call login() when clicked
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        signupBtn.setOnAction(actionEvent -> {
            try {
                signup();                    // call signup() when clicked
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        backBtn.setOnAction(actionEvent -> {
            try {
                back();                     // call back() when clicked
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void login() throws IOException {

        if(AuthenticationService.login(loginEmail.getText(),loginPassword.getText())) {
            Notifications notificationBuilder=Notifications.create()     // notification builder referenced
                    .text("login successful \n\n\n\n")                   // from https://controlsfx.github.io/javadoc/11.1.2/org.controlsfx.controls/org/controlsfx/control/Notifications.html#styleClass(java.lang.String...)
                    .position(Pos.CENTER)
                    .hideAfter(Duration.seconds(5));
            notificationBuilder.darkStyle();
            notificationBuilder.showInformation();
            LeaderBoardService leaderBoardService=LeaderBoardService.getInstance(); //creating the leaderboard instance after logging in
            new Thread(leaderBoardService::addPlayersToLeaderBoard).start();
            SceneController.control(loginBtn, "menu.fxml");        // move to menu page after successfully logging in
        }
        else
        {
            Notifications notificationBuilder=Notifications.create()      // error handling if login not successful
                    .text("login failed \n\n\n\n")
                    .position(Pos.CENTER)
                    .hideAfter(Duration.seconds(5));
            notificationBuilder.showError();
        }
    }

    private void signup() throws IOException {
        SceneController.control(signupBtn,"signup-screen.fxml");    // load the signup page
    }

    private void back() throws IOException {
        SceneController.control(signupBtn,"loading-screen.fxml");   // go back to loading screen if wish to exit program

    }

}
