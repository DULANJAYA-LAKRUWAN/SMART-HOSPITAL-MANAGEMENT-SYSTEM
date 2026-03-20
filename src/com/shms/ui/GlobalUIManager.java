package com.shms.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Applies Global Look and Feel overrides, ensuring all system popups, 
 * un-themed components, and dialogs inherit the premium aesthetic.
 */
public class GlobalUIManager {
    
    public static void setup() {
        try {
            // Attempt to use system native look and feel as baseline, then override
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Override Global Colors & Fonts
            UIManager.put("Panel.background", UIConstants.BACKGROUND_LIGHT);
            UIManager.put("OptionPane.background", Color.WHITE);
            UIManager.put("OptionPane.messageForeground", UIConstants.TEXT_PRIMARY);
            UIManager.put("OptionPane.messageFont", UIConstants.FONT_NORMAL);
            
            UIManager.put("Button.font", UIConstants.FONT_BUTTON);
            UIManager.put("Button.background", UIConstants.CARD_WHITE);
            
            UIManager.put("Label.font", UIConstants.FONT_NORMAL);
            UIManager.put("Label.foreground", UIConstants.TEXT_PRIMARY);
            
            // Text selection colors
            Color selectionColor = new Color(0, 112, 243, 50); // Muted Accent Blue
            UIManager.put("TextField.selectionBackground", selectionColor);
            UIManager.put("TextField.selectionForeground", UIConstants.TEXT_PRIMARY);
            UIManager.put("PasswordField.selectionBackground", selectionColor);
            UIManager.put("PasswordField.selectionForeground", UIConstants.TEXT_PRIMARY);
            
            // Tooltip styling
            UIManager.put("ToolTip.background", UIConstants.SIDE_MENU_DARK);
            UIManager.put("ToolTip.foreground", Color.WHITE);
            UIManager.put("ToolTip.font", new Font("Segoe UI", Font.PLAIN, 12));
            UIManager.put("ToolTip.border", BorderFactory.createEmptyBorder(5, 8, 5, 8));
            
            // ComboBox styling
            UIManager.put("ComboBox.background", Color.WHITE);
            UIManager.put("ComboBox.foreground", UIConstants.TEXT_PRIMARY);
            UIManager.put("ComboBox.font", UIConstants.FONT_NORMAL);
            UIManager.put("ComboBox.selectionBackground", selectionColor);
            UIManager.put("ComboBox.selectionForeground", UIConstants.TEXT_PRIMARY);
            
            // Option Pane
            UIManager.put("OptionPane.messageForeground", UIConstants.TEXT_PRIMARY);
            UIManager.put("Button.focus", new Color(0, 0, 0, 0));

        } catch (Exception ex) {
            System.err.println("Failed to initialize Global UI Manager.");
        }
    }
}
