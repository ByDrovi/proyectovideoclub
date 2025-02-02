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


public static List<Formato> obtenerFormatos() {
    List<Formato> formatos = new ArrayList<>();
    String sql = """
        SELECT f.id, f.nombre, COALESCE(SUM(prod.numdisponiblealquiler), 0) AS numDisponibleAlquiler
        FROM Formatos f
        LEFT JOIN Peliculas p ON f.id = p.formato_id
        LEFT JOIN Productos prod ON p.id = prod.id
        WHERE prod.esBaja = FALSE AND prod.enStock = TRUE
        GROUP BY f.id, f.nombre
    """;

    try (Connection conn = Database.getConnection();
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

// Agregar un nuevo formato
public static void agregarFormato(Connection conn, String nuevoFormato) throws SQLException {
    System.out.println("Agregando un nuevo formato...");
    String query = "INSERT INTO Formatos (nombre) VALUES (?)";

    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
        pstmt.setString(1, nuevoFormato);  // Establecer el valor para el nombre del formato
        pstmt.executeUpdate();  // Ejecutar la consulta
        System.out.println("El nuevo formato '" + nuevoFormato + "' ha sido agregado con éxito.");
    }
}


}
