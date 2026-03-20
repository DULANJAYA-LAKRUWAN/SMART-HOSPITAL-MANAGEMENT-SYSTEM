package com.shms.ui;

import com.shms.ui.components.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Enhanced Modern Base Panel with premium rounded components.
 */
public abstract class BaseModernPanel extends JPanel {

    public BaseModernPanel(String title) {
        initializeBaseUI(title);
    }

    private void initializeBaseUI(String title) {
        setLayout(new BorderLayout(25, 25));
        setBackground(UIConstants.BACKGROUND_LIGHT);
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // Header Title
        JLabel lblHeader = new JLabel(title);
        lblHeader.setFont(UIConstants.FONT_HEADER);
        lblHeader.setForeground(UIConstants.TEXT_PRIMARY);
        lblHeader.setBorder(new EmptyBorder(0, 0, 20, 0));
        add(lblHeader, BorderLayout.NORTH);
    }

    /**
     * Creates a high-end card panel with rounded corners and light shadow.
     */
    protected JPanel createCardPanel() {
        return new RoundedPanel(20, Color.WHITE);
    }

    /**
     * Creates a premium rounded input field with placeholder support.
     */
    protected JTextField createModernInput(JPanel container, String label, String placeholder) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(UIConstants.TEXT_SECONDARY);
        container.add(lbl);
        container.add(Box.createVerticalStrut(5));

        RoundedTextField f = new RoundedTextField(20);
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        f.setPlaceholder(placeholder);
        container.add(f);
        container.add(Box.createVerticalStrut(15));
        return f;
    }

    /**
     * Styles a JScrollPane to use the sleek ModernScrollBarUI.
     */
    protected void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 8));
        scrollPane.getViewport().setBackground(Color.WHITE);
    }

    /**
     * Styles a JTable for professional medical dashboards.
     */
    protected void styleTable(JTable table) {
        table.setRowHeight(40);
        table.setFont(UIConstants.FONT_NORMAL);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(248, 249, 251));
        table.getTableHeader().setPreferredSize(new Dimension(0, 45));
        table.setSelectionBackground(new Color(0, 112, 243, 25));
        table.setSelectionForeground(UIConstants.TEXT_PRIMARY);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setGridColor(new Color(240, 240, 240));
    }
    
    protected JButton createPrimaryButton(String text) {
        return new RoundedButton(text, UIConstants.ACCENT_BLUE, new Color(0, 90, 195));
    }
}
