package frontend;

import backend.User;
import backend.VoteDAO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

public class VotingPage extends JFrame {
    private final User currentUser;
    private JPanel candidatePanel;

    public VotingPage(User user) {
        this.currentUser = user;
        initializeUI();
        loadCandidates();
    }

    private void initializeUI() {
        setTitle("Voting - University Election");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("Cast Your Vote");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JButton backButton = new JButton("← Back to Home");
        backButton.addActionListener(e -> returnToHome());
        styleButton(backButton);
        headerPanel.add(backButton, BorderLayout.WEST);

        // Candidate panel
        candidatePanel = new JPanel();
        candidatePanel.setLayout(new BoxLayout(candidatePanel, BoxLayout.Y_AXIS));
        candidatePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JScrollPane scrollPane = new JScrollPane(candidatePanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadCandidates() {
        candidatePanel.removeAll();

        try {
            // Get candidates from database
            var candidates = backend.UserDAO.getCandidates();

            if (candidates.isEmpty()) {
                JLabel noCandidates = new JLabel("No candidates available");
                noCandidates.setFont(new Font("SansSerif", Font.ITALIC, 16));
                noCandidates.setAlignmentX(Component.CENTER_ALIGNMENT);
                candidatePanel.add(noCandidates);
            } else {
                for (var candidate : candidates) {
                    addCandidateButton(candidate.getName(), candidate.getAdmissionNumber());
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading candidates: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        candidatePanel.revalidate();
        candidatePanel.repaint();
    }

    private void addCandidateButton(String name, int admission) {
        JButton voteButton = new JButton(name);
        voteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        voteButton.setMaximumSize(new Dimension(400, 50));
        styleButton(voteButton);

        // Disable if already voted or is candidate
        try {
            if (currentUser.isCandidate()) {
                voteButton.setText(name + " (Candidates cannot vote)");
                voteButton.setEnabled(false);
            } else if (VoteDAO.hasVoted(currentUser.getAdmissionNumber())) {
                voteButton.setText(name + " (Already Voted)");
                voteButton.setEnabled(false);
            } else {
                voteButton.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(
                            this,
                            "Confirm vote for " + name + "?",
                            "Confirm Vote",
                            JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            VoteDAO.castVote(currentUser.getAdmissionNumber(), admission);
                            voteButton.setText(name + " (Voted)");
                            voteButton.setEnabled(false);
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(this,
                                    "Failed to cast vote: " + ex.getMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
            }
        } catch (SQLException e) {
            voteButton.setEnabled(false);
            voteButton.setText("Error checking vote status");
        }

        candidatePanel.add(voteButton);
        candidatePanel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private void returnToHome() {
        new HomePage(currentUser).setVisible(true);
        dispose();
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setBackground(new Color(100, 149, 237));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    }
}