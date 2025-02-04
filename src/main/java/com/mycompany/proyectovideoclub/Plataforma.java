package com.mycompany.proyectovideoclub;

import java.sql.Connection;
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

public class Plataforma {
    private int id;
    private String nombre;
    
    // Constructor vacío
    public Plataforma() {
    }
 
    // Constructor con parámetros
    public Plataforma(int id, String nombre) {
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


     public static List<Plataforma> consultarPlataformasVideojuegos(Connection conn) throws SQLException {
        String query = "SELECT * FROM Plataformas";
        List<Plataforma> plataformaVideojuegos = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                plataformaVideojuegos.add(new Plataforma(
                        rs.getInt("id"),
                        rs.getString("nombre")
                ));
            }
        }
        System.out.println("Plataformas de videojuegos disponibles");
        return plataformaVideojuegos;
    }
     
         public static void agregarPlataformaVideojuegos(Connection conn, String nuevaPlataforma) throws SQLException {
        System.out.println("Agregando una nueva plataforma de videojuegos");
        String query = "INSERT INTO Plataformas (nombre) VALUES (?)";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, nuevaPlataforma);
            pstmt.executeUpdate();
            System.out.println("La nueva distribuidora de películas '" + nuevaPlataforma + "' ha sido agregada con éxito.");
        }
    }
    
          public static void consultarPlataformasConVideojuegos(Connection conn, String nombrePlataforma) throws SQLException {
        System.out.println("Consultando plataforma '" + nombrePlataforma + "' con sus videojuegos. Espere, por favor...");
        String query = """
            SELECT d.nombre AS plataforma, p2.titulo AS videojuego
            FROM Plataformas d
                LEFT JOIN Videojuegos p ON d.id = p.plataforma_id
                LEFT JOIN Productos p2 ON p.id = p2.id
            WHERE d.nombre = ?;
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, nombrePlataforma);

            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("Plataforma: " + nombrePlataforma);
                boolean tieneVideojuegos = false;

                while (rs.next()) {
                    String videojuego = rs.getString("videojuego");
                    if (videojuego != null) {
                        tieneVideojuegos = true;
                        System.out.println("  - " + videojuego);
                    }
                }

                if (!tieneVideojuegos) {
                    System.out.println("  No hay videojuegos asociados a esta plataforma ");
                }
            }
        }
    }
}

