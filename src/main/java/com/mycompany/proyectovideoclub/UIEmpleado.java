package com.mycompany.proyectovideoclub;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author oscar.lara
 */

public class UIEmpleado extends JFrame {

    private DefaultTableModel tableModel;
    private List<String> rutasImagenes;

 public UIEmpleado() {
        setTitle("Listado de Alquileres en Curso");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Llama a los componentes generados automáticamente
        initComponents();

        // Sobrescribe configuraciones personalizadas
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        // Configurar panelAlquileres (ya generado por NetBeans)
        
                btnCargarProximamente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarImagenes();
            }
                });

        panelAlquileresCurso.setLayout(new BoxLayout(panelAlquileresCurso, BoxLayout.Y_AXIS)); // Organiza los alquileres verticalmente
        JScrollPane scrollPane = new JScrollPane(panelAlquileresCurso);
        getContentPane().add(scrollPane, BorderLayout.CENTER); // Colocar el panel en el centro

        // Cargar datos de la base de datos
        cargarAlquileresEnCurso();
    }
    
    private void cargarImagenes() {
        // Usar un JFileChooser para que el usuario seleccione varias imágenes
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar imágenes");
        fileChooser.setMultiSelectionEnabled(true);  // Permite seleccionar varias imágenes
        fileChooser.setFileFilter(new FileNameExtensionFilter("Imágenes", "jpg", "png", "gif"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File[] files = fileChooser.getSelectedFiles();
            rutasImagenes = new ArrayList<>();
            for (File file : files) {
                rutasImagenes.add(file.getAbsolutePath());
            }

            // Crear y mostrar la interfaz UISocio11 con las imágenes cargadas
            //UISocio11 uiSocio11 = new UISocio11(rutasImagenes);
            //uiSocio11.setVisible(true);
        }
    }
                        

    private void cargarAlquileresEnCurso() {
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT p.titulo, a.fechaAlquiler, a.fechaEntrega " +
                           "FROM Alquileres a " +
                           "JOIN Productos p ON a.idProducto = p.id " +
                           "JOIN Socios s ON a.idSocio = s.id ";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    panelAlquileresCurso.removeAll();  // Limpiar el panel antes de agregar nuevos elementos

                    while (rs.next()) {
                        String titulo = rs.getString("titulo");
                        java.sql.Date fechaAlquiler = rs.getDate("fechaAlquiler");
                        java.sql.Date fechaEntrega = rs.getDate("fechaEntrega");

                        // Crear un JLabel para cada alquiler
                        String alquilerInfo = String.format("<html><b>Película:</b> %s <br><b>Fecha de alquiler:</b> %s <br><b>Fecha de entrega:</b> %s</html>",
                                titulo,  fechaAlquiler.toString(), fechaEntrega.toString());
                        
                        JLabel alquilerLabel = new JLabel(alquilerInfo);
                        panelAlquileresCurso.add(alquilerLabel);  // Agregar el JLabel al panel
                    }

                    panelAlquileresCurso.revalidate();  // Asegura que se repinte correctamente
                    panelAlquileresCurso.repaint();     // Repintar el panel para reflejar cambios
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar los alquileres: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

        detallesDialog = new javax.swing.JDialog();
        jLabel1 = new javax.swing.JLabel();
        panelAlquileresCurso = new javax.swing.JPanel();
        btnCargarProximamente = new javax.swing.JButton();

        javax.swing.GroupLayout detallesDialogLayout = new javax.swing.GroupLayout(detallesDialog.getContentPane());
        detallesDialog.getContentPane().setLayout(detallesDialogLayout);
        detallesDialogLayout.setHorizontalGroup(
            detallesDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        detallesDialogLayout.setVerticalGroup(
            detallesDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Estás en socio");

        javax.swing.GroupLayout panelAlquileresCursoLayout = new javax.swing.GroupLayout(panelAlquileresCurso);
        panelAlquileresCurso.setLayout(panelAlquileresCursoLayout);
        panelAlquileresCursoLayout.setHorizontalGroup(
            panelAlquileresCursoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 377, Short.MAX_VALUE)
        );
        panelAlquileresCursoLayout.setVerticalGroup(
            panelAlquileresCursoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 322, Short.MAX_VALUE)
        );

        btnCargarProximamente.setText("jButton1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(146, 146, 146)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(514, 514, 514)
                        .addComponent(panelAlquileresCurso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(171, 171, 171)
                        .addComponent(btnCargarProximamente)))
                .addContainerGap(364, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(panelAlquileresCurso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCargarProximamente)
                .addContainerGap(228, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UIEmpleado ui = new UIEmpleado();
            ui.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCargarProximamente;
    private javax.swing.JDialog detallesDialog;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel panelAlquileresCurso;
    // End of variables declaration//GEN-END:variables

}
