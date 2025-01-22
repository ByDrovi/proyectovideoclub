package com.mycompany.proyectovideoclub;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import com.mycompany.proyectovideoclub.Database;


public class UISocio extends JFrame {

 private static DefaultTableModel tableModel;  // Declarar estático si se usa en un contexto estático
 private static String logUser;  // Hacer logUser estático

public UISocio(String logUser) {
    this.logUser = logUser;  // Asigna el logUser recibido
    setTitle("Listado de Películas");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(800, 600);
    setLocationRelativeTo(null);

    // Llama a los componentes generados automáticamente
    initComponents();
    //mostrarNombreSocio();

    // Sobrescribe configuraciones de la tabla y carga los datos
    inicializarComponentes();
}

private void cargarPeliculas() {
    // Asumiendo que tienes una conexión a la base de datos
    String query = ""
            + "SELECT p.titulo, pl.actorProtagonista, f.nombre AS formato_id, p.anioLanzamiento, p.numDisponibleAlquiler, p.genero, p.subgenero " +
                   "FROM Productos p " +
                   "JOIN Peliculas pl ON p.id = pl.id " +
                   "JOIN Formatos f ON pl.formato_id = f.id " +
                   "WHERE p.esBaja = FALSE"; // Solo películas activas (no dadas de baja)

    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/videoclub", "root", "");
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {

        // Limpiar el modelo de tabla antes de cargar los nuevos datos
        tableModel.setRowCount(0);

        // Recorrer los resultados y llenar el modelo de la tabla
        while (rs.next()) {
            String titulo = rs.getString("titulo");
            String formato = rs.getString("formato_id");
            int anioLanzamiento = rs.getInt("anioLanzamiento");
            int numDisponibleAlquiler = rs.getInt("numDisponibleAlquiler");
            String genero = rs.getString("genero");
            String subgenero = rs.getString("subgenero");

            // Agregar los datos a la tabla
            Object[] row = {titulo, formato, anioLanzamiento, numDisponibleAlquiler, genero, subgenero};
            tableModel.addRow(row);
        }
    } catch (SQLException e) {
        e.printStackTrace(); // Mostrar cualquier error en la consola
    }
}

private void inicializarComponentes() {

    // Crear el modelo de tabla con las nuevas columnas
    String[] columnNames = {"Título", "Formato", "Duración", "Unidades", "Género", "Subgénero"};
    tableModel = new DefaultTableModel(columnNames, 0);

    // Asigna el modelo de tabla personalizado a la tabla generada automáticamente
    table.setModel(tableModel);

    // Habilitar el ordenamiento alfabético por columnas
    table.setAutoCreateRowSorter(true);

    // Deshabilitar la edición directa de las celdas
    table.setDefaultEditor(Object.class, null);

    // Agregar listener para manejar clics en las filas
    table.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) { // Detecta un clic doble
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    mostrarDetallesPelicula(selectedRow); // Mostrar detalles de la película seleccionada
                }
            }
        }
    });

    // Cargar los datos desde la base de datos
    cargarPeliculas();
}

private void mostrarDetallesPelicula(int rowIndex) {
    String titulo = (String) tableModel.getValueAt(rowIndex, 0); // Obtener el título de la película seleccionada

    // Consulta SQL para obtener los detalles de la película
    String query = "SELECT p.titulo, f.nombre AS formato, p.anioLanzamiento, p.numDisponibleAlquiler, " +
                   "p.genero, p.subgenero, p.sinopsis, pl.cuotaAlquilerPeliculas, pl.actorProtagonista, pl.actorSecundario1, " +
                   "pl.actorSecundario2 " +
                   "FROM Productos p " +
                   "JOIN Peliculas pl ON p.id = pl.id " +
                   "JOIN Formatos f ON pl.formato_id = f.id " +
                   "WHERE p.titulo = ? AND p.esBaja = FALSE"; // Aseguramos que la película no esté dada de baja

    try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, titulo);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            // Crear el JDialog para mostrar los detalles de la película
            JDialog detallesDialog = new JDialog(this, "Detalles de la Película", true);
            detallesDialog.setSize(600, 400);
            detallesDialog.setLocationRelativeTo(this);

            // Configuración del layout principal
            detallesDialog.setLayout(new BorderLayout());

            // Panel para la portada
            JPanel portadaPanel = new JPanel();
            JLabel portadaLabel = new JLabel();
            portadaPanel.add(portadaLabel);

            // Obtener la imagen de portada (alquilado o disponible)
            String imagenPath = "images/" + titulo + (rs.getInt("numDisponibleAlquiler") == 0 ? "_alquilado.png" : "_disponible.png");
            URL imageUrl = getClass().getClassLoader().getResource(imagenPath);

            if (imageUrl != null) {
                ImageIcon portada = new ImageIcon(imageUrl);
                Image img = portada.getImage();  // Obtener la imagen original

                // Redimensionar la imagen manteniendo las proporciones
                int width = 150;  // Ancho deseado
                int height = -1;  // Mantener proporciones (el valor negativo indicará auto-ajuste)
                Image imgEscalada = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);

                portadaLabel.setIcon(new ImageIcon(imgEscalada));  // Asignar la imagen redimensionada al JLabel
            } else {
                portadaLabel.setText("Imagen no disponible");
            }

            // Panel para los detalles
            JPanel detallesPanel = new JPanel();
            detallesPanel.setLayout(new BoxLayout(detallesPanel, BoxLayout.Y_AXIS));

            // Concatenar actores en una sola cadena bajo la etiqueta "Reparto"
            String reparto = "Reparto: " + rs.getString("actorProtagonista") + ", " +
                             rs.getString("actorSecundario1") + ", " +
                             rs.getString("actorSecundario2");

            // Utilizamos JTextArea para el texto largo
            JTextArea detallesTextArea = new JTextArea();
            detallesTextArea.setEditable(false);  // Hacer el área de texto no editable
            detallesTextArea.setLineWrap(true);   // Activar el word wrap
            detallesTextArea.setWrapStyleWord(true);  


