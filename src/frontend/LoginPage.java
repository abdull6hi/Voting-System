package frontend;

import backend.User;
import backend.UserDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPage extends JDialog {
    private JTextField admissionField;
    private JPasswordField passwordField;

    public LoginPage(JFrame parent) {
        super(parent, "University Voting System - Login", true);
        setSize(400, 250);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        // Main panel
        JPanel mainPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Components
        JLabel admissionLabel = new JLabel("Admission Number:");
        admissionField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();

        mainPanel.add(admissionLabel);
        mainPanel.add(admissionField);
        mainPanel.add(passwordLabel);
        mainPanel.add(passwordField);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton loginButton = new JButton("Login");
        JButton signupButton = new JButton("Sign Up");

        // Styling
        loginButton.setBackground(new Color(59, 89, 182));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);

        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);

        // Add components to dialog
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Event handlers
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String admission = admissionField.getText().trim();
                String password = new String(passwordField.getPassword());

                if (admission.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginPage.this,
                            "Please fill in all fields",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (UserDAO.loginUser(admission, password)) {
                    User user = User.getCurrentUser();
                    JOptionPane.showMessageDialog(LoginPage.this,
                            "Welcome, " + user.getName() + "!",
                            "Login Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    new HomePage(user).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(LoginPage.this,
                            "Invalid admission number or password",
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new SignupPage(parent).setVisible(true);
            }
        });

        // Enter key listener
        getRootPane().setDefaultButton(loginButton);
    }
}