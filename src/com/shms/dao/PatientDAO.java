package com.shms.dao;

import com.shms.db.DBConnection;
import com.shms.model.Patient;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    public boolean register(Patient p) {
        String query = "INSERT INTO patients (first_name, last_name, nic_number, dob, gender, contact_number, address) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setString(1, p.getFirstName());
            pst.setString(2, p.getLastName());
            pst.setString(3, p.getNic());
            pst.setDate(4, Date.valueOf(p.getDob()));
            pst.setString(5, p.getGender());
            pst.setString(6, p.getContactNumber());
            pst.setString(7, p.getAddress());

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PatientDAO] Registration Error: " + e.getMessage());
            return false;
        }
    }

    public List<Patient> getAllPatients() {
        return searchPatients("");
    }

    public List<Patient> searchPatients(String keyword) {
        List<Patient> list = new ArrayList<>();
        String query = "SELECT * FROM patients WHERE first_name LIKE ? OR last_name LIKE ? OR nic_number LIKE ? ORDER BY patient_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            String wild = "%" + keyword + "%";
            pst.setString(1, wild);
            pst.setString(2, wild);
            pst.setString(3, wild);
            
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Patient p = mapRow(rs);
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            System.err.println("Search Error: " + e.getMessage());
        }
        return list;
    }

    private Patient mapRow(ResultSet rs) throws SQLException {
        Patient p = new Patient();
        p.setPatientId(rs.getInt("patient_id"));
        p.setFirstName(rs.getString("first_name"));
        p.setLastName(rs.getString("last_name"));
        p.setNic(rs.getString("nic_number"));
        p.setGender(rs.getString("gender"));
        p.setContactNumber(rs.getString("contact_number"));
        
        Timestamp ts = rs.getTimestamp("registered_at");
        if (ts != null) {
            p.setRegisteredAt(ts.toLocalDateTime());
        }
        
        return p;
    }
}
