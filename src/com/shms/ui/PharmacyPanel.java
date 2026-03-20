package com.shms.ui;

import com.shms.service.MedicineService;
import com.shms.dao.LogDAO;
import com.shms.dao.MedicineDAO;
import com.shms.model.Medicine;
import com.shms.ui.components.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PharmacyPanel extends BaseModernPanel {
    
    private RoundedTextField txtBarcode, txtQty;
    private JComboBox<ComboItem> cmbMedicines;
    private JTable tblCart;
    private DefaultTableModel model;
    private MedicineService medicineService;
    private LogDAO logDAO;
    private JLabel lblTotal;
    private double grandTotal = 0.0;

    public PharmacyPanel() {
        super("Pharmacy POS & Inventory Dispatch");
        this.medicineService = new MedicineService();
        this.logDAO = new LogDAO();
        initializeUI();
    }

    private void initializeUI() {
        // --- TOP SCANNER & SELECTION BAR ---
        JPanel topBar = createCardPanel();
        topBar.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
        
        JLabel lblHeader = new JLabel("INVENTORY DISPATCH");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblHeader.setForeground(UIConstants.ACCENT_BLUE);
        topBar.add(lblHeader);

        // 1. Barcode Field
        txtBarcode = new RoundedTextField(15);
        txtBarcode.setPreferredSize(new Dimension(200, 45));
        txtBarcode.setPlaceholder("Scan Barcode...");
        txtBarcode.addActionListener(e -> handleBarcodeScan());
        topBar.add(txtBarcode);

        topBar.add(new JLabel("OR Selection:"));

        // 2. Manual Selector
        List<Medicine> meds = new MedicineDAO().getAllMedicines();
        ComboItem[] medItems = meds.stream()
            .map(m -> new ComboItem(m.getMedicineId(), m.getName() + " [Rs." + m.getUnitPrice() + "]"))
            .toArray(ComboItem[]::new);
        
        cmbMedicines = new JComboBox<>(medItems);
        cmbMedicines.setPreferredSize(new Dimension(250, 45));
        cmbMedicines.setFont(UIConstants.FONT_NORMAL);
        cmbMedicines.setBackground(Color.WHITE);
        topBar.add(cmbMedicines);
        
        JLabel lblQty = new JLabel("QTY:");
        lblQty.setForeground(UIConstants.TEXT_SECONDARY);
        topBar.add(lblQty);

        txtQty = new RoundedTextField(4);
        txtQty.setText("1");
        txtQty.setPreferredSize(new Dimension(50, 45));
        txtQty.setHorizontalAlignment(SwingConstants.CENTER);
        topBar.add(txtQty);

        JButton btnAdd = createPrimaryButton("+ Add Item");
        btnAdd.addActionListener(e -> handleManualAdd());
        topBar.add(btnAdd);

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

        // --- FOOTER ---
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
        String code = txtBarcode.getText().trim();
        if (code.isEmpty()) return;
        Medicine m = medicineService.getMedicineByBarcode(code);
        if (m != null) addItemToCart(m);
        else Toast.showError(null, "Barcode '" + code + "' not found.");
        txtBarcode.setText("");
        txtBarcode.requestFocus();
    }

    private void handleManualAdd() {
        ComboItem selection = (ComboItem) cmbMedicines.getSelectedItem();
        if (selection == null) return;
        Medicine m = new MedicineDAO().getMedicineById(selection.getId());
        if (m != null) addItemToCart(m);
    }

    private void addItemToCart(Medicine m) {
        try {
            int qty = Integer.parseInt(txtQty.getText().trim());
            double sub = m.getUnitPrice() * qty;
            model.addRow(new Object[]{m.getMedicineId(), m.getName(), qty, m.getUnitPrice(), sub});
            grandTotal += sub;
            lblTotal.setText("TOTAL: Rs. " + String.format("%.2f", grandTotal));
        } catch (NumberFormatException ex) {
            Toast.showError(null, "Invalid Quantity.");
        }
    }

    private void finalizeSale() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (model.getRowCount() == 0) {
            Toast.showError(parentFrame, "Cart is empty.");
            return;
        }

        boolean success = true;
        for (int i = 0; i < model.getRowCount(); i++) {
            int medId = (Integer) model.getValueAt(i, 0);
            int qty = (Integer) model.getValueAt(i, 2);
            if (!medicineService.updateStockBalance(medId, qty)) success = false;
        }

        if (success) {
            logDAO.record(1, "PHARMA_SALE: Rs. " + grandTotal, "PHARMACY_POS");
            Toast.showSuccess(parentFrame, "Sale Finalized!");
            model.setRowCount(0);
            grandTotal = 0.0;
            lblTotal.setText("TOTAL: Rs. 0.00");
        } else {
            Toast.showError(parentFrame, "Critical Stock Error.");
        }
    }
}
