package com.spotculture.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/spotculture?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "SpotCulture2025!"; // ton mot de passe

    public static Connection getConnection() throws SQLException {
        try {
            // Chargement explicite du driver JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver non trouvé !", e);
        }

        // Connexion à la base de données
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
