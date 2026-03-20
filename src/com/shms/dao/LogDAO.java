package com.shms.dao;

import com.shms.db.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * System-wide Audit Logger.
 */
public class LogDAO {

    /**
     * Records a user action in the permanent audit trail.
     */
    public void record(int userId, String action, String module) {
        String sql = "INSERT INTO audit_logs (user_id, action, module) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setString(2, action);
            ps.setString(3, module);
            ps.executeUpdate();
            
        } catch (Exception e) {
            System.err.println("Fatal Logging Error: " + e.getMessage());
        }
    }
}
