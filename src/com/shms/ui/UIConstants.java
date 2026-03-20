package com.shms.ui;

import java.awt.*;
import javax.swing.border.Border;
import javax.swing.BorderFactory;

/**
 * Modern Design System for SHMS.
 * Contains all primary colors, fonts, and common UI shapes.
 */
public class UIConstants {

    // --- COLOR PALETTE (Modern Slate/Blue) ---
    public static final Color SIDE_MENU_DARK = new Color(33, 43, 54);
    public static final Color SIDE_MENU_ACTIVE = new Color(0, 176, 255, 40); // Subtle blue highlight
    public static final Color ACCENT_BLUE = new Color(0, 112, 243);
    public static final Color BACKGROUND_LIGHT = new Color(245, 247, 250);
    public static final Color CARD_WHITE = Color.WHITE;
    public static final Color TEXT_PRIMARY = new Color(33, 43, 54);
    public static final Color TEXT_SECONDARY = new Color(153, 163, 173);
    public static final Color SUCCESS_GREEN = new Color(0, 171, 85);
    public static final Color DANGER_RED = new Color(255, 72, 66);

    // --- FONTS ---
    public static final Font FONT_LARGE_TITLE = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_CARD_LABEL = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_CARD_VALUE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);

    // --- BORDERS & PADDING ---
    public static final Border CARD_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
        BorderFactory.createEmptyBorder(15, 15, 15, 15)
    );

    public static final Border ROUNDED_INPUT_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
        BorderFactory.createEmptyBorder(8, 12, 8, 12)
    );
}
