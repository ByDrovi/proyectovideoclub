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

    private JLabel labelImagen;
    private Timer timer;
    private int desplazamientoX = 0;
    private int velocidad = 2; // Velocidad de desplazamiento

    public void iniciarDeslizamiento(JLabel jLabelProximamente, JLabel siguiente) {
        // Asignamos el JLabel que contiene la imagen
        this.labelImagen = jLabelProximamente;

        // Cargar la primera imagen
        cargarImagen(jLabelProximamente, imagenes[indiceActual]);
        cargarImagen(siguiente, imagenes[indiceActual + 1]);

        // Configurar el Timer para mover la imagen hacia la derecha
        timer = new Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Mover la imagen a la derecha
                moverImagen();

                // Si la imagen se ha movido completamente fuera, cambiamos la imagen
                if (desplazamientoX >= labelImagen.getWidth()) {
                    // Cambiar al siguiente índice
                    indiceActual = (indiceActual + 1) % imagenes.length; // Avanzamos al siguiente índice
                    cargarImagen(siguiente, imagenes[indiceActual == imagenes.length - 1 ? 0 : indiceActual + 1]);
                    cargarImagen(jLabelProximamente, imagenes[indiceActual]); // Cargamos la nueva imagen
                    desplazamientoX = 0; // Reiniciar el desplazamiento
                }
            }
        });
        timer.start();
    }

    // Mover la imagen hacia la derecha
    public void moverImagen() {
        desplazamientoX += velocidad; // Aumentamos el desplazamiento

        // Movemos la imagen en el eje X
        labelImagen.setLocation(desplazamientoX, 50);  // Ajustamos la posición vertical según sea necesario
    }

    public static void cargarImagen(JLabel label, String rutaRelativa) {
        try {
            // Obtener la ruta de la imagen
            URL ruta = Utilidades.class.getResource(rutaRelativa);
            if (ruta != null) {
                // Cargar la imagen desde la ruta proporcionada
                ImageIcon icono = new ImageIcon(ruta);
                // Escalar la imagen para que coincida con las dimensiones del JLabel
                Image imagenEscalada = icono.getImage().getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_DEFAULT);
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
