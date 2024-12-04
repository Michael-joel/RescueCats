package com.example.rescuecats.Controller;

import com.example.rescuecats.Service.AuthenticationService;
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

public class SignupController implements Initializable {
    public VBox signupScreen;
    public TextField signupEmail;
    public TextField signupPassword;
    public Button signupBtn;
    public Button backBtn;
    public TextField signupUsername;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addListeners();
    }

    // attaching event listeners to the buttons
    private void addListeners() {

        signupBtn.setOnAction(actionEvent -> {
            try {
                signup();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        backBtn.setOnAction(actionEvent -> {
            try {
                back();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    //account creation
    private void signup() throws IOException {

        if(AuthenticationService.signup(signupEmail.getText(),signupPassword.getText(),signupUsername.getText())) {
            Notifications notificationBuilder=Notifications.create()
                    .text("Account created, login to play \n\n\n\n")
                    .position(Pos.CENTER)
                    .hideAfter(Duration.seconds(5));
            notificationBuilder.darkStyle();
            notificationBuilder.showInformation();

            SceneController.control(signupBtn, "login-screen.fxml");  //move back to login screen after successfully creating the account
        }
        else
        {
            Notifications notificationBuilder=Notifications.create()   //error handling if signup not successful
                    .text("could not create account ")
                    .position(Pos.CENTER)
                    .hideAfter(Duration.seconds(5));
            notificationBuilder.showError();
        }
    }

    private void back() throws IOException {
        SceneController.control(signupBtn,"login-screen.fxml");     //move back to login screen

    }
}
