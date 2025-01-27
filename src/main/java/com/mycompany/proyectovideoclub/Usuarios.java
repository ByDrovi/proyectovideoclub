package com.mycompany.proyectovideoclub;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 *
 * @author oscar.lara
 */

public class Usuarios {
    private Integer id;
    private String logUser;
    private String logPass;
    private String nombre;
    private String apellidos;
    private String dniUser;
    private LocalDate fechaNacimiento;
    private LocalDate fechaAlta;
    private String tipoUsuario;
    

    // Constructor vacío
    public Usuarios() {}

    public Usuarios(Integer id, String nombre, String apellidos, String dniUser, LocalDate fechaAlta) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.dniUser = dniUser;
        this.fechaAlta = fechaAlta;
    }
    
    

    // Constructor con parámetros
    public Usuarios(Integer id, String logUser, String logPass, String nombre, String apellidos, 
                   String dniUser, LocalDate fechaNacimiento, LocalDate fechaAlta, String tipoUsuario) {
        this.id = id;
        this.logUser = logUser;
        this.logPass = logPass;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.dniUser = dniUser;
        this.fechaNacimiento = fechaNacimiento;
        this.fechaAlta = fechaAlta;
        this.tipoUsuario = tipoUsuario;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogUser() {
        return logUser;
    }

    public void setLogUser(String logUser) {
        this.logUser = logUser;
    }

    public String getLogPass() {
        return logPass;
    }

    public void setLogPass(String logPass) {
        this.logPass = logPass;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDniUser() {
        return dniUser;
    }

    public void setDniUser(String dniUser) {
        this.dniUser = dniUser;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public LocalDate getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(LocalDate fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    
    public static Usuarios buscarUsuario(Connection conn, String buscarTexto) throws SQLException {
    String sql = "SELECT * "
            + "FROM usuarios "
            + "WHERE nombre LIKE ? OR dniUser LIKE ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        String criterioBusqueda = "%" + buscarTexto + "%";
        stmt.setString(1, criterioBusqueda);
        stmt.setString(2, criterioBusqueda);
        
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                Integer id = rs.getInt("id");
                String logUser = rs.getString("logUser");
                String logPass = rs.getString("logPass");
                String nombre = rs.getString("nombre");
                String apellidos = rs.getString("apellidos");
                String dniUser = rs.getString("dniUser");
                LocalDate fechaNacimiento = rs.getDate("fechaNacimiento").toLocalDate();
                LocalDate fechaAlta = rs.getDate("fechaAlta").toLocalDate();
                String tipoUsuario = String.valueOf(rs.getString("tipoUsuario"));
                
                return new Usuarios(id, logUser, logPass, nombre, apellidos, dniUser, fechaNacimiento, fechaAlta, tipoUsuario);
            }
        }
    }
    return null;  // Si no encuentra el usuario
}
}

