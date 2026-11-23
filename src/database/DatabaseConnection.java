package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import util.Constants;

public class DatabaseConnection {
    private static Connection connection = null;

    static {
        try {
            Class.forName(Constants.DB_DRIVER);
            System.out.println("[DB] MySQL JDBC Driver registered successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("[DB] Driver error: " + e.getMessage());
            System.err.println("[DB] Ensure mysql-connector-j jar is present in classpath.");
        }
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(Constants.DB_URL, Constants.DB_USER, Constants.DB_PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("[DB] Connection error: " + e.getMessage());
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error closing connection: " + e.getMessage());
        }
    }

    private DatabaseConnection() {
    }
}
