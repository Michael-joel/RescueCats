package com.example.rescuecats.Model;

import com.example.rescuecats.Database.DatabaseManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Player {

    private int highscore;
    private String playerId;
    private String username;


    public Player(int highscore,String playerId,String username) {
        this.highscore = highscore;
        this.playerId = playerId;
        this.username = username;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getUsername() {
        return username;
    }

    public int getHighscore() {
        return highscore;
    }

    public int updateHighScore(int currentScore)
    {
        if(currentScore>highscore)
        {
            String sql="UPDATE players SET highscore=? WHERE email=?";
            try(PreparedStatement pstmt= DatabaseManager.prepareStatement(sql))
            {
                pstmt.setInt(1,currentScore);
                pstmt.setString(2,playerId);
                int rowCount= pstmt.executeUpdate();

                if(rowCount>0)
                {
                    return getHighscore();
                }
            }
            catch(SQLException e)
            {
                e.printStackTrace();
            }
        }

        return getHighscore();
    }

}
