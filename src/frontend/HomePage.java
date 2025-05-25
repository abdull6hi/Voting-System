package frontend;

import backend.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HomePage extends JFrame {
    private User currentUser;

    public HomePage(User user) {
        this.currentUser = user;
        initializeUI();
    }

    public HomePage() {
        this(null);
    }

    private void initializeUI() {
        setTitle("University Voting System");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top panel with navigation
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Center welcome message
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBackground(new Color(70, 130, 180));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (currentUser == null) {
            panel.add(createStyledButton("Login", this::openLogin));
            panel.add(createStyledButton("Sign Up", this::openSignup));
        } else {
            JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getName() + " (" + currentUser.getRole() + ")");
            welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            welcomeLabel.setForeground(Color.WHITE);
            panel.add(welcomeLabel);

            panel.add(createStyledButton("Vote", this::openVoting));
            panel.add(createStyledButton("View Results", this::openResults));

            if (currentUser.isAdmin()) {
                panel.add(createStyledButton("Admin Panel", this::openAdmin));
            }

            panel.add(createStyledButton("Logout", this::logout));
        }
        return panel;
    }

    private JButton createStyledButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setBackground(new Color(100, 149, 237));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.addActionListener(action);
        return button;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        JLabel welcomeLabel = new JLabel("Welcome to University Voting System");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomeLabel.setForeground(new Color(70, 130, 180));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        panel.add(welcomeLabel, gbc);

        return panel;
    }

    private void openLogin(ActionEvent e) {
        dispose();
        new LoginPage(null).setVisible(true);
    }

    private void openSignup(ActionEvent e) {
        dispose();
        new SignupPage(null).setVisible(true);
    }

    private void openVoting(ActionEvent e) {
        if (currentUser == null) {
            showLoginPrompt();
            return;
        }

        if (currentUser.isCandidate()) {
            JOptionPane.showMessageDialog(this,
                    "Candidates cannot vote",
                    "Restricted",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        dispose();
        new VotingPage(currentUser).setVisible(true);
    }

    private void openResults(ActionEvent e) {
        if (currentUser == null) {
            showLoginPrompt();
            return;
        }

        dispose();
        new VoteCountPage(currentUser).setVisible(true);
    }

    private void openAdmin(ActionEvent e) {
        if (currentUser != null && currentUser.isAdmin()) {
            dispose();
            new AdminPage(currentUser).setVisible(true);
        }
    }

    private void logout(ActionEvent e) {
        dispose();
        new HomePage().setVisible(true);
    }

    private void showLoginPrompt() {
        JOptionPane.showMessageDialog(this,
                "Please login to access this feature",
                "Authentication Required",
                JOptionPane.INFORMATION_MESSAGE);
    }
}