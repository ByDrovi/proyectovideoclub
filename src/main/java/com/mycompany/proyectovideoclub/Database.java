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
        String url = "jdbc:postgresql://ep-winter-lab-a8dct6d0-pooler.eastus2.azure.neon.tech/neondb?user=neondb_owner&password=npg_xGP9ftMckXw0&sslmode=require";
        return DriverManager.getConnection(url);
    }
    
}
