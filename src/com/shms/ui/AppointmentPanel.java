package com.shms.ui;

import com.shms.service.AppointmentService;
import com.shms.dao.LogDAO;
import com.shms.model.Appointment;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentPanel extends BaseModernPanel {
    
    private JTextField txtPatientId, txtDoctorId, txtDate, txtTime;
    private AppointmentService appointmentService;
    private LogDAO logDAO;

    public AppointmentPanel() {
        super("Appointment Scheduler");
        this.appointmentService = new AppointmentService();
        this.logDAO = new LogDAO();
        initializeUI();
    }

    private void initializeUI() {
        // Centering the booking card
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        JPanel bookingCard = createCardPanel();
        bookingCard.setPreferredSize(new Dimension(500, 600));
        bookingCard.setLayout(new BoxLayout(bookingCard, BoxLayout.Y_AXIS));
        bookingCard.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel sectionalLabel = new JLabel("BOOK NEW SESSION");
        sectionalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sectionalLabel.setForeground(UIConstants.ACCENT_BLUE);
        sectionalLabel.setBorder(new EmptyBorder(0, 0, 30, 0));
        bookingCard.add(sectionalLabel);

        txtPatientId = createModernInput(bookingCard, "Target Patient ID", "e.g. 1");
        txtDoctorId = createModernInput(bookingCard, "Assigned Doctor ID", "e.g. 1");
        txtDate = createModernInput(bookingCard, "Preferred Date (YYYY-MM-DD)", "2026-11-20");
        txtTime = createModernInput(bookingCard, "Time Slot (HH:MM:SS)", "09:30:00");

        bookingCard.add(Box.createVerticalGlue());

        JButton btnBook = createPrimaryButton("Verify & Confirm Slot");
        btnBook.addActionListener(e -> handleBooking());
        bookingCard.add(btnBook);

        centerWrapper.add(bookingCard);
        add(centerWrapper, BorderLayout.CENTER);
    }

    private void handleBooking() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        try {
            if (txtPatientId.getText().isEmpty() || txtDoctorId.getText().isEmpty()) {
                com.shms.ui.components.Toast.showError(parentFrame, "Validation Error: All fields are required.");
                return;
            }

            int pId = Integer.parseInt(txtPatientId.getText().trim());
            int dId = Integer.parseInt(txtDoctorId.getText().trim());
            LocalDate dt = LocalDate.parse(txtDate.getText().trim());
            LocalTime tm = LocalTime.parse(txtTime.getText().trim());

            Appointment a = new Appointment();
            a.setPatientId(pId);
            a.setDoctorId(dId);
            a.setAppointmentDate(dt);
            a.setTimeSlot(tm);

            if (appointmentService.bookAppointment(a)) {
                logDAO.record(1, "BOOK_APPT: Patient-" + pId + " w/ Doctor-" + dId, "APPT_MGMT");
                com.shms.ui.components.Toast.showSuccess(parentFrame, "Success! Appointment secured.");
                clearForm();
            } else {
                com.shms.ui.components.Toast.showError(parentFrame, "Booking Failed: Conflict detected or IDs invalid.");
            }
        } catch (Exception ex) {
            com.shms.ui.components.Toast.showError(parentFrame, "Input Error: Invalid IDs or Date/Time formats (YYYY-MM-DD, HH:MM:SS).");
        }
    }

    private void clearForm() {
        txtPatientId.setText("");
        txtDoctorId.setText("");
        txtDate.setText("");
        txtTime.setText("");
    }
}
