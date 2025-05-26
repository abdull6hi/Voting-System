package frontend;

import backend.User;
import backend.UserDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class LoginPage extends JFrame {
    private JTextField admissionField;
    private JPasswordField passwordField;

    public LoginPage() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("University Voting System - Login");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel admissionLabel = new JLabel("Admission Number:");
        admissionField = new JTextField();

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();

        mainPanel.add(admissionLabel);
        mainPanel.add(admissionField);
        mainPanel.add(passwordLabel);
        mainPanel.add(passwordField);

        JButton loginButton = new JButton("Login");
        JButton signupButton = new JButton("Sign Up");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loginButton.setBackground(new Color(59, 89, 182));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);

        signupButton.setBackground(new Color(100, 149, 237));
        signupButton.setForeground(Color.WHITE);
        signupButton.setFocusPainted(false);

        // Login action
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String admission = admissionField.getText().trim();
                String password = new String(passwordField.getPassword());

                if (admission.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginPage.this,
                            "Please fill all fields",
                            "Login Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    User loggedUser = UserDAO.loginUser(admission, password);
                    if (loggedUser != null) {
                        // Login success
                        JOptionPane.showMessageDialog(LoginPage.this,
                                "Welcome, " + loggedUser.getName(),
                                "Login Successful",
                                JOptionPane.INFORMATION_MESSAGE);

                        // Open HomePage and close login
                        new HomePage(loggedUser).setVisible(true);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(LoginPage.this,
                                "Invalid admission number or password",
                                "Login Failed",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(LoginPage.this,
                            "Database error: " + ex.getMessage(),
                            "Login Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Open signup page - FIXED: Removed argument
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SignupPage().setVisible(true);
                dispose();
            }
        });

        // Allow Enter key to trigger login
        getRootPane().setDefaultButton(loginButton);
    }
}
