package com.mycompany.proyectovideoclub;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author oscar.lara
 */

public class Database {
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/videoclub";
        String user = "root"; // 
        String password = ""; // 
        return DriverManager.getConnection(url, user, password);
    }
    
}
