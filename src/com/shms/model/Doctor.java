package com.shms.model;

public class Doctor {
    private int doctorId;
    private int userId; // References the account in users table
    private String specialization;
    private String contact;
    private double consultationFee;
    private String fullName; // Temporary field for display logic

    public Doctor() {}

    // Getters and Setters
    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public double getConsultationFee() { return consultationFee; }
    public void setConsultationFee(double consultationFee) { this.consultationFee = consultationFee; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
}
