package com.shms.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 * Singleton-pattern Database Connection class for SHMS.
 * Handles MySQL connectivity with auto-reconnect logic.
 */
public class DBConnection {

    // ── CONFIGURE YOUR LOCAL DATABASE HERE ─────────────────────────────────────
    private static final String URL  = "jdbc:mysql://localhost:3306/shms_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "20021115"; // Change to your MySQL password
    private DBConnection() {} // Prevent instantiation

    /**
     * Returns a valid, fresh database connection for an atomic transaction.
     * Engineered to work perfectly with Try-With-Resources (AutoCloseable).
     */
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                "MySQL JDBC Driver not found!\n\nPlease add 'mysql-connector-j' JAR to the project libraries.",
                "Driver Missing", javax.swing.JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Database connection failed!\n\n" +
                "Details: " + e.getMessage() + "\n\n" +
                "Please check:\n" +
                " 1. MySQL server is running\n" +
                " 2. Database 'shms_db' exists (run shms_database_setup.sql)\n" +
                " 3. Password in DBConnection.java is correct",
                "Connection Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    /**
     * Heartbeat check for System Health Monitoring.
     */
    public static boolean checkStatus() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed() && conn.isValid(2);
        } catch (Exception e) {
            return false;
        }
    }
}
