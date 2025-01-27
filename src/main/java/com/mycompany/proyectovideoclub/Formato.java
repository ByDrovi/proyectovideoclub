package com.mycompany.proyectovideoclub;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author oscar.lara
 */

public class Formato {
    
    private int id;
    private String nombre;
    private int cantidadProductos; // Nueva columna para contar productos

    // Constructor
    public Formato(int id, String nombre, int cantidadProductos) {
        this.id = id;
        this.nombre = nombre;
        this.cantidadProductos = cantidadProductos;
    }

    // Getters y setters
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

    public int getCantidadProductos() {
        return cantidadProductos;
    }

    public void setCantidadProductos(int cantidadProductos) {
        this.cantidadProductos = cantidadProductos;
    }

    // Conexión a la base de datos
    private static Connection conectar() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/videoclub";
        String user = "root";
        String password = "";
        return DriverManager.getConnection(url, user, password);
    }

public static List<Formato> obtenerFormatos() {
    List<Formato> formatos = new ArrayList<>();
    String sql = """
        SELECT f.id, f.nombre, COALESCE(SUM(p.numDisponibleAlquiler), 0) AS numDisponibleAlquiler
        FROM Formatos f
        LEFT JOIN Productos p ON f.id = p.id_formato
        GROUP BY f.id, f.nombre
    """;

    try (Connection conn = conectar();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        while (rs.next()) {
            int id = rs.getInt("id");
            String nombre = rs.getString("nombre");
            int cantidadTotalUnidades = rs.getInt("numDisponibleAlquiler");
            formatos.add(new Formato(id, nombre, cantidadTotalUnidades));
        }

    } catch (SQLException e) {
        System.err.println("Error al obtener formatos: " + e.getMessage());
    }
    return formatos;
}

}
