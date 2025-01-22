package com.mycompany.proyectovideoclub;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Pelicula extends Producto {

    private String director;
    private int duracion;
    private String actorProtagonista;
    private String actorSecundario1;
    private String actorSecundario2;
    private Formato formato;
    private DistribuidoraPeliculas distribuidoraPelicula;
    private double cuotaAlquilerPeliculas;
    private double margenVentaPeliculas;

    public Pelicula() {
    }

    // Esto es pelicula que hace referencia a lo suyo
    public Pelicula(String director, int duracion, String actorProtagonista, String actorSecundario1, String actorSecundario2, Formato formato, DistribuidoraPeliculas distribuidoraPelicula, double cuotaAlquilerPeliculas) {
        this.director = director;
        this.duracion = duracion;
        this.actorProtagonista = actorProtagonista;
        this.actorSecundario1 = actorSecundario1;
        this.actorSecundario2 = actorSecundario2;
        this.formato = formato;
        this.distribuidoraPelicula = distribuidoraPelicula;
        this.cuotaAlquilerPeliculas = cuotaAlquilerPeliculas;
    }

    public Pelicula(int id, String titulo, int anioLanzamiento, double costeUnitario, java.util.Date fechaAltaDatabase, boolean esEstreno) {
        super(id, titulo, anioLanzamiento, costeUnitario, fechaAltaDatabase, esEstreno);
    }

    public Pelicula(
            int id,
            String titulo, //1
            int anioLanzamiento,
            double costeUnitario,
            java.util.Date fechaLanzamiento,
            java.util.Date fechaAltaDatabase,
            int numDisponibleAlquiler,
            double recargoDevolucion,
            boolean esEstreno,
            String genero,
            String subgenero,
            String sinopsis,
            String imagenProductoDisponible,
            String imagenProductoAlquilado) {

        super( // PRODUCTO
                id,
                titulo, //1
                anioLanzamiento, //2
                costeUnitario, //3
                fechaLanzamiento, //4
                fechaAltaDatabase, //5
                numDisponibleAlquiler, //----------6
                recargoDevolucion, //------7
                esEstreno, //8
                genero, //9
                subgenero, //10
                sinopsis, //11
                imagenProductoDisponible, //12
                imagenProductoAlquilado); //13
    }

    public static void agregarPeliculaAlquiler(
            Connection conn, //0
            String titulo, //1
            int anioLanzamiento, // 2
            double costeUnitario, // 3
            Date fechaLanzamiento, // 4                
            Date fechaAltaDatabase, // 5 
            int numDisponibleAlquiler,
            double recargoDevolucion,
            boolean esEstreno, // 6
            String genero, //7
            String subgenero, //8
            String sinopsis, //9
            String imagenProductoDisponible, //10                    
            String imagenProductoAlquilado, //11
            String director, //12
            String duracion, //13
            String actorProtagonista, //14      
            String actorSecundario1, //15
            String actorSecundario2, //16
            String formatoId, //17
            String distribuidoraId, //18
            String cuotaAlquilerPeliculas) throws SQLException { //19

        System.out.println("Agregando una nueva película...");

        // 1. Insertar el nuevo producto en la tabla Productos
        String queryProductos = "INSERT INTO "
                + "Productos ("
                + "titulo, " // 1 (conn no cuenta)
                + "anioLanzamiento, " //2
                + "costeUnitario, " //3
                + "fechaLanzamiento, " //4
                + "fechaAltaDatabase, " //5
                + "numDisponibleAlquiler, " //6
                + "recargoDevolucion, " //7
                + "esEstreno, " //8
                + "genero, " //9
                + "subgenero, " //10
                + "sinopsis, "//11
                + "imagenProductoDisponible, " //12
                + "imagenProductoAlquilado) " //13
                + //11
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try ( PreparedStatement stmtProd = conn.prepareStatement(queryProductos, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmtProd.setString(1, titulo); // String
            stmtProd.setInt(2, anioLanzamiento); // int
            stmtProd.setDouble(3, costeUnitario); // double
            stmtProd.setDate(4, new java.sql.Date(fechaLanzamiento.getTime())); 
            stmtProd.setDate(5, new java.sql.Date(fechaAltaDatabase.getTime())); 
            stmtProd.setInt(6, numDisponibleAlquiler);
            stmtProd.setDouble(7, recargoDevolucion); 
            stmtProd.setBoolean(8, esEstreno); // boolean
            stmtProd.setString(9, genero); // String
            stmtProd.setString(10, subgenero); // String
            stmtProd.setString(11, sinopsis); // String
            stmtProd.setString(12, imagenProductoDisponible); // String
            stmtProd.setString(13, imagenProductoAlquilado); // String

            stmtProd.executeUpdate();

            // Obtener el ID generado para el producto recién insertado
            ResultSet generatedKeys = stmtProd.getGeneratedKeys();
            int productoId = 0;
            if (generatedKeys.next()) {
                productoId = generatedKeys.getInt(1);
            } else {
                throw new SQLException("Error al obtener el ID del producto insertado.");
            }

            // 2. Insertar en la tabla Peliculas usando el ID del producto recién insertado
            String queryPeliculas = "INSERT INTO "
                    + "Peliculas ("
                    + "id, " //(1)
                    + "director, "// (2)-14
                    + "duracion, "// (3)-15
                    + "actorProtagonista, "// (4)-16
                    + "actorSecundario1, "// (5)-17
                    + "actorSecundario2, "// (6)-18
                    + "formato_id, "// (7)-19
                    + "distribuidora_id, "// (8)-20
                    + "cuotaAlquilerPeliculas) "// (9)-21
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try ( PreparedStatement stmtPeli = conn.prepareStatement(queryPeliculas)) {
                stmtPeli.setInt(1, productoId); // Usamos el ID del producto insertado
                stmtPeli.setString(2, director);
                stmtPeli.setString(3, duracion);
                stmtPeli.setString(4, actorProtagonista);
                stmtPeli.setString(5, actorSecundario1);
                stmtPeli.setString(6, actorSecundario2);
                stmtPeli.setString(7, formatoId);
                stmtPeli.setString(8, distribuidoraId);
                stmtPeli.setString(9, cuotaAlquilerPeliculas);

                stmtPeli.executeUpdate();
                System.out.println("Película agregada correctamente.");
            }
        }
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public String getActorProtagonista() {
        return actorProtagonista;
    }

    public void setActorProtagonista(String actorProtagonista) {
        this.actorProtagonista = actorProtagonista;
    }

    public String getActorSecundario1() {
        return actorSecundario1;
    }

    public void setActorSecundario1(String actorSecundario1) {
        this.actorSecundario1 = actorSecundario1;
    }

    public String getActorSecundario2() {
        return actorSecundario2;
    }

    public void setActorSecundario2(String actorSecundario2) {
        this.actorSecundario2 = actorSecundario2;
    }

    public Formato getFormato() {
        return formato;
    }

    public void setFormato(Formato formato) {
        this.formato = formato;
    }

    public DistribuidoraPeliculas getDistribuidoraPelicula() {
        return distribuidoraPelicula;
    }

    public void setDistribuidoraPelicula(DistribuidoraPeliculas distribuidoraPelicula) {
        this.distribuidoraPelicula = distribuidoraPelicula;
    }

    public double getCuotaAlquilerPeliculas() {
        return cuotaAlquilerPeliculas;
    }

    public void setCuotaAlquilerPeliculas(double cuotaAlquilerPeliculas) {
        this.cuotaAlquilerPeliculas = cuotaAlquilerPeliculas;
    }

    public double getMargenVentaPeliculas() {
        return margenVentaPeliculas;
    }

    public void setMargenVentaPeliculas(double margenVentaPeliculas) {
        this.margenVentaPeliculas = margenVentaPeliculas;
    }

//public static void agregarPelicula(Connection conn, String titulo, String anioLanzamiento, String costeUnitario, String fechaLanzamiento,
//                                   String fechaAltaDatabase, boolean esEstreno, boolean enStock, String precioVenta, 
//                                   String genero, String subgenero, String sinopsis, String imagenProductoDisponible, 
//                                   String imagenProductoAlquilado, String director, String duracion, String actorProtagonista, 
//                                   String actorSecundario1, String actorSecundario2, String formatoId, String distribuidoraId, 
//                                   String cuotaAlquilerPeliculas, String margenVentaPeliculas) throws SQLException {
//
//    System.out.println("Agregando una nueva película...");
//
//    // 1. Insertar el nuevo producto en la tabla Productos
//    String queryProductos = "INSERT INTO Productos (titulo, anioLanzamiento, costeUnitario, fechaLanzamiento, fechaAltaDatabase, " +
//                            "esEstreno, enStock, precioVenta, genero, subgenero, sinopsis, imagenProductoDisponible, imagenProductoAlquilado) " +
//                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//
//    try (PreparedStatement stmtProd = conn.prepareStatement(queryProductos, PreparedStatement.RETURN_GENERATED_KEYS)) {
//        stmtProd.setString(1, titulo);
//        stmtProd.setString(2, anioLanzamiento);
//        stmtProd.setString(3, costeUnitario);
//        stmtProd.setString(4,fechaLanzamiento);
//        stmtProd.setString(5, fechaAltaDatabase);
//        stmtProd.setBoolean(6, esEstreno);
//        stmtProd.setBoolean(7, enStock);
//        stmtProd.setString(8, precioVenta);
//        stmtProd.setString(9, genero);
//        stmtProd.setString(10, subgenero);
//        stmtProd.setString(11, sinopsis);
//        stmtProd.setString(12, imagenProductoDisponible);
//        stmtProd.setString(13, imagenProductoAlquilado);
//
//        stmtProd.executeUpdate();
//
//        // Obtener el ID generado para el producto recién insertado
//        ResultSet generatedKeys = stmtProd.getGeneratedKeys();
//        int productoId = 0;
//        if (generatedKeys.next()) {
//            productoId = generatedKeys.getInt(1);
//        } else {
//            throw new SQLException("Error al obtener el ID del producto insertado.");
//        }
//
//        // 2. Insertar en la tabla Peliculas usando el ID del producto recién insertado
//        String queryPeliculas = "INSERT INTO Peliculas (id, director, duracion, actorProtagonista, actorSecundario1, actorSecundario2, " +
//                                "formato_id, distribuidora_id, cuotaAlquilerPeliculas, margenVentaPeliculas) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//
//        try (PreparedStatement stmtPeli = conn.prepareStatement(queryPeliculas)) {
//            stmtPeli.setInt(1, productoId); // Usamos el ID del producto insertado
//            stmtPeli.setString(2, director);
//            stmtPeli.setString(3, duracion);
//            stmtPeli.setString(4, actorProtagonista);
//            stmtPeli.setString(5, actorSecundario1);
//            stmtPeli.setString(6, actorSecundario2);
//            stmtPeli.setString(7, formatoId);
//            stmtPeli.setString(8, distribuidoraId);
//            stmtPeli.setString(9, cuotaAlquilerPeliculas);
//            stmtPeli.setString(10, margenVentaPeliculas);
//
//            stmtPeli.executeUpdate();
//            System.out.println("Película agregada correctamente.");
//        }
//    }
//}
}