// Hacer que el word wrap ocurra solo al final de las palabras
            detallesTextArea.setText(
                "" + rs.getString("titulo") + "\n" +
                "Formato: " + rs.getString("formato") + "\n" +
                "Año: " + rs.getInt("anioLanzamiento") + "\n" +
                "Género: " + rs.getString("genero") + "\n" +
                "Subgénero: " + rs.getString("subgenero") + "\n" +
                "Unidades disponibles: " + rs.getInt("numDisponibleAlquiler") + "\n" +
                reparto + "\n" +  // Mostrar el reparto concatenado
                "Sinopsis: " + rs.getString("sinopsis") + "\n" +
                "Precio de alquiler: " + rs.getDouble("cuotaAlquilerPeliculas")
            );

            // Añadir el JTextArea al panel de detalles
            detallesPanel.add(new JScrollPane(detallesTextArea));  // Usamos JScrollPane para habilitar el scroll

            // Agregar un botón de "Alquilar" (toggle)
            toggleAlquiler = new JToggleButton("Alquilar");
            toggleAlquiler.setEnabled(rs.getInt("numDisponibleAlquiler") > 0); // Activar/desactivar según la disponibilidad
            toggleAlquiler.addActionListener(e -> {
                if (toggleAlquiler.isSelected()) {
                    JOptionPane.showMessageDialog(this, "Producto alquilado: " + titulo);
                } else {
                    JOptionPane.showMessageDialog(this, "Alquiler cancelado.");
                }
            });

            // Añadir el botón de alquiler al panel de detalles
            detallesPanel.add(toggleAlquiler);

            // Añadir los paneles al JDialog
            detallesDialog.add(portadaPanel, BorderLayout.WEST);
            detallesDialog.add(detallesPanel, BorderLayout.CENTER);

            // Mostrar el JDialog con los detalles
            detallesDialog.setVisible(true);
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al cargar los detalles: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
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
        jPanelSocio = new javax.swing.JPanel();
        nombreLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        tfFechaHoy = new javax.swing.JTextField();
        toggleAlquiler = new javax.swing.JToggleButton();
        jLabel1 = new javax.swing.JLabel();
        jLabelProximamente = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btnSeguimiento = new javax.swing.JButton();
        btnDevolucion = new javax.swing.JButton();

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
        setMaximumSize(new java.awt.Dimension(21474, 21474));

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(table);

        tfFechaHoy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfFechaHoyActionPerformed(evt);
            }
        });

        toggleAlquiler.setText("Alquilar");

        jLabel1.setText("PRÓXIMAMENTE");

        jLabel2.setText("Fecha actual:");

        btnSeguimiento.setText("Seguimiento");

        btnDevolucion.setText("Devolución");

        javax.swing.GroupLayout jPanelSocioLayout = new javax.swing.GroupLayout(jPanelSocio);
        jPanelSocio.setLayout(jPanelSocioLayout);
        jPanelSocioLayout.setHorizontalGroup(
            jPanelSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSocioLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanelSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnDevolucion, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelSocioLayout.createSequentialGroup()
                        .addGroup(jPanelSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelSocioLayout.createSequentialGroup()
                                .addGroup(jPanelSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tfFechaHoy, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(nombreLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelSocioLayout.createSequentialGroup()
                                .addComponent(btnSeguimiento)
                                .addGap(207, 207, 207)
                                .addComponent(toggleAlquiler))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanelSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabelProximamente, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelSocioLayout.setVerticalGroup(
            jPanelSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSocioLayout.createSequentialGroup()
                .addGroup(jPanelSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelSocioLayout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(nombreLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelSocioLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tfFechaHoy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)))
                .addGroup(jPanelSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelSocioLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabelProximamente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(toggleAlquiler)
                    .addComponent(btnSeguimiento))
                .addGap(18, 18, 18)
                .addComponent(btnDevolucion)
                .addContainerGap(70, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jPanelSocio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jPanelSocio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tfFechaHoyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfFechaHoyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfFechaHoyActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String logUser = "usuarioEjemplo"; // Asignar un valor a logUser
            UISocio ui = new UISocio(logUser);
            ui.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDevolucion;
    private javax.swing.JButton btnSeguimiento;
    private javax.swing.JDialog detallesDialog;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelProximamente;
    private javax.swing.JPanel jPanelSocio;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel nombreLabel;
    private javax.swing.JTable table;
    private javax.swing.JTextField tfFechaHoy;
    private javax.swing.JToggleButton toggleAlquiler;
    // End of variables declaration//GEN-END:variables

}
