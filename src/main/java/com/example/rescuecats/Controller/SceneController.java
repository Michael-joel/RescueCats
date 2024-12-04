package com.example.rescuecats.Controller;

import com.example.rescuecats.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneController {


    /**this method is responsible for loading any scene/fxml file into a stage **/
    static public void control(Button button ,String fxmlfile) throws IOException {

        Stage stage = (Stage) button.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxmlfile));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 650);
        stage.setTitle("Rescue Cats ! 😺💣");
        stage.setScene(scene);
        stage.show();
    }
}
