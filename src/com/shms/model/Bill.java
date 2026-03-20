package com.shms.model;

import java.time.LocalDateTime;

public class Bill {
    private int billId;
    private int patientId;
    private int appointmentId;
    private Double consultationTotal;
    private Double pharmacyTotal;
    private Double grandTotal;
    private String paymentStatus;
    private LocalDateTime billDate;

    public Bill() {}

    // Getters and Setters
    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }
    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }
    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }
    public Double getConsultationTotal() { return consultationTotal; }
    public void setConsultationTotal(Double consultationTotal) { this.consultationTotal = consultationTotal; }
    public Double getPharmacyTotal() { return pharmacyTotal; }
    public void setPharmacyTotal(Double pharmacyTotal) { this.pharmacyTotal = pharmacyTotal; }
    public Double getGrandTotal() { return grandTotal; }
    public void setGrandTotal(Double grandTotal) { this.grandTotal = grandTotal; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public LocalDateTime getBillDate() { return billDate; }
    public void setBillDate(LocalDateTime billDate) { this.billDate = billDate; }
}
