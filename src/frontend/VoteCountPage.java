package frontend;

import backend.DBUtil;
import backend.User;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class VoteCountPage extends JFrame {
    private final User currentUser;

    public VoteCountPage(User user) {
        this.currentUser = user;
        initializeUI();
        displayResults();
    }

    private void initializeUI() {
        setTitle("Election Results - University Voting System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("Election Results");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JButton backButton = new JButton("← Back to Home");
        backButton.addActionListener(e -> returnToHome());
        styleButton(backButton);
        headerPanel.add(backButton, BorderLayout.WEST);

        // Results panel
        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        resultsPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void displayResults() {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT u.full_name, COUNT(v.id) AS votes " +
                    "FROM tbl_users u LEFT JOIN tbl_votes v ON u.admission_number = v.candidate_admission " +
                    "WHERE u.role = 'candidate' " +
                    "GROUP BY u.admission_number " +
                    "ORDER BY votes DESC";

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            JPanel resultsPanel = (JPanel) ((JScrollPane) getContentPane().getComponent(1)).getViewport().getView();
            resultsPanel.removeAll();

            if (!rs.isBeforeFirst()) {
                JLabel noResults = new JLabel("No voting results available yet");
                noResults.setFont(new Font("SansSerif", Font.ITALIC, 16));
                noResults.setAlignmentX(Component.CENTER_ALIGNMENT);
                resultsPanel.add(noResults);
            } else {
                while (rs.next()) {
                    resultsPanel.add(createCandidateResultPanel(
                            rs.getString("full_name"),
                            rs.getInt("votes")
                    ));
                    resultsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                }
            }

            resultsPanel.revalidate();
            resultsPanel.repaint();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading results: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createCandidateResultPanel(String name, int votes) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setMaximumSize(new Dimension(700, 60));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        panel.setBackground(new Color(240, 248, 255));

        // Candidate name
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        // Vote count
        JLabel votesLabel = new JLabel(votes + " vote" + (votes != 1 ? "s" : ""));
        votesLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        votesLabel.setForeground(new Color(70, 130, 180));

        // Percentage bar
        int maxWidth = 500;
        int percentage = getPercentage(votes);
        JPanel barPanel = new JPanel();
        barPanel.setLayout(new BoxLayout(barPanel, BoxLayout.X_AXIS));
        barPanel.setMaximumSize(new Dimension(maxWidth, 15));

        JPanel filledBar = new JPanel();
        filledBar.setBackground(new Color(100, 149, 237));
        filledBar.setPreferredSize(new Dimension((maxWidth * percentage) / 100, 15));

        JPanel emptyBar = new JPanel();
        emptyBar.setBackground(new Color(220, 220, 220));
        emptyBar.setPreferredSize(new Dimension((maxWidth * (100 - percentage)) / 100, 15));

        barPanel.add(filledBar);
        barPanel.add(emptyBar);

        // Add components
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(new Color(240, 248, 255));
        leftPanel.add(nameLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        leftPanel.add(barPanel);

        panel.add(leftPanel, BorderLayout.CENTER);
        panel.add(votesLabel, BorderLayout.EAST);

        return panel;
    }

    private int getPercentage(int votes) {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT COUNT(*) as total FROM tbl_votes";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int totalVotes = rs.getInt("total");
                return totalVotes > 0 ? (votes * 100) / totalVotes : 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
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