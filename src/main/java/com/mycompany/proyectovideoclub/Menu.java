package com.mycompany.proyectovideoclub;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

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
        
        // SwingUtilites.invokeLater(() -> new Menu());
        
        
        
        
        
        
        
        //Ejemplo button
//        SwingUtilities.invokeLater(() -> {
//            JFrame frame = new JFrame("Rounded Button Test");
//            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            frame.setSize(400, 300);
//            frame.setLayout(new FlowLayout());
//
//            // Crear un botón redondeado
//            CustomRoundedButton roundedButton = new  CustomRoundedButton("Click Me");
//            frame.add(roundedButton);
//
//            frame.setVisible(true);
//        });
        
        
        
        
        

 //Se inicializa la interfaz de login y se hace visible
        UILogin login = new UILogin();
        login.setVisible(true);
        login.setLocationRelativeTo(null);
}
}


  