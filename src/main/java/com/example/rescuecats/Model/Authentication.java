package com.example.rescuecats.Model;

import com.example.rescuecats.Database.DatabaseManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Authentication {

    private String password;
    private String email;

    /** method to login the player. return true if a player exist else false**/
    public static boolean login(String email,String password)
    {
        String sql="SELECT * FROM players WHERE email=? and password=? ";
        try(PreparedStatement pstmt=DatabaseManager.prepareStatement(sql))
        {
            pstmt.setString(1,email);
            pstmt.setString(2,password);
            ResultSet rs= pstmt.executeQuery();

            if(rs.next())
            {   Player player=new Player(rs.getInt("highscore"));
                System.out.println("an player does exist");
                return true;
            }
            rs.close();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return false;

    }


    /** method to create a user account and add the user to database , return true upon successfully
     * making account. every new player is inserted with a default highscore of zero**/
    public static boolean signup(String email,String password)
    {
        String sql="INSERT INTO players VALUES(?,?,0)";
        try(PreparedStatement pstmt=DatabaseManager.prepareStatement(sql))
        {
            pstmt.setString(1,email);
            pstmt.setString(2,password);
            int rowCount= pstmt.executeUpdate();

            if(rowCount>0)
            {
                System.out.println("account successfully made, go back to login page to play");
                return true;
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

}
