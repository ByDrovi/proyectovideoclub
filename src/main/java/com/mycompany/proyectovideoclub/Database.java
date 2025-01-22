package com.mycompany.proyectovideoclub;

/**
 *
 * @author oscar.lara
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/videoclub";
        String user = "root"; // 
        String password = ""; // 
        return DriverManager.getConnection(url, user, password);
    }
    
}
