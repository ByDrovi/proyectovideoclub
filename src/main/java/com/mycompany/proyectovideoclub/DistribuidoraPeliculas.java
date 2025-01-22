package com.mycompany.proyectovideoclub;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author oscar.lara
 */

public class DistribuidoraPeliculas {
    private int id;
    private String nombre;

    // Constructor vacío
    public DistribuidoraPeliculas() {}

    // Constructor con parámetros
    public DistribuidoraPeliculas(int id, String nombre) {
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
    public static List<DistribuidoraPeliculas> consultarDistribuidorasPeliculas(Connection conn) throws SQLException {
        String query = "SELECT * FROM DistribuidorasPeliculas";
        List<DistribuidoraPeliculas> distribuidorasPeliculas = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                distribuidorasPeliculas.add(new DistribuidoraPeliculas(
                        rs.getInt("id"),
                        rs.getString("nombre")
                ));
            }
        }
        System.out.println("Distribuidoras de PELÍCULAS disponibles: ");
        return distribuidorasPeliculas;
    }

    // Agregar una nueva distribuidora
    public static void agregarDistribuidoraPeliculas(Connection conn, String nuevaDistribuidoraPeliculas) throws SQLException {
        System.out.println("Agregando una nueva distribuidora de películas...");
        String query = "INSERT INTO DistribuidorasPeliculas (nombre) VALUES (?)";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, nuevaDistribuidoraPeliculas);
            pstmt.executeUpdate();
            System.out.println("La nueva distribuidora de películas '" + nuevaDistribuidoraPeliculas + "' ha sido agregada con éxito.");
        }
    }

    // Consultar una distribuidora específica junto con todas sus películas
    public static void consultarDistribuidoraConPeliculas(Connection conn, String nombreDistribuidora) throws SQLException {
        System.out.println("Consultando distribuidora '" + nombreDistribuidora + "' con sus películas.. Espere, por favor...");
        String query = """
            SELECT d.nombre AS distribuidora, p2.titulo AS pelicula
            FROM DistribuidorasPeliculas d
                LEFT JOIN Peliculas p ON d.id = p.distribuidora_id
                LEFT JOIN Productos p2 ON p.id = p2.id
            WHERE d.nombre = ?;
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, nombreDistribuidora);

            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("Distribuidora: " + nombreDistribuidora);
                boolean tienePeliculas = false;

                while (rs.next()) {
                    String pelicula = rs.getString("pelicula");
                    if (pelicula != null) {
                        tienePeliculas = true;
                        System.out.println("  - " + pelicula);
                    }
                }

                if (!tienePeliculas) {
                    System.out.println("  No tiene películas asociadas.");
                }
            }
        }
    }
    
            // Conexión a la base de datos
//        try (Connection conn = DistribuidoraPeliculas.conectar()) {
//
//            // Consultar todas las distribuidoras
//            System.out.println("Consultando todas las distribuidoras:");
//            List<DistribuidoraPeliculas> distribuidoras = DistribuidoraPeliculas.consultarDistribuidorasPeliculas(conn);
//            for (DistribuidoraPeliculas distribuidora : distribuidoras) {
//                System.out.println(distribuidora.getId() + " - " + distribuidora.getNombre());
//            }
//
//            // Agregar una nueva distribuidora
//            String nuevoNombre = "Nueva Distribuidora"; // Cambia el nombre por el que desees
//            DistribuidoraPeliculas.agregarDistribuidoraPeliculas(conn, nuevoNombre);
//
//            // Consultar distribuidora específica y sus películas
//            String nombreDistribuidora = "20th Century Fox"; // Cambia por el nombre de la distribuidora que deseas consultar
//            DistribuidoraPeliculas.consultarDistribuidoraConPeliculas(conn, nombreDistribuidora);
//
//        } catch (SQLException e) {
//            System.err.println("Error de conexión o en la consulta: " + e.getMessage());
//        }
}
