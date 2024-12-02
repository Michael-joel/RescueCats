package com.example.rescuecats.Service;

import com.example.rescuecats.Database.DatabaseManager;
import com.example.rescuecats.Model.Achievement;
import com.example.rescuecats.Model.Authentication;
import com.example.rescuecats.Model.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AchievementService {

    private ArrayList<Achievement> acheivementsList=new ArrayList<>();
    private Player player= Authentication.player;



    /** getAchievements() method will retrieve all the unlocked and locked achievements of the logged in player from the database
     * and for every unlocked or locked achievements it will create an achievements object and add them to an achievements list.
     * finally the achievement list will contain achievement objects with their unlocked statuses marked appropriately.
     * this list will then be used to populate the achievement UI section ,showing the unlocked and locked achievements with the help
     * of the unlocked status field within an achievement object**/
    public ArrayList<Achievement> getAchievements()
    {
        String unlocked_achievement_sql="""
                                        SELECT achievements.achievement_ID,achievements.name FROM unlocked_achievements,achievements
                                        where email=? and unlocked_achievements.achievement_ID=achievements.achievement_ID
                                        """;                                                         //sql to get unlocked achievements

        String locked_achievement_sql= """                                                          
                                        SELECT achievements.achievement_ID,achievements.name FROM `achievements`
                                        WHERE achievements.achievement_ID NOT IN (SELECT unlocked_achievements.achievement_ID FROM unlocked_achievements
                                                                                  WHERE email=?
                                                                                  )
                """;                                                                                  //sql to get locked achievements

        //first adding unlocked achievements to the achievements list
        try(PreparedStatement pstmt= DatabaseManager.prepareStatement(unlocked_achievement_sql))
        {
            pstmt.setString(1, player.getPlayerId());
            ResultSet unlocked_achievement_resultset= pstmt.executeQuery();

            while (unlocked_achievement_resultset.next())
            {
                acheivementsList.add(new Achievement(unlocked_achievement_resultset.getInt("achievement_ID"),unlocked_achievement_resultset.getString("name"),true ));
            }
            unlocked_achievement_resultset.close();
            System.out.println("got unlocked ones");
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }

        //now adding locked achievements to the achievements list
        try(PreparedStatement pstmt= DatabaseManager.prepareStatement(locked_achievement_sql))
        {
            pstmt.setString(1, player.getPlayerId());
            ResultSet locked_achievement_resultset= pstmt.executeQuery();

            while (locked_achievement_resultset.next())
            {
                acheivementsList.add(new Achievement(locked_achievement_resultset.getInt("achievement_ID"),locked_achievement_resultset.getString("name"),false ));
            }
            locked_achievement_resultset.close();
            System.out.println("got locked ones");

        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }

        return acheivementsList;

    }




}
