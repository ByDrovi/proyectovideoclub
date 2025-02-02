package com.mycompany.proyectovideoclub;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author oscar.lara
 */
public class GestionEmpleados1 extends javax.swing.JFrame {

    /**
     * Creates new form GestionEmpleados
     */
    public GestionEmpleados1() {
        initComponents();
        setContentPane(jLayeredBackground);
        jLayeredBackground.setLayout(null);
        Utilidades.cargarImagenEnLabel(jLabelBackground, "/images/blueshades.png");
        
        
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                String logUser = tflogUser.getText();
                String logPass = tflogPass.getText();
                String nombre = tfnombre.getText();
                String apellidos = tfapellidos.getText();
                String dniUser = tfdni.getText();
                String fechaNacimientoStr = tfNacimiento.getText();
                String fechaAltaStr = tfAlta.getText();
                String fechaBajaStr = tfBaja.getText();
                boolean esActivo = !Inactivo.isSelected();  // Se activa si "Inactivo" no está marcado

                // Validar que los campos obligatorios no estén vacíos
                if (logUser.isEmpty() || logPass.isEmpty() || nombre.isEmpty() || apellidos.isEmpty() || dniUser.isEmpty()) {
                    javax.swing.JOptionPane.showMessageDialog(null, "Rellena todos los campos.");
                    return;
                }

                // Convertir las fechas de String a java.sql.Date
                Date fechaNacimiento = convertirFecha(fechaNacimientoStr);
                Date fechaAlta = convertirFecha(fechaAltaStr);
                Date fechaBaja = convertirFecha(fechaBajaStr);

                if (fechaNacimiento == null || fechaAlta == null || fechaBaja == null) {
                    javax.swing.JOptionPane.showMessageDialog(null, "Formato de fecha incorrecto.");
                    return;
                }

                // Agregar empleado a la base de datos
                try (Connection conn = Database.getConnection()) {
                    Empleados.agregarEmpleado(
                            conn,
                            logUser,
                            logPass,
                            nombre,
                            apellidos,
                            dniUser,
                            fechaNacimiento,
                            fechaAlta,
                            "EMPLEADO", // Tipo de usuario
                            fechaBaja,
                            esActivo // Estado de activo
                    );
                    javax.swing.JOptionPane.showMessageDialog(null, "Empleado agregado con éxito.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    javax.swing.JOptionPane.showMessageDialog(null, "Error al agregar empleado: " + ex.getMessage());
                }
            }
        });
    }

    // Método para convertir String a java.sql.Date
    private Date convertirFecha(String fechaStr) {
        try {
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date fecha = formato.parse(fechaStr);
            return new Date(fecha.getTime());
        } catch (Exception e) {
            return null;
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

        tflogUser = new javax.swing.JTextField();
        tflogPass = new javax.swing.JTextField();
        btnGuardar = new javax.swing.JButton();
        jLabelUser = new javax.swing.JLabel();
        tfnombre = new javax.swing.JTextField();
        jLabelNombre = new javax.swing.JLabel();
        jLabelApellido = new javax.swing.JLabel();
        jLabelDNI = new javax.swing.JLabel();
        jLabelNacimiento = new javax.swing.JLabel();
        jLabelAlta = new javax.swing.JLabel();
        jLabelBaja = new javax.swing.JLabel();
        Inactivo = new javax.swing.JCheckBox();
        tfapellidos = new javax.swing.JTextField();
        tfdni = new javax.swing.JTextField();
        tfAlta = new javax.swing.JTextField();
        tfBaja = new javax.swing.JTextField();
        jLabelPass = new javax.swing.JLabel();
        btnBuscarEmpleado = new javax.swing.JButton();
        tfBuscarEmpleado = new javax.swing.JTextField();
        jLayeredBackground = new javax.swing.JLayeredPane();
        jLabelBackground = new javax.swing.JLabel();
        tfNacimiento = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tflogUser.setCaretColor(new java.awt.Color(51, 51, 255));
        tflogUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tflogUserActionPerformed(evt);
            }
        });

        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agregarPelicula(evt);
            }
        });

        jLabelUser.setFont(new java.awt.Font("NSimSun", 1, 14)); // NOI18N
        jLabelUser.setText("User");

        jLabelNombre.setText("Nombre:");

        jLabelApellido.setText("Apellidos:");

        jLabelDNI.setText("DNI:");

        jLabelNacimiento.setText("Fecha nacimiento:");

        jLabelAlta.setText("Fecha alta:");

        jLabelBaja.setText("Fecha baja:");

        Inactivo.setText("Inactivo");
        Inactivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InactivoActionPerformed(evt);
            }
        });

        tfapellidos.setText(" ");

        jLabelPass.setText("Contraseña:");

        btnBuscarEmpleado.setText("Buscar");

        jLabelBackground.setText("jLabel1");

        jLayeredBackground.setLayer(jLabelBackground, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredBackground.setLayer(tfNacimiento, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredBackgroundLayout = new javax.swing.GroupLayout(jLayeredBackground);
        jLayeredBackground.setLayout(jLayeredBackgroundLayout);
        jLayeredBackgroundLayout.setHorizontalGroup(
            jLayeredBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredBackgroundLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelBackground, javax.swing.GroupLayout.PREFERRED_SIZE, 501, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfNacimiento, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(287, Short.MAX_VALUE))
        );
        jLayeredBackgroundLayout.setVerticalGroup(
            jLayeredBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredBackgroundLayout.createSequentialGroup()
                .addGroup(jLayeredBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jLayeredBackgroundLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabelBackground, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jLayeredBackgroundLayout.createSequentialGroup()
                        .addGap(86, 86, 86)
                        .addComponent(tfNacimiento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(76, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelUser)
                            .addComponent(jLabelNombre)
                            .addComponent(jLabelApellido)
                            .addComponent(jLabelDNI)
                            .addComponent(jLabelPass))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(tfapellidos, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(83, 83, 83)
                                .addComponent(Inactivo))
                            .addComponent(tfdni, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(tflogUser, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
                                    .addComponent(tflogPass))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(99, 99, 99)
                                        .addComponent(jLabelAlta)
                                        .addGap(42, 42, 42)
                                        .addComponent(tfAlta, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(72, 72, 72)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addComponent(jLabelBaja)
                                                    .addComponent(jLabelNacimiento))
                                                .addGap(28, 28, 28)
                                                .addComponent(tfBaja, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(38, 38, 38)
                                                .addComponent(btnGuardar))))))
                            .addComponent(tfnombre, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(tfBuscarEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41)
                        .addComponent(btnBuscarEmpleado)))
                .addGap(67, 67, 67)
                .addComponent(jLayeredBackground, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(53, 53, 53)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(tflogUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabelUser)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(46, 46, 46)
                                .addComponent(jLabelNacimiento)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tflogPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelPass)
                            .addComponent(jLabelAlta)
                            .addComponent(tfAlta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tfnombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelNombre)
                            .addComponent(jLabelBaja)
                            .addComponent(tfBaja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelApellido)
                            .addComponent(tfapellidos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Inactivo))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelDNI)
                            .addComponent(tfdni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnGuardar))
                        .addGap(27, 27, 27)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tfBuscarEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnBuscarEmpleado)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addComponent(jLayeredBackground, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(79, Short.MAX_VALUE))
        );

        CustomTextField customTextField = new CustomTextField(20, 20); // Borde redondeado con un radio de 20px

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tflogUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tflogUserActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tflogUserActionPerformed

    private void agregarPelicula(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agregarPelicula
        // TODO add your handling code here:
    }//GEN-LAST:event_agregarPelicula

    private void InactivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InactivoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_InactivoActionPerformed

