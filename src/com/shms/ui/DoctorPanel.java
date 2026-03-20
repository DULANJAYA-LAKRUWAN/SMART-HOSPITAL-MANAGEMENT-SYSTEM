package com.shms.ui;

import com.shms.service.DoctorService;
import com.shms.service.UserService;
import com.shms.dao.LogDAO;
import com.shms.model.Doctor;
import com.shms.model.User;
import com.shms.ui.components.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DoctorPanel extends BaseModernPanel {
    
    private RoundedComboBox<ComboItem> cmbUser;
    private JTextField txtSpecialization, txtContact, txtFee;
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
        JPanel formCard = createCardPanel();
        formCard.setPreferredSize(new Dimension(380, 0));
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));

        JLabel sectionalLabel = new JLabel("REGISTER NEW DOCTOR");
        sectionalLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        sectionalLabel.setForeground(UIConstants.ACCENT_BLUE);
        sectionalLabel.setBorder(new EmptyBorder(10, 0, 20, 0));
        formCard.add(sectionalLabel);

        // Fetch valid doctor accounts
        List<User> doctorAccounts = new UserService().getUserByRole("DOCTOR");
        ComboItem[] uItems = doctorAccounts.stream()
            .map(u -> new ComboItem(u.getUserId(), u.getFullName() + " (@" + u.getUsername() + ")"))
            .toArray(ComboItem[]::new);
        cmbUser = (RoundedComboBox<ComboItem>) createModernCombo(formCard, "Link to System Account", uItems);

        txtSpecialization = createModernInput(formCard, "Professional Specialization", "e.g. Cardiology");
        txtContact = createModernInput(formCard, "Primary Contact Number", "e.g. 0771234567");
        txtFee = createModernInput(formCard, "Standard Consultation Fee (Rs.)", "e.g. 1500.00");

        formCard.add(Box.createVerticalGlue());

        JButton btnSave = createPrimaryButton("Commit Registration");
        btnSave.addActionListener(e -> registerDoctor());
        formCard.add(btnSave);

        add(formCard, BorderLayout.WEST);

        // --- TABLE PANEL ---
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
            ComboItem selUser = (ComboItem) cmbUser.getSelectedItem();
            if (selUser == null || txtSpecialization.getText().isEmpty()) {
                com.shms.ui.components.Toast.showError(parentFrame, "Validation Error: Please select an account and enter specialization.");
                return;
            }

            Doctor d = new Doctor();
            d.setUserId(selUser.getId());
            d.setSpecialization(txtSpecialization.getText().trim());
            d.setContact(txtContact.getText().trim());
            d.setConsultationFee(Double.parseDouble(txtFee.getText().trim()));

            if (doctorService.registerDoctor(d)) {
                logDAO.record(1, "REG_DOCTOR: Linked to UID-" + d.getUserId(), "DOCTOR_MGMT");
                com.shms.ui.components.Toast.showSuccess(parentFrame, "Doctor Profile Created Successfully!");
                loadData();
                clearForm();
            } else {
                com.shms.ui.components.Toast.showError(parentFrame, "Registration failed. Check if user already has a profile.");
            }
        } catch (NumberFormatException ex) {
            com.shms.ui.components.Toast.showError(parentFrame, "Input Error: Invalid Fee format.");
        }
    }

    private void clearForm() {
        txtSpecialization.setText("");
        txtContact.setText("");
        txtFee.setText("");
    }
}
