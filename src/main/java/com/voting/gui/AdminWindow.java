package com.voting.gui;


import com.voting.Main;
import com.voting.exception.VotingException;
import com.voting.model.Candidate;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

public class AdminWindow extends JFrame {
    private final JTextArea logArea = new JTextArea(10, 50);
    private JLabel statusLabel;
    private JTable candidatesTable;
    private DefaultTableModel candidatesModel;

    public AdminWindow() {
        setTitle("Admin Dashboard");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(245, 245, 245));

        // Configure log area
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(new Color(70, 130, 180));
        tabs.setForeground(Color.BLACK);
        tabs.setFont(new Font("SansSerif", Font.BOLD, 14));

        tabs.addTab("Election Control", createElectionPanel());
        tabs.addTab("Candidate Management", createCandidatePanel());
        tabs.addTab("System Log", createLogPanel());

        add(tabs);
        refreshElectionStatus();
        loadCandidates();
    }

    private JPanel createElectionPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        panel.setBackground(new Color(240, 248, 255));

        // Status Panel
        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(new Color(173, 216, 230));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        statusLabel = new JLabel("Checking election status...");
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        statusPanel.add(statusLabel);

        // Control Buttons
        JButton openBtn = createStyledButton("Open Election", new Color(50, 205, 50));
        JButton closeBtn = createStyledButton("Close Election", new Color(220, 20, 60));
        JButton resetBtn = createStyledButton("Reset All Data", new Color(255, 165, 0));

        openBtn.addActionListener(e -> handleOpenElection());
        closeBtn.addActionListener(e -> handleCloseElection());
        resetBtn.addActionListener(e -> handleResetElection());

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 15, 15));
        buttonPanel.setBackground(new Color(240, 248, 255));
        buttonPanel.add(openBtn);
        buttonPanel.add(closeBtn);
        buttonPanel.add(resetBtn);

        panel.add(statusPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCandidatePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 248, 255));

        // Table setup
        candidatesModel = new DefaultTableModel(
                new Object[]{"ID", "Name", "Bio", "Votes", "Action"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only action column is editable
            }
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 4 ? JButton.class : Object.class;
            }
        };

        candidatesTable = new JTable(candidatesModel);
        candidatesTable.setRowHeight(35);
        candidatesTable.setSelectionBackground(new Color(220, 240, 255));
        candidatesTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        candidatesTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        candidatesTable.getTableHeader().setBackground(new Color(70, 130, 180));
        candidatesTable.getTableHeader().setForeground(Color.BLACK);

        // Configure action column
        candidatesTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        candidatesTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(candidatesTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Current Candidates"));
        scrollPane.setBackground(new Color(240, 248, 255));

        // Add Candidate Form
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Add New Candidate"));
        formPanel.setBackground(new Color(240, 248, 255));

        JTextField idField = createStyledTextField();
        JTextField nameField = createStyledTextField();
        JTextField bioField = createStyledTextField();
        JButton addBtn = createStyledButton("Add Candidate", new Color(70, 130, 180));

        formPanel.add(createFormLabel("Candidate ID:"));
        formPanel.add(idField);
        formPanel.add(createFormLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(createFormLabel("Bio:"));
        formPanel.add(bioField);
        formPanel.add(new JLabel());
        formPanel.add(addBtn);

        addBtn.addActionListener(e -> {
            try {
                Candidate candidate = new Candidate(
                        idField.getText().trim(),
                        nameField.getText().trim(),
                        bioField.getText().trim(),
                        0
                );
                Main.getAdminService().addCandidate(candidate);
                candidatesModel.addRow(new Object[]{
                        candidate.getId(),
                        candidate.getName(),
                        candidate.getBio(),
                        0,
                        "Remove"
                });
                log("Added candidate: " + candidate.getName());
                clearFields(idField, nameField, bioField);
            } catch (Exception ex) {
                showError("Failed to add candidate", ex);
            }
        });

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(formPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(240, 248, 255));

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("System Log"));

        JButton clearBtn = createStyledButton("Clear Log", new Color(100, 100, 100));
        clearBtn.addActionListener(e -> logArea.setText(""));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(240, 248, 255));
        buttonPanel.add(clearBtn);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Helper methods
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return field;
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        return label;
    }

    private void clearFields(JTextField... fields) {
        for (JTextField field : fields) {
            field.setText("");
        }
    }

    // Button Renderer for the Action column
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setBackground(new Color(220, 20, 60));
            setForeground(Color.BLACK);
            setFont(new Font("SansSerif", Font.BOLD, 12));
            setText("Remove");
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    // Button Editor for the Action column
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private int currentRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.setBackground(new Color(220, 20, 60));
            button.setForeground(Color.BLACK);
            button.setFont(new Font("SansSerif", Font.BOLD, 12));
            button.setText("Remove");
            button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            button.addActionListener(e -> {
                fireEditingStopped();
                int confirm = JOptionPane.showConfirmDialog(
                        AdminWindow.this,
                        "Are you sure you want to remove this candidate?",
                        "Confirm Removal",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        String candidateId = (String) candidatesModel.getValueAt(currentRow, 0);
                        Main.getAdminService().removeCandidate(candidateId);
                        candidatesModel.removeRow(currentRow);
                        log("Removed candidate: " + candidateId);
                    } catch (Exception ex) {
                        showError("Failed to remove candidate", ex);
                    }
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            currentRow = row;
            return button;
        }
    }

    // Existing methods (handleOpenElection, handleCloseElection, etc.)
    private void handleOpenElection() {
        try {
            Main.getAdminService().openElection();
            log("Election opened successfully");
            refreshElectionStatus();
        } catch (Exception ex) {
            showError("Failed to open election", ex);
        }
    }

    private void handleCloseElection() {
        try {
            Main.getAdminService().closeElection();
            log("Election closed successfully");
            refreshElectionStatus();
        } catch (Exception ex) {
            showError("Failed to close election", ex);
        }
    }

    private void handleResetElection() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "This will reset ALL voting data. Continue?",
                "Confirm Reset",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Main.getAdminService().resetAllVotes();
                log("All voting data has been reset");
                refreshElectionStatus();
                loadCandidates(); // Refresh candidate list
            } catch (Exception ex) {
                showError("Failed to reset election", ex);
            }
        }
    }

    private void refreshElectionStatus() {
        try {
            boolean isOpen = Main.getAdminService().isElectionOpen();
            statusLabel.setText("Current Status: " + (isOpen ? "OPEN" : "CLOSED"));
            statusLabel.setForeground(isOpen ? new Color(0, 128, 0) : new Color(139, 0, 0));
        } catch (SQLException ex) {
            showError("Failed to check election status", ex);
        }
    }

    private void loadCandidates() {
        candidatesModel.setRowCount(0); // Clear existing data
        try {
            Main.getDbManager().executeQuery(
                    "SELECT id, name, bio, votes FROM candidates",
                    rs -> {
                        while (rs.next()) {
                            candidatesModel.addRow(new Object[]{
                                    rs.getString("id"),
                                    rs.getString("name"),
                                    rs.getString("bio"),
                                    rs.getInt("votes"),
                                    "Remove"
                            });
                        }
                    }
            );
        } catch (SQLException ex) {
            showError("Failed to load candidates", ex);
        }
    }

    private void log(String message) {
        logArea.append("[LOG] " + message + "\n");
    }

    private void showError(String message, Exception ex) {
        log("[ERROR] " + message + ": " + ex.getMessage());
        JOptionPane.showMessageDialog(this,
                message + "\n" + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void display() {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            AdminWindow window = new AdminWindow();
            window.setVisible(true);
        });
    }
}
