package com.voting.gui;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    public MainWindow() {
        setTitle("Voting System");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(240, 240, 240)); // Light gray background

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(new Color(240, 240, 240));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title Label
        JLabel titleLabel = new JLabel("Voting System");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);

        // User Login Button (Blue background, black text)
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        JButton userLoginBtn = new JButton("User Login");
        styleButton(userLoginBtn, new Color(100, 149, 237)); // Cornflower blue
        mainPanel.add(userLoginBtn, gbc);

        // Admin Login Button (Green background, black text)
        gbc.gridx = 1;
        JButton adminLoginBtn = new JButton("Admin Login");
        styleButton(adminLoginBtn, new Color(144, 238, 144)); // Light green
        mainPanel.add(adminLoginBtn, gbc);

        // Register Button (Orange background, black text)
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JButton registerBtn = new JButton("Register");
        styleButton(registerBtn, new Color(255, 165, 0)); // Orange
        mainPanel.add(registerBtn, gbc);

        // Button Actions
        userLoginBtn.addActionListener(e -> showLoginWindow(false));
        adminLoginBtn.addActionListener(e -> showLoginWindow(true));
        registerBtn.addActionListener(e -> showRegisterWindow());

        add(mainPanel);
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK); // Black text
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect - slightly darker background
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(darkenColor(bgColor, 0.8f));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }

    private Color darkenColor(Color color, float factor) {
        return new Color(
                Math.max((int)(color.getRed() * factor), 0),
                Math.max((int)(color.getGreen() * factor), 0),
                Math.max((int)(color.getBlue() * factor), 0)
        );
    }

    private void showLoginWindow(boolean isAdmin) {
        LoginWindow.display(isAdmin);
    }

    private void showRegisterWindow() {
        RegisterWindow.display();
    }

    public static void display() {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}