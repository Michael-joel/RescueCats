package com.example.rescuecats.Database;

import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseManager {

    /** this class is responsible for using the JDBC API provided by the java.sql package
     *  to establish a connection to the database and to make a prepared sql statement which will
     *  later be executed against the database.
     *  this class provides reusable methods to connect to database and make a prepared sql
     *  statement */

    /** credentials for accessing the database**/
    private static final String DB_URL = "jdbc:mysql://localhost:3306/rescuecats_db";
    private static final String USER = "root";
    private static final String PASS = "";

    /** the getConnection() method will attempt to load the available JDBC driver.
     * after loading, a connection will be established to given database URL using the driver
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    /** the PreparedStatement method will make connection to database and create
     * and return a prepared statement object that holds the sql statement passed to this method as a
     * argument**/
    public static PreparedStatement prepareStatement(String sql) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            System.out.println("connection successful....");

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert=new Alert(Alert.AlertType.ERROR);
            alert.setContentText("could not connect to database, please try again later");
            alert.setTitle("DATABASE CONNECTION ERROR");
            alert.showAndWait();
            throw e;
        }

        return pstmt;
    }

}



