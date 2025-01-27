package com.mycompany.proyectovideoclub;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

        comboBoxFormato = new JComboBox<>();
        panel.add(new JLabel("Formato:"));
        panel.add(comboBoxFormato);

        // Cargar los nombres de los formatos al inicializar
        cargarNombresDeFormatos();

        // Crear el JSpinner para "Disponible Alquiler"
        JSpinner spinnerDisponibleAlquiler = new JSpinner();
        spinnerDisponibleAlquiler.setModel(new SpinnerNumberModel(0, 0, 100, 1)); // Valor inicial: 1, mínimo: 1, máximo: 100, incremento: 1
        panel.add(new JLabel("Disponible Alquiler:"));
        panel.add(spinnerDisponibleAlquiler); // Agregar el spinner al panel

        // Acción para seleccionar imagen disponible
        btnSeleccionarDisponible.addActionListener(e -> {
            String rutaImagen = seleccionarImagen();
            if (rutaImagen != null) {
                tfDisponible.setText(rutaImagen); // Asigna la ruta al campo de texto
                cargarPreviewEnLabel(rutaImagen, jLabelPreviewDisponible); // Muestra la imagen en el JLabel
            }
        });

        // Acción para seleccionar imagen alquilado
        btnSeleccionarAlquilado.addActionListener(e -> {
            String rutaImagen = seleccionarImagen();
            if (rutaImagen != null) {
                tfAlquilado.setText(rutaImagen); // Asigna la ruta al campo de texto
                cargarPreviewEnLabel(rutaImagen, jLabelPreviewAlquilado); // Muestra la imagen en el JLabel
            }
        });

        // Acción para guardar la película
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
                int formatoId = Integer.parseInt(tfFormato.getText());
                int distribuidoraId = Integer.parseInt(tfDistribuidora.getText());
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
                            + "costeUnitario, f"
                            + "echaLanzamiento, "
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
                        stmtProductos.setInt(6, numDisponibleAlquiler); // Usar valor del spinner
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
        jPanel3 = new javax.swing.JPanel();
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
        jPanel1 = new javax.swing.JPanel();
        jRadioButtonEstreno = new javax.swing.JRadioButton();
        jCheckBoxAlquilado = new javax.swing.JCheckBox();
        jCheckBoxStock = new javax.swing.JCheckBox();
        jCheckBoxBaja = new javax.swing.JCheckBox();
        btnGuardar = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
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
        jPanel5 = new javax.swing.JPanel();
        jLabelTitulo = new javax.swing.JLabel();
        tfTitulo = new javax.swing.JTextField();
        jLabelAnioLanzamiento = new javax.swing.JLabel();
        tfAnioLanzamiento = new javax.swing.JTextField();
        jLabelFormato = new javax.swing.JLabel();
        comboBoxFormato = new javax.swing.JComboBox<>();
        comboBoxDistribuidora = new javax.swing.JComboBox<>();
        jLabelDistribuidoras = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabelDisponible = new javax.swing.JLabel();
        btnSeleccionarDisponible = new javax.swing.JButton();
        jLabelAlquilado = new javax.swing.JLabel();
        btnSeleccionarAlquilado = new javax.swing.JButton();
        jLabelDisponibleAlquiler = new javax.swing.JLabel();
        spinnerDisponibleAlquiler = new javax.swing.JSpinner();
        jPanel8 = new javax.swing.JPanel();
        tfDisponible = new javax.swing.JTextField();
        tfAlquilado = new javax.swing.JTextField();
        tfFormato = new javax.swing.JTextField();
        tfDistribuidora = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jLabelPreviewAlquilado = new javax.swing.JLabel();
        jLabelPreviewDisponible = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabelDirector.setText("Director: ");

        jLabelDuracion.setText("Duración (min): ");

        jLabelGenero.setText("Género: ");

        jLabelSubgenero.setText("Subgénero: ");

        jLabelSinopsis.setText("Sinopsis: ");

        tfSinopsis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfSinopsisActionPerformed(evt);
            }
        });

        jLabelProtagonista.setText("Protagonista:");

        jLabelSecun1.setText("Secundario 1: ");

        jLabelSecun2.setText("Secundario 2: ");

        jRadioButtonEstreno.setText("Estreno");
        jRadioButtonEstreno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonEstrenoActionPerformed(evt);
            }
        });

        jCheckBoxAlquilado.setText("Alquilado");

        jCheckBoxStock.setText("Stock");
        jCheckBoxStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxStockActionPerformed(evt);
            }
        });

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
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBoxBaja)
                    .addComponent(jRadioButtonEstreno)
                    .addComponent(jCheckBoxAlquilado)
                    .addComponent(jCheckBoxStock))
                .addContainerGap(40, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jRadioButtonEstreno)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBoxAlquilado)
                .addGap(12, 12, 12)
                .addComponent(jCheckBoxStock)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBoxBaja)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelDuracion)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabelGenero)
                                .addComponent(jLabelDirector))
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabelSecun1)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabelProtagonista)
                                    .addComponent(jLabelSubgenero))
                                .addComponent(jLabelSecun2))
                            .addComponent(jLabelSinopsis))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(tfDuracion, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tfDirector, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tfGenero, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tfSubgenero, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tfSecun1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tfSecun2)
                            .addComponent(tfSinopsis)
                            .addComponent(tfProtagonista))
                        .addGap(27, 27, 27))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(38, Short.MAX_VALUE))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfDuracion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelDuracion))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelDirector)
                    .addComponent(tfDirector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelGenero)
                    .addComponent(tfGenero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelSubgenero)
                    .addComponent(tfSubgenero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelProtagonista)
                    .addComponent(tfProtagonista, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelSecun1)
                    .addComponent(tfSecun1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelSecun2)
                    .addComponent(tfSecun2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tfSinopsis, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelSinopsis))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(57, 57, 57)
                        .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jLabelFechaAlta.setText("Fecha de alta: ");

        jLabelFechaLanzamiento.setText("Fecha lanzamiento: ");

        jLabelPrecioVenta.setText("P.V.P");

        jLabelRecargo.setText("Recargo: ");

        jLabelCosteUnitario.setText("Coste unitario: ");

        jLabelCuotaAlquiler.setText("Cuota alquiler: ");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelRecargo)
                            .addComponent(jLabelPrecioVenta))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tfRecargo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tfPVP, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelCosteUnitario)
                            .addComponent(jLabelCuotaAlquiler))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tfCuotaAlquilerPelicula, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tfCosteUnitario, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(31, Short.MAX_VALUE))
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

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(19, 19, 19)
                                .addComponent(jLabelFechaLanzamiento))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabelFechaAlta)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tfFechaLanzamiento, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                            .addComponent(tfFechaAlta)))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(53, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(28, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelFechaAlta)
                    .addComponent(tfFechaAlta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelFechaLanzamiento)
                    .addComponent(tfFechaLanzamiento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabelTitulo.setText("Título:  ");

        tfTitulo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfTituloActionPerformed(evt);
            }
        });

        jLabelAnioLanzamiento.setText("Año lanzamiento:  ");

        jLabelFormato.setText("Formato");

        comboBoxFormato.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxFormatoActionPerformed(evt);
            }
        });

        comboBoxDistribuidora.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "20TH Century Fox" }));
        comboBoxDistribuidora.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxDistribuidoraActionPerformed(evt);
            }
        });

        jLabelDistribuidoras.setText("Distribuidora");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabelAnioLanzamiento)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tfAnioLanzamiento, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(74, 74, 74)
                        .addComponent(jLabelTitulo)
                        .addGap(18, 18, 18)
                        .addComponent(tfTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelDistribuidoras)
                            .addComponent(jLabelFormato))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(comboBoxFormato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(comboBoxDistribuidora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(26, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelTitulo)
                    .addComponent(tfTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelAnioLanzamiento)
                    .addComponent(tfAnioLanzamiento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelFormato)
                    .addComponent(comboBoxFormato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboBoxDistribuidora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelDistribuidoras))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabelDisponible.setText("Disponible");

        btnSeleccionarDisponible.setText("Seleccionar");
        btnSeleccionarDisponible.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSeleccionarDisponibleActionPerformed(evt);
            }
        });

        jLabelAlquilado.setText("Alquilado:");

        btnSeleccionarAlquilado.setText("Seleccionar");
        btnSeleccionarAlquilado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSeleccionarAlquiladoActionPerformed(evt);
            }
        });

        jLabelDisponibleAlquiler.setText("Disponible alquiler: ");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelDisponible)
                            .addComponent(jLabelAlquilado))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnSeleccionarDisponible, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnSeleccionarAlquilado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabelDisponibleAlquiler)
                        .addGap(18, 18, 18)
                        .addComponent(spinnerDisponibleAlquiler, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(32, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelDisponibleAlquiler)
                    .addComponent(spinnerDisponibleAlquiler, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSeleccionarDisponible)
                    .addComponent(jLabelDisponible))
                .addGap(18, 21, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelAlquilado)
                    .addComponent(btnSeleccionarAlquilado))
                .addGap(23, 23, 23))
        );

        tfDisponible.setText("jTextField2");
        tfDisponible.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfDisponibleActionPerformed(evt);
            }
        });

        tfAlquilado.setText("jTextField1");

        tfFormato.setText("jTextField1");

        tfDistribuidora.setText("jTextField1");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(54, 54, 54)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(tfDistribuidora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfFormato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfAlquilado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfDisponible, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(102, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(tfDisponible, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(tfAlquilado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(tfFormato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(tfDistribuidora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(79, Short.MAX_VALUE))
        );

        jLabelPreviewDisponible.setBackground(new java.awt.Color(255, 204, 204));
        jLabelPreviewDisponible.setForeground(new java.awt.Color(255, 204, 204));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabelPreviewAlquilado, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 69, Short.MAX_VALUE)
                .addComponent(jLabelPreviewDisponible, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabelPreviewDisponible, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelPreviewAlquilado, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11))
        );

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelLayout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelLayout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 180, Short.MAX_VALUE)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(538, Short.MAX_VALUE))
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelLayout.createSequentialGroup()
                        .addGap(275, 275, 275)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelLayout.createSequentialGroup()
                                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(panelLayout.createSequentialGroup()
                                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

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
            java.util.logging.Logger.getLogger(GestionPeliculas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GestionPeliculas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GestionPeliculas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GestionPeliculas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GestionPeliculas().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnSeleccionarAlquilado;
    private javax.swing.JButton btnSeleccionarDisponible;
    private javax.swing.JComboBox<String> comboBoxDistribuidora;
    private javax.swing.JComboBox<String> comboBoxFormato;
    private javax.swing.JCheckBox jCheckBoxAlquilado;
    private javax.swing.JCheckBox jCheckBoxBaja;
    private javax.swing.JCheckBox jCheckBoxStock;
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
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JRadioButton jRadioButtonEstreno;
    private javax.swing.JPanel panel;
    private javax.swing.JSpinner spinnerDisponibleAlquiler;
    private javax.swing.JTextField tfAlquilado;
    private javax.swing.JTextField tfAnioLanzamiento;
    private javax.swing.JTextField tfCosteUnitario;
    private javax.swing.JTextField tfCuotaAlquilerPelicula;
    private javax.swing.JTextField tfDirector;
    private javax.swing.JTextField tfDisponible;
    private javax.swing.JTextField tfDistribuidora;
    private javax.swing.JTextField tfDuracion;
    private javax.swing.JTextField tfFechaAlta;
    private javax.swing.JTextField tfFechaLanzamiento;
    private javax.swing.JTextField tfFormato;
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
