package com.shms.db;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 * Modern Database Connection Manager for SHMS.
 */
public class DBConnection {

    private static String URL;
    private static String USER;
    private static String PASS;

    static {
        try {
            Properties props = new Properties();
            try (InputStream input = new FileInputStream("config.properties")) {
                props.load(input);
            }
            URL = props.getProperty("db.url");
            USER = props.getProperty("db.user");
            PASS = props.getProperty("db.password");
            
            // Standard Driver Registration
            Class.forName("com.mysql.cj.jdbc.Driver");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "System Initialization Error: config.properties missing or malformed!\n" + e.getMessage(), 
                "Critical Setup Failure", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Managed connection for atomic operations.
     */
    public static Connection getConnection() throws SQLException {
        if (URL == null) throw new SQLException("Configuration not loaded correctly.");
        return DriverManager.getConnection(URL, USER, PASS);
    }

    /**
     * Heartbeat check for DB node health.
     */
    public static boolean checkStatus() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed() && conn.isValid(2);
        } catch (Exception e) {
            return false;
        }
    }
}
