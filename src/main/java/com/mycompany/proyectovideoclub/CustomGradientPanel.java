/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectovideoclub;

import javax.swing.*;
import java.awt.*;


public class CustomGradientPanel extends JPanel {
    private Color startColor = Color.BLUE;
    private Color endColor = Color.CYAN;

    public void setGradientColors(Color start, Color end) {
        this.startColor = start;
        this.endColor = end;
        repaint(); // Redibujar el panel con los nuevos colores
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();
        GradientPaint gradient = new GradientPaint(0, 0, startColor, width, height, endColor);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);
    }
}
