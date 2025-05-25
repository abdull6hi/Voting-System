package frontend;

import backend.UserDAO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SignupPage extends JDialog {
    private JTextField nameField;
    private JTextField admissionField;
    private JPasswordField passwordField;

    public SignupPage(JFrame parent) {
        super(parent, "University Voting System - Sign Up", true);
        setSize(450, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        // Main panel
        JPanel mainPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Components
        JLabel nameLabel = new JLabel("Full Name:");
        nameField = new JTextField();
        JLabel admissionLabel = new JLabel("Admission Number:");
        admissionField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        JLabel confirmLabel = new JLabel("Confirm Password:");
        JPasswordField confirmField = new JPasswordField();

        mainPanel.add(nameLabel);
        mainPanel.add(nameField);
        mainPanel.add(admissionLabel);
        mainPanel.add(admissionField);
        mainPanel.add(passwordLabel);
        mainPanel.add(passwordField);
        mainPanel.add(confirmLabel);
        mainPanel.add(confirmField);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton signupButton = new JButton("Sign Up");
        JButton loginButton = new JButton("Already Registered? Login");

        // Styling
        signupButton.setBackground(new Color(59, 89, 182));
        signupButton.setForeground(Color.WHITE);
        signupButton.setFocusPainted(false);

        buttonPanel.add(signupButton);
        buttonPanel.add(loginButton);

        // Add components to dialog
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Event handlers
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fullName = nameField.getText().trim();
                String admission = admissionField.getText().trim();
                String password = new String(passwordField.getPassword());
                String confirm = new String(confirmField.getPassword());

                // Validation
                if (fullName.isEmpty() || admission.isEmpty() || password.isEmpty()) {
                    showError("All fields are required");
                    return;
                }

                if (!password.equals(confirm)) {
                    showError("Passwords do not match");
                    return;
                }

                if (password.length() < 6) {
                    showError("Password must be at least 6 characters");
                    return;
                }

                if (UserDAO.registerUser(fullName, admission, password)) {
                    JOptionPane.showMessageDialog(SignupPage.this,
                            "Registration successful!\nYou can now login.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    new LoginPage(parent).setVisible(true);
                } else {
                    showError("Admission number already exists");
                }
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginPage(parent).setVisible(true);
            }
        });

        // Enter key listener
        getRootPane().setDefaultButton(signupButton);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Registration Error",
                JOptionPane.ERROR_MESSAGE);
    }
}