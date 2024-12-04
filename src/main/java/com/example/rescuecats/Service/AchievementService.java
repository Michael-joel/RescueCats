package com.example.rescuecats.Service;

import com.example.rescuecats.Database.DatabaseManager;
import com.example.rescuecats.Model.Achievement;
import com.example.rescuecats.Model.Player;
import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AchievementService {

    private ArrayList<Achievement> achievementsList=new ArrayList<>();
    private ArrayList<Achievement> lockedAcheivementsList=new ArrayList<>();
    private Player player= AuthenticationService.player;
    private static AchievementService instance;


    public static AchievementService getInstance() {
        if (instance == null) {
            instance = new AchievementService();
        }
        return instance;
    }

    public AchievementService() {
        this.achievementsList = getAchievements();
    }

    public ArrayList<Achievement> getAchievementsList() {
        return achievementsList;
    }

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
                achievementsList.add(new Achievement(unlocked_achievement_resultset.getInt("achievement_ID"),unlocked_achievement_resultset.getString("name"),true ));
            }
            unlocked_achievement_resultset.close();
            System.out.println("got unlocked ones");
            for(Achievement achievement:achievementsList)
            {
                System.out.println("unlocked"+achievement.getAchievement_ID()+"name"+achievement.getAchievement_name()+"status"+achievement.isUnlocked());
            }

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
                achievementsList.add(new Achievement(locked_achievement_resultset.getInt("achievement_ID"),locked_achievement_resultset.getString("name"),false ));
                lockedAcheivementsList.add(new Achievement(locked_achievement_resultset.getInt("achievement_ID"),locked_achievement_resultset.getString("name"),false ));
            }
            locked_achievement_resultset.close();
            System.out.println("got locked ones");

        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        for(Achievement achievement:achievementsList)
        {
            System.out.println("locked"+achievement.getAchievement_ID()+"name"+achievement.getAchievement_name()+"status"+achievement.isUnlocked());
        }
        return achievementsList;
    }

    public int getNoOfAchievementsUnlocked() {

        return achievementsList.size()-lockedAcheivementsList.size();
    }

    /** alreadyUnlocked() will check if the achievement is within the lockedAcheivementsList, if its not thr it means the player has already
     * unlocked it and it will return false meaning no action needs to be taken, but if it's there in the list then it will return true
     * meaning it has to be unlocked
     * **/
    public boolean alreadyUnlocked(int newAchievementID)
    {
        Achievement unlockedAchievement=null;
        for(Achievement achievement:lockedAcheivementsList)
        {
            if(newAchievementID==achievement.getAchievement_ID())
            {
                unlockedAchievement=lockedAcheivementsList.get(lockedAcheivementsList.indexOf(achievement));
                break;
            }
        }
        if(unlockedAchievement==null)
        {
            return false;
        }
        else
        {
            lockedAcheivementsList.remove(unlockedAchievement);
            return true;
        }

    }

    /** unlockAchievement() will unlock the achievement once the criteria for that achievement has been reached, and insert the newly unlocked
     * achievement to the unlocked_achievements table in the database**/
    public void unlockAchievement(int newAchievementID)
    {
        System.out.println("in ssql");
        Achievement achievement=achievementsList.get(newAchievementID-1);
       String sql="INSERT INTO unlocked_achievements VALUES(?,?)";

       try(PreparedStatement pstmt=DatabaseManager.prepareStatement(sql))
       {
           pstmt.setString(1, player.getPlayerId());
           pstmt.setInt(2,achievement.getAchievement_ID());
           pstmt.executeUpdate();

           System.out.println("unlocked achievement");
           Notifications notificationBuilder=Notifications.create()
                       .title("⭐ UNLOCKED ACHIEVEMENT ⭐")                  // notification builder refrenced
                       .text(achievement.getAchievement_name())                   // from https://controlsfx.github.io/javadoc/11.1.2/org.controlsfx.controls/org/controlsfx/control/Notifications.html#styleClass(java.lang.String...)
                       .position(Pos.CENTER)
                       .hideAfter(Duration.seconds(5));
           notificationBuilder.darkStyle();
           notificationBuilder.showInformation();

           achievement.setUnlocked(true);

       }
       catch (Exception e)
       {
           e.printStackTrace();
       }
    }

}
