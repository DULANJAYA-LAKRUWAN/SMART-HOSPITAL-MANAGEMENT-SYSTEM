package com.shms.ui;

import com.shms.service.BillingService;
import com.shms.service.PatientService;
import com.shms.service.DoctorService;
import com.shms.service.MedicineService;
import com.shms.model.Medicine;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class ReportPanel extends BaseModernPanel {
    
    private BillingService billingService;
    private PatientService patientService;
    private DoctorService doctorService;
    private MedicineService medicineService;
    
    private JLabel lblDailyRev, lblTotalPatients, lblTotalDoctors, lblTotalInvoices;
    private JPanel alertContainer;

    public ReportPanel() {
        super("Hospital Analytics & KPI Dashboard");
        this.billingService = new BillingService();
        this.patientService = new PatientService();
        this.doctorService = new DoctorService();
        this.medicineService = new MedicineService();
        initializeUI();
        refreshData();
    }

    private void initializeUI() {
        // --- KPI GRID ---
        JPanel gridPanel = new JPanel(new GridLayout(2, 2, 30, 30));
        gridPanel.setOpaque(false);
        gridPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        lblDailyRev = createKpiWidget(gridPanel, "DAILY REVENUE", "Rs. 0.00", UIConstants.SUCCESS_GREEN);
        lblTotalPatients = createKpiWidget(gridPanel, "TOTAL REGISTERED PATIENTS", "0", UIConstants.ACCENT_BLUE);
        lblTotalDoctors = createKpiWidget(gridPanel, "TOTAL MEDICAL STAFF", "0", UIConstants.SIDE_MENU_DARK);
        lblTotalInvoices = createKpiWidget(gridPanel, "INVOICES GENERATED", "0", UIConstants.DANGER_RED);

        // --- ALERTS SECTION ---
        alertContainer = createCardPanel();
        alertContainer.setLayout(new BoxLayout(alertContainer, BoxLayout.Y_AXIS));
        alertContainer.setBorder(new EmptyBorder(20,30,20,30));
        
        JLabel alertTitle = new JLabel("⚠️ CRITICAL INVENTORY ALERTS");
        alertTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        alertTitle.setForeground(UIConstants.DANGER_RED);
        alertTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        alertContainer.add(alertTitle);
        alertContainer.add(Box.createVerticalStrut(10));

        JPanel wrapper = new JPanel(new BorderLayout(0, 20));
        wrapper.setOpaque(false);
        wrapper.add(gridPanel, BorderLayout.CENTER);
        wrapper.add(alertContainer, BorderLayout.SOUTH);

        add(wrapper, BorderLayout.CENTER);
        
        // --- BOTTOM ACTION ---
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        
        JButton btnRefresh = createPrimaryButton("🔄 Synchronize Real-time Data");
        btnRefresh.setPreferredSize(new Dimension(300, 50));
        btnRefresh.addActionListener(e -> {
            refreshData();
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            com.shms.ui.components.Toast.showSuccess(parentFrame, "System synchronised seamlessly.");
        });
        bottom.add(btnRefresh);
        add(bottom, BorderLayout.SOUTH);
    }

    private JLabel createKpiWidget(JPanel p, String title, String val, Color valColor) {
        JPanel card = createCardPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(30,30,30,30));
        
        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.setForeground(UIConstants.TEXT_SECONDARY);
        card.add(t, BorderLayout.NORTH);
        
        JLabel v = new JLabel(val);
        v.setFont(new Font("Segoe UI", Font.BOLD, 42));
        v.setForeground(valColor);
        card.add(v, BorderLayout.CENTER);
        
        p.add(card);
        return v;
    }

    private void refreshData() {
        lblDailyRev.setText("Rs. " + String.format("%.2f", billingService.getDailyRevenue()));
        lblTotalPatients.setText(String.valueOf(patientService.getAllPatients().size()));
        lblTotalDoctors.setText(String.valueOf(doctorService.getAllDoctors().size()));
        lblTotalInvoices.setText(String.valueOf(billingService.getDailyTransactionCount()));

        // Update Dynamic Alerts (Threshold = 10 units)
        List<Medicine> lowStockItems = medicineService.getLowStockAlerts(10);
        
        // Clear previous alert messages (skip the title and spacer)
        for (int i = alertContainer.getComponentCount() - 1; i > 1; i--) {
            alertContainer.remove(i);
        }

        if (lowStockItems.isEmpty()) {
            JLabel healthy = new JLabel("✅ All inventory levels are optimal. No critical shortages detected.");
            healthy.setForeground(UIConstants.SUCCESS_GREEN);
            alertContainer.add(healthy);
        } else {
            for (Medicine m : lowStockItems) {
                JLabel l = new JLabel("• " + m.getName() + " is critically low (" + m.getStockQuantity() + " units left).");
                l.setForeground(UIConstants.TEXT_PRIMARY);
                alertContainer.add(l);
            }
        }
        alertContainer.revalidate();
        alertContainer.repaint();
    }
}
