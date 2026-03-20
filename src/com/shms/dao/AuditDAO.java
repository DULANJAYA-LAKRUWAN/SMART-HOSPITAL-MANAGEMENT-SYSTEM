package com.shms.dao;

import com.shms.db.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * System Auditing Layer: Tracks critical system events.
 */
public class AuditDAO {

    public void log(int userId, String action, String module) {
        String query = "INSERT INTO audit_logs (user_id, action, module) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setInt(1, userId);
            pst.setString(2, action);
            pst.setString(3, module);
            pst.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("[Audit] Log Failure: " + e.getMessage());
        }
    }

    /**
     * Transactional Audit: Uses an existing connection to maintain atomicity.
     */
    public void logTransactional(Connection conn, int userId, String action, String module) throws SQLException {
        String query = "INSERT INTO audit_logs (user_id, action, module) VALUES (?, ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, userId);
            pst.setString(2, action);
            pst.setString(3, module);
            pst.executeUpdate();
        }
    }

    public java.util.List<Object[]> getAllLogs() {
        java.util.List<Object[]> list = new java.util.ArrayList<>();
        String query = "SELECT log_id, user_id, action, module, log_time FROM audit_logs ORDER BY log_id DESC LIMIT 100";
        try (java.sql.Connection conn = DBConnection.getConnection();
             java.sql.PreparedStatement pst = conn.prepareStatement(query);
             java.sql.ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4), rs.getTimestamp(5).toString()
                });
            }
        } catch (java.sql.SQLException e) {
            System.err.println("[Audit] Fetch Error: " + e.getMessage());
        }
        return list;
    }
}
