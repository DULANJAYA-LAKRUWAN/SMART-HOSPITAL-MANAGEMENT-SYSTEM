package com.shms.ui.components;

import com.shms.ui.UIConstants;
import javax.swing.*;
import java.awt.*;

/**
 * Modern, non-blocking Toast Notification system.
 * Replaces ugly JOptionPanes for success/info messages.
 */
public class Toast extends JWindow {
    
    private final int displayTime = 3000; // 3 seconds
    private float opacity = 0.0f;
    private Timer fadeInTimer;
    private Timer fadeOutTimer;
    private Timer pauseTimer;

    public Toast(JFrame parent, String message, boolean isSuccess) {
        super(parent);
        
        setLayout(new BorderLayout());
        setBackground(new Color(0, 0, 0, 0)); // Transparent window background
        setAlwaysOnTop(true);
        setFocusableWindowState(false); // Do not steal focus

        // Main pill body
        JPanel pill = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
            }
        };
        pill.setOpaque(false);
        pill.setBackground(isSuccess ? UIConstants.SUCCESS_GREEN : UIConstants.DANGER_RED);
        pill.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 15));
        
        // Message label
        JLabel lblMessage = new JLabel(message);
        lblMessage.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblMessage.setForeground(Color.WHITE);
        
        // Icon
        JLabel lblIcon = new JLabel(isSuccess ? "✓" : "⚠");
        lblIcon.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblIcon.setForeground(Color.WHITE);

        pill.add(lblIcon);
        pill.add(lblMessage);

        add(pill, BorderLayout.CENTER);
        pack();

        // Position at top center of parent
        if (parent != null) {
            int x = parent.getX() + (parent.getWidth() - getWidth()) / 2;
            int y = parent.getY() + 50; // 50px from top
            setLocation(x, y);
        } else {
            setLocationRelativeTo(null);
        }

        setOpacity(0.0f);
        setupAnimations();
    }

    private void setupAnimations() {
        // Fade in
        fadeInTimer = new Timer(15, e -> {
            opacity += 0.1f;
            if (opacity >= 1.0f) {
                opacity = 1.0f;
                fadeInTimer.stop();
                pauseTimer.start(); // Start countdown to fade out
            }
            setOpacity(opacity);
        });

        // Pause
        pauseTimer = new Timer(displayTime, e -> {
            pauseTimer.stop();
            fadeOutTimer.start();
        });
        pauseTimer.setRepeats(false);

        // Fade out
        fadeOutTimer = new Timer(15, e -> {
            opacity -= 0.1f;
            if (opacity <= 0.0f) {
                opacity = 0.0f;
                fadeOutTimer.stop();
                dispose();
            }
            setOpacity(opacity);
        });
    }

    public void showToast() {
        setVisible(true);
        fadeInTimer.start();
    }

    /**
     * Static helper to quickly show a success toast.
     */
    public static void showSuccess(JFrame parent, String message) {
        new Toast(parent, message, true).showToast();
    }
    
    /**
     * Static helper to quickly show an error toast.
     */
    public static void showError(JFrame parent, String message) {
        new Toast(parent, message, false).showToast();
    }
}
