package com.shms.ui;

import com.shms.dao.BillingDAO;
import com.shms.dao.LogDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class BillingPanel extends BaseModernPanel {
    
    private JTextField txtPatientId, txtAppointmentId, txtPharmTotal, txtConsultFee;
    private BillingDAO billingDAO;
    private LogDAO logDAO;

    public BillingPanel() {
        super("Enterprise Billing & Finance");
        this.billingDAO = new BillingDAO();
        this.logDAO = new LogDAO();
        initializeUI();
    }

    private void initializeUI() {
        // Centering the billing card
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        JPanel billingCard = createCardPanel();
        billingCard.setPreferredSize(new Dimension(500, 600));
        billingCard.setLayout(new BoxLayout(billingCard, BoxLayout.Y_AXIS));
        billingCard.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel sectionalLabel = new JLabel("GENERATE PATIENT INVOICE");
        sectionalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sectionalLabel.setForeground(UIConstants.ACCENT_BLUE);
        sectionalLabel.setBorder(new EmptyBorder(0, 0, 30, 0));
        billingCard.add(sectionalLabel);

        txtPatientId = createModernInput(billingCard, "Patient ID Number", "e.g. 1");
        txtAppointmentId = createModernInput(billingCard, "Appointment Reference ID", "e.g. 1");
        txtConsultFee = createModernInput(billingCard, "Doctor Consultation Fee (Rs.)", "e.g. 1500.00");
        txtPharmTotal = createModernInput(billingCard, "Pharmacy Charges (Rs.)", "e.g. 350.50");

        billingCard.add(Box.createVerticalGlue());

        JButton btnBill = createPrimaryButton("Issue Final Invoice");
        btnBill.setBackground(UIConstants.SUCCESS_GREEN);
        btnBill.addActionListener(e -> generateInvoice());
        billingCard.add(btnBill);

        centerWrapper.add(billingCard);
        add(centerWrapper, BorderLayout.CENTER);
    }

    private void generateInvoice() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        try {
            int pId = Integer.parseInt(txtPatientId.getText().trim());
            int aId = Integer.parseInt(txtAppointmentId.getText().trim());
            double cFee = Double.parseDouble(txtConsultFee.getText().trim());
            double pTotal = Double.parseDouble(txtPharmTotal.getText().trim());

            if (billingDAO.createBill(pId, aId, pTotal, cFee)) {
                logDAO.record(1, "GEN_INVOICE: Rs. " + String.format("%.2f", cFee + pTotal), "FINANCE");
                com.shms.ui.components.Toast.showSuccess(parentFrame, "Invoice Generated Successfully!");
                clearForm();
            } else {
                com.shms.ui.components.Toast.showError(parentFrame, "System Error: Failed to generate invoice. Verify Patient/Appointment IDs exist.");
            }
        } catch (NumberFormatException ex) {
            com.shms.ui.components.Toast.showError(parentFrame, "Input Error: Ensure all fields contain valid numbers.");
        }
    }

    private void clearForm() {
        txtPatientId.setText("");
        txtAppointmentId.setText("");
        txtConsultFee.setText("");
        txtPharmTotal.setText("");
    }
}

