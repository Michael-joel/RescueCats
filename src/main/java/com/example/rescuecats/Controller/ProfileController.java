package com.example.rescuecats.Controller;

import com.example.rescuecats.Model.Achievement;
import com.example.rescuecats.Service.AuthenticationService;
import com.example.rescuecats.Service.AchievementService;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {

    public Label profileHIghScoreLbl;
    public Label achievementsUnlockedLbl;
    public GridPane achievementsGridPane;
    public Button backBtn;
    public Label playerName;

    ArrayList<Achievement> achievementList=new ArrayList<>(); // list of unlocked and locked achievement of the player

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addListeners();
        profileHIghScoreLbl.setText(String.valueOf(AuthenticationService.player.getHighscore())); // displaying the highscore of logged in player
        playerName.setText(AuthenticationService.player.getUsername());                           // displaying the username of logged in player

        AchievementService achievementService=AchievementService.getInstance();
        achievementsUnlockedLbl.setText(String.valueOf(achievementService.getNoOfAchievementsUnlocked()));  // displaying the no of achievements of logged in player

        if(achievementList.isEmpty()) {
            achievementList = achievementService.getAchievementsList();  //populating the achievement list from the database
        }else
        {
            achievementList.clear();            // clearing the list of old contents before updating it with new information
        }

        //for every achievements in the achievementList, create a box and add description of achievement and add it to the gridpane.
        for(int i = 0; i < achievementList.size(); i++)
        {
            Achievement achievement = achievementList.get(i);

            HBox achievementBox = new HBox();
            achievementBox.setSpacing(10);

            ImageView badgeIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/medal.png")));
            badgeIcon.setFitHeight(50);
            badgeIcon.setFitWidth(50);

            Label description = new Label(achievement.getAchievement_name());
            description.setStyle("-fx-font-size: 15; -fx-font-weight: bold");

            // Apply CSS styles
            achievementBox.getStyleClass().add(achievement.isUnlocked() ? "unlocked" : "locked"); // style of the achievement depends on if its unlocked or not
            achievementBox.getChildren().addAll(badgeIcon, description);

            achievementsGridPane.setHgap(20);
            achievementsGridPane.setVgap(20);
            achievementsGridPane.setPadding(new Insets(20, 20, 20, 20));
            achievementsGridPane.setAlignment(Pos.CENTER);

            int col = i % 2; // 2 achievements per row
            int row = i / 2;
            achievementsGridPane.add(achievementBox, col, row);
        }
    }

    // attaching event listeners to the button
    private void addListeners() {
        backBtn.setOnAction(actionevent->{
            try {
                SceneController.control(backBtn,"menu.fxml");   //move back to menu
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }


}
