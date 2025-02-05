package com.mycompany.proyectovideoclub;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author oscar.lara
 */
public class UILogin extends javax.swing.JFrame {

    public UILogin() {
        setTitle("Videoclub - Login");
        setSize(1000, 1000);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initComponents();
        setLocationRelativeTo(null); // Abrir centrado en la pantalla, debe ir después de initComponents
        Utilidades.cargarImagenEnLabel(labelLogin, "/images/locklogin.png");
        Utilidades.cargarImagenEnLabel(labelGalaxy, "/images/galaxy.gif");
        Utilidades.cargarImagenEnLabel(jLabelRetro, "/images/retro.png");
        Utilidades.cargarImagenEnLabel(jLabelGalaxy, "/images/galaxy.png");

        loginButton = new Utilidades.CustomButton("Iniciar sesión");
        this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowOpened(WindowEvent e) {
                    jPanelGeneral.requestFocusInWindow();  // Solicitar que el panel reciba el foco
                }});
    }

    private void autenticar() throws SQLException {
        String logUser = userField.getText();
        String logPass = new String(passField.getPassword());

        if (logUser.isEmpty() || logPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe completar todos los campos.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Connection conn = Database.getConnection();
        Usuarios usuarioLogin = LoginService.autenticarUsuario(logUser, logPass, conn);

        if (usuarioLogin != null) {
            switch (usuarioLogin.getTipoUsuario()) {
                case "Admin":
                    new UIAdmin().setVisible(true);
                    break;
                case "Empleado":
                    new UIEmpleado().setVisible(true);
                    break;
                case "Socio":
                    // ###############################################################
                    new UISocio(Socios.convertirASocio(usuarioLogin.getId())).setVisible(true);
                    // ###############################################################
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Lo sentimos, el usuario no existe o la contraseña es incorrecta.", "Error de credencial", JOptionPane.ERROR_MESSAGE);
                    break;
            }
            this.dispose();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelGeneral = new javax.swing.JPanel();
        labelLogin = new javax.swing.JLabel();
        labelGalaxy = new javax.swing.JLabel();
        userField = new javax.swing.JTextField();
        loginButton = new javax.swing.JButton();
        passField = new javax.swing.JPasswordField();
        jLabelRetro = new javax.swing.JLabel();
        jLabelGalaxy = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanelGeneral.setBackground(new java.awt.Color(46, 1, 67));
        jPanelGeneral.setForeground(new java.awt.Color(119, 172, 254));
        jPanelGeneral.setToolTipText("");
        jPanelGeneral.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        labelGalaxy.setText(" ");

        userField.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        userField.setForeground(new java.awt.Color(102, 102, 102));
        userField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        userField.setText("User");
        userField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                userFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                userFieldFocusLost(evt);
            }
        });
        userField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userFieldActionPerformed(evt);
            }
        });

        loginButton.setForeground(new java.awt.Color(102, 204, 255));
        loginButton.setText("Iniciar sesión");
        loginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                verificarLogin(evt);
            }
        });

        passField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        passField.setForeground(new java.awt.Color(102, 102, 102));
        passField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        passField.setText("jPasswordField1");
        passField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                passFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                passFieldFocusLost(evt);
            }
        });

        jLabelRetro.setText("jLabel1");

        jLabelGalaxy.setText("jLabel1");

        javax.swing.GroupLayout jPanelGeneralLayout = new javax.swing.GroupLayout(jPanelGeneral);
        jPanelGeneral.setLayout(jPanelGeneralLayout);
        jPanelGeneralLayout.setHorizontalGroup(
            jPanelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelGeneralLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(loginButton, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelGeneralLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(jPanelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(userField, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(passField, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(316, 316, 316))
            .addGroup(jPanelGeneralLayout.createSequentialGroup()
                .addGroup(jPanelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelGeneralLayout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addComponent(jLabelRetro, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelGalaxy, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabelGalaxy, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelGeneralLayout.createSequentialGroup()
                        .addGap(351, 351, 351)
                        .addComponent(labelLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelGeneralLayout.setVerticalGroup(
            jPanelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGeneralLayout.createSequentialGroup()
                .addGroup(jPanelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelGeneralLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabelGalaxy, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelGeneralLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(jPanelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelRetro, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelGalaxy, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(userField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(passField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(loginButton, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(51, Short.MAX_VALUE))
        );

        CustomRoundedButton customModel = new CustomRoundedButton();
        customModel.setNormalColor(new Color(0x01E3FD));
        customModel.setHoverColor(new Color(0xC3C3C3));

        // Crear un botón estándar de Swing

        loginButton.setModel(customModel); // Asignar el modelo personalizado

        // Configurar colores dinámicamente con un MouseListener
        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(customModel.getHoverColor());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(customModel.getNormalColor());
            }
        });

        // Configurar el botón
        loginButton.setFocusPainted(false); // Quitar el borde de enfoque
        loginButton.setBackground(customModel.getNormalColor()); // Color inicial
        loginButton.setForeground(Color.WHITE); // Color del texto
        loginButton.setFont(new Font("Arial", Font.BOLD,16));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanelGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void userFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userFieldActionPerformed

    }//GEN-LAST:event_userFieldActionPerformed

    private void verificarLogin(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_verificarLogin
        try {
            autenticar();
        } catch (SQLException ex) {
            Logger.getLogger(UILogin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_verificarLogin

    private void userFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_userFieldFocusGained
        if (userField.getText().equals("User")) {
            userField.setText("");
        }
    }//GEN-LAST:event_userFieldFocusGained

    private void userFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_userFieldFocusLost
        if (userField.getText().equals("")) {
            userField.setText("User");
        }
    }//GEN-LAST:event_userFieldFocusLost

    private void passFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_passFieldFocusGained
        String pass = new String(passField.getPassword());
        if (pass.equals("jPasswordField1")) {
            passField.setText("");
        }
    }//GEN-LAST:event_passFieldFocusGained

    private void passFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_passFieldFocusLost
        char[] pass = passField.getPassword();
        if (pass.length == 0) {
            passField.setText("jPasswordField1");
        }
    }//GEN-LAST:event_passFieldFocusLost

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(UILogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UILogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UILogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UILogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new UILogin().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelGalaxy;
    private javax.swing.JLabel jLabelRetro;
    private javax.swing.JPanel jPanelGeneral;
    private javax.swing.JLabel labelGalaxy;
    private javax.swing.JLabel labelLogin;
    private javax.swing.JButton loginButton;
    private javax.swing.JPasswordField passField;
    private javax.swing.JTextField userField;
    // End of variables declaration//GEN-END:variables
}
