package com.mycompany.proyectovideoclub;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author oscar.lara
 */

public class Socios extends Usuarios {

    private int alquileresTotales;

    private int comprasTotales;

    private boolean recargoActivo;

    private String tipoSocio;

    public Socios() {
        this.alquileresTotales = 0;
        this.comprasTotales = 0;
        this.recargoActivo = false;
        this.tipoSocio = "Estandar"; // O el valor por defecto de la base de datos
    }

    public Socios(Integer id, String logUser, String logPass, String nombre, String apellidos, String dniUser,
            LocalDate fechaNacimiento, LocalDate fechaAlta, String tipoUsuario,
            int alquileresTotales, int comprasTotales, boolean recargoActivo, String tipoSocio) {
        super(id, logUser, logPass, nombre, apellidos, dniUser, fechaNacimiento, fechaAlta, tipoUsuario);
        this.alquileresTotales = alquileresTotales;
        this.comprasTotales = comprasTotales;
        this.recargoActivo = recargoActivo;
        this.tipoSocio = tipoSocio;
    }

    public int getAlquileresTotales() {
        return alquileresTotales;
    }

    public void setAlquileresTotales(int alquileresTotales) {
        this.alquileresTotales = alquileresTotales;
    }

    public int getComprasTotales() {
        return comprasTotales;
    }

    public void setComprasTotales(int comprasTotales) {
        this.comprasTotales = comprasTotales;
    }

    public boolean isRecargoActivo() {
        return recargoActivo;
    }

    public void setRecargoActivo(boolean recargoActivo) {
        this.recargoActivo = recargoActivo;
    }

    public String getTipoSocio() {
        return tipoSocio;
    }

    public void setTipoSocio(String tipoSocio) {
        this.tipoSocio = tipoSocio;
    }

    // ####################################################################################################################################
    public static Socios convertirASocio(int usuarioId) throws SQLException {
        // Query para obtener los datos del usuario junto con los datos del socio
        String query = "SELECT u.id, u.logUser, u.logPass, u.nombre, u.apellidos, u.dniUser, "
                + "u.fechaNacimiento, u.fechaAlta, u.tipo_usuario, "
                + "s.tipoSocio, s.alquileresTotales, s.comprasTotales, s.recargoActivo "
                + "FROM Usuarios u "
                + "LEFT JOIN Socios s ON u.id = s.id "
                + "WHERE u.id = ?";

        // Consultar los datos del usuario y del socio
        try (Connection conn = Database.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, usuarioId); // Establecer el parámetro del ID del usuario
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Crear un nuevo objeto Socios a partir de los datos
                    Socios socio = new Socios();
                    socio.setId(rs.getInt("id"));
                    socio.setLogUser(rs.getString("logUser"));
                    socio.setLogPass(rs.getString("logPass"));
                    socio.setNombre(rs.getString("nombre"));
                    socio.setApellidos(rs.getString("apellidos"));
                    socio.setDniUser(rs.getString("dniUser"));
                    socio.setFechaNacimiento(rs.getDate("fechaNacimiento").toLocalDate());
                    socio.setFechaAlta(rs.getDate("fechaAlta").toLocalDate());
                    socio.setTipoUsuario(rs.getString("tipo_usuario"));

                    // Datos específicos de la tabla Socios
                    socio.setTipoSocio(rs.getString("tipoSocio") != null ? rs.getString("tipoSocio") : "Estandar");
                    socio.setAlquileresTotales(rs.getInt("alquileresTotales"));
                    socio.setComprasTotales(rs.getInt("comprasTotales"));
                    socio.setRecargoActivo(rs.getBoolean("recargoActivo"));

                    return socio; // Devolver el objeto Socios
                } else {
                    throw new SQLException("Usuario con ID " + usuarioId + " no encontrado en la base de datos.");
                }
            }
        }
    }
    // #######################################################################################################################################

    public static List<Socios> consultarSocios(Connection conn) throws SQLException {
        System.out.println("Listado de SOCIOS:");
        String query = "SELECT u.id, u.nombre, u.apellidos, u.dniUser, u.fechaNacimiento, u.fechaAlta, "
                + "s.tipoSocio, s.alquileresTotales, s.comprasTotales, s.recargoActivo "
                + "FROM Usuarios u JOIN Socios s ON u.id = s.id";

        List<Socios> socios = new ArrayList<>();

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Socios socio = new Socios();
                socio.setId(rs.getInt("id"));
                socio.setNombre(rs.getString("nombre"));
                socio.setApellidos(rs.getString("apellidos"));
                socio.setDniUser(rs.getString("dniUser"));
                socio.setFechaNacimiento(rs.getDate("fechaNacimiento").toLocalDate());
                socio.setFechaAlta(rs.getDate("fechaAlta").toLocalDate());
                socio.setTipoSocio(rs.getString("tipoSocio"));
                socio.setAlquileresTotales(rs.getInt("alquileresTotales"));
                socio.setComprasTotales(rs.getInt("comprasTotales"));
                socio.setRecargoActivo(rs.getBoolean("recargoActivo"));

                socios.add(socio);
            }
        }

        System.out.println("La lista de socios obtenida correctamente.");
        return socios;
    }

    public static void agregarSocio(Connection conn, String logUser, String logPass, String nombre, String apellidos,
            String dniUser, Date fechaNacimiento, Date fechaAlta,
            String tipoUsuario, String tipoSocio) throws SQLException {
        System.out.println("Agregando un nuevo Socio...");

        // Insertar el nuevo usuario en la tabla Usuarios
        String queryUsuario = ""
                + "INSERT INTO Usuarios ("
                + "logUser, "
                + "logPass, "
                + "nombre, "
                + "apellidos, "
                + "dniUser, "
                + "fechaNacimiento, "
                + "fechaAlta,"
                + " tipo_usuario) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmtUsuario = conn.prepareStatement(queryUsuario, Statement.RETURN_GENERATED_KEYS)) {
            pstmtUsuario.setString(1, logUser);
            pstmtUsuario.setString(2, logPass);
            pstmtUsuario.setString(3, nombre);
            pstmtUsuario.setString(4, apellidos);
            pstmtUsuario.setString(5, dniUser);
            pstmtUsuario.setDate(6, fechaNacimiento);
            pstmtUsuario.setDate(7, fechaAlta);
            pstmtUsuario.setString(8, tipoUsuario);

            // Ejecutar la inserción del usuario
            pstmtUsuario.executeUpdate();

            // Obtener el ID generado automáticamente para el nuevo usuario
            try (ResultSet rs = pstmtUsuario.getGeneratedKeys()) {
                if (rs.next()) {
                    int idUsuario = rs.getInt(1); // ID generado automáticamente

                    // Insertar en la tabla Socios
                    String querySocio = "INSERT INTO Socios ("
                            + "id, "
                            + "tipoSocio, "
                            + "alquileresTotales, "
                            + "comprasTotales) "
                            + "VALUES (?, ?, ?, ?)";

                    try (PreparedStatement pstmtSocio = conn.prepareStatement(querySocio)) {
                        pstmtSocio.setInt(1, idUsuario);  // Usar el ID generado del usuario
                        pstmtSocio.setString(2, tipoSocio);  // Tipo de socio
                        pstmtSocio.setInt(3, 0);  // Por defecto, 0 alquileres
                        pstmtSocio.setInt(4, 0);  // Por defecto, 0 compras

                        // Ejecutar la inserción del socio
                        pstmtSocio.executeUpdate();
                        System.out.println("El nuevo socio ha sido agregado con éxito.");
                    }
                }
            }
        }
    }
}
