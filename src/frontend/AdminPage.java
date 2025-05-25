package frontend;

import backend.DBUtil;
import backend.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminPage extends JFrame {
    private final User currentUser;
    private JTable studentTable, candidateTable;
    private DefaultTableModel studentModel, candidateModel;

    public AdminPage(User admin) {
        this.currentUser = admin;
        initializeUI();
        loadTables();
    }

    private void initializeUI() {
        setTitle("Admin Dashboard - University Voting System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("Admin Dashboard - " + currentUser.getName());
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JButton backButton = new JButton("← Back to Home");
        backButton.addActionListener(e -> returnToHome());
        styleButton(backButton);
        headerPanel.add(backButton, BorderLayout.WEST);

        // Table setup
        studentModel = new DefaultTableModel(
                new Object[]{"Admission No", "Full Name", "Email"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        candidateModel = new DefaultTableModel(
                new Object[]{"Admission No", "Full Name", "Email"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        studentTable = createTable(studentModel);
        candidateTable = createTable(candidateModel);

        // Table panels with titles
        JPanel studentPanel = createTablePanel("Students", studentTable);
        JPanel candidatePanel = createTablePanel("Candidates", candidateTable);

        // Action buttons
        JButton promoteButton = new JButton("Promote to Candidate →");
        JButton demoteButton = new JButton("← Demote to Student");
        styleButton(promoteButton);
        styleButton(demoteButton);

        promoteButton.addActionListener(e -> changeRole("candidate", studentTable));
        demoteButton.addActionListener(e -> changeRole("student", candidateTable));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        buttonPanel.add(promoteButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonPanel.add(demoteButton);

        // Main content panel
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.add(studentPanel);
        contentPanel.add(candidatePanel);

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JTable createTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.setRowHeight(25);
        return table;
    }

    private JPanel createTablePanel(String title, JTable table) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(450, 550));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadTables() {
        studentModel.setRowCount(0);
        candidateModel.setRowCount(0);

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT admission_number, full_name, email, role FROM tbl_users WHERE role IN ('student', 'candidate')";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                        rs.getString("admission_number"),
                        rs.getString("full_name"),
                        rs.getString("email")
                };

                if (rs.getString("role").equals("student")) {
                    studentModel.addRow(row);
                } else {
                    candidateModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading user data: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void changeRole(String newRole, JTable sourceTable) {
        int selectedRow = sourceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a user first",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String admissionNumber = (String) sourceTable.getValueAt(selectedRow, 0);
        String currentRole = newRole.equals("candidate") ? "student" : "candidate";
        String action = newRole.equals("candidate") ? "Promote" : "Demote";

        int confirm = JOptionPane.showConfirmDialog(
                this,
                String.format("%s %s to %s?", action,
                        sourceTable.getValueAt(selectedRow, 1), newRole),
                "Confirm Role Change",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBUtil.getConnection()) {
                String sql = "UPDATE tbl_users SET role = ? WHERE admission_number = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, newRole);
                stmt.setString(2, admissionNumber);

                if (stmt.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Role updated successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadTables();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Error updating role: " + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
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