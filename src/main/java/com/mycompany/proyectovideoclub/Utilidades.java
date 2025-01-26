package com.mycompany.proyectovideoclub;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

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

    // Clase CustomButton dentro de Utilidades
    public static class CustomButton extends JButton {

        public CustomButton(String text) {
            super(text);
            setBackground(Color.LIGHT_GRAY);
            setForeground(Color.BLACK);
            setFont(new Font("Arial", Font.BOLD, 14));
            setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        }
    }

}
