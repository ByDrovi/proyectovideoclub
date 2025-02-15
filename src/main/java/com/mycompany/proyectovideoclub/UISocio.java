package com.mycompany.proyectovideoclub;

import java.awt.*;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Image;
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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 *
 * @author oscar.lara
 */
public class UISocio extends JFrame {

    private static DefaultTableModel tableModel;  // Declarar estático si se usa en un contexto estático
    private Socios usuarioLogin;

    public UISocio() {
    }

    public UISocio(Socios usuarioLogin) {
        this.usuarioLogin = usuarioLogin;  // Asigna el logUser recibido
        setTitle("Listado de Películas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        // Llama a los componentes generados automáticamente
        initComponents();
        setLocationRelativeTo(null); // Abrir centrado en la pantalla, debe ir después de initComponents

        //Utilidades.cargarImagenEnLabel(labelBackground, "/images/blueshades.png");
        //Utilidades.cargarImagenEnLabel(labelTextoProximamente, "/images/proximamente.png");
        //mostrarNombreSocio();
        Utilidades.setPredefinedText(tfFechaHoy, "yyyy-mm-dd");

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
                + "WHERE p.esBaja = FALSE AND LOWER(p.titulo) LIKE LOWER(?)"; // Filtrar por título
        try ( Connection conn = Database.getConnection();  PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Configurar el parámetro del filtro
            pstmt.setString(1, "%" + filtroTabla.getText().toLowerCase() + "%");

            try ( ResultSet rs = pstmt.executeQuery()) {
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

        ImagenesDeslizantes deslizantes = new ImagenesDeslizantes();
        deslizantes.iniciarDeslizamiento(jLabelProximamente, siguiente);

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
                    int selectedRowView = table.getSelectedRow(); // Obtiene el índice visual de la fila seleccionada

                    if (selectedRowView != -1) {
                        // Convierte el índice visual a índice en el modelo de datos
                        int selectedRowModel = table.convertRowIndexToModel(selectedRowView);

                        // Llama al método para mostrar los detalles con el índice correcto en el modelo de datos
                        mostrarDetallesPelicula(selectedRowModel);
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

        //Boton salir
        btnSalir.addActionListener(e -> {
            // Mostrar mensaje emergente
            JOptionPane.showMessageDialog(UISocio.this, "Nos alegramos de verte, ¡hasta pronto!", "Bye, bye...", JOptionPane.INFORMATION_MESSAGE);

            // Cerrar la ventana actual
            dispose();

            // Abrir la interfaz de inicio de sesión
            new UILogin().setVisible(true);
        });

        // Acción para el botón de devolución
        btnDevolucion.addActionListener(e -> {
            try {
                // Obtener datos necesarios
                String fechaDevolucionStr = tfFechaHoy.getText(); // Fecha de devolución desde el JTextField
                if (fechaDevolucionStr.equals("yyyy-mm-dd")) {
                    JOptionPane.showMessageDialog(UISocio.this, "Selecciona la fecha en la que estas devolviendo", "Error", JOptionPane.ERROR_MESSAGE);
                    toggleAlquiler.setSelected(false);
                    return;
                }
                LocalDate fechaDevolucion = LocalDate.parse(fechaDevolucionStr); // Convertir a LocalDate
                int selectedRowView = table.getSelectedRow();

                if (selectedRowView == -1) {
                    JOptionPane.showMessageDialog(UISocio.this, "Seleccione una película para devolver.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int selectedRowModel = table.convertRowIndexToModel(selectedRowView);
                String titulo = (String) tableModel.getValueAt(selectedRowModel, 0); // Título de la película seleccionada

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
                + "p.genero, p.subgenero, p.sinopsis, pl.director, pl.cuotaAlquilerPeliculas, pl.actorProtagonista, pl.actorSecundario1, "
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
                portadaPanel.setLayout(new BorderLayout());
                JLabel portadaLabel = new JLabel();
                portadaLabel.setHorizontalAlignment(SwingConstants.CENTER);
                portadaPanel.add(portadaLabel, BorderLayout.CENTER);

                // Obtener la imagen de portada (alquilado o disponible)
                String imagenPath = "images/" + titulo + (rs.getInt("numDisponibleAlquiler") == 0 ? "_alquilado.png" : "_disponible.png");
                URL imageUrl = getClass().getClassLoader().getResource(imagenPath);

                if (imageUrl != null) {
                    ImageIcon portada = new ImageIcon(imageUrl);
                    Image imgEscalada = portada.getImage().getScaledInstance(220, -1, Image.SCALE_SMOOTH);
                    portadaLabel.setIcon(new ImageIcon(imgEscalada));
                } else {
                    portadaLabel.setText("Imagen no disponible");
                }

                // Panel para los detalles con BoxLayout en Y_AXIS
                JPanel detallesPanel = new JPanel();
                detallesPanel.setLayout(new BoxLayout(detallesPanel, BoxLayout.Y_AXIS));
                detallesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Margen para mejor visualización

                // Campo de título con altura fija
                JTextField campoTitulo = new JTextField(rs.getString("titulo"));
                campoTitulo.setEditable(false);
                campoTitulo.setCaretColor(new Color(214, 217, 223));
                campoTitulo.setFont(new Font("Arial", Font.BOLD, 18));
                campoTitulo.setBorder(null);
                campoTitulo.setAlignmentX(Component.LEFT_ALIGNMENT); // Alineación correcta en BoxLayout
                campoTitulo.setBackground(new Color(214, 217, 223));

                // Crear un panel para envolverlo y darle altura
                JPanel tituloPanel = new JPanel(new BorderLayout());
                tituloPanel.add(campoTitulo, BorderLayout.CENTER);
                tituloPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45)); // Altura del JTextField

                // Concatenar géneros en una sola cadena bajo la etiqueta "Géneros"
                StringBuilder generos = new StringBuilder("  Géneros: ");
                String genero = rs.getString("genero");  // Obtiene el primer género
                String subgenero = rs.getString("subgenero");  // Obtiene el subgénero

                // Si existe un género, se concatena
                if (genero != null && !genero.isEmpty()) {
                    generos.append(genero);
                }

                // Si existe un subgénero, se concatena después del género
                if (subgenero != null && !subgenero.isEmpty()) {
                    generos.append(", ").append(subgenero);
                }

                String director = "  Director: " + rs.getString("director");
                String reparto = "  Reparto: " + rs.getString("actorProtagonista") + ", "
                        + rs.getString("actorSecundario1") + ", "
                        + rs.getString("actorSecundario2");

// Área de detalles con fondo transparente
                JTextArea detallesTextArea = new JTextArea();
                detallesTextArea.setEditable(false);
                detallesTextArea.setLineWrap(true);
                detallesTextArea.setWrapStyleWord(true);
                detallesTextArea.setBorder(null);
                detallesTextArea.setBackground(new Color(214, 217, 223));
                detallesTextArea.setText(
                        "  Año: " + rs.getInt("anioLanzamiento") + "\n\n"
                        + director + "\n\n"
                        + reparto + "\n\n"
                        + generos.toString() + "\n\n" // Usamos la cadena de géneros concatenados
                        + "  Sinopsis: " + rs.getString("sinopsis") + "\n\n"
                        + "  Formato: " + rs.getString("formato") + "\n\n"
                        + "  Unidades disponibles: " + rs.getInt("numDisponibleAlquiler") + "\n\n"
                        + "  Precio de alquiler: " + rs.getDouble("cuotaAlquilerPeliculas") + " €"
                );

                // ScrollPane para detallesTextArea
                JScrollPane scrollPane = new JScrollPane(detallesTextArea);
                scrollPane.setBorder(null);
                scrollPane.setOpaque(false);
                scrollPane.getViewport().setOpaque(false);
                scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT); // Alineación correcta en BoxLayout

                // Botón de "Alquilar"
                toggleAlquiler = new JToggleButton("Alquilar");
                toggleAlquiler.setEnabled(rs.getInt("numDisponibleAlquiler") > 0);
                toggleAlquiler.setAlignmentX(Component.LEFT_ALIGNMENT);
                toggleAlquiler.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println(">>>>> Botón pulsado");

                        if (toggleAlquiler.isSelected()) {
                            try ( Connection conn = Database.getConnection()) {
                                System.out.println(">>>>>  Conexión establecida");

                                String fechaAlquilerStr = tfFechaHoy.getText().trim(); // Eliminar espacios en blanco
                                System.out.println(">>>>> Fecha ingresada" + fechaAlquilerStr);
                                if (fechaAlquilerStr.isEmpty()) {
                                    System.out.println(">>>>> ERROR: La fecha está vacía");
                                    JOptionPane.showMessageDialog(UISocio.this, "Selecciona la fecha en la que quieres alquilar", "Error", JOptionPane.ERROR_MESSAGE);
                                    toggleAlquiler.setSelected(false);
                                    return;
                                }

                                LocalDate fechaAlquiler;
                                try {
                                    fechaAlquiler = LocalDate.parse(fechaAlquilerStr); // Intentar convertir a LocalDate
                                } catch (DateTimeParseException ex) {
                                    JOptionPane.showMessageDialog(UISocio.this, "Por favor, introduce una fecha válida", "Error", JOptionPane.ERROR_MESSAGE);
                                    toggleAlquiler.setSelected(false);
                                    return;
                                }

                                int selectedRowView = table.getSelectedRow();
                                if (selectedRowView == -1) {
                                    JOptionPane.showMessageDialog(UISocio.this, "Seleccione una película para alquilar.", "Error", JOptionPane.ERROR_MESSAGE);
                                    toggleAlquiler.setSelected(false);
                                    return;
                                }
                                
                                int selectedRowModel = table.convertRowIndexToModel(selectedRowView);
                                String titulo = (String) tableModel.getValueAt(selectedRowModel, 0);
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

                                // Continuar con el proceso de alquiler...
                            } catch (SQLException ex) {
                                JOptionPane.showMessageDialog(UISocio.this, "Error de conexión con la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }

                });

                // Agregar componentes al panel de detalles
                detallesPanel.add(tituloPanel);
                detallesPanel.add(Box.createVerticalStrut(10)); // Espaciado
                detallesPanel.add(scrollPane);
                detallesPanel.add(Box.createVerticalStrut(10)); // Espaciado
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
        toggleAlquiler = new javax.swing.JToggleButton();
        jPanelSocio = new javax.swing.JPanel();
        nombreLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        tfFechaHoy = new javax.swing.JTextField();
        jLabelPeticionFecha = new javax.swing.JLabel();
        btnSeguimiento = new javax.swing.JButton();
        btnDevolucion = new javax.swing.JButton();
        welcomeUser = new javax.swing.JLabel();
        filtroTabla = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btnActualizarTabla = new javax.swing.JButton();
        jPanelProximamente = new javax.swing.JPanel();
        jLabelProximamente = new javax.swing.JLabel();
        siguiente = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        btnSalir = new javax.swing.JButton();

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

        toggleAlquiler.setText("Alquilarrrr");
        toggleAlquiler.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleAlquilerActionPerformed(evt);
            }
        });

        jPanelSocio.setBackground(new java.awt.Color(153, 204, 255));

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

        tfFechaHoy.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        tfFechaHoy.setForeground(new java.awt.Color(102, 0, 255));
        tfFechaHoy.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfFechaHoy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfFechaHoyActionPerformed(evt);
            }
        });

        jLabelPeticionFecha.setFont(new java.awt.Font("Franklin Gothic Medium", 1, 18)); // NOI18N
        jLabelPeticionFecha.setForeground(new java.awt.Color(0, 51, 204));
        jLabelPeticionFecha.setText("Disculpa, ¿qué día es hoy?");

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

        welcomeUser.setBackground(new java.awt.Color(102, 0, 255));
        welcomeUser.setFont(new java.awt.Font("Georgia", 1, 48)); // NOI18N
        welcomeUser.setForeground(new java.awt.Color(76, 4, 149));
        welcomeUser.setText("Bienvenido");

        filtroTabla.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                cargarPeliculas();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                cargarPeliculas();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                cargarPeliculas();
            }
        });

        jLabel3.setFont(new java.awt.Font("Georgia", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Buscar:");

        btnActualizarTabla.setText("Actualizar");
        btnActualizarTabla.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarTablaActionPerformed(evt);
            }
        });

        jPanelProximamente.setBorder(new javax.swing.border.MatteBorder(null));
        jPanelProximamente.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanelProximamente.add(jLabelProximamente, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 280, 440));
        jPanelProximamente.add(siguiente, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 280, 440));

        jLabel1.setFont(new java.awt.Font("Franklin Gothic Medium", 1, 28)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("PRÓXIMAMENTE");
        jPanelProximamente.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 0, 230, 40));

        btnSalir.setText("Salir");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelSocioLayout = new javax.swing.GroupLayout(jPanelSocio);
        jPanelSocio.setLayout(jPanelSocioLayout);
        jPanelSocioLayout.setHorizontalGroup(
            jPanelSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSocioLayout.createSequentialGroup()
                .addGroup(jPanelSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelSocioLayout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addGroup(jPanelSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(jPanelSocioLayout.createSequentialGroup()
                                    .addComponent(btnDevolucion, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(113, 113, 113)
                                    .addComponent(jLabel3)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(filtroTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanelSocioLayout.createSequentialGroup()
                                    .addComponent(btnSeguimiento, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnActualizarTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 674, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanelSocioLayout.createSequentialGroup()
                                .addComponent(welcomeUser, javax.swing.GroupLayout.PREFERRED_SIZE, 505, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(nombreLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelSocioLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanelSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelSocioLayout.createSequentialGroup()
                                .addComponent(jLabelPeticionFecha)
                                .addGap(247, 247, 247))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelSocioLayout.createSequentialGroup()
                                .addComponent(tfFechaHoy, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(291, 291, 291)))))
                .addComponent(jPanelProximamente, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );
        jPanelSocioLayout.setVerticalGroup(
            jPanelSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelSocioLayout.createSequentialGroup()
                .addGroup(jPanelSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelSocioLayout.createSequentialGroup()
                        .addContainerGap(58, Short.MAX_VALUE)
                        .addComponent(jPanelProximamente, javax.swing.GroupLayout.PREFERRED_SIZE, 511, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelSocioLayout.createSequentialGroup()
                        .addGroup(jPanelSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelSocioLayout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addComponent(welcomeUser, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(nombreLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelPeticionFecha)
                        .addGap(18, 18, 18)
                        .addComponent(tfFechaHoy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24)
                        .addGroup(jPanelSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnSeguimiento, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnActualizarTabla, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanelSocioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(filtroTabla, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(btnDevolucion)
                            .addComponent(btnSalir))))
                .addGap(13, 13, 13))
        );

        CustomTextField customTextField = new CustomTextField(30, 30); // Bordes más redondeados
        CustomRoundedButton customModelSeguimiento = new CustomRoundedButton();
        customModelSeguimiento.setNormalColor(Color.BLACK);
        customModelSeguimiento.setHoverColor(Color.GRAY);

        // Crear un botón estándar de Swing

        btnSeguimiento.setModel(customModelSeguimiento); // Asignar el modelo personalizado

        // Configurar colores dinámicamente con un MouseListener
        btnSeguimiento.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnSeguimiento.setBackground(customModelSeguimiento.getHoverColor());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnSeguimiento.setBackground(customModelSeguimiento.getNormalColor());
            }
        });

        // Configurar el botón
        btnSeguimiento.setFocusPainted(false); // Quitar el borde de enfoque
        btnSeguimiento.setBackground(customModelSeguimiento.getNormalColor()); // Color inicial
        btnSeguimiento.setForeground(Color.WHITE); // Color del texto
        btnSeguimiento.setFont(new Font("Arial", Font.BOLD,16));
        CustomRoundedButton customModel = new CustomRoundedButton();
        customModel.setNormalColor(Color.BLACK);
        customModel.setHoverColor(Color.GRAY);

        // Crear un botón estándar de Swing

        btnDevolucion.setModel(customModel); // Asignar el modelo personalizado

        // Configurar colores dinámicamente con un MouseListener
        btnDevolucion.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnDevolucion.setBackground(customModel.getHoverColor());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnDevolucion.setBackground(customModel.getNormalColor());
            }
        });

        // Configurar el botón
        btnDevolucion.setFocusPainted(false); // Quitar el borde de enfoque
        btnDevolucion.setBackground(customModel.getNormalColor()); // Color inicial
        btnDevolucion.setForeground(Color.WHITE); // Color del texto
        btnDevolucion.setFont(new Font("Arial", Font.BOLD,16));
        welcomeUser.setOpaque(false);
        CustomRoundedButton customModelActualizar = new CustomRoundedButton();
        customModelActualizar.setNormalColor(Color.GRAY);
        customModelActualizar.setHoverColor(new Color(153, 255, 204));

        // Crear un botón estándar de Swing

        btnActualizarTabla.setModel(customModelActualizar); // Asignar el modelo personalizado

        // Configurar colores dinámicamente con un MouseListener
        btnActualizarTabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnActualizarTabla.setBackground(customModelActualizar.getHoverColor());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnActualizarTabla.setBackground(customModelActualizar.getNormalColor());
            }
        });

        // Configurar el botón
        btnActualizarTabla.setFocusPainted(false); // Quitar el borde de enfoque
        btnActualizarTabla.setBackground(customModelActualizar.getNormalColor()); // Color inicial
        btnActualizarTabla.setForeground(Color.WHITE); // Color del texto
        btnActualizarTabla.setFont(new Font("Arial", Font.BOLD,16));
        jPanelProximamente.setOpaque(false);
        CustomRoundedButton customModelSalir= new CustomRoundedButton();
        customModelSalir.setNormalColor(Color.GRAY);
        customModelSalir.setHoverColor(new Color(255, 102, 102));

        // Crear un botón estándar de Swing

        btnSalir.setModel(customModelSalir); // Asignar el modelo personalizado

        // Configurar colores dinámicamente con un MouseListener
        btnSalir.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnSalir.setBackground(customModelSalir.getHoverColor());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnSalir.setBackground(customModelSalir.getNormalColor());
            }
        });

        // Configurar el botón
        btnSalir.setFocusPainted(false); // Quitar el borde de enfoque
        btnSalir.setBackground(customModelSalir.getNormalColor()); // Color inicial
        btnSalir.setForeground(Color.WHITE); // Color del texto
        btnSalir.setFont(new Font("Arial", Font.BOLD,16));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelSocio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(608, 608, 608)
                    .addComponent(toggleAlquiler)
                    .addContainerGap(373, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelSocio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(286, 286, 286)
                    .addComponent(toggleAlquiler)
                    .addContainerGap(273, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void toggleAlquilerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleAlquilerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_toggleAlquilerActionPerformed

    private void btnActualizarTablaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarTablaActionPerformed
        cargarPeliculas();
    }//GEN-LAST:event_btnActualizarTablaActionPerformed

    private void btnDevolucionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDevolucionActionPerformed

    }//GEN-LAST:event_btnDevolucionActionPerformed

    private void btnSeguimientoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSeguimientoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSeguimientoActionPerformed

    private void tfFechaHoyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfFechaHoyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfFechaHoyActionPerformed

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSalirActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizarTabla;
    private javax.swing.JButton btnDevolucion;
    private javax.swing.JButton btnSalir;
    private javax.swing.JButton btnSeguimiento;
    private javax.swing.JDialog detallesDialog;
    private javax.swing.JTextField filtroTabla;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabelPeticionFecha;
    private javax.swing.JLabel jLabelProximamente;
    private javax.swing.JPanel jPanelProximamente;
    private javax.swing.JPanel jPanelSocio;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel nombreLabel;
    private javax.swing.JLabel siguiente;
    private javax.swing.JTable table;
    private javax.swing.JTextField tfFechaHoy;
    private javax.swing.JToggleButton toggleAlquiler;
    private javax.swing.JLabel welcomeUser;
    // End of variables declaration//GEN-END:variables

}
