package com.mycompany.proyectovideoclub;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

/**
 *
 * @author oscar.lara
 */
public class GestionPeliculas extends javax.swing.JFrame {

    public GestionPeliculas() {
        initComponents();
        tfDisponible.setVisible(false);
        tfAlquilado.setVisible(false);
        Utilidades.setPredefinedText(tfFechaAlta, "yyyy-mm-dd");
        Utilidades.setPredefinedText(tfFechaLanzamiento, "yyyy-mm-dd");
        Utilidades.setPredefinedText(tfCosteUnitario, "0.00");
        Utilidades.setPredefinedText(tfCuotaAlquilerPelicula, "0.00");
        Utilidades.setPredefinedText(tfRecargo, "0.00");
        Utilidades.setPredefinedText(tfPVP, "0.00");
        Utilidades.setPredefinedText(tfDuracion, "Ej: 120 (minutos)");
        Utilidades.setPredefinedText(tfGenero, "Ej: Aventura");
        Utilidades.setPredefinedText(tfDirector, "Ej: Steven Spielberg");
        Utilidades.cargarImagenEnLabel(jLabelLogoPelicula, "/images/logopelicula.png");
        setLocationRelativeTo(null); // Abrir centrado en la pantalla, debe ir después de initComponents

        // Crear el panel personalizado con gradiente
        CustomGradientPanel panel = new CustomGradientPanel();
        panel.setLayout(new BorderLayout());  // # Usar BorderLayout para gestionar el contenido dentro del panel
        panel.setGradientColors(new Color(0, 102, 204), new Color(102, 204, 255));

        // Añadir contenido al panel
        panel.add(new JLabel("Disponible Alquiler:"), BorderLayout.NORTH);  // # Etiqueta con texto al panel

        // Cargar los nombres de los formatos al inicializar
        cargarNombresDeFormatos();
        cargarDistribuidoras();

        // Acciones de botones
        btnSeleccionarDisponible.addActionListener(e -> {
            String rutaImagen = seleccionarImagen();
            if (rutaImagen != null) {
                tfDisponible.setText(rutaImagen);
                cargarPreviewEnLabel(rutaImagen, jLabelPreviewDisponible);
            }
        });

        btnSeleccionarAlquilado.addActionListener(e -> {
            String rutaImagen = seleccionarImagen();
            if (rutaImagen != null) {
                tfAlquilado.setText(rutaImagen);
                cargarPreviewEnLabel(rutaImagen, jLabelPreviewAlquilado);
            }
        });

        btnGuardar.addActionListener(evt -> {
            try {
                // Capturar valores desde la interfaz
                String titulo = tfTitulo.getText();
                int anioLanzamiento = Integer.parseInt(tfAnioLanzamiento.getText());
                double costeUnitario = Double.parseDouble(tfCosteUnitario.getText());

                int numDisponibleAlquiler = (int) spinnerDisponibleAlquiler.getValue(); // Obtener valor del spinner

                double recargoDevolucion = Double.parseDouble(tfRecargo.getText());
                boolean esEstreno = jRadioButtonEstreno.isSelected();
                String genero = tfGenero.getText();
                String subgenero = tfSubgenero.getText();
                String sinopsis = tfSinopsis.getText();
                String director = tfDirector.getText();
                int duracion = Integer.parseInt(tfDuracion.getText());
                String actorProtagonista = tfProtagonista.getText();
                String actorSecundario1 = tfSecun1.getText();
                String actorSecundario2 = tfSecun2.getText();

                String formatoSeleccionado = (String) comboBoxFormato.getSelectedItem();
                int formatoId = Integer.parseInt(formatoSeleccionado.split(" - ")[0]);
                String distSeleccionada = (String) comboBoxDistribuidora.getSelectedItem();
                int distribuidoraId = Integer.parseInt(distSeleccionada.split(" - ")[0]);

                double cuotaAlquilerPeliculas = Double.parseDouble(tfCuotaAlquilerPelicula.getText());

                // Obtener el texto completo de la ruta ingresada en los campos de texto
                String rutaCompletaDisponible = tfDisponible.getText();
                String rutaCompletaAlquilado = tfAlquilado.getText();

                // Extraer solo el nombre del archivo de cada ruta
                String imagenProductoDisponible = new File(rutaCompletaDisponible).getName();
                String imagenProductoAlquilado = new File(rutaCompletaAlquilado).getName();

                // Convertir fechas
                Date fechaLanzamiento = convertirFecha(tfFechaLanzamiento.getText());
                Date fechaAltaDatabase = convertirFecha(tfFechaAlta.getText());

                // Validar campos obligatorios
                if (titulo.isEmpty() || genero.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor, rellena todos los campos obligatorios.");
                    return;
                }

                try ( Connection conn = Database.getConnection()) {
                    // Insertar en Productos y recuperar el ID generado
                    String queryProductos = "INSERT INTO Productos ("
                            + "titulo, "
                            + "anioLanzamiento, "
                            + "costeUnitario, "
                            + "fechaLanzamiento, "
                            + "fechaAltaDatabase, "
                            + "numDisponibleAlquiler, "
                            + "recargoDevolucion, "
                            + "esEstreno, "
                            + "genero, "
                            + "subgenero, "
                            + "sinopsis, "
                            + "imagenProductoDisponible, "
                            + "imagenProductoAlquilado) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    try ( PreparedStatement stmtProductos = conn.prepareStatement(queryProductos, Statement.RETURN_GENERATED_KEYS)) {
                        stmtProductos.setString(1, titulo);
                        stmtProductos.setInt(2, anioLanzamiento);
                        stmtProductos.setDouble(3, costeUnitario);
                        stmtProductos.setDate(4, fechaLanzamiento);
                        stmtProductos.setDate(5, fechaAltaDatabase);
                        stmtProductos.setInt(6, numDisponibleAlquiler);
                        stmtProductos.setDouble(7, recargoDevolucion);
                        stmtProductos.setBoolean(8, esEstreno);
                        stmtProductos.setString(9, genero);
                        stmtProductos.setString(10, subgenero);
                        stmtProductos.setString(11, sinopsis);
                        stmtProductos.setString(12, imagenProductoDisponible);
                        stmtProductos.setString(13, imagenProductoAlquilado);
                        stmtProductos.executeUpdate();

                        ResultSet generatedKeys = stmtProductos.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            int productoId = generatedKeys.getInt(1);

                            // Insertar en Peliculas usando el ID de Productos
                            String queryPeliculas = "INSERT INTO Peliculas ("
                                    + "id, "
                                    + "director, "
                                    + "duracion, "
                                    + "actorProtagonista, "
                                    + "actorSecundario1, "
                                    + "actorSecundario2, "
                                    + "formato_id, "
                                    + "distribuidora_id, "
                                    + "cuotaAlquilerPeliculas) "
                                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                            try ( PreparedStatement stmtPeliculas = conn.prepareStatement(queryPeliculas)) {
                                stmtPeliculas.setInt(1, productoId);
                                stmtPeliculas.setString(2, director);
                                stmtPeliculas.setInt(3, duracion);
                                stmtPeliculas.setString(4, actorProtagonista);
                                stmtPeliculas.setString(5, actorSecundario1);
                                stmtPeliculas.setString(6, actorSecundario2);
                                stmtPeliculas.setInt(7, formatoId);
                                stmtPeliculas.setInt(8, distribuidoraId);
                                stmtPeliculas.setDouble(9, cuotaAlquilerPeliculas);
                                stmtPeliculas.executeUpdate();

                                JOptionPane.showMessageDialog(null, "Película agregada con éxito.");
                            }
                        } else {
                            throw new SQLException("No se pudo obtener el ID generado para el producto.");
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error al agregar película: " + ex.getMessage());
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Error en los datos numéricos. Por favor, verifica los campos.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error inesperado: " + ex.getMessage());
            }
        });
    }

    // Método para cargar los nombres de los formatos desde la base de datos
    private void cargarNombresDeFormatos() {
        SwingUtilities.invokeLater(() -> {  // Aseguramos que se ejecute en el hilo de la interfaz gráfica
            try ( Connection conn = Database.getConnection()) {
                if (conn == null) {
                    JOptionPane.showMessageDialog(this, "Error: conexión a la base de datos no válida.", "Error de conexión", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String query = "SELECT id, nombre "
                        + "FROM Formatos";
                try ( PreparedStatement stmt = conn.prepareStatement(query);  ResultSet rs = stmt.executeQuery()) {

                    comboBoxFormato.removeAllItems();  // Limpiar el JComboBox

                    if (!rs.isBeforeFirst()) {  // Comprueba si hay resultados
                        System.out.println("No se encontraron formatos en la base de datos.");
                        JOptionPane.showMessageDialog(this, "No hay formatos disponibles.", "Información", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String nombre = rs.getString("nombre");
                        comboBoxFormato.addItem(id + " - " + nombre);  // Añadir al JComboBox
                        System.out.println("Formato cargado: " + id + " - " + nombre);  // Mensaje de depuración
                    }

                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al cargar formatos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // Método para cargar los nombres de los formatos desde la base de datos
    private void cargarDistribuidoras() {
        SwingUtilities.invokeLater(() -> {  // Aseguramos que se ejecute en el hilo de la interfaz gráfica
            try ( Connection conn = Database.getConnection()) {
                if (conn == null) {
                    JOptionPane.showMessageDialog(this, "Error: conexión a la base de datos no válida.", "Error de conexión", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String query = "SELECT id, nombre "
                        + "FROM distribuidoraspeliculas";
                try ( PreparedStatement stmt = conn.prepareStatement(query);  ResultSet rs = stmt.executeQuery()) {

                    comboBoxDistribuidora.removeAllItems();  // Limpiar el JComboBox

                    if (!rs.isBeforeFirst()) {  // Comprueba si hay resultados
                        System.out.println("No se encontraron formatos en la base de datos.");
                        JOptionPane.showMessageDialog(this, "No hay formatos disponibles.", "Información", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String nombre = rs.getString("nombre");
                        comboBoxDistribuidora.addItem(id + " - " + nombre);  // Añadir al JComboBox
                        System.out.println("Distribuidora cargada: " + id + " - " + nombre);  // Mensaje de depuración
                    }

                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al cargar formatos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

// Método para abrir JFileChooser y seleccionar una imagen
    private String seleccionarImagen() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        // Filtro para mostrar solo imágenes
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Imágenes", "jpg", "png", "jpeg"));

        // Mostrar el cuadro de diálogo para abrir el archivo
        int seleccion = fileChooser.showOpenDialog(this);

        // Si el usuario selecciona un archivo, obtener su ruta
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            File archivoSeleccionado = fileChooser.getSelectedFile();
            try {
                // Verificar que el archivo seleccionado sea válido y no esté vacío
                if (archivoSeleccionado.exists() && archivoSeleccionado.isFile()) {
                    return archivoSeleccionado.getAbsolutePath();
                } else {
                    JOptionPane.showMessageDialog(this, "El archivo seleccionado no es válido.", "Error", JOptionPane.ERROR_MESSAGE);
                    return null;
                }
            } catch (Exception e) {
                // Capturar cualquier excepción inesperada
                JOptionPane.showMessageDialog(this, "Error al obtener la ruta de la imagen: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }

        // Si el usuario cancela la selección
        return null;
    }

// Método para cargar imagen en el JLabel
    private void cargarPreviewEnLabel(String rutaImagen, JLabel label) {
        try {
            // Cargar la imagen desde la ruta
            ImageIcon imgProd = new ImageIcon(rutaImagen);

            // Escalar la imagen al tamaño de la JLabel
            ImageIcon tamaño = new ImageIcon(imgProd.getImage().getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_DEFAULT));

            // Establecer la imagen escalada en el JLabel
            label.setIcon(tamaño);
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(null, "Error al cargar la imagen: " + e.getMessage());
        }
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

        panel = new javax.swing.JPanel();
        jPanelInfoBasica = new javax.swing.JPanel();
        jLabelTitulo = new javax.swing.JLabel();
        tfTitulo = new javax.swing.JTextField();
        jLabelAnioLanzamiento = new javax.swing.JLabel();
        tfAnioLanzamiento = new javax.swing.JTextField();
        jLabelFormato = new javax.swing.JLabel();
        comboBoxFormato = new javax.swing.JComboBox<>();
        comboBoxDistribuidora = new javax.swing.JComboBox<>();
        jLabelDistribuidoras = new javax.swing.JLabel();
        jPanelDisponibles = new javax.swing.JPanel();
        jLabelDisponibleAlquiler = new javax.swing.JLabel();
        spinnerDisponibleAlquiler = new javax.swing.JSpinner();
        jPanel1 = new javax.swing.JPanel();
        jRadioButtonEstreno = new javax.swing.JRadioButton();
        jCheckBoxAlquilado = new javax.swing.JCheckBox();
        jCheckBoxStock = new javax.swing.JCheckBox();
        jCheckBoxBaja = new javax.swing.JCheckBox();
        jPanelInfoFechaCostes = new javax.swing.JPanel();
        jLabelFechaAlta = new javax.swing.JLabel();
        tfFechaAlta = new javax.swing.JTextField();
        jLabelFechaLanzamiento = new javax.swing.JLabel();
        tfFechaLanzamiento = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabelPrecioVenta = new javax.swing.JLabel();
        tfPVP = new javax.swing.JTextField();
        jLabelRecargo = new javax.swing.JLabel();
        tfRecargo = new javax.swing.JTextField();
        jLabelCosteUnitario = new javax.swing.JLabel();
        jLabelCuotaAlquiler = new javax.swing.JLabel();
        tfCuotaAlquilerPelicula = new javax.swing.JTextField();
        tfCosteUnitario = new javax.swing.JTextField();
        jPanelInfoAdicional = new javax.swing.JPanel();
        jLabelDirector = new javax.swing.JLabel();
        tfDirector = new javax.swing.JTextField();
        tfDuracion = new javax.swing.JTextField();
        jLabelDuracion = new javax.swing.JLabel();
        jLabelGenero = new javax.swing.JLabel();
        tfSubgenero = new javax.swing.JTextField();
        jLabelSubgenero = new javax.swing.JLabel();
        tfGenero = new javax.swing.JTextField();
        jLabelSinopsis = new javax.swing.JLabel();
        tfSinopsis = new javax.swing.JTextField();
        jLabelProtagonista = new javax.swing.JLabel();
        tfProtagonista = new javax.swing.JTextField();
        jLabelSecun1 = new javax.swing.JLabel();
        tfSecun1 = new javax.swing.JTextField();
        jLabelSecun2 = new javax.swing.JLabel();
        tfSecun2 = new javax.swing.JTextField();
        btnGuardar = new javax.swing.JButton();
        jPanelEliminar = new javax.swing.JPanel();
        tfDisponible = new javax.swing.JTextField();
        tfAlquilado = new javax.swing.JTextField();
        jPanelPreviewPortadas = new javax.swing.JPanel();
        jLabelPreviewAlquilado = new javax.swing.JLabel();
        jLabelPreviewDisponible = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabelDisponible = new javax.swing.JLabel();
        btnSeleccionarDisponible = new javax.swing.JButton();
        jLabelAlquilado = new javax.swing.JLabel();
        btnSeleccionarAlquilado = new javax.swing.JButton();
        jLabelLogoPelicula = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabelAgregarPelicula = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        panel.setBackground(new java.awt.Color(199, 231, 206));

        jLabelTitulo.setFont(new java.awt.Font("DialogInput", 1, 14)); // NOI18N
        jLabelTitulo.setText("Título:  ");

        tfTitulo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfTituloActionPerformed(evt);
            }
        });

        jLabelAnioLanzamiento.setFont(new java.awt.Font("DialogInput", 1, 14)); // NOI18N
        jLabelAnioLanzamiento.setText("Año lanzamiento:  ");

        jLabelFormato.setFont(new java.awt.Font("DialogInput", 1, 14)); // NOI18N
        jLabelFormato.setText("Formato");

        comboBoxFormato.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxFormatoActionPerformed(evt);
            }
        });

        comboBoxDistribuidora.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxDistribuidoraActionPerformed(evt);
            }
        });

        jLabelDistribuidoras.setFont(new java.awt.Font("DialogInput", 1, 14)); // NOI18N
        jLabelDistribuidoras.setText("Distribuidora");

        javax.swing.GroupLayout jPanelInfoBasicaLayout = new javax.swing.GroupLayout(jPanelInfoBasica);
        jPanelInfoBasica.setLayout(jPanelInfoBasicaLayout);
        jPanelInfoBasicaLayout.setHorizontalGroup(
            jPanelInfoBasicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelInfoBasicaLayout.createSequentialGroup()
                .addGroup(jPanelInfoBasicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelInfoBasicaLayout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addGroup(jPanelInfoBasicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelFormato)
                            .addComponent(jLabelDistribuidoras))
                        .addGap(26, 26, 26)
                        .addGroup(jPanelInfoBasicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(comboBoxFormato, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(comboBoxDistribuidora, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanelInfoBasicaLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanelInfoBasicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelAnioLanzamiento)
                            .addComponent(jLabelTitulo))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelInfoBasicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tfTitulo, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                            .addComponent(tfAnioLanzamiento))))
                .addContainerGap())
        );
        jPanelInfoBasicaLayout.setVerticalGroup(
            jPanelInfoBasicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelInfoBasicaLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanelInfoBasicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelTitulo)
                    .addComponent(tfTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelInfoBasicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelAnioLanzamiento)
                    .addComponent(tfAnioLanzamiento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelInfoBasicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelFormato)
                    .addComponent(comboBoxFormato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelInfoBasicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboBoxDistribuidora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelDistribuidoras))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabelDisponibleAlquiler.setFont(new java.awt.Font("DialogInput", 1, 14)); // NOI18N
        jLabelDisponibleAlquiler.setText("Disponible alquiler: ");

        jRadioButtonEstreno.setFont(new java.awt.Font("DialogInput", 1, 14)); // NOI18N
        jRadioButtonEstreno.setText("Estreno");
        jRadioButtonEstreno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonEstrenoActionPerformed(evt);
            }
        });

        jCheckBoxAlquilado.setFont(new java.awt.Font("DialogInput", 1, 14)); // NOI18N
        jCheckBoxAlquilado.setText("Alquilado");

        jCheckBoxStock.setFont(new java.awt.Font("DialogInput", 1, 14)); // NOI18N
        jCheckBoxStock.setText("Stock");
        jCheckBoxStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxStockActionPerformed(evt);
            }
        });

        jCheckBoxBaja.setFont(new java.awt.Font("DialogInput", 1, 14)); // NOI18N
        jCheckBoxBaja.setText("Baja");
        jCheckBoxBaja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxBajaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButtonEstreno)
                    .addComponent(jCheckBoxStock))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBoxBaja)
                    .addComponent(jCheckBoxAlquilado))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButtonEstreno)
                    .addComponent(jCheckBoxAlquilado))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBoxStock)
                    .addComponent(jCheckBoxBaja))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanelDisponiblesLayout = new javax.swing.GroupLayout(jPanelDisponibles);
        jPanelDisponibles.setLayout(jPanelDisponiblesLayout);
        jPanelDisponiblesLayout.setHorizontalGroup(
            jPanelDisponiblesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDisponiblesLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanelDisponiblesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelDisponiblesLayout.createSequentialGroup()
                        .addComponent(jLabelDisponibleAlquiler)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinnerDisponibleAlquiler, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelDisponiblesLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35))))
        );
        jPanelDisponiblesLayout.setVerticalGroup(
            jPanelDisponiblesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDisponiblesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelDisponiblesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelDisponibleAlquiler)
                    .addComponent(spinnerDisponibleAlquiler, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(10, Short.MAX_VALUE))
        );

        jPanel1.setOpaque(false);

        jLabelFechaAlta.setFont(new java.awt.Font("DialogInput", 1, 14)); // NOI18N
        jLabelFechaAlta.setText("Fecha de alta: ");

        jLabelFechaLanzamiento.setFont(new java.awt.Font("DialogInput", 1, 14)); // NOI18N
        jLabelFechaLanzamiento.setText("Fecha lanzamiento: ");

        jLabelPrecioVenta.setFont(new java.awt.Font("DialogInput", 1, 14)); // NOI18N
        jLabelPrecioVenta.setText("P.V.P");

        jLabelRecargo.setFont(new java.awt.Font("DialogInput", 1, 14)); // NOI18N
        jLabelRecargo.setText("Recargo:");

        jLabelCosteUnitario.setFont(new java.awt.Font("DialogInput", 1, 14)); // NOI18N
        jLabelCosteUnitario.setText("Coste unitario: ");

        jLabelCuotaAlquiler.setFont(new java.awt.Font("DialogInput", 1, 14)); // NOI18N
        jLabelCuotaAlquiler.setText("Cuota alquiler: ");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(43, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabelCuotaAlquiler)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfCuotaAlquilerPelicula, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelPrecioVenta)
                            .addComponent(jLabelRecargo, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(tfPVP, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
                            .addComponent(tfRecargo)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabelCosteUnitario)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfCosteUnitario, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfCosteUnitario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelCosteUnitario))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelCuotaAlquiler)
                    .addComponent(tfCuotaAlquilerPelicula, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfRecargo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelRecargo))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfPVP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelPrecioVenta))
                .addGap(345, 345, 345))
        );

        javax.swing.GroupLayout jPanelInfoFechaCostesLayout = new javax.swing.GroupLayout(jPanelInfoFechaCostes);
        jPanelInfoFechaCostes.setLayout(jPanelInfoFechaCostesLayout);
        jPanelInfoFechaCostesLayout.setHorizontalGroup(
            jPanelInfoFechaCostesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelInfoFechaCostesLayout.createSequentialGroup()
                .addGroup(jPanelInfoFechaCostesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelInfoFechaCostesLayout.createSequentialGroup()
                        .addGroup(jPanelInfoFechaCostesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelInfoFechaCostesLayout.createSequentialGroup()
                                .addGap(51, 51, 51)
                                .addComponent(jLabelFechaAlta))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelInfoFechaCostesLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabelFechaLanzamiento)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelInfoFechaCostesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tfFechaAlta, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tfFechaLanzamiento, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelInfoFechaCostesLayout.setVerticalGroup(
            jPanelInfoFechaCostesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelInfoFechaCostesLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanelInfoFechaCostesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelFechaAlta)
                    .addComponent(tfFechaAlta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelInfoFechaCostesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelFechaLanzamiento)
                    .addComponent(tfFechaLanzamiento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel2.setOpaque(false);

        jLabelDirector.setFont(new java.awt.Font("DialogInput", 1, 14)); // NOI18N
        jLabelDirector.setText("Director: ");

        jLabelDuracion.setFont(new java.awt.Font("DialogInput", 1, 14)); // NOI18N
        jLabelDuracion.setText("Duración (min): ");

        jLabelGenero.setFont(new java.awt.Font("DialogInput", 1, 14)); // NOI18N
        jLabelGenero.setText("Género: ");

        jLabelSubgenero.setFont(new java.awt.Font("DialogInput", 1, 14)); // NOI18N
        jLabelSubgenero.setText("Subgénero: ");

        jLabelSinopsis.setFont(new java.awt.Font("DialogInput", 1, 14)); // NOI18N
        jLabelSinopsis.setText("Sinopsis: ");

        tfSinopsis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfSinopsisActionPerformed(evt);
            }
        });

        jLabelProtagonista.setFont(new java.awt.Font("DialogInput", 1, 14)); // NOI18N
        jLabelProtagonista.setText("Protagonista:");

        jLabelSecun1.setFont(new java.awt.Font("DialogInput", 1, 14)); // NOI18N
        jLabelSecun1.setText("Secundario 1: ");

        jLabelSecun2.setFont(new java.awt.Font("DialogInput", 1, 14)); // NOI18N
        jLabelSecun2.setText("Secundario 2: ");

        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelInfoAdicionalLayout = new javax.swing.GroupLayout(jPanelInfoAdicional);
        jPanelInfoAdicional.setLayout(jPanelInfoAdicionalLayout);
        jPanelInfoAdicionalLayout.setHorizontalGroup(
            jPanelInfoAdicionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelInfoAdicionalLayout.createSequentialGroup()
                .addGroup(jPanelInfoAdicionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelInfoAdicionalLayout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addGroup(jPanelInfoAdicionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelDuracion)
                            .addComponent(jLabelDirector)
                            .addGroup(jPanelInfoAdicionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabelSecun1)
                                .addComponent(jLabelProtagonista)
                                .addComponent(jLabelSecun2))
                            .addComponent(jLabelSinopsis)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelInfoAdicionalLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanelInfoAdicionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelGenero, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelSubgenero, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelInfoAdicionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(tfDuracion, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                    .addComponent(tfDirector, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tfGenero, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tfSubgenero, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tfSecun1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tfSecun2)
                    .addComponent(tfSinopsis)
                    .addComponent(tfProtagonista))
                .addGap(27, 27, 27))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelInfoAdicionalLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42))
        );
        jPanelInfoAdicionalLayout.setVerticalGroup(
            jPanelInfoAdicionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelInfoAdicionalLayout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanelInfoAdicionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfDuracion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelDuracion))
                .addGap(18, 18, 18)
                .addGroup(jPanelInfoAdicionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelDirector)
                    .addComponent(tfDirector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelInfoAdicionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelGenero)
                    .addComponent(tfGenero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanelInfoAdicionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfSubgenero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelSubgenero))
                .addGap(16, 16, 16)
                .addGroup(jPanelInfoAdicionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelProtagonista)
                    .addComponent(tfProtagonista, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelInfoAdicionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelSecun1)
                    .addComponent(tfSecun1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelInfoAdicionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelSecun2)
                    .addComponent(tfSecun2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(jPanelInfoAdicionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelSinopsis)
                    .addComponent(tfSinopsis, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        CustomRoundedButton customModel = new CustomRoundedButton();
        customModel.setNormalColor(Color.BLACK);
        customModel.setHoverColor(Color.DARK_GRAY);

        // Crear un botón estándar de Swing

        btnGuardar.setModel(customModel); // Asignar el modelo personalizado

        // Configurar colores dinámicamente con un MouseListener
        btnGuardar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnGuardar.setBackground(customModel.getHoverColor());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnGuardar.setBackground(customModel.getNormalColor());
            }
        });

        // Configurar el botón
        btnGuardar.setFocusPainted(false); // Quitar el borde de enfoque
        btnGuardar.setBackground(customModel.getNormalColor()); // Color inicial
        btnGuardar.setForeground(Color.WHITE); // Color del texto
        btnGuardar.setFont(new Font("Arial", Font.BOLD,16));

        tfDisponible.setText("tfDisponible");
        tfDisponible.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfDisponibleActionPerformed(evt);
            }
        });

        tfAlquilado.setText("tfAlquilado");

        javax.swing.GroupLayout jPanelEliminarLayout = new javax.swing.GroupLayout(jPanelEliminar);
        jPanelEliminar.setLayout(jPanelEliminarLayout);
        jPanelEliminarLayout.setHorizontalGroup(
            jPanelEliminarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelEliminarLayout.createSequentialGroup()
                .addGap(54, 54, 54)
                .addGroup(jPanelEliminarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(tfAlquilado, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tfDisponible, javax.swing.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE))
                .addContainerGap(102, Short.MAX_VALUE))
        );
        jPanelEliminarLayout.setVerticalGroup(
            jPanelEliminarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelEliminarLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(tfDisponible, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(tfAlquilado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(409, Short.MAX_VALUE))
        );

        jLabelPreviewDisponible.setBackground(new java.awt.Color(255, 204, 204));
        jLabelPreviewDisponible.setForeground(new java.awt.Color(255, 204, 204));

        jLabelDisponible.setFont(new java.awt.Font("DialogInput", 1, 14)); // NOI18N
        jLabelDisponible.setText("Disponible:");

        btnSeleccionarDisponible.setText("Seleccionar");
        btnSeleccionarDisponible.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSeleccionarDisponibleActionPerformed(evt);
            }
        });

        jLabelAlquilado.setFont(new java.awt.Font("DialogInput", 1, 14)); // NOI18N
        jLabelAlquilado.setText("Alquilado:");

        btnSeleccionarAlquilado.setText("Seleccionar");
        btnSeleccionarAlquilado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSeleccionarAlquiladoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabelDisponible, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(btnSeleccionarDisponible)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 99, Short.MAX_VALUE)
                .addComponent(jLabelAlquilado)
                .addGap(26, 26, 26)
                .addComponent(btnSeleccionarAlquilado)
                .addGap(34, 34, 34))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelDisponible)
                    .addComponent(btnSeleccionarDisponible)
                    .addComponent(jLabelAlquilado)
                    .addComponent(btnSeleccionarAlquilado)))
        );

        CustomRoundedButton customModelDisponible = new CustomRoundedButton();
        customModelDisponible.setNormalColor(new Color(82, 2, 120));
        customModelDisponible.setHoverColor(new Color(25, 231, 255));

        // Crear un botón estándar de Swing

        btnSeleccionarDisponible.setModel(customModelDisponible); // Asignar el modelo personalizado

        // Configurar colores dinámicamente con un MouseListener
        btnSeleccionarDisponible.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnSeleccionarDisponible.setBackground(customModelDisponible.getHoverColor());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnSeleccionarDisponible.setBackground(customModelDisponible.getNormalColor());
            }
        });

        // Configurar el botón
        btnSeleccionarDisponible.setFocusPainted(false); // Quitar el borde de enfoque
        btnSeleccionarDisponible.setBackground(customModelDisponible.getNormalColor()); // Color inicial
        btnSeleccionarDisponible.setForeground(Color.WHITE); // Color del texto
        btnSeleccionarDisponible.setFont(new Font("Arial", Font.BOLD,16));
        CustomRoundedButton customModelAlquilado = new CustomRoundedButton();
        customModelAlquilado.setNormalColor(new Color(82, 2, 120));
        customModelAlquilado.setHoverColor(new Color(25, 231, 255));

        // Crear un botón estándar de Swing

        btnSeleccionarAlquilado.setModel(customModelAlquilado); // Asignar el modelo personalizado

        // Configurar colores dinámicamente con un MouseListener
        btnSeleccionarAlquilado.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnSeleccionarAlquilado.setBackground(customModelAlquilado.getHoverColor());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnSeleccionarAlquilado.setBackground(customModelAlquilado.getNormalColor());
            }
        });

        // Configurar el botón
        btnSeleccionarAlquilado.setFocusPainted(false); // Quitar el borde de enfoque
        btnSeleccionarAlquilado.setBackground(customModelAlquilado.getNormalColor()); // Color inicial
        btnSeleccionarAlquilado.setForeground(Color.WHITE); // Color del texto
        btnSeleccionarAlquilado.setFont(new Font("Arial", Font.BOLD,16));

        javax.swing.GroupLayout jPanelPreviewPortadasLayout = new javax.swing.GroupLayout(jPanelPreviewPortadas);
        jPanelPreviewPortadas.setLayout(jPanelPreviewPortadasLayout);
        jPanelPreviewPortadasLayout.setHorizontalGroup(
            jPanelPreviewPortadasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPreviewPortadasLayout.createSequentialGroup()
                .addContainerGap(44, Short.MAX_VALUE)
                .addGroup(jPanelPreviewPortadasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelPreviewPortadasLayout.createSequentialGroup()
                        .addComponent(jLabelPreviewDisponible, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelPreviewAlquilado, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(13, 13, 13))
        );
        jPanelPreviewPortadasLayout.setVerticalGroup(
            jPanelPreviewPortadasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPreviewPortadasLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelPreviewPortadasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelPreviewDisponible, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelPreviewAlquilado, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7))
        );

        jPanel3.setOpaque(false);

        jLabelAgregarPelicula.setFont(new java.awt.Font("Georgia", 1, 36)); // NOI18N
        jLabelAgregarPelicula.setForeground(new java.awt.Color(0, 0, 0));
        jLabelAgregarPelicula.setText("Agregar una nueva película");

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addComponent(jLabelLogoPelicula, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(jLabelAgregarPelicula)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(panelLayout.createSequentialGroup()
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelLayout.createSequentialGroup()
                        .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelLayout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addComponent(jPanelDisponibles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanelPreviewPortadas, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jPanelInfoBasica, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanelInfoFechaCostes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanelInfoAdicional, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanelEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelLayout.createSequentialGroup()
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 1003, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabelLogoPelicula, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabelAgregarPelicula)
                        .addGap(34, 34, 34)))
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanelInfoAdicional, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelLayout.createSequentialGroup()
                        .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanelInfoFechaCostes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelLayout.createSequentialGroup()
                                .addComponent(jPanelInfoBasica, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanelDisponibles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanelPreviewPortadas, javax.swing.GroupLayout.PREFERRED_SIZE, 350, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelInfoBasica.setOpaque(false);
        jPanelDisponibles.setOpaque(false);
        jPanelInfoFechaCostes.setOpaque(false);
        jPanelInfoAdicional.setOpaque(false);
        jPanelEliminar.setOpaque(false);
        jPanelPreviewPortadas.setOpaque(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        // Crear una instancia de CustomGradientPanel
        CustomGradientPanel panel = new CustomGradientPanel();

        // Configurar propiedades del panel si es necesario
        panel.setLayout(new BorderLayout());  // Si deseas usar un layout específico, ajusta aquí

        // Si quieres cambiar los colores del degradado, puedes hacerlo de esta manera:
        panel.setGradientColors(new Color(0, 102, 204), new Color(102, 204, 255));

        // Ahora agregar el panel a tu contenedor
        add(panel, BorderLayout.CENTER); // O el layout que estés usando

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBoxBajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxBajaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBoxBajaActionPerformed

    private void jCheckBoxStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxStockActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBoxStockActionPerformed

    private void jRadioButtonEstrenoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonEstrenoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButtonEstrenoActionPerformed

    private void tfDisponibleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfDisponibleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfDisponibleActionPerformed

    private void tfTituloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfTituloActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfTituloActionPerformed

    private void tfSinopsisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfSinopsisActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfSinopsisActionPerformed

    private void comboBoxDistribuidoraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxDistribuidoraActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_comboBoxDistribuidoraActionPerformed

    private void comboBoxFormatoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxFormatoActionPerformed

    }//GEN-LAST:event_comboBoxFormatoActionPerformed

    private void btnSeleccionarAlquiladoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSeleccionarAlquiladoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSeleccionarAlquiladoActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnSeleccionarDisponibleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSeleccionarDisponibleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSeleccionarDisponibleActionPerformed



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnSeleccionarAlquilado;
    private javax.swing.JButton btnSeleccionarDisponible;
    private javax.swing.JComboBox<String> comboBoxDistribuidora;
    private javax.swing.JComboBox<String> comboBoxFormato;
    private javax.swing.JCheckBox jCheckBoxAlquilado;
    private javax.swing.JCheckBox jCheckBoxBaja;
    private javax.swing.JCheckBox jCheckBoxStock;
    private javax.swing.JLabel jLabelAgregarPelicula;
    private javax.swing.JLabel jLabelAlquilado;
    private javax.swing.JLabel jLabelAnioLanzamiento;
    private javax.swing.JLabel jLabelCosteUnitario;
    private javax.swing.JLabel jLabelCuotaAlquiler;
    private javax.swing.JLabel jLabelDirector;
    private javax.swing.JLabel jLabelDisponible;
    private javax.swing.JLabel jLabelDisponibleAlquiler;
    private javax.swing.JLabel jLabelDistribuidoras;
    private javax.swing.JLabel jLabelDuracion;
    private javax.swing.JLabel jLabelFechaAlta;
    private javax.swing.JLabel jLabelFechaLanzamiento;
    private javax.swing.JLabel jLabelFormato;
    private javax.swing.JLabel jLabelGenero;
    private javax.swing.JLabel jLabelLogoPelicula;
    private javax.swing.JLabel jLabelPrecioVenta;
    private javax.swing.JLabel jLabelPreviewAlquilado;
    private javax.swing.JLabel jLabelPreviewDisponible;
    private javax.swing.JLabel jLabelProtagonista;
    private javax.swing.JLabel jLabelRecargo;
    private javax.swing.JLabel jLabelSecun1;
    private javax.swing.JLabel jLabelSecun2;
    private javax.swing.JLabel jLabelSinopsis;
    private javax.swing.JLabel jLabelSubgenero;
    private javax.swing.JLabel jLabelTitulo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelDisponibles;
    private javax.swing.JPanel jPanelEliminar;
    private javax.swing.JPanel jPanelInfoAdicional;
    private javax.swing.JPanel jPanelInfoBasica;
    private javax.swing.JPanel jPanelInfoFechaCostes;
    private javax.swing.JPanel jPanelPreviewPortadas;
    private javax.swing.JRadioButton jRadioButtonEstreno;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPanel panel;
    private javax.swing.JSpinner spinnerDisponibleAlquiler;
    private javax.swing.JTextField tfAlquilado;
    private javax.swing.JTextField tfAnioLanzamiento;
    private javax.swing.JTextField tfCosteUnitario;
    private javax.swing.JTextField tfCuotaAlquilerPelicula;
    private javax.swing.JTextField tfDirector;
    private javax.swing.JTextField tfDisponible;
    private javax.swing.JTextField tfDuracion;
    private javax.swing.JTextField tfFechaAlta;
    private javax.swing.JTextField tfFechaLanzamiento;
    private javax.swing.JTextField tfGenero;
    private javax.swing.JTextField tfPVP;
    private javax.swing.JTextField tfProtagonista;
    private javax.swing.JTextField tfRecargo;
    private javax.swing.JTextField tfSecun1;
    private javax.swing.JTextField tfSecun2;
    private javax.swing.JTextField tfSinopsis;
    private javax.swing.JTextField tfSubgenero;
    private javax.swing.JTextField tfTitulo;
    // End of variables declaration//GEN-END:variables
}
