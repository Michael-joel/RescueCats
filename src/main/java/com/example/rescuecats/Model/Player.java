package com.example.rescuecats.Model;

import com.example.rescuecats.Database.DatabaseManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Player {

    private int highscore;
    private String playerId;


    public Player(int highscore,String playerId) {
        this.highscore = highscore;
        this.playerId = playerId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public int getHighscore() {
        return highscore;
    }

    public void setHighscore(int highscore) {
        this.highscore = highscore;
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
