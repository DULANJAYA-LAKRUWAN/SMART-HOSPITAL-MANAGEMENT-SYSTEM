package com.shms.ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * A modern panel with rounded corners and a subtle drop-shadow effect.
 */
public class RoundedPanel extends JPanel {

    private int radius;

    public RoundedPanel(int radius, Color backgroundColor) {
        super();
        this.radius = radius;
        setBackground(backgroundColor);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Premium Multi-Layer Soft Drop Shadow
        int shadowDrops = 5;
        for (int i = 0; i < shadowDrops; i++) {
            g2.setColor(new Color(0, 0, 0, 4)); // Very light transparency
            g2.fillRoundRect(
                shadowDrops - i, 
                shadowDrops - i, 
                width - ((shadowDrops - i) * 2), 
                height - ((shadowDrops - i) * 2), 
                radius + i, radius + i
            );
        }

        // Main Panel Body
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, width - 6, height - 6, radius, radius);
        
        g2.dispose();
        super.paintComponent(g);
    }
}
