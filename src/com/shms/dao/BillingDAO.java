package com.shms.dao;

import com.shms.db.DBConnection;
import java.sql.*;

public class BillingDAO {

    public boolean createBill(int patientId, int appointmentId, double pharmTotal, double consultFee) {
        double grand = pharmTotal + consultFee;
        String query = "INSERT INTO bills (patient_id, appointment_id, consultation_total, pharmacy_total, grand_total, payment_status) VALUES (?, ?, ?, ?, ?, 'PAID')";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setInt(1, patientId);
            pst.setInt(2, appointmentId);
            pst.setDouble(3, consultFee);
            pst.setDouble(4, pharmTotal);
            pst.setDouble(5, grand);

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BillingDAO] Error creating bill: " + e.getMessage());
            return false;
        }
    }

    public double getDailyRevenue() {
        String query = "SELECT SUM(grand_total) FROM bills WHERE DATE(bill_date) = CURDATE()";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (Exception e) { 
            System.err.println("[BillingDAO] Revenue fetch error: " + e.getMessage());
        }
        return 0.0;
    }

    public int getDailyTransactionCount() {
        String query = "SELECT COUNT(*) FROM bills WHERE DATE(bill_date) = CURDATE()";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { 
            System.err.println("[BillingDAO] Transaction count fetch error: " + e.getMessage()); 
        }
        return 0;
    }
}
