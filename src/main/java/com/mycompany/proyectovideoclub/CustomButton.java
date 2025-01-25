
package com.mycompany.proyectovideoclub;

import javax.swing.*;
import java.awt.*;

public class CustomButton extends JButton {
    public CustomButton(String text) {
        super(text);
        setBackground(Color.LIGHT_GRAY);
        setForeground(Color.BLACK);
        setFont(new Font("Arial", Font.BOLD, 14));
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
    }
}
