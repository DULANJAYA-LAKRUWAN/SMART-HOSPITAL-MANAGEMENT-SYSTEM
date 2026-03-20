package com.shms.ui;

import com.shms.service.DoctorService;
import com.shms.dao.LogDAO;
import com.shms.model.Doctor;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DoctorPanel extends BaseModernPanel {
    
    private JTextField txtSpecialization, txtContact, txtFee, txtUserId;
    private JTable tblDoctors;
    private DefaultTableModel model;
    private DoctorService doctorService;
    private LogDAO logDAO;

    public DoctorPanel() {
        super("Medical Staff Directory");
        this.doctorService = new DoctorService();
        this.logDAO = new LogDAO();
        initializeUI();
        loadData();
    }

    private void initializeUI() {
        // --- FORM PANEL (Left Card) ---
        JPanel formCard = createCardPanel();
        formCard.setPreferredSize(new Dimension(380, 0));
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));

        JLabel sectionalLabel = new JLabel("REGISTER NEW DOCTOR");
        sectionalLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        sectionalLabel.setForeground(UIConstants.ACCENT_BLUE);
        sectionalLabel.setBorder(new EmptyBorder(10, 0, 20, 0));
        formCard.add(sectionalLabel);

        txtUserId = createModernInput(formCard, "System User ID (Login Account)", "e.g. 5");
        txtSpecialization = createModernInput(formCard, "Professional Specialization", "e.g. Cardiology");
        txtContact = createModernInput(formCard, "Primary Contact Number", "e.g. 0771234567");
        txtFee = createModernInput(formCard, "Standard Consultation Fee (Rs.)", "e.g. 1500.00");

        formCard.add(Box.createVerticalGlue());

        JButton btnSave = createPrimaryButton("Commit Registration");
        btnSave.addActionListener(e -> registerDoctor());
        formCard.add(btnSave);

        add(formCard, BorderLayout.WEST);

        // --- TABLE PANEL (Center Card) ---
        JPanel tableCard = createCardPanel();
        tableCard.setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{"ID", "Full Name", "Specialization", "Contact", "Fee (Rs.)"}, 0);
        tblDoctors = new JTable(model);
        styleTable(tblDoctors);

        JScrollPane scroll = new JScrollPane(tblDoctors);
        styleScrollPane(scroll);
        tableCard.add(scroll, BorderLayout.CENTER);

        add(tableCard, BorderLayout.CENTER);
    }

    private void loadData() {
        model.setRowCount(0);
        List<Doctor> doctors = doctorService.getAllDoctors();
        for (Doctor d : doctors) {
            model.addRow(new Object[]{
                d.getDoctorId(), d.getFullName(), d.getSpecialization(), d.getContact(), d.getConsultationFee()
            });
        }
    }

    private void registerDoctor() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        try {
            if (txtUserId.getText().isEmpty() || txtSpecialization.getText().isEmpty()) {
                com.shms.ui.components.Toast.showError(parentFrame, "Validation Error: User ID and Specialization are required.");
                return;
            }

            Doctor d = new Doctor();
            d.setUserId(Integer.parseInt(txtUserId.getText().trim()));
            d.setSpecialization(txtSpecialization.getText().trim());
            d.setContact(txtContact.getText().trim());
            d.setConsultationFee(Double.parseDouble(txtFee.getText().trim()));

            if (doctorService.registerDoctor(d)) {
                logDAO.record(1, "REG_DOCTOR: ID-" + d.getUserId(), "DOCTOR_MGMT");
                com.shms.ui.components.Toast.showSuccess(parentFrame, "Doctor Registered Successfully!");
                loadData();
                clearForm();
            } else {
                com.shms.ui.components.Toast.showError(parentFrame, "System Error: Registration failed. Verify User ID exists and is unique.");
            }
        } catch (NumberFormatException ex) {
            com.shms.ui.components.Toast.showError(parentFrame, "Input Error: Invalid ID or Fee format.");
        }
    }

    private void clearForm() {
        txtUserId.setText("");
        txtSpecialization.setText("");
        txtContact.setText("");
        txtFee.setText("");
    }
}
