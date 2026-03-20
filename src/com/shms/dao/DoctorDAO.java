package com.shms.dao;

import com.shms.db.DBConnection;
import com.shms.model.Doctor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {

    public List<Doctor> getAllDoctors() {
        List<Doctor> list = new ArrayList<>();
        // Join with users table to get the full_name
        String query = "SELECT d.*, u.full_name FROM doctors d JOIN users u ON d.user_id = u.user_id";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
            
            while (rs.next()) {
                Doctor d = new Doctor();
                d.setDoctorId(rs.getInt("doctor_id"));
                d.setSpecialization(rs.getString("specialization"));
                d.setConsultationFee(rs.getDouble("consultation_fee"));
                d.setFullName(rs.getString("full_name"));
                d.setContact(rs.getString("contact"));
                list.add(d);
            }
        } catch (SQLException e) {
            System.err.println("[DoctorDAO] Error fetching doctors: " + e.getMessage());
        }
        return list;
    }

    public boolean saveDoctor(Doctor d) {
        String query = "INSERT INTO doctors (user_id, specialization, contact, consultation_fee) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setInt(1, d.getUserId());
            pst.setString(2, d.getSpecialization());
            pst.setString(3, d.getContact());
            pst.setDouble(4, d.getConsultationFee());

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DoctorDAO] Save Error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateDoctor(Doctor d) {
        String query = "UPDATE doctors SET specialization=?, contact=?, consultation_fee=? WHERE doctor_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setString(1, d.getSpecialization());
            pst.setString(2, d.getContact());
            pst.setDouble(3, d.getConsultationFee());
            pst.setInt(4, d.getDoctorId());

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DoctorDAO] Update Error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteDoctor(int id) {
        String query = "DELETE FROM doctors WHERE doctor_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DoctorDAO] Delete Error: " + e.getMessage());
            return false;
        }
    }

    public Doctor getDoctorById(int id) {
        String query = "SELECT d.*, u.full_name FROM doctors d JOIN users u ON d.user_id = u.user_id WHERE d.doctor_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Doctor d = new Doctor();
                    d.setDoctorId(rs.getInt("doctor_id"));
                    d.setUserId(rs.getInt("user_id"));
                    d.setSpecialization(rs.getString("specialization"));
                    d.setConsultationFee(rs.getDouble("consultation_fee"));
                    d.setFullName(rs.getString("full_name"));
                    d.setContact(rs.getString("contact"));
                    return d;
                }
            }
        } catch (SQLException e) {
            System.err.println("[DoctorDAO] Error fetching doctor: " + e.getMessage());
        }
        return null;
    }
}
