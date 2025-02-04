package com.mycompany.proyectovideoclub;

/**
 *
 * @author oscar.lara
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class LoginService {
    
public static Usuarios autenticarUsuario(String logUser, String logPass, Connection conn) {
        String sql = "SELECT * "
                + "FROM Usuarios "
                + "WHERE logUser = ? AND logPass = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, logUser);
            stmt.setString(2, logPass);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Usuarios usuario = new Usuarios(
                        rs.getInt("id"),
                        rs.getString("logUser"),
                        rs.getString("logPass"),
                        rs.getString("nombre"),
                        rs.getString("apellidos"),
                        rs.getString("dniUser"),
                        rs.getDate("fechaNacimiento").toLocalDate(),
                        rs.getDate("fechaAlta").toLocalDate(),
                        rs.getString("tipo_usuario")
                );
                JOptionPane.showMessageDialog(null, "¡Login correcto!. Todo listo, " + usuario.getNombre());
                return usuario;
            } else {
                JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos.", "Error de Login", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error de conexión: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}