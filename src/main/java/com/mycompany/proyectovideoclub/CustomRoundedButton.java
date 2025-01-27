package com.mycompany.proyectovideoclub;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 *
 * @author oscar.lara
 */

public class CustomRoundedButton extends JPanel {

    private String text;
    private Color normalColor = Color.BLACK;
    private Color hoverColor = Color.GRAY;
    private Color currentColor = normalColor; // Color actual del botón
    private Shape shape;

    public CustomRoundedButton(String text) {
        this.text = text;

        // Habilitar detección de eventos del mouse
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                currentColor = hoverColor; // Cambiar a color de hover
                repaint(); // Redibujar el componente
            }

            @Override
            public void mouseExited(MouseEvent e) {
                currentColor = normalColor; // Volver al color normal
                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(CustomRoundedButton.this, "¡Botón presionado!");
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Suavizar bordes
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dibujar el rectángulo redondeado externo
        int x = 5;
        int y = 5;
        int w = getWidth() - 10;
        int h = getHeight() - 10;
        shape = new RoundRectangle2D.Float(x, y, w, h, 40, 40);


        g2d.setColor(currentColor);
        g2d.fill(shape);

        // Dibujar texto centrado
        g2d.setColor(Color.WHITE); // Color del texto
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        int textX = x + (w - textWidth) / 2;
        int textY = y + (h - textHeight) / 2 + fm.getAscent();
        g2d.drawString(text, textX, textY);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(150, 50); // Tamaño predeterminado del botón
    }

    @Override
    public boolean contains(int x, int y) {
        return shape.contains(x, y); // Detectar si el mouse está dentro del botón
    }
}
