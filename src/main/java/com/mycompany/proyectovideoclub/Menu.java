package com.mycompany.proyectovideoclub;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author oscar.lara
 */

public class Menu {
    
//    public Menu(){
//    
//    setTitle("Custom Textfield");
//    setSize(400,300);
//    setDefaultCloseOpetation(JFrame.EXIT_ON_CLOSE);
//    setLayout(new FlowLayout());
//    
//    CustomTextField textField = new CustomTextField(40,40);
//    
//    textField.setColumns(15);
//    textField.setFont(new Font("Arial", Font.PLAIN, 16));
//    textField.setMargin(new Insets(5, 10, 5, 10));
//    add(textField);
//    
//    setVisible(true);
//    
//    }
    
    

    public static void main(String[] args) {
       
        
        
        
        

 //Se inicializa la interfaz de login y se hace visible
        UILogin login = new UILogin();
        login.setVisible(true);
        login.setLocationRelativeTo(null);
}
}


  