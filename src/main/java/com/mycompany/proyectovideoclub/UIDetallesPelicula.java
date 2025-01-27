package com.mycompany.proyectovideoclub;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.MediaTracker;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author oscar.lara
 */

public class UIDetallesPelicula extends JFrame {

    private DefaultTableModel tableModel;
    
 public UIDetallesPelicula() {
        // Inicializa la interfaz
        setTitle("Listado de Películas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Inicializa el modelo de tabla
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Título");
        tableModel.addColumn("Director");
        tableModel.addColumn("Formato");
        tableModel.addColumn("Duración");
        tableModel.addColumn("Género");
        tableModel.addColumn("Unidades Disponibles");
        tableModel.addColumn("Actor Principal");
        tableModel.addColumn("Actor Secundario 1");
        tableModel.addColumn("Actor Secundario 2");
        tableModel.addColumn("Sinopsis");
        tableModel.addColumn("Precio Venta");

        // Inicializa los componentes de la interfaz
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        // Inicializa el panel
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Cargar las películas desde la base de datos
        cargarPeliculas();

        // Agregar el panel al contenedor principal
        add(new JScrollPane(panel), BorderLayout.CENTER);
    }

    private void cargarPeliculas() {
        String query = "SELECT titulo, director, formato_id, duracion, genero, numDisponibleAlquiler, "
                + "actorProtagonista, actorSecundario1, actorSecundario2, sinopsis, precioVenta "
                + "FROM Peliculas JOIN Productos ON Peliculas.id = Productos.id ORDER BY titulo";

        try (Connection conn = Database.getConnection(); // Método de conexión
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                // Crear la fila con los datos de cada película
                String titulo = rs.getString("titulo");
                String director = rs.getString("director");
                String formato = rs.getString("formato_id");
                int duracion = rs.getInt("duracion");
                String genero = rs.getString("genero");
                int numDisponibleAlquiler = rs.getInt("numDisponibleAlquiler");
                String actorPrincipal = rs.getString("actorProtagonista");
                String actorSecundario1 = rs.getString("actorSecundario1");
                String actorSecundario2 = rs.getString("actorSecundario2");
                String sinopsis = rs.getString("sinopsis");
                double precioVenta = rs.getDouble("precioVenta");

                // Añadir la película al panel
                JButton boton = new JButton(titulo);
                boton.addActionListener(e -> mostrarDetallesPelicula(titulo, director, formato, duracion, genero, 
                        actorPrincipal, actorSecundario1, actorSecundario2, sinopsis, precioVenta, numDisponibleAlquiler));
                panel.add(boton);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar las películas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarDetallesPelicula(String titulo, String director, String formato, int duracion, String genero, 
                                         String actorPrincipal, String actorSecundario1, String actorSecundario2, 
                                         String sinopsis, double precioVenta, int numDisponibleAlquiler) {

        // Crear un panel para mostrar los detalles
        JPanel detallesPanel = new JPanel();
        detallesPanel.setLayout(new BoxLayout(detallesPanel, BoxLayout.Y_AXIS));

        // Agregar los detalles en etiquetas
        detallesPanel.add(new JLabel("Título: " + titulo));
        detallesPanel.add(new JLabel("Director: " + director));
        detallesPanel.add(new JLabel("Formato: " + formato));
        detallesPanel.add(new JLabel("Duración: " + duracion + " minutos"));
        detallesPanel.add(new JLabel("Género: " + genero));
        detallesPanel.add(new JLabel("Actor Principal: " + actorPrincipal));
        detallesPanel.add(new JLabel("Actor Secundario 1: " + actorSecundario1));
        detallesPanel.add(new JLabel("Actor Secundario 2: " + actorSecundario2));
        detallesPanel.add(new JLabel("Sinopsis: " + sinopsis));
        detallesPanel.add(new JLabel("Precio Venta: " + precioVenta));
        detallesPanel.add(new JLabel("Unidades Disponibles: " + numDisponibleAlquiler));

        // Imagen de la película
        String imagenPath = "images/" + titulo + (numDisponibleAlquiler == 0 ? "_alquilado.png" : "_disponible.png");
        ImageIcon portada = new ImageIcon(getClass().getClassLoader().getResource(imagenPath));
        JLabel imagenLabel = new JLabel(portada);
        detallesPanel.add(imagenLabel);

        // Crear un nuevo JFrame para mostrar los detalles
        JFrame detallesFrame = new JFrame("Detalles de la Película");
        detallesFrame.setSize(500, 600);
        detallesFrame.setLocationRelativeTo(this);
        detallesFrame.add(detallesPanel);
        detallesFrame.setVisible(true);
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
        panel = new javax.swing.JPanel();

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

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 692, Short.MAX_VALUE)
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 464, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UIDetallesPelicula ui = new UIDetallesPelicula();
            ui.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog detallesDialog;
    private javax.swing.JPanel panel;
    // End of variables declaration//GEN-END:variables

}
