package com.mycompany.proyectovideoclub;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Alquiler {

    private int id;
    private int idSocio;
    private int idProducto;
    private Date fechaHoy;
    private Date fechaAlquiler;
    private Date fechaDevolucion;
    private Date fechaRealEntrega;
    private String estado;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    // Constructor vacío
    public Alquiler() {
    }

    // Constructor completo
    public Alquiler(int id, int idSocio, int idProducto, Date fechaHoy, Date fechaAlquiler, Date fechaDevolucion, Date fechaRealEntrega, String estado) {
        this.id = id;
        this.idSocio = idSocio;
        this.idProducto = idProducto;
        this.fechaHoy = fechaHoy;
        this.fechaAlquiler = fechaAlquiler;
        this.fechaDevolucion = fechaDevolucion;
        this.fechaRealEntrega = fechaRealEntrega;
        this.estado = estado;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdSocio() {
        return idSocio;
    }

    public void setIdSocio(int idSocio) {
        this.idSocio = idSocio;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public Date getFechaHoy() {
        return fechaHoy;
    }

    public void setFechaHoy(Date fechaHoy) {
        this.fechaHoy = fechaHoy;
    }

    public Date getFechaAlquiler() {
        return fechaAlquiler;
    }

    public void setFechaAlquiler(Date fechaAlquiler) {
        this.fechaAlquiler = fechaAlquiler;
    }

    public Date getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(Date fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public Date getFechaRealEntrega() {
        return fechaRealEntrega;
    }

    public void setFechaRealEntrega(Date fechaRealEntrega) {
        this.fechaRealEntrega = fechaRealEntrega;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    // Método para registrar un alquiler
    public static boolean registrarAlquiler(Connection conn, int idSocio, int idProducto, String fechaAlquilerStr) throws SQLException, ParseException {
        // Convertir la fecha simulada (String) a Date
        Date fechaAlquiler = dateFormat.parse(fechaAlquilerStr);

        // Calcular fechaDevolucion sumando 3 días
        Date fechaDevolucion = new Date(fechaAlquiler.getTime() + 3L * 24 * 60 * 60 * 1000);

        String query = "INSERT INTO Alquileres (idSocio, idProducto, fechaHoy, fechaAlquiler, fechaDevolucion, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idSocio);
            stmt.setInt(2, idProducto);
            stmt.setDate(3, new java.sql.Date(new Date().getTime())); // Fecha actual como fechaHoy
            stmt.setDate(4, new java.sql.Date(fechaAlquiler.getTime())); // Fecha de alquiler
            stmt.setDate(5, new java.sql.Date(fechaDevolucion.getTime())); // Fecha de devolución calculada
            stmt.setString(6, "Prestado"); // Estado inicial
            return stmt.executeUpdate() > 0;
        }
    }

    // Método para registrar la devolución
    public static boolean registrarDevolucion(Connection conn, int idAlquiler, String fechaRealEntregaStr) throws SQLException, ParseException {
        // Convertir la fecha simulada (String) a Date
        Date fechaRealEntrega = dateFormat.parse(fechaRealEntregaStr);

        String query = "UPDATE Alquileres SET estado = 'Devuelto', fechaRealEntrega = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDate(1, new java.sql.Date(fechaRealEntrega.getTime()));
            stmt.setInt(2, idAlquiler);
            return stmt.executeUpdate() > 0;
        }
    }

}
