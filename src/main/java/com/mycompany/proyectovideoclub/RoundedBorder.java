package com.mycompany.proyectovideoclub;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;

public class RoundedBorder extends AbstractBorder {
    private final Color color;
    private final int radius;
    private final int thickness;

    public RoundedBorder(Color color, int radius, int thickness) {
        this.color = color;
        this.radius = radius;
        this.thickness = thickness;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int adjustedWidth = width - thickness;
        int adjustedHeight = height - thickness;

        g2.setColor(color);
        g2.setStroke(new BasicStroke(thickness));
        g2.drawRoundRect(x + thickness / 2, y + thickness / 2, adjustedWidth, adjustedHeight, radius, radius);

        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(radius, radius, radius, radius);
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.right = insets.top = insets.bottom = radius;
        return insets;
    }
}
