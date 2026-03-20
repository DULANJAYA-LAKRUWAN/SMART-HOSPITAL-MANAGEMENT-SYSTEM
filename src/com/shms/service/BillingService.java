package com.shms.service;

import com.shms.dao.BillingDAO;
import com.shms.model.Bill;
import java.util.List;

/**
 * Service Layer for Billing & Financial reporting.
 * Automates consolidated billing calculations.
 */
public class BillingService {

    private final BillingDAO billingDAO;

    public BillingService() {
        this.billingDAO = new BillingDAO();
    }

    public List<Bill> getAllBills() {
        return billingDAO.getAllBills();
    }

    /**
     * Advanced Logic: Consolidated billing from different departments.
     */
    public boolean generateBill(Bill bill) {
        // Grand total calculation: Consultation + Pharmacy
        double total = (bill.getConsultationTotal() != null ? bill.getConsultationTotal() : 0.0)
                     + (bill.getPharmacyTotal() != null ? bill.getPharmacyTotal() : 0.0);
        bill.setGrandTotal(total);
        return billingDAO.saveBill(bill);
    }


    /**
     * ATOMIC FINANCIAL TRANSACTION: Implements manual commit/rollback protocol.
     * Ensures data integrity across Billing and Audit tables.
     */
    public boolean processOfficialPayment(int billId, com.shms.model.User user) {
        try (java.sql.Connection conn = com.shms.db.DBConnection.getConnection()) {
            conn.setAutoCommit(false); // START ATOMIC TRANSACTION
            
            try {
                // 1. Update Payment Status in Billing Table
                boolean ok = billingDAO.updatePaymentStatusTransactional(conn, billId, "PAID");
                if (!ok) throw new java.sql.SQLException("Billing update failure.");

                // 2. Log Financial Audit Event
                new com.shms.dao.AuditDAO().logTransactional(conn, user.getUserId(), "PAYMENT_COMPLETED", "BILLING_ID_" + billId);

                conn.commit(); // COMMIT ALL CHANGES
                return true;
            } catch (java.sql.SQLException e) {
                conn.rollback(); // UNDO ALL ON FAILURE
                System.err.println("[Billing] Transaction Rollback: " + e.getMessage());
                return false;
            }
        } catch (java.sql.SQLException e) {
            System.err.println("[Billing] Persistence Error: " + e.getMessage());
            return false;
        }
    }

    public List<Bill> getBillsByPatient(int patientId) {
        return billingDAO.getBillsByPatient(patientId);
    }

    public double getDailyRevenue() {
        return billingDAO.getDailyRevenue();
    }

    public int getDailyTransactionCount() {
        return billingDAO.getDailyTransactionCount();
    }

    /**
     * Peripheral Device Simulation: Printer Layout.
     */
    public void simulatePrintBill(Bill bill) {
        System.out.println("--- PRINTING THERMAL RECEIPT ---");
        System.out.println("SHMS - SMART HOSPITAL SYSTEM");
        System.out.println("Patient ID: " + bill.getPatientId());
        System.out.println("Cons. Fee: " + bill.getConsultationTotal());
        System.out.println("Drug Total: " + bill.getPharmacyTotal());
        System.out.println("GRAND TOTAL: " + bill.getGrandTotal());
        System.out.println("Status: " + bill.getPaymentStatus());
        System.out.println("Thank you for choosing SHMS Pro!");
        System.out.println("---------------------------------");
    }
}
