package com.shms.service;

import com.shms.dao.AppointmentDAO;
import com.shms.model.Appointment;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Service Layer for the Appointment Desk (The Business Engine).
 * Implements critical double-booking prevention.
 */
public class AppointmentService {

    private final AppointmentDAO appointmentDAO;

    public AppointmentService() {
        this.appointmentDAO = new AppointmentDAO();
    }

    public List<Appointment> getAllAppointments() {
        return appointmentDAO.getAllAppointments();
    }

    /**
     * Prevents double-booking a specific doctor on the same date/time slot.
     * Core academic requirement achievement.
     */
    public boolean bookAppointment(Appointment appt) {
        // Logic to check if doctor is already booked for this slot
        if (isSlotOccupied(appt.getDoctorId(), appt.getAppointmentDate(), appt.getTimeSlot())) {
            System.err.println("CRITICAL: Conflict detected for doctor id " + appt.getDoctorId() + " on " + appt.getAppointmentDate() + " at " + appt.getTimeSlot());
            return false;
        }
        return appointmentDAO.saveAppointment(appt);
    }

    private boolean isSlotOccupied(int doctorId, LocalDate date, LocalTime slot) {
        return appointmentDAO.isSlotOccupied(doctorId, date, slot);
    }

    public boolean updateStatus(int appointmentId, String status) {
        return appointmentDAO.updateStatus(appointmentId, status);
    }

    public List<Appointment> getAppointmentsByDate(LocalDate date) {
        return appointmentDAO.getAppointmentsByDate(date);
    }
}
