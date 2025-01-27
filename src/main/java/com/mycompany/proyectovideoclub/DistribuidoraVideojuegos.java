package com.mycompany.proyectovideoclub;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author oscar.lara
 */

public class DistribuidoraVideojuegos {
    
    private int id;
    private String nombre;

    // Constructor vacío
    public DistribuidoraVideojuegos() {
    }

    // Constructor con parámetros
    public DistribuidoraVideojuegos(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
        // Método para conectar a la base de datos
    public static Connection conectar() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/videoclub";
        String usuario = "root"; 
        String contraseña = "";
        return DriverManager.getConnection(url, usuario, contraseña);
    }
    
        // Consultar todas las distribuidoras
    public static List<DistribuidoraVideojuegos> consultarDistribuidorasVideojuegos(Connection conn) throws SQLException {
        String query = "SELECT * FROM DistribuidorasVideojuegos";
        List<DistribuidoraVideojuegos> distribuidorasVideojuegos = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                distribuidorasVideojuegos.add(new DistribuidoraVideojuegos(
                        rs.getInt("id"),
                        rs.getString("nombre")
                ));
            }
        }
        System.out.println("Distribuidoras de VIDEOJUEGOS disponibles: ");
        return distribuidorasVideojuegos;
    }
    
        public static void agregarDistribuidoraVideojuegos(Connection conn, String nombre) throws SQLException {
        System.out.println("Agregando nueva distribuidora de videojuegos...");
        String query = "INSERT INTO DistribuidorasVideojuegos (nombre) VALUES (?)";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, nombre);
            pstmt.executeUpdate();
            System.out.println("La nueva distribuidora de videojuegos '" + nombre + "' ha sido agregada con éxito.");
        }
    }
        
         public static void consultarDistribuidoraConVideojuegos(Connection conn, String nombreDistribuidora) throws SQLException {
        System.out.println("Consultando distribuidora '" + nombreDistribuidora + "' con sus videojuegos. Espere, por favor...");
        String query = """
            SELECT d.nombre AS distribuidora, p2.titulo AS videojuego
            FROM DistribuidorasVideojuegos d
                LEFT JOIN Videojuegos p ON d.id = p.distribuidora_id
                LEFT JOIN Productos p2 ON p.id = p2.id
            WHERE d.nombre = ?;
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, nombreDistribuidora);

            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("Distribuidora: " + nombreDistribuidora);
                boolean tieneVideojuegos = false;

                while (rs.next()) {
                    String videojuego = rs.getString("videojuego");
                    if (videojuego != null) {
                        tieneVideojuegos = true;
                        System.out.println("  - " + videojuego);
                    }
                }

                if (!tieneVideojuegos) {
                    System.out.println("  No tiene videojuegos asociados.");
                }
            }
        }
    } 
}
