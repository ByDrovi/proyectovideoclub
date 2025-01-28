package com.mycompany.proyectovideoclub;

import javax.swing.*;
import java.awt.*;

public class CustomRoundedButton extends DefaultButtonModel {
    
    private Color normalColor = Color.BLACK;
    private Color hoverColor = Color.DARK_GRAY;

    // Getters y setters para los colores
    public Color getNormalColor() {
        return normalColor;
    }

    public void setNormalColor(Color normalColor) {
        this.normalColor = normalColor;
    }

    public Color getHoverColor() {
        return hoverColor;
    }

    public void setHoverColor(Color hoverColor) {
        this.hoverColor = hoverColor;
}

}