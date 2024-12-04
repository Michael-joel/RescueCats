package com.example.rescuecats.Controller;

import com.example.rescuecats.Model.Achievement;
import com.example.rescuecats.Model.Authentication;
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

    ArrayList<Achievement> achievementList=new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addListeners();
        profileHIghScoreLbl.setText(String.valueOf(Authentication.player.getHighscore()));
       AchievementService achievementService=AchievementService.getInstance();
        achievementsUnlockedLbl.setText(String.valueOf(achievementService.getNoOfAchievementsUnlocked()));

        if(achievementList.isEmpty()) {
            achievementList = achievementService.getAchievementsList();
        }else
        {
            achievementList.clear();
        }
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
            achievementBox.getStyleClass().add(achievement.isUnlocked() ? "unlocked" : "locked");
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

    private void addListeners() {
        backBtn.setOnAction(actionevent->{
            try {
                SceneController.control(backBtn,"menu.fxml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }


}
