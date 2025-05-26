package frontend;

import backend.User;

import javax.swing.*;
import java.awt.*;

public class HomePage extends JFrame {
    private final User currentUser;

    public HomePage(User user) {
        this.currentUser = user;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("University Voting System - Home");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Welcome label
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getName() + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        add(welcomeLabel, BorderLayout.NORTH);

        // Main buttons panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(0, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Vote button
        JButton voteButton = new JButton("Cast Vote");
        styleButton(voteButton);
        voteButton.addActionListener(e -> {
            new VotingPage(currentUser).setVisible(true);
            dispose();
        });
        mainPanel.add(voteButton);

        // View results button
        JButton resultsButton = new JButton("View Election Results");
        styleButton(resultsButton);
        resultsButton.addActionListener(e -> {
            new VoteCountPage(currentUser).setVisible(true);
            dispose();
        });
        mainPanel.add(resultsButton);

        // Admin button (only visible if user is admin)
        if (currentUser.isAdmin()) {
            JButton adminButton = new JButton("Admin Panel");
            styleButton(adminButton);
            adminButton.addActionListener(e -> {
                new AdminPage(currentUser).setVisible(true);
                dispose();
            });
            mainPanel.add(adminButton);
        }

        // Logout button
        JButton logoutButton = new JButton("Logout");
        styleButton(logoutButton);
        logoutButton.addActionListener(e -> {
            new LoginPage().setVisible(true);
            dispose();
        });
        mainPanel.add(logoutButton);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.PLAIN, 16));
        button.setBackground(new Color(100, 149, 237));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
    }
}
