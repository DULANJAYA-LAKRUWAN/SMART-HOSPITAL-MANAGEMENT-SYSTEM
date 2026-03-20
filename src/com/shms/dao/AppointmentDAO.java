package com.shms.dao;

import com.shms.db.DBConnection;
import com.shms.model.Appointment;
import java.sql.*;

public class AppointmentDAO {

    public boolean isDoctorAvailable(int doctorId, Date date, Time slot) {
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ? AND time_slot = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setInt(1, doctorId);
            pst.setDate(2, date);
            pst.setTime(3, slot);
            
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0; // Available if count is 0
                }
            }
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO] Availability check failed: " + e.getMessage());
        }
        return false;
    }

    public boolean book(Appointment a) {
        String query = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, time_slot, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setInt(1, a.getPatientId());
            pst.setInt(2, a.getDoctorId());
            pst.setDate(3, Date.valueOf(a.getAppointmentDate()));
            pst.setTime(4, Time.valueOf(a.getTimeSlot()));
            pst.setString(5, "PENDING");

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO] Booking failed: " + e.getMessage());
            return false;
        }
    }
}
