package com.shms.ui;

import com.shms.dao.UserDAO;
import com.shms.model.User;
import com.shms.ui.components.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginUI extends JFrame {
    
    private RoundedTextField txtUsername;
    private RoundedPasswordField txtPassword;
    private RoundedButton btnLogin;
    private UserDAO userDAO;

    public LoginUI() {
        this.userDAO = new UserDAO();
        initializeModernUI();
    }

    private void initializeModernUI() {
        setTitle("SHMS | Secure Login");
        setSize(450, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UIConstants.BACKGROUND_LIGHT);
        setLayout(new GridBagLayout());

        // --- POLISHED LOGIN CARD ---
        RoundedPanel card = new RoundedPanel(30, Color.WHITE);
        card.setPreferredSize(new Dimension(380, 520));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        // Header
        JLabel lblIcon = new JLabel("⚕", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI", Font.BOLD, 48));
        lblIcon.setForeground(UIConstants.ACCENT_BLUE);
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblIcon.setBorder(new EmptyBorder(40, 0, 10, 0));
        card.add(lblIcon);

        JLabel lblTitle = new JLabel("Welcome Back");
        lblTitle.setFont(UIConstants.FONT_HEADER);
        lblTitle.setForeground(UIConstants.TEXT_PRIMARY);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblTitle);

        JLabel lblSub = new JLabel("Enter credentials to access SHMS");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(UIConstants.TEXT_SECONDARY);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSub.setBorder(new EmptyBorder(0, 0, 40, 0));
        card.add(lblSub);

        // Inputs Container
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setOpaque(false);
        inputPanel.setBorder(new EmptyBorder(0, 30, 0, 30));

        txtUsername = new RoundedTextField(20);
        txtUsername.setMaximumSize(new Dimension(320, 45));
        txtUsername.setPlaceholder("e.g. admin or dr.silva");
        txtUsername.addActionListener(e -> handleLogin());
        
        txtPassword = new RoundedPasswordField();
        txtPassword.setMaximumSize(new Dimension(320, 45));
        txtPassword.setPlaceholder("Enter your secure password");
        txtPassword.addActionListener(e -> handleLogin());

        inputPanel.add(new JLabel("Username"));
        inputPanel.add(Box.createVerticalStrut(5));
        inputPanel.add(txtUsername);
        inputPanel.add(Box.createVerticalStrut(20));
        inputPanel.add(new JLabel("Password"));
        inputPanel.add(Box.createVerticalStrut(5));
        inputPanel.add(txtPassword);
        card.add(inputPanel);

        // Action Button
        btnLogin = new RoundedButton("Login to System", UIConstants.ACCENT_BLUE, new Color(0, 90, 195));
        btnLogin.setMaximumSize(new Dimension(320, 50));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.addActionListener(e -> handleLogin());
        
        card.add(Box.createVerticalStrut(40));
        card.add(btnLogin);

        // Health Heartbeat
        JLabel lblHealth = new JLabel(com.shms.db.DBConnection.checkStatus() ? "📗 System Health: Optimal" : "📕 System Health: Database Offline");
        lblHealth.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblHealth.setForeground(new Color(180, 180, 180));
        lblHealth.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblHealth.setBorder(new EmptyBorder(20, 0, 0, 0));
        card.add(lblHealth);

        add(card);
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            com.shms.ui.components.Toast.showError(this, "Please enter both username and password.");
            return;
        }

        User user = userDAO.authenticate(username, password);
        if (user != null) {
            this.dispose();
            new MainDashboardUI(user).setVisible(true);
        } else {
            com.shms.ui.components.Toast.showError(this, "Invalid credentials. Access Denied.");
        }
    }

    public static void main(String[] args) {
        GlobalUIManager.setup();
        SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
    }
}
