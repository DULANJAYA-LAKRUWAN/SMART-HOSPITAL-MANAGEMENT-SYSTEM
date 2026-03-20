package com.shms.ui;

import com.shms.service.UserService;
import com.shms.model.User;
import com.shms.ui.components.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginUI extends JFrame {
    
    private RoundedTextField txtUsername;
    private RoundedPasswordField txtPassword;
    private RoundedButton btnLogin;
    private UserService userService;

    public LoginUI() {
        this.userService = new UserService();
        initializeModernUI();
    }

    private void initializeModernUI() {
        setTitle("SHMS | Secure Authentication");
        setSize(480, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // --- GRADIENT BACKGROUND ---
        JPanel background = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(30, 39, 46), 0, getHeight(), new Color(15, 18, 20));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        background.setLayout(new GridBagLayout());
        setContentPane(background);

        // --- FLOATING LOGIN CARD ---
        RoundedPanel card = new RoundedPanel(40, Color.WHITE);
        card.setPreferredSize(new Dimension(400, 580));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        // 🟢 Header Section
        JLabel lblIcon = new JLabel("⚕", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI", Font.BOLD, 64));
        lblIcon.setForeground(UIConstants.ACCENT_BLUE);
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblIcon.setBorder(new EmptyBorder(50, 0, 10, 0));
        card.add(lblIcon);

        JLabel lblTitle = new JLabel("SHMS PRO LOGIN");
        lblTitle.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 22));
        lblTitle.setForeground(new Color(44, 62, 80));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblTitle);

        JLabel lblSub = new JLabel("Please enter your credentials to login");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(UIConstants.TEXT_SECONDARY);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSub.setBorder(new EmptyBorder(5, 0, 45, 0));
        card.add(lblSub);

        // 🔵 Input Fields Section
        JPanel inputs = new JPanel();
        inputs.setLayout(new BoxLayout(inputs, BoxLayout.Y_AXIS));
        inputs.setOpaque(false);
        inputs.setBorder(new EmptyBorder(0, 40, 0, 40));
        inputs.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtUsername = new RoundedTextField(25);
        txtUsername.setMaximumSize(new Dimension(320, 45));
        txtUsername.setPlaceholder("e.g. admin or dr.perera");
        txtUsername.setToolTipText("Enter your assigned system username");
        txtUsername.addActionListener(e -> handleLogin());
        txtUsername.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtPassword = new RoundedPasswordField();
        txtPassword.setMaximumSize(new Dimension(320, 45));
        txtPassword.setPlaceholder("e.g. admin123");
        txtPassword.setToolTipText("Enter your secure password");
        txtPassword.addActionListener(e -> handleLogin());
        txtPassword.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblUser = createLabel("USERNAME / ID");
        lblUser.setAlignmentX(Component.CENTER_ALIGNMENT);
        inputs.add(lblUser);
        inputs.add(Box.createVerticalStrut(8));
        inputs.add(txtUsername);
        
        inputs.add(Box.createVerticalStrut(25));
        
        JLabel lblPass = createLabel("PASSWORD");
        lblPass.setAlignmentX(Component.CENTER_ALIGNMENT);
        inputs.add(lblPass);
        inputs.add(Box.createVerticalStrut(8));
        inputs.add(txtPassword);
        
        // 🔑 Forgot Password (UI Only)
        JLabel lblForgot = new JLabel("Having trouble logging in?");
        lblForgot.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblForgot.setForeground(UIConstants.ACCENT_BLUE);
        lblForgot.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblForgot.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblForgot.setBorder(new EmptyBorder(15, 0, 0, 0));
        inputs.add(lblForgot);

        card.add(inputs);

        // 🚀 Login Button
        btnLogin = new RoundedButton("SECURE LOGIN", UIConstants.ACCENT_BLUE, new Color(0, 80, 180));
        btnLogin.setMaximumSize(new Dimension(320, 55));
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.addActionListener(e -> handleLogin());

        card.add(Box.createVerticalStrut(40));
        card.add(btnLogin);

        // 🏥 Footer Health Indicator
        JLabel lblHealth = new JLabel(com.shms.db.DBConnection.checkStatus() ? "📗 System Online | Database Connected" : "📕 System Alert: Database Unreachable");
        lblHealth.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblHealth.setForeground(new Color(180, 180, 180));
        lblHealth.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblHealth.setBorder(new EmptyBorder(25, 0, 30, 0));
        card.add(lblHealth);

        // Explicit GridBag Centering
        background.add(card, new GridBagConstraints());
    }

    private JLabel createLabel(String txt) {
        JLabel l = new JLabel(txt);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(new Color(127, 140, 141));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            com.shms.ui.components.Toast.showError(this, "Empty Fields: Please enter your credentials.");
            return;
        }

        User user = userService.login(username, password);
        if (user != null) {
            this.dispose();
            new MainDashboardUI(user).setVisible(true);
        } else {
            com.shms.ui.components.Toast.showError(this, "Access Denied: Invalid credentials provided.");
        }
    }

    public static void main(String[] args) {
        GlobalUIManager.setup();
        SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
    }
}
