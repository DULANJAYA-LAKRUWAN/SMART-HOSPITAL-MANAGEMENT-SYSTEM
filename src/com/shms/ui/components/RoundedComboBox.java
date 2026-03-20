package com.shms.ui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * Premium rounded ComboBox component.
 */
public class RoundedComboBox<E> extends JComboBox<E> {

    private int radius = 25;
    private Color borderColor = new Color(200, 200, 200);
    private Color focusColor = new Color(0, 112, 243);
    private boolean isFocused = false;

    public RoundedComboBox(E[] items) {
        super(items);
        setOpaque(false);
        setBackground(Color.WHITE);
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
        setBorder(new EmptyBorder(5, 15, 5, 15));

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                isFocused = true;
                borderColor = focusColor;
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                isFocused = false;
                borderColor = new Color(200, 200, 200);
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Background
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        
        super.paintComponent(g2);
        
        // Custom Border
        g2.setColor(borderColor);
        if (isFocused) {
            g2.setStroke(new BasicStroke(2.0f));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, radius, radius);
        } else {
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        }
        
        g2.dispose();
    }
}
