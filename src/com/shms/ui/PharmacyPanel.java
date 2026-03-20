package com.shms.ui;

import com.shms.dao.MedicineDAO;
import com.shms.dao.LogDAO;
import com.shms.model.Medicine;
import com.shms.ui.components.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PharmacyPanel extends BaseModernPanel {
    
    private RoundedTextField txtBarcode, txtQty;
    private JTable tblCart;
    private DefaultTableModel model;
    private MedicineDAO medicineDAO;
    private LogDAO logDAO;
    private JLabel lblTotal;
    private double grandTotal = 0.0;

    public PharmacyPanel() {
        super("Pharmacy POS & Inventory Dispatch");
        this.medicineDAO = new MedicineDAO();
        this.logDAO = new LogDAO();
        initializeUI();
    }

    private void initializeUI() {
        // --- TOP SCANNER BAR ---
        JPanel topBar = createCardPanel();
        topBar.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
        
        JLabel lblScan = new JLabel("READY TO SCAN BARCODE");
        lblScan.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblScan.setForeground(UIConstants.ACCENT_BLUE);
        topBar.add(lblScan);

        txtBarcode = new RoundedTextField(25);
        txtBarcode.setPreferredSize(new Dimension(350, 45));
        txtBarcode.setFont(new Font("Segoe UI", Font.BOLD, 18));
        txtBarcode.setPlaceholder("Scan or Type SKU...");
        txtBarcode.addActionListener(e -> handleBarcodeScan());
        topBar.add(txtBarcode);
        
        JLabel lblQty = new JLabel("QTY:");
        lblQty.setForeground(UIConstants.TEXT_SECONDARY);
        topBar.add(lblQty);

        txtQty = new RoundedTextField(5);
        txtQty.setText("1");
        txtQty.setPreferredSize(new Dimension(60, 45));
        txtQty.setHorizontalAlignment(SwingConstants.CENTER);
        topBar.add(txtQty);

        add(topBar, BorderLayout.NORTH);

        // --- CART TABLE ---
        JPanel tableCard = createCardPanel();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(10, 10, 10, 10));

        model = new DefaultTableModel(new String[]{"ID", "Item Name", "Qty", "Unit Price", "Subtotal"}, 0);
        tblCart = new JTable(model);
        styleTable(tblCart);
        
        JScrollPane scroll = new JScrollPane(tblCart);
        styleScrollPane(scroll);
        tableCard.add(scroll, BorderLayout.CENTER);

        add(tableCard, BorderLayout.CENTER);

        // --- FOOTER (Grand Total & Checkout) ---
        JPanel footerBar = createCardPanel();
        footerBar.setLayout(new BorderLayout(30, 0));
        footerBar.setBorder(new EmptyBorder(20, 30, 20, 30));

        lblTotal = new JLabel("TOTAL: Rs. 0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTotal.setForeground(UIConstants.TEXT_PRIMARY);
        footerBar.add(lblTotal, BorderLayout.EAST);
        
        JButton btnCheckout = createPrimaryButton("Finalize & Print Receipt");
        btnCheckout.setPreferredSize(new Dimension(320, 55));
        btnCheckout.setBackground(UIConstants.SUCCESS_GREEN);
        btnCheckout.addActionListener(e -> finalizeSale());
        footerBar.add(btnCheckout, BorderLayout.WEST);
        
        add(footerBar, BorderLayout.SOUTH);
    }

    private void handleBarcodeScan() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        String code = txtBarcode.getText().trim();
        if (code.isEmpty()) return;

        try {
            Medicine m = medicineDAO.findByBarcode(code);
            if (m != null) {
                int qty = Integer.parseInt(txtQty.getText().trim());
                double sub = m.getUnitPrice() * qty;
                model.addRow(new Object[]{m.getMedicineId(), m.getName(), qty, m.getUnitPrice(), sub});
                
                grandTotal += sub;
                lblTotal.setText("TOTAL: Rs. " + String.format("%.2f", grandTotal));
                
                txtBarcode.setText(""); // Ready for next scan
                txtBarcode.requestFocus();
            } else {
                Toast.showError(parentFrame, "Product code '" + code + "' not recognized.");
            }
        } catch (NumberFormatException ex) {
            Toast.showError(parentFrame, "Quantity must be a valid number.");
        }
    }

    private void finalizeSale() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (model.getRowCount() == 0) {
            Toast.showError(parentFrame, "Cart is empty. Scan items to checkout.");
            return;
        }

        // --- ATOMIC STOCK SYNCHRONIZATION ---
        boolean success = true;
        for (int i = 0; i < model.getRowCount(); i++) {
            int medId = (Integer) model.getValueAt(i, 0);
            int qty = (Integer) model.getValueAt(i, 2);
            if (!medicineDAO.updateStock(medId, qty)) {
                success = false;
            }
        }

        if (success) {
            logDAO.record(1, "PHARMA_SALE: Rs. " + grandTotal, "PHARMACY_POS");
            Toast.showSuccess(parentFrame, "Transaction Finalized & Inventory Updated!");
            model.setRowCount(0);
            grandTotal = 0.0;
            lblTotal.setText("TOTAL: Rs. 0.00");
        } else {
            Toast.showError(parentFrame, "Critical Error: Could not update certain stock records.");
        }
    }
}
