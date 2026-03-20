package com.shms.ui;

import com.shms.model.User;
import com.shms.ui.components.*;
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

        // Brand Header & Live Digital Watch
        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setOpaque(false);
        brandPanel.setBorder(new EmptyBorder(30, 0, 30, 0));

        JLabel logo = new JLabel("⚕ SHMS PRO", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        brandPanel.add(logo);

        JLabel lblWatch = new JLabel("00:00:00 AM", SwingConstants.CENTER);
        lblWatch.setFont(new Font("Consolas", Font.BOLD, 18));
        lblWatch.setForeground(UIConstants.ACCENT_BLUE);
        lblWatch.setAlignmentX(Component.CENTER_ALIGNMENT);
        brandPanel.add(Box.createVerticalStrut(10));
        brandPanel.add(lblWatch);

        // Update Clock every second
        Timer clockTimer = new Timer(1000, e -> {
            lblWatch.setText(java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("hh:mm:ss a")));
        });
        clockTimer.start();
        
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

    private JLabel lblTotalPatients, lblActiveDoctors, lblTodayRev, lblApptCount;
    private javax.swing.table.DefaultTableModel dashboardFeedModel;

    private JPanel buildDashboardHome() {
        JPanel home = new JPanel(new BorderLayout(0, 30));
        home.setBackground(UIConstants.BACKGROUND_LIGHT);
        home.setBorder(new EmptyBorder(40, 50, 40, 50));

        // Header
        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setOpaque(false);
        JLabel wlcm = new JLabel("Welcome Back, " + currentUser.getFullName().split(" ")[0] + "!");
        wlcm.setFont(UIConstants.FONT_LARGE_TITLE);
        wlcm.setForeground(UIConstants.TEXT_PRIMARY);
        hdr.add(wlcm, BorderLayout.NORTH);

        JLabel sub = new JLabel("Live Hospital System Monitor | Active Session Dashboard");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sub.setForeground(UIConstants.TEXT_SECONDARY);
        hdr.add(sub, BorderLayout.SOUTH);
        home.add(hdr, BorderLayout.NORTH);

        // KPI Grid
        JPanel kpiGrid = new JPanel(new GridLayout(1, 4, 25, 0));
        kpiGrid.setOpaque(false);

        lblTotalPatients = new JLabel("0");
        lblActiveDoctors = new JLabel("0");
        lblTodayRev = new JLabel("Rs. 0");
        lblApptCount = new JLabel("0");

        kpiGrid.add(createLiveCard("👥 PATIENTS", lblTotalPatients, UIConstants.ACCENT_BLUE));
        kpiGrid.add(createLiveCard("🩺 DOCTORS", lblActiveDoctors, UIConstants.SUCCESS_GREEN));
        kpiGrid.add(createLiveCard("⚖️ REVENUE", lblTodayRev, UIConstants.DANGER_RED));
        kpiGrid.add(createLiveCard("📅 BOOKINGS", lblApptCount, UIConstants.SIDE_MENU_DARK));
        
        JPanel midContent = new JPanel(new BorderLayout(0, 25));
        midContent.setOpaque(false);
        midContent.add(kpiGrid, BorderLayout.NORTH);

        // Recent Activity table
        RoundedPanel activityCard = new RoundedPanel(25, Color.WHITE);
        activityCard.setLayout(new BorderLayout());
        activityCard.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel activityTitle = new JLabel("REAL-TIME CLINIC ACTIVITY");
        activityTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        activityTitle.setForeground(UIConstants.TEXT_PRIMARY);
        activityTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        activityCard.add(activityTitle, BorderLayout.NORTH);

        dashboardFeedModel = new javax.swing.table.DefaultTableModel(new String[]{"ID", "Patient Name", "Department", "Time Slot", "Status"}, 0);
        JTable tblRecent = new JTable(dashboardFeedModel);
        tblRecent.setRowHeight(40);
        tblRecent.setShowVerticalLines(false);
        
        JScrollPane scroll = new JScrollPane(tblRecent);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        activityCard.add(scroll, BorderLayout.CENTER);
        
        midContent.add(activityCard, BorderLayout.CENTER);
        home.add(midContent, BorderLayout.CENTER);

        // Auto-refresh Timer (Every 10 seconds)
        Timer refreshTimer = new Timer(10000, e -> refreshDashboardData());
        refreshTimer.start();
        refreshDashboardData(); // Initial load

        return home;
    }

    private void refreshDashboardData() {
        try {
            // Stats Queries
            int pCount = new com.shms.dao.PatientDAO().getAllPatients().size();
            int dCount = new com.shms.dao.DoctorDAO().getAllDoctors().size();
            double rev = new com.shms.service.BillingService().getDailyRevenue();
            int aCount = new com.shms.dao.AppointmentDAO().getDailyAppointmentCount();

            // Update Labels
            lblTotalPatients.setText(String.valueOf(pCount));
            lblActiveDoctors.setText(String.valueOf(dCount));
            lblTodayRev.setText("Rs. " + String.format("%.0f", rev));
            lblApptCount.setText(String.valueOf(aCount));

            // Update Feed
            dashboardFeedModel.setRowCount(0);
            java.util.List<Object[]> feed = new com.shms.dao.AppointmentDAO().getRecentActivityFeed();
            for (Object[] row : feed) {
                dashboardFeedModel.addRow(row);
            }
        } catch (Exception e) {}
    }

    private JPanel createLiveCard(String title, JLabel valueLabel, Color accent) {
        RoundedPanel card = new RoundedPanel(20, Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
        t.setForeground(UIConstants.TEXT_SECONDARY);
        
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(accent);

        card.add(t);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);
        return card;
    }
}
