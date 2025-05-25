package frontend;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                // Initialize and show home page
                new HomePage().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}