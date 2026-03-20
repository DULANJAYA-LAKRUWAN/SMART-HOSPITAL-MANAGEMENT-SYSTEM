package com.shms.ui;

import com.shms.dao.AuditDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Audit Trail Hub: A centralized view for system logs and staff activity tracking.
 * Provides transparency for all financial and administrative actions.
 */
public class AuditTrailPanel extends JPanel {

    private final AuditDAO auditDAO = new AuditDAO();
    private JTable logTable;
    private DefaultTableModel logModel;

    public AuditTrailPanel() {
        setLayout(new BorderLayout(0, 20));
        setBackground(UIConstants.BACKGROUND_LIGHT);
        setBorder(new EmptyBorder(30, 30, 30, 30));

        initComponents();
        refreshLogs();
    }

    private void initComponents() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        JLabel title = new JLabel("🛡️ System Operations Audit Hub");
        title.setFont(UIConstants.FONT_LARGE_TITLE);
        header.add(title, BorderLayout.WEST);

        JButton btnRefresh = new JButton("↻ Refresh Logs");
        btnRefresh.addActionListener(e -> refreshLogs());
        header.add(btnRefresh, BorderLayout.EAST);
        
        add(header, BorderLayout.NORTH);

        // Log Table
        logModel = new DefaultTableModel(new String[]{"Log ID", "Staff ID", "Action Executed", "Target Module", "Event Timestamp"}, 0);
        logTable = new JTable(logModel);
        logTable.setRowHeight(35);
        logTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        JScrollPane scroll = new JScrollPane(logTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        scroll.getViewport().setBackground(Color.WHITE);
        add(scroll, BorderLayout.CENTER);
        
        // Footer hint
        JLabel hint = new JLabel("Note: Showing last 100 system events for performance optimization.");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        hint.setForeground(Color.GRAY);
        add(hint, BorderLayout.SOUTH);
    }

    private void refreshLogs() {
        logModel.setRowCount(0);
        java.util.List<Object[]> logs = auditDAO.getAllLogs();
        for (Object[] row : logs) {
            logModel.addRow(row);
        }
    }
}
