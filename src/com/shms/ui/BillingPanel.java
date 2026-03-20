package com.shms.ui;

import com.shms.service.BillingService;
import com.shms.dao.LogDAO;
import com.shms.dao.PatientDAO;
import com.shms.dao.AppointmentDAO;
import com.shms.model.Bill;
import com.shms.model.Patient;
import com.shms.model.Appointment;
import com.shms.ui.components.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BillingPanel extends BaseModernPanel {
    
    private RoundedComboBox<ComboItem> cmbPatient, cmbAppt;
    private JTextField txtPharmTotal, txtConsultFee;
    private BillingService billingService;
    private LogDAO logDAO;

    public BillingPanel() {
        super("Enterprise Billing & Finance");
        this.billingService = new BillingService();
        this.logDAO = new LogDAO();
        initializeUI();
    }

    private void initializeUI() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // --- TAB 1: GENERATE INVOICE ---
        JPanel generateTab = new JPanel(new GridBagLayout());
        generateTab.setOpaque(false);

        JPanel billingCard = createCardPanel();
        billingCard.setPreferredSize(new Dimension(550, 700));
        billingCard.setLayout(new BoxLayout(billingCard, BoxLayout.Y_AXIS));
        billingCard.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel sectionalLabel = new JLabel("GENERATE PATIENT INVOICE");
        sectionalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sectionalLabel.setForeground(UIConstants.ACCENT_BLUE);
        sectionalLabel.setBorder(new EmptyBorder(0, 0, 30, 0));
        billingCard.add(sectionalLabel);

        List<Patient> patients = new PatientDAO().getAllPatients();
        ComboItem[] pItems = patients.stream()
            .map(p -> new ComboItem(p.getPatientId(), p.getFirstName() + " " + p.getLastName()))
            .toArray(ComboItem[]::new);
        cmbPatient = (RoundedComboBox<ComboItem>) createModernCombo(billingCard, "Select Payer (Patient)", pItems);

        List<Appointment> appts = new AppointmentDAO().getAllAppointments();
        ComboItem[] aItems = appts.stream()
            .map(a -> new ComboItem(a.getAppointmentId(), "Session: " + a.getAppointmentDate() + " [ID: " + a.getAppointmentId() + "]"))
            .toArray(ComboItem[]::new);
        cmbAppt = (RoundedComboBox<ComboItem>) createModernCombo(billingCard, "Reference Appointment", aItems);

        txtConsultFee = createModernInput(billingCard, "Doctor Consultation Fee (Rs.)", "1500.00");
        txtPharmTotal = createModernInput(billingCard, "Pharmacy Charges (Rs.)", "0.00");

        billingCard.add(Box.createVerticalGlue());

        JButton btnBill = createPrimaryButton("Issue Final Invoice");
        btnBill.setBackground(UIConstants.SUCCESS_GREEN);
        btnBill.addActionListener(e -> generateInvoice());
        billingCard.add(btnBill);

        generateTab.add(billingCard);

        // --- TAB 2: BILLING HISTORY ---
        JPanel historyTab = new JPanel(new BorderLayout(0, 25));
        historyTab.setOpaque(false);
        historyTab.setBorder(new EmptyBorder(25, 0, 0, 0));

        DefaultTableModel histModel = new DefaultTableModel(new String[]{"Bill ID", "Patient ID", "Appt ID", "Consultation", "Pharmacy", "Grand Total", "Status", "Date"}, 0);
        JTable tblHistory = new JTable(histModel);
        styleTable(tblHistory);
        
        JScrollPane scroll = new JScrollPane(tblHistory);
        styleScrollPane(scroll);
        historyTab.add(scroll, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setOpaque(false);
        JButton btnRefresh = createPrimaryButton("↻ Load Transaction History");
        btnRefresh.addActionListener(e -> {
            histModel.setRowCount(0);
            List<Bill> bills = billingService.getAllBills();
            for (Bill b : bills) {
                histModel.addRow(new Object[]{
                    b.getBillId(), b.getPatientId(), b.getAppointmentId(), 
                    b.getConsultationTotal(), b.getPharmacyTotal(), b.getGrandTotal(),
                    b.getPaymentStatus(), b.getBillDate()
                });
            }
        });
        actionPanel.add(btnRefresh);
        historyTab.add(actionPanel, BorderLayout.NORTH);

        tabs.addTab("💳 Invoice Generation", generateTab);
        tabs.addTab("📜 Billing History & Archives", historyTab);

        add(tabs, BorderLayout.CENTER);
        
        // Load initial history
        btnRefresh.doClick();
    }

    private void generateInvoice() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        try {
            ComboItem selPatient = (ComboItem) cmbPatient.getSelectedItem();
            ComboItem selAppt = (ComboItem) cmbAppt.getSelectedItem();

            if (selPatient == null || selAppt == null) return;

            Bill b = new Bill();
            b.setPatientId(selPatient.getId());
            b.setAppointmentId(selAppt.getId());
            b.setConsultationTotal(Double.parseDouble(txtConsultFee.getText().trim()));
            b.setPharmacyTotal(Double.parseDouble(txtPharmTotal.getText().trim()));
            b.setPaymentStatus("PAID");

            if (billingService.generateBill(b)) {
                logDAO.record(1, "GEN_INVOICE: Patient " + selPatient.toString(), "FINANCE");
                com.shms.ui.components.Toast.showSuccess(parentFrame, "Invoice Issued!");
                billingService.simulatePrintBill(b);
            } else {
                com.shms.ui.components.Toast.showError(parentFrame, "System Error: Check IDs.");
            }
        } catch (Exception ex) {
            com.shms.ui.components.Toast.showError(parentFrame, "Invalid Price Format.");
        }
    }
}

