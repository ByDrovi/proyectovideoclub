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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.time.LocalDate;
import javax.swing.JButton;

/**
 *
 * @author oscar.lara
 */
public class UISocio extends JFrame {

    private static DefaultTableModel tableModel;  // Declarar estático si se usa en un contexto estático
    private Socios usuarioLogin;

    public UISocio(Socios usuarioLogin) {
        this.usuarioLogin = usuarioLogin;  // Asigna el logUser recibido
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
        // Consulta SQL con filtro por título
        String query = ""
                + "SELECT p.titulo, pl.actorProtagonista, f.nombre AS formato_id, p.anioLanzamiento, p.numDisponibleAlquiler, p.genero, p.subgenero "
                + "FROM Productos p "
                + "JOIN Peliculas pl ON p.id = pl.id "
                + "JOIN Formatos f ON pl.formato_id = f.id "
                + "WHERE p.esBaja = FALSE AND p.titulo LIKE ?"; // Filtrar por título
        System.out.println(filtroTabla.getText());
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/videoclub", "root", ""); PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Configurar el parámetro del filtro
            pstmt.setString(1, "%" + filtroTabla.getText() + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
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
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Mostrar cualquier error en la consola
        }
    }

    private void inicializarComponentes() {
        welcomeUser.setText("¡Hola, " + usuarioLogin.getNombre() + "!");

        jLabelProximamente.setHorizontalAlignment(JLabel.CENTER);
        jLabelProximamente.setBounds(50, 50, 600, 400); // Ajusta las dimensiones según sea necesario
        ImagenesDeslizantes deslizantes = new ImagenesDeslizantes();
        deslizantes.iniciarDeslizamiento(jLabelProximamente);

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

        // Acción para el seguimiento
        btnSeguimiento.addActionListener(e -> {
            try ( Connection conn = Database.getConnection()) {
                // Consulta para obtener los productos alquilados del socio
                String query = "SELECT p.titulo, a.fechaEntrega, a.cuotaAlquiler "
                        + "FROM Alquileres a "
                        + "JOIN Productos p ON a.idProducto = p.id "
                        + "WHERE a.idSocio = ? "
                        + "ORDER BY a.fechaEntrega";

                try ( PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setInt(1, usuarioLogin.getId());

                    try ( ResultSet rs = stmt.executeQuery()) {
                        StringBuilder seguimiento = new StringBuilder("Productos alquilados:\n");

                        // Recorrer los resultados y construir la lista
                        while (rs.next()) {
                            String titulo = rs.getString("titulo");
                            Date fechaEntrega = rs.getDate("fechaEntrega");
                            double cuotaAlquiler = rs.getDouble("cuotaAlquiler");

                            seguimiento.append("Título: ").append(titulo)
                                    .append("\nFecha de entrega: ").append(fechaEntrega)
                                    .append("\nPrecio: ").append(cuotaAlquiler).append("€\n\n");
                        }

                        // Mostrar la lista al usuario
                        JOptionPane.showMessageDialog(this, seguimiento.toString(), "Seguimiento de Alquileres", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al cargar el seguimiento: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Acción para el botón de devolución
        btnDevolucion.addActionListener(e -> {
            try {
                // Obtener datos necesarios
                String fechaDevolucionStr = tfFechaHoy.getText(); // Fecha de devolución desde el JTextField
                LocalDate fechaDevolucion = LocalDate.parse(fechaDevolucionStr); // Convertir a LocalDate
                int selectedRow = table.getSelectedRow();

                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(UISocio.this, "Seleccione una película para devolver.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String titulo = (String) tableModel.getValueAt(selectedRow, 0); // Título de la película seleccionada

                // Obtener el ID del producto
                String queryProductoId = ""
                        + "SELECT id "
                        + "FROM Productos "
                        + "WHERE titulo = ?";
                try ( Connection conn = Database.getConnection();  PreparedStatement stmt = conn.prepareStatement(queryProductoId)) {
                    stmt.setString(1, titulo);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        int productoId = rs.getInt("id");

                        // Llamada al método devolverProducto
                        Alquiler.devolverProducto(productoId, usuarioLogin.getId(), fechaDevolucion);

                        JOptionPane.showMessageDialog(UISocio.this, "Producto devuelto correctamente.");
                    }
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(UISocio.this, "Error al procesar la devolución: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void mostrarDetallesPelicula(int rowIndex) {
        String titulo = (String) tableModel.getValueAt(rowIndex, 0); // Obtener el título de la película seleccionada

        // Consulta SQL para obtener los detalles de la película
        String query = ""
                + "SELECT "
                + "p.titulo, "
                + "f.nombre "
                + "AS formato, p.anioLanzamiento, p.numDisponibleAlquiler, "
                + "p.genero, p.subgenero, p.sinopsis, pl.cuotaAlquilerPeliculas, pl.actorProtagonista, pl.actorSecundario1, "
                + "pl.actorSecundario2 "
                + "FROM Productos p "
                + "JOIN Peliculas pl ON p.id = pl.id "
                + "JOIN Formatos f ON pl.formato_id = f.id "
                + "WHERE p.titulo = ? AND p.esBaja = FALSE"; // Aseguramos que la película no esté dada de baja

        try ( Connection conn = Database.getConnection();  PreparedStatement stmt = conn.prepareStatement(query)) {
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
                String reparto = "Reparto: " + rs.getString("actorProtagonista") + ", "
                        + rs.getString("actorSecundario1") + ", "
                        + rs.getString("actorSecundario2");

                // Utilizamos JTextArea para el texto largo
                JTextArea detallesTextArea = new JTextArea();
                detallesTextArea.setEditable(false);  // Hacer el área de texto no editable
                detallesTextArea.setLineWrap(true);   // Activar el word wrap
                detallesTextArea.setWrapStyleWord(true);

                // Hacer que el word wrap ocurra solo al final de las palabras
                detallesTextArea.setText(
                        "" + rs.getString("titulo") + "\n"
                        + "Formato: " + rs.getString("formato") + "\n"
                        + "Año: " + rs.getInt("anioLanzamiento") + "\n"
                        + "Género: " + rs.getString("genero") + "\n"
                        + "Subgénero: " + rs.getString("subgenero") + "\n"
                        + "Unidades disponibles: " + rs.getInt("numDisponibleAlquiler") + "\n"
                        + reparto + "\n"
                        + // Mostrar el reparto concatenado
                        "Sinopsis: " + rs.getString("sinopsis") + "\n"
                        + "Precio de alquiler: " + rs.getDouble("cuotaAlquilerPeliculas")
                );

                // Añadir el JTextArea al panel de detalles
                detallesPanel.add(new JScrollPane(detallesTextArea));  // Usamos JScrollPane para habilitar el scroll

                // Agregar un botón de "Alquilar" (toggle)
                toggleAlquiler = new JToggleButton("Alquilar");
                toggleAlquiler.setEnabled(rs.getInt("numDisponibleAlquiler") > 0); // Activar/desactivar según la disponibilidad
                toggleAlquiler.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (toggleAlquiler.isSelected()) {
                            try ( Connection conn = Database.getConnection()) {
                                String fechaAlquilerStr = tfFechaHoy.getText(); // Fecha de alquiler desde el JTextField
                                LocalDate fechaAlquiler = LocalDate.parse(fechaAlquilerStr); // Convertir a LocalDate
                                int selectedRow = table.getSelectedRow();
                                if (selectedRow == -1) {
                                    JOptionPane.showMessageDialog(UISocio.this, "Seleccione una película para alquilar.", "Error", JOptionPane.ERROR_MESSAGE);
                                    toggleAlquiler.setSelected(false);
                                    return;
                                }
                                String titulo = (String) tableModel.getValueAt(selectedRow, 0); // Título de la película seleccionada

                                // Consulta para obtener cuota de alquiler y unidades disponibles
                                String queryDetalles = ""
                                        + "SELECT pl.cuotaAlquilerPeliculas, p.numDisponibleAlquiler, p.id "
                                        + "FROM Peliculas pl "
                                        + "JOIN Productos p ON pl.id = p.id "
                                        + "WHERE p.titulo = ? AND p.esBaja = FALSE";

                                try ( PreparedStatement stmt = conn.prepareStatement(queryDetalles)) {
                                    stmt.setString(1, titulo);
                                    ResultSet rs = stmt.executeQuery();
                                    if (rs.next()) {
                                        double cuotaAlquiler = rs.getDouble("cuotaAlquilerPeliculas");
                                        int numDisponible = rs.getInt("numDisponibleAlquiler");
                                        int productoId = rs.getInt("id");

                                        if (numDisponible <= 0) {
                                            JOptionPane.showMessageDialog(UISocio.this, "No hay unidades disponibles para alquilar.", "Error", JOptionPane.ERROR_MESSAGE);
                                            toggleAlquiler.setSelected(false);
                                            return;
                                        }

                                        // Llamada al método alquilarProducto
                                        Alquiler.alquilarProducto(productoId, usuarioLogin.getId(), fechaAlquiler, cuotaAlquiler);

                                        JOptionPane.showMessageDialog(UISocio.this, "Producto alquilado correctamente.\n"
                                                + "Título: " + titulo + "\n"
                                                + "Fecha de alquiler: " + fechaAlquiler + "\n"
                                                + "Fecha de entrega: " + fechaAlquiler.plusDays(3) + "\n"
                                                + "Cuota de alquiler: " + cuotaAlquiler + "€");
                                    }
                                }
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(UISocio.this, "Error al procesar el alquiler: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(UISocio.this, "Alquiler cancelado.");
                        }
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
        jLabel2 = new javax.swing.JLabel();
        btnSeguimiento = new javax.swing.JButton();
        btnDevolucion = new javax.swing.JButton();
        btnActualizarTabla = new javax.swing.JButton();
        welcomeUser = new javax.swing.JLabel();
        filtroTabla = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabelProximamente = new javax.swing.JLabel();

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

        toggleAlquiler.setText("Alquilarrrr");
        toggleAlquiler.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleAlquilerActionPerformed(evt);
            }
        });

        jLabel2.setText("Fecha actual:");

        btnSeguimiento.setText("Seguimiento");
        btnSeguimiento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSeguimientoActionPerformed(evt);
            }
        });

        btnDevolucion.setText("Devolución");
        btnDevolucion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDevolucionActionPerformed(evt);
            }
        });

        btnActualizarTabla.setText("Actualizar");
        btnActualizarTabla.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarTablaActionPerformed(evt);
            }
        });

        welcomeUser.setText("Bienvenido");

        filtroTabla.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                filtroTablaKeyTyped(evt);
            }
        });

