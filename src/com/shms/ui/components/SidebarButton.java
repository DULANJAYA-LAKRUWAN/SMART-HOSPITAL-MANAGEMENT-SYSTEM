package com.shms.ui.components;

import com.shms.ui.UIConstants;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Premium sidebar navigation button with animated hover states and active indicators.
 */
public class SidebarButton extends JButton {

    private boolean isHovered = false;
    private boolean isActive = false;
    private Color hoverBg = new Color(255, 255, 255, 12);
    private Color activeBg = new Color(255, 255, 255, 18);

    public SidebarButton(String text) {
        super(text);
        
        setMaximumSize(new Dimension(260, 50));
        setAlignmentX(Component.CENTER_ALIGNMENT);
        setHorizontalAlignment(SwingConstants.LEFT);
        setBackground(UIConstants.SIDE_MENU_DARK);
        setForeground(new Color(220, 220, 220));
        setFont(UIConstants.FONT_BUTTON);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(new EmptyBorder(10, 30, 10, 10));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }

    public void setActive(boolean active) {
        this.isActive = active;
        if (active) {
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
        } else {
            setForeground(new Color(220, 220, 220));
            setFont(UIConstants.FONT_BUTTON);
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fill background
        if (isActive) {
            g2.setColor(activeBg);
            g2.fillRect(0, 0, getWidth(), getHeight());
            
            // Left active border strip indicator
            g2.setColor(UIConstants.ACCENT_BLUE);
            g2.fillRect(0, 0, 4, getHeight());
        } else if (isHovered) {
            g2.setColor(hoverBg);
            g2.fillRect(0, 3, getWidth() - 10, getHeight() - 6);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
