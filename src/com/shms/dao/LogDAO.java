package com.shms.dao;

import com.shms.db.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * System-wide Audit Logger.
 */
public class LogDAO {

    private static final String LOG_FILE = "system.audit.log";

    /**
     * Records a user action in both the permanent DB and external file.
     */
    public void record(int userId, String action, String module) {
        // --- TARGET 1: RELATIONAL DATABASE ---
        String sql = "INSERT INTO audit_logs (user_id, action, module) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setString(2, action);
            ps.setString(3, module);
            ps.executeUpdate();
            
        } catch (Exception e) {
            System.err.println("Audit Exception (DB): " + e.getMessage());
        }

        // --- TARGET 2: FILESYSTEM (Log4j Simulation) ---
        try (java.io.FileWriter fw = new java.io.FileWriter(LOG_FILE, true);
             java.io.PrintWriter pw = new java.io.PrintWriter(fw)) {
            String ts = java.time.LocalDateTime.now().toString();
            pw.printf("[%s] [USER_ID:%d] [MODULE:%s] - ACTION: %s\n", ts, userId, module, action);
        } catch (java.io.IOException e) {
            System.err.println("Audit Exception (FILE): " + e.getMessage());
        }
    }
}
