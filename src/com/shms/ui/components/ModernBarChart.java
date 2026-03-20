package com.shms.ui.components;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Enterprise Native Bar Chart Engine (Zero-Dependency)
 * Custom graphics representation of weekly analytics.
 */
public class ModernBarChart extends JPanel {

    private Map<String, Double> data;
    private Color barColor = new Color(0, 112, 243); // Premium Blue

    public ModernBarChart(Map<String, Double> data) {
        this.data = data;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (data == null || data.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int padding = 30;
        int barWidth = (width - 2 * padding) / data.size() - 20;

        double max = data.values().stream().mapToDouble(Double::doubleValue).max().orElse(1.0);
        int x = padding + 10;
        
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            double value = entry.getValue();
            int barHeight = (int) ((value / max) * (height - 2 * padding - 20));

            // Bar Background
            g2.setColor(new Color(240, 240, 240));
            g2.fillRoundRect(x, padding, barWidth, height - 2 * padding, 15, 15);

            // Active Bar
            g2.setColor(barColor);
            g2.fillRoundRect(x, height - padding - barHeight, barWidth, barHeight, 15, 15);

            // Label
            g2.setColor(new Color(60, 60, 60));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            String label = entry.getKey().substring(5); // e.g., 03-20
            g2.drawString(label, x + (barWidth / 4), height - padding + 15);

            // Amount
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
            String amt = "Rs." + (int) value;
            g2.drawString(amt, x + (barWidth / 6), height - padding - barHeight - 5);

            x += barWidth + 20;
        }

        g2.dispose();
    }
}
