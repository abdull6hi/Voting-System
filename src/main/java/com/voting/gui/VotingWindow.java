package com.voting.gui;

import com.voting.Main;
import com.voting.model.Candidate;
import com.voting.exception.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class VotingWindow extends JFrame {
    private final String currentUserId;
    private JTable candidatesTable;

    public VotingWindow(String userId) {
        this.currentUserId = userId;
        setTitle("Voting Dashboard - " + userId);
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(245, 245, 245));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(new Color(70, 130, 180));
        tabs.setForeground(Color.BLACK);
        tabs.setFont(new Font("SansSerif", Font.BOLD, 14));

        tabs.addTab("View Candidates", createCandidatePanel());
        tabs.addTab("Cast Vote", createVotingPanel());

        add(tabs);
    }

    private JPanel createCandidatePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 248, 255));

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Name", "Bio"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        candidatesTable = new JTable(model);
        candidatesTable.setRowHeight(30);
        candidatesTable.setSelectionBackground(new Color(220, 240, 255));
        candidatesTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        candidatesTable.setForeground(Color.BLACK);
        candidatesTable.setBackground(Color.WHITE);
        candidatesTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        candidatesTable.getTableHeader().setBackground(new Color(70, 130, 180));
        candidatesTable.getTableHeader().setForeground(Color.WHITE);
        candidatesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(candidatesTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Available Candidates"));

        JButton refreshBtn = createStyledButton("Refresh List", new Color(100, 149, 237));
        refreshBtn.addActionListener(e -> loadCandidates());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(240, 248, 255));
        buttonPanel.add(refreshBtn);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        loadCandidates(); // Initial load

        return panel;
    }

    private JPanel createVotingPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(240, 248, 255));

        DefaultComboBoxModel<Candidate> comboModel = new DefaultComboBoxModel<>();
        JComboBox<Candidate> candidateCombo = new JComboBox<>(comboModel);
        candidateCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Candidate) {
                    setText(((Candidate) value).getName());
                }
                return this;
            }
        });
        loadCandidateCombo(comboModel);
//
        JButton voteBtn = createStyledButton("Submit Vote", new Color(50, 205, 50));
        voteBtn.addActionListener(e -> castVote(candidateCombo));

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        centerPanel.setBackground(new Color(240, 248, 255));
        centerPanel.add(new JLabel("Select candidate:"));
        centerPanel.add(candidateCombo);

        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(voteBtn, BorderLayout.SOUTH);

        return panel;
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(darkenColor(bgColor, 0.8f));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }

    private Color darkenColor(Color color, float factor) {
        return new Color(
                Math.max((int)(color.getRed() * factor), 0),
                Math.max((int)(color.getGreen() * factor), 0),
                Math.max((int)(color.getBlue() * factor), 0)
        );
    }

    private void loadCandidates() {
        DefaultTableModel model = (DefaultTableModel) candidatesTable.getModel();
        model.setRowCount(0);
        try {
            Main.getDbManager().executeQuery(
                    "SELECT id, name, bio FROM candidates",
                    rs -> {
                        while (rs.next()) {
                            model.addRow(new Object[]{
                                    rs.getString("id"),
                                    rs.getString("name"),
                                    rs.getString("bio")
                            });
                        }
                    }
            );
        } catch (SQLException ex) {
            showError("Failed to load candidates", ex.getMessage());
        }
    }

    private void loadCandidateCombo(DefaultComboBoxModel<Candidate> model) {
        model.removeAllElements();
        try {
            Main.getDbManager().executeQuery(
                    "SELECT id, name, bio FROM candidates",
                    rs -> {
                        while (rs.next()) {
                            model.addElement(new Candidate(
                                    rs.getString("id"),
                                    rs.getString("name"),
                                    rs.getString("bio"),
                                    0
                            ));
                        }
                    }
            );
        } catch (SQLException ex) {
            showError("Failed to load candidates", ex.getMessage());
        }
    }

    private void castVote(JComboBox<Candidate> combo) {
        Candidate selected = (Candidate) combo.getSelectedItem();
        if (selected == null) {
            showError("Please select a candidate first");
            return;
        }

        try {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Vote for " + selected.getName() + "?",
                    "Confirm Vote",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                Main.getVotingService().castVote("1", currentUserId, selected.getId());
                showSuccess("Vote recorded for " + selected.getName());
                combo.setSelectedIndex(-1);
            }
        } catch (CandidateNotFoundException e) {
            showError("Invalid Candidate", e.getMessage());
            loadCandidateCombo((DefaultComboBoxModel<Candidate>) combo.getModel());
        } catch (ElectionClosedException | AlreadyVotedException e) {
            showError("Voting Error", e.getMessage());
        } catch (SQLException e) {
            showError("Database Error", "Please try again later");
        }
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

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

    private void showError(String title, String message) {
        JOptionPane.showMessageDialog(this,
                message,
                title,
                JOptionPane.ERROR_MESSAGE);
    }

    private void showError(String message) {
        showError("Error", message);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }
}