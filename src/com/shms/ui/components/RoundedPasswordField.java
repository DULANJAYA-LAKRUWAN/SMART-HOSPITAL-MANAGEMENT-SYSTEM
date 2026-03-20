package com.shms.ui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * Modern rounded password field.
 */
public class RoundedPasswordField extends JPasswordField {

    private int radius = 10;
    private Color borderColor = new Color(200, 200, 200);
    private Color focusColor = new Color(0, 112, 243);
    private boolean isFocused = false;
    private String placeholder = "";

    public RoundedPasswordField() {
        super();
        setOpaque(false);
        setBorder(new EmptyBorder(10, 15, 10, 15));
        setFont(new Font("Segoe UI", Font.PLAIN, 14));

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

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Background
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        
        // Draw native text/selection highlighting
        super.paintComponent(g2);
        
        // Draw Placeholder if empty
        if (getPassword().length == 0 && !placeholder.isEmpty()) {
            g2.setColor(new Color(150, 150, 150));
            FontMetrics fm = g2.getFontMetrics(getFont());
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            int x = getInsets().left;
            g2.drawString(placeholder, x, y);
        }

        // Draw Border over text selection
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
