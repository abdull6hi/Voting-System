package frontend;

import backend.UserDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SignupPage extends JFrame {
    private JTextField nameField;
    private JTextField admissionField;
    private JTextField emailField;
    private JPasswordField passwordField;

    public SignupPage() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("University Voting System - Sign Up");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("Full Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Admission Number:"));
        admissionField = new JTextField();
        formPanel.add(admissionField);

        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        formPanel.add(emailField);

        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton signupButton = new JButton("Sign Up");
        JButton backButton = new JButton("Back to Login");

        signupButton.addActionListener(this::handleSignUp);
        backButton.addActionListener(e -> {
            new LoginPage().setVisible(true);
            dispose();
        });

        buttonPanel.add(signupButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void handleSignUp(ActionEvent e) {
        String name = nameField.getText().trim();
        String admission = admissionField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (name.isEmpty() || admission.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // No SQLException needs to be caught here because registerUser() handles it internally
        boolean success = UserDAO.registerUser(name, admission, password);
        if (success) {
            JOptionPane.showMessageDialog(this, "Registration successful! Please login.", "Success", JOptionPane.INFORMATION_MESSAGE);
            new LoginPage().setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed. Admission number or email might already be in use.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
