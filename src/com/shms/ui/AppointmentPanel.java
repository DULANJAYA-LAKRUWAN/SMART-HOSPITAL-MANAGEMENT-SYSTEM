package com.shms.ui;

import com.shms.service.AppointmentService;
import com.shms.dao.LogDAO;
import com.shms.dao.PatientDAO;
import com.shms.dao.DoctorDAO;
import com.shms.model.Appointment;
import com.shms.model.Patient;
import com.shms.model.Doctor;
import com.shms.ui.components.ComboItem;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AppointmentPanel extends BaseModernPanel {
    
    private JComboBox<ComboItem> cmbPatient, cmbDoctor;
    private JTextField txtDate;
    private JSpinner spnTime;
    private AppointmentService appointmentService;
    private LogDAO logDAO;

    public AppointmentPanel() {
        super("Appointment Scheduler");
        this.appointmentService = new AppointmentService();
        this.logDAO = new LogDAO();
        initializeUI();
    }

    private void initializeUI() {
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        JPanel bookingCard = createCardPanel();
        bookingCard.setPreferredSize(new Dimension(550, 700));
        bookingCard.setLayout(new BoxLayout(bookingCard, BoxLayout.Y_AXIS));
        bookingCard.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel sectionalLabel = new JLabel("BOOK NEW SESSION");
        sectionalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sectionalLabel.setForeground(UIConstants.ACCENT_BLUE);
        sectionalLabel.setBorder(new EmptyBorder(0, 0, 30, 0));
        bookingCard.add(sectionalLabel);

        // Selection Combos
        List<Patient> patients = new PatientDAO().getAllPatients();
        ComboItem[] pItems = patients.stream().map(p -> new ComboItem(p.getPatientId(), p.getFirstName() + " " + p.getLastName())).toArray(ComboItem[]::new);
        cmbPatient = createModernCombo(bookingCard, "Select Patient", pItems);

        List<Doctor> doctors = new DoctorDAO().getAllDoctors();
        ComboItem[] dItems = doctors.stream().map(d -> new ComboItem(d.getDoctorId(), d.getFullName())).toArray(ComboItem[]::new);
        cmbDoctor = createModernCombo(bookingCard, "Select Doctor", dItems);

        // Date with Calendar Icon
        JPanel datePanel = new JPanel(new BorderLayout(10, 0));
        datePanel.setOpaque(false);
        txtDate = createModernInput(bookingCard, "Preferred Date", LocalDate.now().toString());
        
        JButton btnCal = new JButton("📅 Pick Date");
        btnCal.addActionListener(e -> {
            LocalDate picked = new com.shms.ui.components.ModernDatePicker((Frame)SwingUtilities.getWindowAncestor(this)).pick();
            if (picked != null) txtDate.setText(picked.toString());
        });
        bookingCard.add(btnCal);
        bookingCard.add(Box.createVerticalStrut(15));

        // Time with Digital Watch feel (Spinner)
        spnTime = createModernTimeSelection(bookingCard, "Time Slot (Digital Scroll)");

        bookingCard.add(Box.createVerticalGlue());

        JButton btnBook = createPrimaryButton("Confirm Appointment Slot");
        btnBook.addActionListener(e -> handleBooking());
        bookingCard.add(btnBook);

        centerWrapper.add(bookingCard);
        add(centerWrapper, BorderLayout.CENTER);
    }

    private void handleBooking() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        try {
            ComboItem selPatient = (ComboItem) cmbPatient.getSelectedItem();
            ComboItem selDoctor = (ComboItem) cmbDoctor.getSelectedItem();
            
            if (selPatient == null || selDoctor == null) return;

            LocalDate dt = LocalDate.parse(txtDate.getText().trim());
            
            // Extract Time from Spinner
            java.util.Date sTime = (java.util.Date) spnTime.getValue();
            LocalTime tm = sTime.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime();

            Appointment a = new Appointment();
            a.setPatientId(selPatient.getId());
            a.setDoctorId(selDoctor.getId());
            a.setAppointmentDate(dt);
            a.setTimeSlot(tm);

            if (appointmentService.bookAppointment(a)) {
                logDAO.record(1, "BOOK_APPT: " + dt + " at " + tm, "APPT_MGMT");
                com.shms.ui.components.Toast.showSuccess(parentFrame, "Success! Appointment booked.");
            } else {
                com.shms.ui.components.Toast.showError(parentFrame, "Booking Conflict.");
            }
        } catch (Exception ex) {
            com.shms.ui.components.Toast.showError(parentFrame, "Input Format Error.");
        }
    }
}
