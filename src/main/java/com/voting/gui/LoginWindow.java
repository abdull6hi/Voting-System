package com.voting.gui;

import com.voting.Main;
import com.voting.exception.AuthException;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class LoginWindow extends JFrame {
    public LoginWindow(boolean isAdmin) {
        setTitle(isAdmin ? "Admin Login" : "User Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(new Color(240, 240, 240));

        // Main panel
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel(isAdmin ? "ADMIN LOGIN" : "USER LOGIN");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(new Color(70, 130, 180));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);

        // Username field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        JTextField usernameField = new JTextField(15);
        usernameField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(usernameField, gbc);

        // Password field
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(passwordField, gbc);

        // Login button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JButton loginBtn = new JButton("LOGIN");
        loginBtn.setBackground(new Color(70, 130, 180));
        loginBtn.setForeground(Color.BLACK);
        loginBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        loginBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        loginBtn.setFocusPainted(false);
        panel.add(loginBtn, gbc);

        // Login action
        loginBtn.addActionListener(e -> {
            char[] passwordChars = passwordField.getPassword();
            try {
                String username = usernameField.getText().trim();
                String password = new String(passwordChars);

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Please enter both username and password",
                            "Login Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Main.getAuthService().login(username, password);

                if (isAdmin) {
                    new AdminWindow().setVisible(true);
                } else {
                    new VotingWindow(username).setVisible(true);
                }
                dispose();
            } catch (AuthException ex) {
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(),
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
            } finally {
                Arrays.fill(passwordChars, '\0');
            }
        });

        add(panel);
    }

    public static void display(boolean isAdmin) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            LoginWindow window = new LoginWindow(isAdmin);
            window.setVisible(true);
        });
    }
}