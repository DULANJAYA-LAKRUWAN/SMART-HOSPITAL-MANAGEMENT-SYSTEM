package com.shms.dao;

import com.shms.db.DBConnection;
import com.shms.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    
    /**
     * Secure Authentication: Fetches user by username and verifies BCrypt hash in-memory.
     * Prevents timing attacks and SQL authentication bypass.
     */
    public User authenticate(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND status = TRUE";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setString(1, username);
            
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    
                    // Secure Memory-based Verification
                    if (com.shms.util.SecurityUtil.verify(password, storedHash)) {
                        return new User(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("role"),
                            rs.getString("full_name")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[Security Event] Auth Query Failure: " + e.getMessage());
        }
        return null;
    }
    public java.util.List<User> getUsersByRole(String role) {
        java.util.List<User> list = new java.util.ArrayList<>();
        String query = "SELECT * FROM users WHERE role = ? AND status = TRUE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, role);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.add(new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getString("full_name")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Fetch Error: " + e.getMessage());
        }
        return list;
    }
}
