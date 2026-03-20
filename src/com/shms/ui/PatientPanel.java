package com.shms.ui;

import com.shms.dao.PatientDAO;
import com.shms.dao.LogDAO;
import com.shms.model.Patient;
import com.shms.ui.components.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class PatientPanel extends BaseModernPanel {
    
    private RoundedTextField txtFirstName, txtLastName, txtNIC, txtContact, txtDOB, txtSearch;
    private JComboBox<String> cmbGender;
    private JTable tblPatients;
    private DefaultTableModel model;
    private PatientDAO patientDAO;
    private LogDAO logDAO;

    public PatientPanel() {
        super("Enterprise Patient Directory");
        this.patientDAO = new PatientDAO();
        this.logDAO = new LogDAO();
        initializeUI();
        loadData("");
    }

    private void initializeUI() {
        // --- SEARCH HEADER (North) ---
        JPanel searchHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchHeader.setOpaque(false);
        searchHeader.setBorder(new EmptyBorder(0, 0, 15, 0));

        txtSearch = new RoundedTextField(25);
        txtSearch.setPreferredSize(new Dimension(300, 45));
        txtSearch.setPlaceholder("Search by Name, NIC or Phone...");
        
        JButton btnSearch = createPrimaryButton("🔍 Perform Lookup");
        btnSearch.addActionListener(e -> loadData(txtSearch.getText().trim()));
        
        searchHeader.add(new JLabel("Search Index: "));
        searchHeader.add(txtSearch);
        searchHeader.add(btnSearch);
        add(searchHeader, BorderLayout.NORTH);

        // --- FORM CARD (WEST) ---
        JPanel formCard = createCardPanel();
        formCard.setPreferredSize(new Dimension(400, 0));
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel titleLabel = new JLabel("ONBOARD NEW PATIENT");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setForeground(UIConstants.ACCENT_BLUE);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        formCard.add(titleLabel);

        txtFirstName = (RoundedTextField) createModernInput(formCard, "Given Name", "e.g. John");
        txtLastName = (RoundedTextField) createModernInput(formCard, "Surname", "e.g. Doe");
        txtNIC = (RoundedTextField) createModernInput(formCard, "Government Identity (NIC)", "e.g. 199012345678");
        txtDOB = (RoundedTextField) createModernInput(formCard, "Birth Date (YYYY-MM-DD)", "e.g. 1990-05-15");
        
        formCard.add(new JLabel("Patient Gender"));
        formCard.add(Box.createVerticalStrut(5));
        cmbGender = new JComboBox<>(new String[]{"MALE", "FEMALE", "OTHER"});
        cmbGender.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        cmbGender.setFont(UIConstants.FONT_NORMAL);
        formCard.add(cmbGender);
        formCard.add(Box.createVerticalStrut(20));

        txtContact = (RoundedTextField) createModernInput(formCard, "Primary Phone Contact", "e.g. 0712345678");

        formCard.add(Box.createVerticalGlue());

        JButton btnSave = createPrimaryButton("Confirm Registration");
        btnSave.setBackground(UIConstants.SUCCESS_GREEN);
        btnSave.setPreferredSize(new Dimension(0, 55));
        btnSave.addActionListener(e -> registerPatient());
        formCard.add(btnSave);

        add(formCard, BorderLayout.WEST);

        // --- TABLE CARD (CENTER) ---
        JPanel tableCard = createCardPanel();
        tableCard.setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{"PID", "Legal Name", "NIC Number", "Gender", "Date Added"}, 0);
        tblPatients = new JTable(model);
        styleTable(tblPatients);

        JScrollPane scroll = new JScrollPane(tblPatients);
        styleScrollPane(scroll);
        tableCard.add(scroll, BorderLayout.CENTER);

        add(tableCard, BorderLayout.CENTER);
    }

    private void loadData(String query) {
        model.setRowCount(0);
        List<Patient> patients = patientDAO.searchPatients(query);
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Patient p : patients) {
            String dateAdded = p.getRegisteredAt() != null ? p.getRegisteredAt().format(formatter) : "Unknown";
            model.addRow(new Object[]{
                p.getPatientId(), 
                p.getFirstName() + " " + p.getLastName(), 
                p.getNic(), 
                p.getGender(), 
                dateAdded
            });
        }
    }

    private void registerPatient() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        try {
            if (txtFirstName.getText().isEmpty() || txtNIC.getText().isEmpty()) {
                Toast.showError(parentFrame, "Missing required fields: Name and NIC.");
                return;
            }

            Patient p = new Patient();
            p.setFirstName(txtFirstName.getText().trim());
            p.setLastName(txtLastName.getText().trim());
            p.setNic(txtNIC.getText().trim());
            p.setDob(LocalDate.parse(txtDOB.getText().trim()));
            p.setGender(cmbGender.getSelectedItem().toString());
            p.setContactNumber(txtContact.getText().trim());
            p.setAddress("N/A");

            if (patientDAO.register(p)) {
                Toast.showSuccess(parentFrame, "Patient Registered Successfully!");
                logDAO.record(1, "REG_PATIENT: " + p.getNic(), "PATIENT_MGMT");
                loadData("");
                clearForm();
            } else {
                Toast.showError(parentFrame, "Registration Failed: NIC might already exist.");
            }
        } catch (Exception ex) {
            Toast.showError(parentFrame, "Invalid Data: Ensure Date follows YYYY-MM-DD and inputs are correct.");
        }
    }

    private void clearForm() {
        txtFirstName.setText("");
        txtLastName.setText("");
        txtNIC.setText("");
        txtDOB.setText("");
        txtContact.setText("");
        cmbGender.setSelectedIndex(0);
    }
}
