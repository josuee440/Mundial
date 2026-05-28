package com.umg.mundial.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private static final String URL = "jdbc:postgresql://localhost:5432/mundial_fut_2026";
    private static final String USER = "postgres";
    private static final String PASS = "fc17181931"; // <-- Cambia esto

    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
