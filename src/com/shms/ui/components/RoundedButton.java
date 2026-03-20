package com.shms.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A sleek, high-end button with soft rounded corners and hover feedback.
 */
public class RoundedButton extends JButton {

    private int radius = 25;
    @SuppressWarnings("unused")
    private Color hoverColor;
    private Color originalColor;

    public RoundedButton(String text, Color baseColor, Color hoverColor) {
        super(text);
        this.originalColor = baseColor;
        this.hoverColor = hoverColor;
        
        setBackground(baseColor);
        setForeground(Color.WHITE);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(originalColor);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        
        g2.dispose();
        super.paintComponent(g);
    }
}