        jLabel3.setText("Buscar:");

        javax.swing.GroupLayout jPanelSocioLayout = new javax.swing.GroupLayout(jPanelSocio);
        jPanelSocio.setLayout(jPanelSocioLayout);
        jPanelSocioLayout.setHorizontalGroup(
            jPanelSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSocioLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanelSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelSocioLayout.createSequentialGroup()
                        .addGroup(jPanelSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelSocioLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnActualizarTabla))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelSocioLayout.createSequentialGroup()
                                .addComponent(btnSeguimiento)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(filtroTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(14, 14, 14))
                    .addGroup(jPanelSocioLayout.createSequentialGroup()
                        .addGroup(jPanelSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelSocioLayout.createSequentialGroup()
                                .addComponent(btnDevolucion, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(toggleAlquiler))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 668, Short.MAX_VALUE)
                            .addGroup(jPanelSocioLayout.createSequentialGroup()
                                .addGroup(jPanelSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(welcomeUser, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanelSocioLayout.createSequentialGroup()
                                        .addGroup(jPanelSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(tfFechaHoy, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel2))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(nombreLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        jPanelSocioLayout.setVerticalGroup(
            jPanelSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSocioLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(welcomeUser, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanelSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelSocioLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nombreLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelSocioLayout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(6, 6, 6)
                                .addComponent(tfFechaHoy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelSocioLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                        .addComponent(btnActualizarTabla)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanelSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(filtroTabla, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSeguimiento))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(toggleAlquiler)
                    .addComponent(btnDevolucion))
                .addContainerGap(106, Short.MAX_VALUE))
        );

        jLabel1.setText("PRÓXIMAMENTE");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(59, 59, 59)
                .addComponent(jLabelProximamente, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(88, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(78, 78, 78)
                .addComponent(jLabelProximamente, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(80, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jPanelSocio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39)
                .addComponent(jLabel1)
                .addGap(116, 116, 116)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(35, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(jPanelSocio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(157, 157, 157)
                        .addComponent(jLabel1)))
                .addGap(6, 6, 6))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(68, 68, 68))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tfFechaHoyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfFechaHoyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfFechaHoyActionPerformed

    private void btnSeguimientoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSeguimientoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSeguimientoActionPerformed

    private void btnActualizarTablaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarTablaActionPerformed
        cargarPeliculas();
    }//GEN-LAST:event_btnActualizarTablaActionPerformed

    private void btnDevolucionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDevolucionActionPerformed

    }//GEN-LAST:event_btnDevolucionActionPerformed

    private void toggleAlquilerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleAlquilerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_toggleAlquilerActionPerformed

    private void filtroTablaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_filtroTablaKeyTyped
        cargarPeliculas();
    }//GEN-LAST:event_filtroTablaKeyTyped


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizarTabla;
    private javax.swing.JButton btnDevolucion;
    private javax.swing.JButton btnSeguimiento;
    private javax.swing.JDialog detallesDialog;
    private javax.swing.JTextField filtroTabla;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabelProximamente;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelSocio;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel nombreLabel;
    private javax.swing.JTable table;
    private javax.swing.JTextField tfFechaHoy;
    private javax.swing.JToggleButton toggleAlquiler;
    private javax.swing.JLabel welcomeUser;
    // End of variables declaration//GEN-END:variables

}
