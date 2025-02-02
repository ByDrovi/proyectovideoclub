/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectovideoclub;

import javax.swing.*;
import java.awt.*;

public class CustomGradientPanel extends JPanel {
    private Color color1;
    private Color color2;

    public CustomGradientPanel() {
        this.color1 = new Color(0, 102, 204);  // Azul oscuro
        this.color2 = new Color(102, 204, 255); // Azul claro
        setOpaque(false); // Asegura que el fondo se pinte correctamente
    }

    public void setGradientColors(Color c1, Color c2) {
        this.color1 = c1;
        this.color2 = c2;
        repaint(); // Redibuja el panel con los nuevos colores
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();

        GradientPaint gp = new GradientPaint(0, 0, color1, width, height, color2);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, width, height);
    }
}

