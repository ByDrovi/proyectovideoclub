package com.mycompany.proyectovideoclub;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import javax.swing.JOptionPane;

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

    public static void alquilarProducto(int productoId, int socioId, LocalDate fechaAlquiler, double cuotaAlquiler) throws SQLException {
        try ( Connection conn = Database.getConnection()) {
            LocalDate fechaEntrega = fechaAlquiler.plusDays(3); // Sumar 3 días para la fecha de entrega

            // Insertar el alquiler en la tabla Alquileres
            String queryInsert
                    = "INSERT INTO Alquileres (idProducto, idSocio, fechaAlquiler, fechaEntrega, cuotaAlquiler) "
                    + "VALUES (?, ?, ?, ?, ?)";

            try ( PreparedStatement insertStmt = conn.prepareStatement(queryInsert)) {
                insertStmt.setInt(1, productoId); // ID del producto
                insertStmt.setInt(2, socioId); // ID del socio
                insertStmt.setDate(3, java.sql.Date.valueOf(fechaAlquiler)); // Fecha de alquiler
                insertStmt.setDate(4, java.sql.Date.valueOf(fechaEntrega)); // Fecha de entrega
                insertStmt.setDouble(5, cuotaAlquiler); // Cuota de alquiler

                int rowsInserted = insertStmt.executeUpdate();
                if (rowsInserted > 0) {
                    // Actualizar unidades disponibles
                    String queryUpdateUnidades
                            = "UPDATE Productos SET numDisponibleAlquiler = numDisponibleAlquiler - 1 WHERE id = ?";

                    try ( PreparedStatement updateStmt = conn.prepareStatement(queryUpdateUnidades)) {
                        updateStmt.setInt(1, productoId);
                        updateStmt.executeUpdate();
                    }
                }
            }

        } catch (SQLException e) {
            throw new SQLException("Error al procesar el alquiler: " + e.getMessage());
        }
    }

    public static void devolverProducto(int productoId, int socioId, LocalDate fechaRealEntrega) throws SQLException {
        try ( Connection conn = Database.getConnection()) {
            // Consultar la fecha de entrega original y el recargo por devolución tardía
            String queryDetalles
                    = "SELECT fechaEntrega, recargoDevolucion "
                    + "FROM Alquileres "
                    + "JOIN Productos p ON Alquileres.idProducto = p.id "
                    + "WHERE idSocio = ? AND idProducto = ? AND fechaRealEntrega IS NULL";
            try ( PreparedStatement stmt = conn.prepareStatement(queryDetalles)) {
                stmt.setInt(1, socioId);
                stmt.setInt(2, productoId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    LocalDate fechaEntrega = rs.getDate("fechaEntrega").toLocalDate();
                    double recargoDevolucion = rs.getDouble("recargoDevolucion");

                    // Comprobar si se ha retrasado la devolución
                    if (fechaRealEntrega.isAfter(fechaEntrega)) {
                        // Si es así, aplicar el recargo en el próximo alquiler
                        String updateRecargo = ""
                                + "UPDATE Socios "
                                + "SET recargoDevolucion = ? "
                                + "WHERE id = ?";
                        try ( PreparedStatement updateStmt = conn.prepareStatement(updateRecargo)) {
                            updateStmt.setDouble(1, recargoDevolucion); // Recargo para el siguiente alquiler
                            updateStmt.setInt(2, socioId);
                            updateStmt.executeUpdate();
                        }

                        // Avisar al usuario del recargo
                        JOptionPane.showMessageDialog(null, "¡Atención! Tendrás un recargo de " + recargoDevolucion
                                + "€ en tu próximo alquiler debido a la devolución tardía.");
                    }

                    // Actualizar las unidades disponibles del producto
                    String queryUpdateUnidades
                            = "UPDATE Productos "
                            + "SET numDisponibleAlquiler = numDisponibleAlquiler + 1 "
                            + "WHERE id = ?";
                    try ( PreparedStatement updateStmt = conn.prepareStatement(queryUpdateUnidades)) {
                        updateStmt.setInt(1, productoId);
                        updateStmt.executeUpdate();
                    }

                    // Marcar la devolución en la tabla Alquileres
                    String updateDevolucion
                            = "UPDATE Alquileres "
                            + "SET fechaRealEntrega = ? "
                            + "WHERE idSocio = ? AND idProducto = ?";
                    try ( PreparedStatement updateDevolucionStmt = conn.prepareStatement(updateDevolucion)) {
                        updateDevolucionStmt.setDate(1, java.sql.Date.valueOf(fechaRealEntrega)); // Fecha de devolución
                        updateDevolucionStmt.setInt(2, socioId);
                        updateDevolucionStmt.setInt(3, productoId);
                        updateDevolucionStmt.executeUpdate();
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "No se encontró un alquiler para este producto.");
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error al procesar la devolución: " + e.getMessage());
        }
    }

}
