package com.voting.gui;

import com.voting.Main;
import com.voting.exception.AuthException;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Arrays;

public class RegisterWindow extends JFrame {

    public RegisterWindow() {
        setTitle("Voter Registration");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(new Color(240, 240, 240)); // Light gray background

        // Main panel with GridBagLayout
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(240, 240, 240)); // Match window background
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title Label - Now with better contrast
        JLabel titleLabel = new JLabel("VOTER REGISTRATION");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLACK); // Changed to black for better visibility
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);

        // Student ID Field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        JLabel idLabel = new JLabel("Student ID:");
        idLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        idLabel.setForeground(Color.BLACK); // Explicit black text
        panel.add(idLabel, gbc);

        gbc.gridx = 1;
        JTextField idField = new JTextField();
        idField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        idField.setForeground(Color.BLACK); // Black text
        idField.setBackground(Color.WHITE); // White background
        idField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        panel.add(idField, gbc);

        // Full Name Field
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        nameLabel.setForeground(Color.BLACK);
        panel.add(nameLabel, gbc);

        gbc.gridx = 1;
        JTextField nameField = new JTextField();
        nameField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        nameField.setForeground(Color.BLACK);
        nameField.setBackground(Color.WHITE);
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        panel.add(nameField, gbc);

        // Password Field
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        passLabel.setForeground(Color.BLACK);
        panel.add(passLabel, gbc);

        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passwordField.setForeground(Color.BLACK);
        passwordField.setBackground(Color.WHITE);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        panel.add(passwordField, gbc);

        // Confirm Password Field
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel confirmPassLabel = new JLabel("Confirm Password:");
        confirmPassLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        confirmPassLabel.setForeground(Color.BLACK);
        panel.add(confirmPassLabel, gbc);

        gbc.gridx = 1;
        JPasswordField confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        confirmPasswordField.setForeground(Color.BLACK);
        confirmPasswordField.setBackground(Color.WHITE);
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        panel.add(confirmPasswordField, gbc);

        // Register Button
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        JButton registerBtn = new JButton("REGISTER");
        registerBtn.setBackground(new Color(70, 130, 180)); // Dark blue background
        registerBtn.setForeground(Color.BLACK); // Black text
        registerBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        registerBtn.setFocusPainted(false);
        registerBtn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        registerBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hover effect
        registerBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                registerBtn.setBackground(new Color(50, 100, 150)); // Darker blue on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                registerBtn.setBackground(new Color(70, 130, 180));
            }
        });

        panel.add(registerBtn, gbc);

        // Register Button Action
        registerBtn.addActionListener(e -> {
            char[] password = passwordField.getPassword();
            char[] confirmPassword = confirmPasswordField.getPassword();

            try {
                String studentId = idField.getText().trim();
                String name = nameField.getText().trim();

                // Validate inputs
                if (studentId.isEmpty() || name.isEmpty() || password.length == 0) {
                    JOptionPane.showMessageDialog(this,
                            "All fields are required",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!Arrays.equals(password, confirmPassword)) {
                    JOptionPane.showMessageDialog(this,
                            "Passwords do not match",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Register student
                Main.getAuthService().registerStudent(
                        studentId,
                        name,
                        new String(password)
                );

                JOptionPane.showMessageDialog(this,
                        "Registration successful!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();

            } catch (AuthException ex) {
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(),
                        "Registration Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Database error. Please try again later.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } finally {
                // Clear sensitive data from memory
                Arrays.fill(password, '\0');
                Arrays.fill(confirmPassword, '\0');
                passwordField.setText("");
                confirmPasswordField.setText("");
            }
        });

        add(panel);
    }

    public static void display() {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            RegisterWindow window = new RegisterWindow();
            window.setVisible(true);
        });
    }
}