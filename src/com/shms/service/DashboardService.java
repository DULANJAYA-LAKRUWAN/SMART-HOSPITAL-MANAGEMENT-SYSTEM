package com.shms.service;

import com.shms.dao.PatientDAO;
import com.shms.dao.DoctorDAO;
import com.shms.dao.AppointmentDAO;
import java.util.List;

/**
 * Unified Dashboard Service to consolidate stats & data fetching for the UI.
 */
public class DashboardService {
    
    private final PatientDAO patientDAO = new PatientDAO();
    private final DoctorDAO doctorDAO = new DoctorDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final BillingService billingService = new BillingService();

    /**
     * Data Transfer Object (DTO) for UI thread safety.
     */
    public static class DashboardStats {
        public int pCount, dCount, aCount;
        public double totalRevenue;
        public List<Object[]> feed;
        public java.util.Map<String, Double> trendData;
    }

    public DashboardStats getLiveStats() {
        DashboardStats stats = new DashboardStats();
        try {
            stats.pCount = patientDAO.getAllPatients().size();
            stats.dCount = doctorDAO.getAllDoctors().size();
            stats.totalRevenue = billingService.getDailyRevenue();
            stats.aCount = appointmentDAO.getDailyAppointmentCount();
            stats.feed = appointmentDAO.getRecentActivityFeed();
            stats.trendData = getRevenueTrendData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stats;
    }

    /**
     * Analytical Engine: Fetches revenue trends for the last 7 active days.
     * Required for JFreeChart KPI Dashboard.
     */
    public java.util.Map<String, Double> getRevenueTrendData() {
        java.util.Map<String, Double> data = new java.util.LinkedHashMap<>();
        String sql = "SELECT DATE(bill_date) as day, SUM(grand_total) " +
                     "FROM bills WHERE bill_date >= CURDATE() - INTERVAL 7 DAY " +
                     "AND payment_status = 'PAID' " +
                     "GROUP BY day ORDER BY day ASC";
        try (java.sql.Connection conn = com.shms.db.DBConnection.getConnection();
             java.sql.PreparedStatement pst = conn.prepareStatement(sql);
             java.sql.ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                data.put(rs.getString(1), rs.getDouble(2));
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return data;
    }
}
