package org.example.Com;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://lab-db.cunqycqegtvt.us-east-1.rds.amazonaws.com:3306/ecommerce_db";
    private static final String USER = "admin"; // altere para o seu usuário
    private static final String PASSWORD = "Db_17400"; // altere para sua senha

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
