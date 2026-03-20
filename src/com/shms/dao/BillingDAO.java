package com.shms.dao;

import com.shms.db.DBConnection;
import java.sql.*;

import com.shms.model.Bill;
import java.util.ArrayList;
import java.util.List;

public class BillingDAO {

    public List<Bill> getAllBills() {
        List<Bill> list = new ArrayList<>();
        String query = "SELECT * FROM bills ORDER BY bill_date DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {}
        return list;
    }

    public boolean saveBill(Bill b) {
        String query = "INSERT INTO bills (patient_id, appointment_id, consultation_total, pharmacy_total, grand_total, payment_status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, b.getPatientId());
            pst.setInt(2, b.getAppointmentId());
            pst.setDouble(3, b.getConsultationTotal());
            pst.setDouble(4, b.getPharmacyTotal());
            pst.setDouble(5, b.getGrandTotal());
            pst.setString(6, b.getPaymentStatus() != null ? b.getPaymentStatus() : "UNPAID");
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean updatePaymentStatus(int id, String status) {
        try (Connection conn = DBConnection.getConnection()) {
            return updatePaymentStatusTransactional(conn, id, status);
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Transactional Update: Participates in an existing connection/transaction.
     */
    public boolean updatePaymentStatusTransactional(Connection conn, int id, String status) throws SQLException {
        String query = "UPDATE bills SET payment_status = ? WHERE bill_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, status);
            pst.setInt(2, id);
            return pst.executeUpdate() > 0;
        }
    }

    public List<Bill> getBillsByPatient(int pId) {
        List<Bill> list = new ArrayList<>();
        String query = "SELECT * FROM bills WHERE patient_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, pId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {}
        return list;
    }

    public double getDailyRevenue() {
        String query = "SELECT SUM(grand_total) FROM bills WHERE DATE(bill_date) = CURDATE() AND payment_status = 'PAID'";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (Exception e) {}
        return 0.0;
    }

    private Bill mapRow(ResultSet rs) throws SQLException {
        Bill b = new Bill();
        b.setBillId(rs.getInt("bill_id"));
        b.setPatientId(rs.getInt("patient_id"));
        b.setAppointmentId(rs.getInt("appointment_id"));
        b.setConsultationTotal(rs.getDouble("consultation_total"));
        b.setPharmacyTotal(rs.getDouble("pharmacy_total"));
        b.setGrandTotal(rs.getDouble("grand_total"));
        b.setPaymentStatus(rs.getString("payment_status"));
        if (rs.getTimestamp("bill_date") != null) {
            b.setBillDate(rs.getTimestamp("bill_date").toLocalDateTime());
        }
        return b;
    }

    public int getDailyTransactionCount() {
        String query = "SELECT COUNT(*) FROM bills WHERE DATE(bill_date) = CURDATE()";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {}
        return 0;
    }
}
