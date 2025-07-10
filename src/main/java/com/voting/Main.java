package com.voting;

import com.voting.dao.DatabaseManager;
import com.voting.gui.MainWindow;
import com.voting.service.AdminService;
import com.voting.service.AuthService;
import com.voting.service.VotingService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class Main {
    // Shared services
    private static DatabaseManager db;
    private static AuthService auth;
    private static VotingService voting;
    private static AdminService admin;


    public static void main(String[] args) {
        try {
            initializeServices();

            if (guiModeRequested(args)) {
                startGUI();
            } else {
                startConsole();
            }
        } catch (Exception e) {
            handleStartupError(e);
        }
    }

    private static void initializeServices() throws SQLException {
        db = DatabaseManager.getInstance();
        auth = new AuthService(db);
        voting = new VotingService(db);
        admin = new AdminService(db);
    }

    private static boolean guiModeRequested(String[] args) {
        return args.length > 0 && args[0].equals("--gui")
                && !GraphicsEnvironment.isHeadless();
    }

    private static void startGUI() {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                MainWindow window = new MainWindow();
                window.setVisible(true);
            } catch (Exception e) {
                fallbackToConsole("GUI failed: " + e.getMessage());
            }
        });
    }

    private static void startConsole() {
        System.out.println("=== VOTING SYSTEM CONSOLE ===");
        // Your existing console logic here
        // Example: new ConsoleInterface().start();
    }

    private static void handleStartupError(Exception e) {
        String errorMsg = "Startup failed: " + e.getMessage();
        if (GraphicsEnvironment.isHeadless()) {
            System.err.println(errorMsg);
        } else {
            JOptionPane.showMessageDialog(null, errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void fallbackToConsole(String message) {
        System.err.println(message + " - Falling back to console mode");
        startConsole();
    }

    // Service getters for GUI
    public static AuthService getAuthService() { return auth; }
    public static VotingService getVotingService() { return voting; }
    public static AdminService getAdminService() { return admin; }
    public static DatabaseManager getDbManager() { return db; }
}