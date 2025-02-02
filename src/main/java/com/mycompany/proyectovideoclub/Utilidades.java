package com.mycompany.proyectovideoclub;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.net.URL;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author oscar.lara
 */
public class Utilidades {

    public static void cargarImagenEnLabel(JLabel label, String rutaRelativa) {
        URL ruta = Utilidades.class.getResource(rutaRelativa);
        if (ruta != null) {
            ImageIcon imgProd = new ImageIcon(ruta);
            ImageIcon tamaño = new ImageIcon(imgProd.getImage().getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_DEFAULT));
            label.setIcon(tamaño);
        } else {
            System.err.println("No se encontró la imagen en: " + rutaRelativa);
        }
    }

    private Date convertirFecha(String fechaStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            java.util.Date utilDate = sdf.parse(fechaStr);
            return new java.sql.Date(utilDate.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class CustomButton extends JButton {

        public CustomButton(String text) {
            super(text);
            setBackground(Color.LIGHT_GRAY);
            setForeground(Color.BLACK);
            setFont(new Font("Arial", Font.BOLD, 14));
            setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        }
    }

    // Método para establecer texto predefinido en JTextField
    public static void setPredefinedText(final JTextField textField, final String defaultText) {
        // Establecer el texto predefinido solo si el campo está vacío
        if (textField.getText().isEmpty()) {
            textField.setText(defaultText);
        }

        // Agregar un FocusListener para manejar el evento de ganar el foco
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(defaultText)) {
                    textField.setText("");  // Limpiar el texto predefinido al obtener el foco
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(defaultText);  // Reestablecer el texto predefinido si el campo está vacío
                }
            }
        });
    }

    public static void volverALogin(JFrame currentWindow) {
        // Crear una nueva instancia de UILogin
        UILogin uiLogin = new UILogin();

        // Hacer visible la nueva interfaz
        uiLogin.setVisible(true);

        // Cerrar la ventana actual
        currentWindow.dispose();
    }

}
