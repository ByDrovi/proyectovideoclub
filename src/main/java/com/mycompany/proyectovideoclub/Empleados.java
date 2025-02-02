package com.mycompany.proyectovideoclub;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author oscar.lara
 */

public class Empleados extends Usuarios {

    private LocalDate fechaBaja;
    private boolean esActivo;
    
    

    // Getters y Setters
    public LocalDate getFechaBaja() {
        return fechaBaja;
    }

    public void setFechaBaja(LocalDate fechaBaja) {
        this.fechaBaja = fechaBaja;
    }

    public boolean isEsActivo() {
        return esActivo;
    }

    public void setEsActivo(boolean esActivo) {
        this.esActivo = esActivo;
    }

    public static List<Empleados> consultarEmpleados(Connection conn) throws SQLException {
        System.out.println("Listado de empleados actualmente activos");
        String query = "SELECT u.id, u.nombre, u.apellidos, u.dniUser, e.fechaBaja, e.esActivo " +
               "FROM Usuarios u " +
               "JOIN Empleados e ON u.id = e.id " +
               "WHERE e.esActivo = TRUE";

        List<Empleados> empleados = new ArrayList<>();

        try ( Statement stmt = conn.createStatement();  ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Empleados empleado = new Empleados();
                empleado.setId(rs.getInt("id"));
                empleado.setNombre(rs.getString("nombre"));
                empleado.setApellidos(rs.getString("apellidos"));
                empleado.setDniUser(rs.getString("dniUser"));

                // Convertir SQL Date a LocalDate
                if (rs.getDate("fechaBaja") != null) {
                    empleado.setFechaBaja(rs.getDate("fechaBaja").toLocalDate());
                }

                empleado.setEsActivo(rs.getBoolean("esActivo"));

                empleados.add(empleado);
            }
        }

        System.out.println("La lista de empleados obtenida correctamente.");
        return empleados;
    }
    
public static void agregarEmpleado(Connection conn, String logUser, String logPass, String nombre, String apellidos, 
                                   String dniUser, Date fechaNacimiento, Date fechaAlta, 
                                   String tipoUsuario, Date fechaBaja, boolean esActivo) throws SQLException {
    System.out.println("Agregando un nuevo empleado...");

    // 1. Insertar el nuevo usuario en la tabla Usuarios
    String queryUsuario = "INSERT INTO Usuarios ("
            + "logUser, "
            + "logPass, "
            + "nombre, "
            + "apellidos, "
            + "dniUser, "
            + "fechaNacimiento, "
            + "fechaAlta, "
            + "tipo_usuario) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    try (PreparedStatement stmt = conn.prepareStatement(queryUsuario)) {
        stmt.setString(1, logUser);
        stmt.setString(2, logPass);
        stmt.setString(3, nombre);
        stmt.setString(4, apellidos);
        stmt.setString(5, dniUser);
        stmt.setDate(6, fechaNacimiento);  // Aquí usamos el tipo Date para las fechas
        stmt.setDate(7, fechaAlta);        // Lo mismo para la fecha de alta
        stmt.setString(8, tipoUsuario);    // Tipo de usuario
        stmt.executeUpdate();
    }

    // 2. Si es un empleado, insertar en la tabla Empleados (usamos el ID del usuario recién insertado)
    String queryEmpleado = "INSERT INTO Empleados ("
            + "id, "
            + "fechaBaja, "
            + "esActivo) "
            + "VALUES (("
                        + "SELECT id "
                        + "FROM Usuarios "
                        + "WHERE logUser = ?), ?, ?)";

    try (PreparedStatement stmt = conn.prepareStatement(queryEmpleado)) {
        stmt.setString(1, logUser);
        stmt.setDate(2, fechaBaja);   // Aquí usamos la fecha de baja
        stmt.setBoolean(3, esActivo); // Si está activo o no
        stmt.executeUpdate();
    }
}
}  


