
package com.mycompany.proyectovideoclub;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ImagenesDeslizantes extends JFrame {
    
    private JLabel imagenLabel;
    private int indiceActual = 0;
    private String[] imagenes = {
            "estreno1.jpg",
            "estreno2.jpg",
            "estreno3.jpg"
    };

    public ImagenesDeslizantes() {
        setTitle("Nuevos Estrenos");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Cargar la primera imagen
        imagenLabel = new JLabel();
        imagenLabel.setHorizontalAlignment(JLabel.CENTER);
        cargarImagen(imagenes[indiceActual]);
        add(imagenLabel, BorderLayout.CENTER);

        // Configurar el Timer
        Timer timer = new Timer(3000, new ActionListener() { // Cambiar cada 3 segundos
            @Override
            public void actionPerformed(ActionEvent e) {
                indiceActual = (indiceActual + 1) % imagenes.length; // Avanzar al siguiente índice
                cargarImagen(imagenes[indiceActual]);
            }
        });
        timer.start();

        setVisible(true);
    }

    private void cargarImagen(String ruta) {
        // Cargar la imagen y establecerla en el JLabel
        ImageIcon icono = new ImageIcon(ruta);
        Image imagen = icono.getImage().getScaledInstance(600, 400, Image.SCALE_SMOOTH);
        imagenLabel.setIcon(new ImageIcon(imagen));
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> new ImagenesDeslizantes());
//    }
}

