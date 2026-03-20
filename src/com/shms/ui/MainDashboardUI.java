package com.shms.ui;

import com.shms.model.User;
import com.shms.ui.components.*;
import com.shms.db.DBConnection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Polished Modern Dashboard for SHMS.
 * Uses high-fidelity rounded components for a premium feel.
 */
public class MainDashboardUI extends JFrame {

    private User currentUser;
    private JPanel sideMenu;
    private JPanel contentArea;
    private CardLayout cardLayout;

    // CardLayout keys
    private static final String CARD_HOME        = "HOME";
    private static final String CARD_PATIENTS    = "PATIENTS";
    private static final String CARD_DOCTORS     = "DOCTORS";
    private static final String CARD_APPTS       = "APPOINTMENTS";
    private static final String CARD_PHARMACY    = "PHARMACY";
    private static final String CARD_BILLING     = "BILLING";
    private static final String CARD_REPORTS     = "REPORTS";

    public MainDashboardUI(User user) {
        this.currentUser = user;
        initializeModernUI();
    }

    private void initializeModernUI() {
        setTitle("SHMS Pro Dashboard — " + currentUser.getFullName());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 800));
        setLayout(new BorderLayout());

        buildModernSideMenu();
        buildModernContentArea();
    }

    // ── SIDE MENU ────────────────────────────────────────────────────────────────
    private void buildModernSideMenu() {
        sideMenu = new JPanel();
        sideMenu.setPreferredSize(new Dimension(260, 0));
        sideMenu.setBackground(UIConstants.SIDE_MENU_DARK); 
        sideMenu.setLayout(new BoxLayout(sideMenu, BoxLayout.Y_AXIS));

        // Brand Header
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 30));
        brandPanel.setOpaque(false);
        JLabel logo = new JLabel("⚕ SHMS PRO");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logo.setForeground(Color.WHITE);
        brandPanel.add(logo);
        sideMenu.add(brandPanel);

        // Profile Section
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setOpaque(false);
        profilePanel.setBorder(new EmptyBorder(0, 20, 30, 20));

        JLabel lblName = new JLabel(currentUser.getFullName());
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblName.setForeground(Color.WHITE);
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblRole = new JLabel(currentUser.getRole());
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRole.setForeground(UIConstants.TEXT_SECONDARY);
        lblRole.setAlignmentX(Component.CENTER_ALIGNMENT);

        profilePanel.add(lblName);
        profilePanel.add(Box.createVerticalStrut(5));
        profilePanel.add(lblRole);
        sideMenu.add(profilePanel);

        // Sidebar Navigation
        addModernMenuButton("📈 Dashboard Overview", CARD_HOME);
        addModernMenuButton("🧑‍⚕ Patient Management", CARD_PATIENTS);
        addModernMenuButton("📅 Appointment Desk",   CARD_APPTS);

        if ("ADMIN".equals(currentUser.getRole())) {
            addModernMenuButton("🩺 Medical Staff",    CARD_DOCTORS);
            addModernMenuButton("💊 Retail Pharmacy",  CARD_PHARMACY);
            addModernMenuButton("💰 Billing & Finance", CARD_BILLING);
            addModernMenuButton("📊 Analytical Reports", CARD_REPORTS);
        }

        sideMenu.add(Box.createVerticalGlue());

        // Polished Logout
        RoundedButton btnLogout = new RoundedButton("Logout of Portal", UIConstants.DANGER_RED, new Color(183, 28, 28));
        btnLogout.setMaximumSize(new Dimension(220, 50));
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Are you sure you want to end your session?", "Logout Confirm", JOptionPane.YES_NO_OPTION) == 0) {
                this.dispose();
                new LoginUI().setVisible(true);
            }
        });
        sideMenu.add(btnLogout);
        sideMenu.add(Box.createVerticalStrut(30));

        add(sideMenu, BorderLayout.WEST);
    }

    private java.util.List<SidebarButton> menuButtons = new java.util.ArrayList<>();

    private void addModernMenuButton(String text, String cardKey) {
        SidebarButton btn = new SidebarButton(text);
        menuButtons.add(btn);
        
        btn.addActionListener(e -> {
            for (SidebarButton b : menuButtons) {
                b.setActive(false);
            }
            btn.setActive(true);
            cardLayout.show(contentArea, cardKey);
        });

        sideMenu.add(btn);
        sideMenu.add(Box.createVerticalStrut(2));
        
        // Initial Active State for Home
        if (CARD_HOME.equals(cardKey)) btn.setActive(true);
    }

    // ── CONTENT AREA ─────────────────────────────────────────────────────────────
    private void buildModernContentArea() {
        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(UIConstants.BACKGROUND_LIGHT);

        contentArea.add(buildDashboardHome(), CARD_HOME);
        contentArea.add(new PatientPanel(),     CARD_PATIENTS);
        contentArea.add(new DoctorPanel(),      CARD_DOCTORS);
        contentArea.add(new AppointmentPanel(), CARD_APPTS);
        contentArea.add(new PharmacyPanel(),    CARD_PHARMACY);
        contentArea.add(new ReportPanel(),      CARD_REPORTS);
        contentArea.add(new BillingPanel(), CARD_BILLING);

        cardLayout.show(contentArea, CARD_HOME);
        add(contentArea, BorderLayout.CENTER);
    }

    private JPanel buildDashboardHome() {
        JPanel home = new JPanel(new BorderLayout());
        home.setBackground(UIConstants.BACKGROUND_LIGHT);
        home.setBorder(new EmptyBorder(40, 50, 40, 50));

        // Header
        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setOpaque(false);
        JLabel wlcm = new JLabel("Welcome Back, " + currentUser.getFullName().split(" ")[0] + "!");
        wlcm.setFont(UIConstants.FONT_LARGE_TITLE);
        wlcm.setForeground(UIConstants.TEXT_PRIMARY);
        hdr.add(wlcm, BorderLayout.NORTH);

        JLabel sub = new JLabel("Health Center Performance Snapshot for March 20, 2026");
        sub.setFont(UIConstants.FONT_NORMAL);
        sub.setForeground(UIConstants.TEXT_SECONDARY);
        hdr.add(sub, BorderLayout.SOUTH);
        home.add(hdr, BorderLayout.NORTH);

        // KPI Grid
        JPanel kpiGrid = new JPanel(new GridLayout(1, 4, 25, 0));
        kpiGrid.setOpaque(false);
        kpiGrid.setBorder(new EmptyBorder(35, 0, 35, 0));

        // Dynamic KPI Logic
        int totalPatients = new com.shms.dao.PatientDAO().getAllPatients().size();
        int totalDoctors = new com.shms.dao.DoctorDAO().getAllDoctors().size();
        int totalInvoices = new com.shms.dao.BillingDAO().getDailyTransactionCount();
        
        kpiGrid.add(createSummaryCard("TOTAL PATIENTS", String.valueOf(totalPatients), UIConstants.ACCENT_BLUE));
        kpiGrid.add(createSummaryCard("TODAY'S SLOTS", "42", UIConstants.SUCCESS_GREEN)); // Advanced date-based logic later
        kpiGrid.add(createSummaryCard("INVOICES TODAY", String.valueOf(totalInvoices), UIConstants.DANGER_RED)); 
        kpiGrid.add(createSummaryCard("ON-DUTY STAFF", String.valueOf(totalDoctors), UIConstants.SIDE_MENU_DARK));
        home.add(kpiGrid, BorderLayout.CENTER);

        // Status Card
        RoundedPanel status = new RoundedPanel(20, Color.WHITE);
        status.setLayout(new BorderLayout());
        status.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        boolean isConnected = DBConnection.checkStatus();
        String statusText = isConnected ? "System Status: ⚡ Ultra Fast | Database: 📗 Connected" : "System Status: ⚠️ Critical | Database: 🔴 DISCONNECTED";
        Color statusColor = isConnected ? UIConstants.SUCCESS_GREEN : UIConstants.DANGER_RED;

        JLabel stitle = new JLabel(statusText);
        stitle.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 15));
        stitle.setForeground(statusColor);
        status.add(stitle);
        home.add(status, BorderLayout.SOUTH);

        return home;
    }

    private JPanel createSummaryCard(String title, String value, Color accent) {
        RoundedPanel card = new RoundedPanel(20, Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
        t.setForeground(UIConstants.TEXT_SECONDARY);
        
        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.BOLD, 36));
        v.setForeground(accent);

        card.add(t);
        card.add(Box.createVerticalStrut(10));
        card.add(v);
        return card;
    }
}