//    /**
//     * @param args the command line arguments
//     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(GestionEmpleados.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(GestionEmpleados.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(GestionEmpleados.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(GestionEmpleados.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new GestionEmpleados().setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox Inactivo;
    private javax.swing.JButton btnBuscarEmpleado;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JLabel jLabelAlta;
    private javax.swing.JLabel jLabelApellido;
    private javax.swing.JLabel jLabelBackground;
    private javax.swing.JLabel jLabelBaja;
    private javax.swing.JLabel jLabelDNI;
    private javax.swing.JLabel jLabelNacimiento;
    private javax.swing.JLabel jLabelNombre;
    private javax.swing.JLabel jLabelPass;
    private javax.swing.JLabel jLabelUser;
    private javax.swing.JLayeredPane jLayeredBackground;
    private javax.swing.JTextField tfAlta;
    private javax.swing.JTextField tfBaja;
    private javax.swing.JTextField tfBuscarEmpleado;
    private javax.swing.JTextField tfNacimiento;
    private javax.swing.JTextField tfapellidos;
    private javax.swing.JTextField tfdni;
    private javax.swing.JTextField tflogPass;
    private javax.swing.JTextField tflogUser;
    private javax.swing.JTextField tfnombre;
    // End of variables declaration//GEN-END:variables
}
