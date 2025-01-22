package com.mycompany.proyectovideoclub;

/**
 *
 * @author oscar.lara
 */

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Producto {
    private int id;
    private String titulo;
    private int anioLanzamiento;
    private double costeUnitario;
    private Date fechaLanzamiento;
    private Date fechaAltaDatabase;
    
    private int numDisponibleAlquiler;
    private int numUnidadesAlquiladas;
    private int numDisponibleVenta;
    private int numUnidadesVendidas;
    private double precioVenta;
    private double recargoDevolucion;
    
    private boolean esEstreno;
    private boolean enStock;
    private boolean alquilado;
    private boolean esBaja;
    
    private String genero;
    private String subgenero;
    private String sinopsis;
    private String imagenProductoDisponible;
    private String imagenProductoAlquilado;


    public Producto() {
    }

    public Producto(int id, String titulo, int anioLanzamiento, double costeUnitario, Date fechaAltaDatabase, boolean esEstreno) {
        this.id = id;
        this.titulo = titulo;
        this.anioLanzamiento = anioLanzamiento;
        this.costeUnitario = costeUnitario;
        this.fechaAltaDatabase = fechaAltaDatabase;
        this.esEstreno = esEstreno;
    }

    public Producto(int id, String titulo, int anioLanzamiento, double costeUnitario, Date fechaLanzamiento, Date fechaAltaDatabase, int numDisponibleAlquiler, int numUnidadesAlquiladas, int numDisponibleVenta, int numUnidadesVendidas, double precioVenta, double recargoDevolucion, boolean esEstreno, boolean enStock, boolean alquilado, boolean esBaja, String genero, String subgenero, String sinopsis, String imagenProductoDisponible, String imagenProductoAlquilado) {
        this.id = id;
        this.titulo = titulo;
        this.anioLanzamiento = anioLanzamiento;
        this.costeUnitario = costeUnitario;
        this.fechaLanzamiento = fechaLanzamiento;
        this.fechaAltaDatabase = fechaAltaDatabase;
        this.numDisponibleAlquiler = numDisponibleAlquiler;
        this.numUnidadesAlquiladas = numUnidadesAlquiladas;
        this.numDisponibleVenta = numDisponibleVenta;
        this.numUnidadesVendidas = numUnidadesVendidas;
        this.precioVenta = precioVenta;
        this.recargoDevolucion = recargoDevolucion;
        this.esEstreno = esEstreno;
        this.enStock = enStock;
        this.alquilado = alquilado;
        this.esBaja = esBaja;
        this.genero = genero;
        this.subgenero = subgenero;
        this.sinopsis = sinopsis;
        this.imagenProductoDisponible = imagenProductoDisponible;
        this.imagenProductoAlquilado = imagenProductoAlquilado;
    }

    ///---Constructor bueno
    public Producto(
            int id,  //0
            String titulo, //1
            int anioLanzamiento, //2
            double costeUnitario, //3
            Date fechaLanzamiento, //4
            Date fechaAltaDatabase, //5
            int numDisponibleAlquiler, //6
            double recargoDevolucion, //7
            boolean esEstreno, //8
            String genero, //9
            String subgenero, //10
            String sinopsis, //11
            String imagenProductoDisponible, //12
            String imagenProductoAlquilado) { //13
        this.id = id;
        this.titulo = titulo;
        this.anioLanzamiento = anioLanzamiento;
        this.costeUnitario = costeUnitario;
        this.fechaLanzamiento = fechaLanzamiento;
        this.fechaAltaDatabase = fechaAltaDatabase;
        this.numDisponibleAlquiler = numDisponibleAlquiler;
        this.recargoDevolucion = recargoDevolucion;
        this.esEstreno = esEstreno;
        this.genero = genero;
        this.subgenero = subgenero;
        this.sinopsis = sinopsis;
        this.imagenProductoDisponible = imagenProductoDisponible;
        this.imagenProductoAlquilado = imagenProductoAlquilado;
    }
    
    
    
    // Métodos getter y setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getAnioLanzamiento() {
        return anioLanzamiento;
    }

    public void setAnioLanzamiento(int anioLanzamiento) {
        this.anioLanzamiento = anioLanzamiento;
    }

    public double getCosteUnitario() {
        return costeUnitario;
    }

    public void setCosteUnitario(double costeUnitario) {
        this.costeUnitario = costeUnitario;
    }

    public Date getFechaLanzamiento() {
        return fechaLanzamiento;
    }

    public void setFechaLanzamiento(Date fechaLanzamiento) {
        this.fechaLanzamiento = fechaLanzamiento;
    }

    public Date getFechaAltaDatabase() {
        return fechaAltaDatabase;
    }

    public void setFechaAltaDatabase(Date fechaAltaDatabase) {
        this.fechaAltaDatabase = fechaAltaDatabase;
    }

    public int getNumDisponibleAlquiler() {
        return numDisponibleAlquiler;
    }

    public void setNumDisponibleAlquiler(int numDisponibleAlquiler) {
        this.numDisponibleAlquiler = numDisponibleAlquiler;
    }

    public int getNumUnidadesAlquiladas() {
        return numUnidadesAlquiladas;
    }

    public void setNumUnidadesAlquiladas(int numUnidadesAlquiladas) {
        this.numUnidadesAlquiladas = numUnidadesAlquiladas;
    }

    public int getNumDisponibleVenta() {
        return numDisponibleVenta;
    }

    public void setNumDisponibleVenta(int numDisponibleVenta) {
        this.numDisponibleVenta = numDisponibleVenta;
    }

    public int getNumUnidadesVendidas() {
        return numUnidadesVendidas;
    }

    public void setNumUnidadesVendidas(int numUnidadesVendidas) {
        this.numUnidadesVendidas = numUnidadesVendidas;
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public double getRecargoDevolucion() {
        return recargoDevolucion;
    }

    public void setRecargoDevolucion(double recargoDevolucion) {
        this.recargoDevolucion = recargoDevolucion;
    }

    public boolean isEsEstreno() {
        return esEstreno;
    }

    public void setEsEstreno(boolean esEstreno) {
        this.esEstreno = esEstreno;
    }

    public boolean isEnStock() {
        return enStock;
    }

    public void setEnStock(boolean enStock) {
        this.enStock = enStock;
    }

    public boolean isAlquilado() {
        return alquilado;
    }

    public void setAlquilado(boolean alquilado) {
        this.alquilado = alquilado;
    }

    public boolean isEsBaja() {
        return esBaja;
    }

    public void setEsBaja(boolean esBaja) {
        this.esBaja = esBaja;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getSubgenero() {
        return subgenero;
    }

    public void setSubgenero(String subgenero) {
        this.subgenero = subgenero;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

    public String getImagenProductoDisponible() {
        return imagenProductoDisponible;
    }

    public void setImagenProductoDisponible(String imagenProductoDisponible) {
        this.imagenProductoDisponible = imagenProductoDisponible;
    }

    public String getImagenProductoAlquilado() {
        return imagenProductoAlquilado;
    }

    public void setImagenProductoAlquilado(String imagenProductoAlquilado) {
        this.imagenProductoAlquilado = imagenProductoAlquilado;
    }
    
     // Método para obtener todos los productos, ordenados alfabéticamente por título
    public static List<Producto> obtenerProductos(Connection conn) throws SQLException {
        String query = "SELECT * FROM Productos ORDER BY titulo ASC"; // Orden por título alfabéticamente
        List<Producto> productos = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Producto producto = new Producto();
                producto.setId(rs.getInt("id"));
                producto.setTitulo(rs.getString("titulo"));
                producto.setAnioLanzamiento(rs.getInt("anioLanzamiento"));
                
                productos.add(producto);
            }
        }
        return productos;
    }

    // Método de conexión con la base de datos
    public static Connection conectar() throws SQLException {
        return Database.getConnection();
    }
    

  
//        try (Connection conn = Producto.conectar()) {
//            List<Producto> productos = Producto.obtenerProductos(conn);
//            
//            // Mostrar los productos
//            System.out.println("Productos disponibles en el videoclub:");
//            for (Producto producto : productos) {
//                System.out.println(producto.getTitulo() + " (" + producto.getAnioLanzamiento() + ")");
//            }
//        } catch (SQLException e) {
//            System.err.println("Error de conexión o consulta: " + e.getMessage());
//        }
        
        
        
    }

