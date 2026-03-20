package com.shms.dao;

import com.shms.db.DBConnection;
import com.shms.model.Appointment;
import java.sql.*;

public class AppointmentDAO {

    public java.util.List<Appointment> getAllAppointments() {
        java.util.List<Appointment> list = new java.util.ArrayList<>();
        String query = "SELECT * FROM appointments ORDER BY appointment_date DESC, time_slot DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[AppointmentDAO] Fetch Error: " + e.getMessage());
        }
        return list;
    }

    public boolean isSlotOccupied(int doctorId, java.time.LocalDate date, java.time.LocalTime slot) {
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ? AND time_slot = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, doctorId);
            pst.setDate(2, Date.valueOf(date));
            pst.setTime(3, Time.valueOf(slot));
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {}
        return false;
    }

    public boolean saveAppointment(Appointment a) {
        String query = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, time_slot, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, a.getPatientId());
            pst.setInt(2, a.getDoctorId());
            pst.setDate(3, Date.valueOf(a.getAppointmentDate()));
            pst.setTime(4, Time.valueOf(a.getTimeSlot()));
            pst.setString(5, a.getStatus() != null ? a.getStatus() : "PENDING");
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean updateStatus(int id, String status) {
        String query = "UPDATE appointments SET status = ? WHERE appointment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, status);
            pst.setInt(2, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public java.util.List<Appointment> getAppointmentsByDate(java.time.LocalDate date) {
        java.util.List<Appointment> list = new java.util.ArrayList<>();
        String query = "SELECT * FROM appointments WHERE appointment_date = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setDate(1, Date.valueOf(date));
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {}
        return list;
    }

    public int getDailyAppointmentCount() {
        String query = "SELECT COUNT(*) FROM appointments WHERE appointment_date = CURDATE()";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {}
        return 0;
    }

    public java.util.List<Object[]> getRecentActivityFeed() {
        java.util.List<Object[]> list = new java.util.ArrayList<>();
        String query = "SELECT a.appointment_id, p.first_name, d.specialization, a.time_slot, a.status " +
                       "FROM appointments a " +
                       "JOIN patients p ON a.patient_id = p.patient_id " +
                       "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                       "ORDER BY a.appointment_id DESC LIMIT 10";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getInt(1), rs.getString(2), rs.getString(3), rs.getTime(4).toString(), rs.getString(5)
                });
            }
        } catch (SQLException e) {}
        return list;
    }

    private Appointment mapRow(ResultSet rs) throws SQLException {
        Appointment a = new Appointment();
        a.setAppointmentId(rs.getInt("appointment_id"));
        a.setPatientId(rs.getInt("patient_id"));
        a.setDoctorId(rs.getInt("doctor_id"));
        a.setAppointmentDate(rs.getDate("appointment_date").toLocalDate());
        a.setTimeSlot(rs.getTime("time_slot").toLocalTime());
        a.setStatus(rs.getString("status"));
        return a;
    }
}
