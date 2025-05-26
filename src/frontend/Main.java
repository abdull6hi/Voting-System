package frontend;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                // Start the app by showing the login page
                new LoginPage().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
