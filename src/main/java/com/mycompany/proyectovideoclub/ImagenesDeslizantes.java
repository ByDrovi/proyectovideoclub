package com.mycompany.proyectovideoclub;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

public class ImagenesDeslizantes {

    private int indiceActual = 0;
    private String[] imagenes = {
            "/images/estreno1.jpg",
            "/images/estreno2.jpg",
            "/images/estreno3.jpg"
    };

    public void iniciarDeslizamiento(JLabel jLabelProximamente) {
        // Cargar la primera imagen
        cargarImagen(jLabelProximamente, imagenes[indiceActual]);

        // Configurar el Timer para cambiar la imagen cada 3 segundos
        Timer timer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                indiceActual = (indiceActual + 1) % imagenes.length; // Avanzar al siguiente índice
                cargarImagen(jLabelProximamente, imagenes[indiceActual]);
            }
        });
        timer.start();
    }

public static void cargarImagen(JLabel label, String rutaRelativa) {
    try {
        // Obtener la ruta de la imagen
        URL ruta = Utilidades.class.getResource(rutaRelativa);
        if (ruta != null) {
            // Cargar la imagen desde la ruta proporcionada
            ImageIcon icono = new ImageIcon(ruta);
            // Escalar la imagen para que coincida con las dimensiones del JLabel
            Image imagenEscalada = icono.getImage().getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH);
            // Establecer la imagen escalada como icono del JLabel
            label.setIcon(new ImageIcon(imagenEscalada));
        } else {
            // Si la ruta no es válida, mostrar un error
            System.err.println("No se encontró la imagen en: " + rutaRelativa);
        }
    } catch (Exception e) {
        // Manejar cualquier otra excepción, como NullPointerException
        System.err.println("Error al cargar la imagen: " + rutaRelativa);
    }
}
}

