package com.shms.ui.components;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

/**
 * A modern, sleek scrollbar UI without arrow buttons, featuring a rounded thumb.
 * Inspired by modern web and MacOS designs.
 */
public class ModernScrollBarUI extends BasicScrollBarUI {

    private final int thumbSize = 8;

    @Override
    protected void configureScrollBarColors() {
        this.thumbColor = new Color(200, 200, 200);
        this.trackColor = Color.WHITE;
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }

    private JButton createZeroButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        button.setMaximumSize(new Dimension(0, 0));
        return button;
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(trackColor);
        g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        g2.dispose();
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(isDragging ? thumbColor.darker() : thumbColor);
        
        // Add a little margin
        int margin = 2;
        g2.fillRoundRect(thumbBounds.x + margin, thumbBounds.y + margin, 
                         thumbBounds.width - (margin * 2), thumbBounds.height - (margin * 2), 
                         thumbSize, thumbSize);
        
        g2.dispose();
    }
}
