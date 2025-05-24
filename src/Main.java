import dao.DatabaseConnection;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        try {
            boolean isConnected = DatabaseConnection.testConnection();
            if (!isConnected) {
                JOptionPane.showMessageDialog(null, "Cannot connect to database. Please check your MySQL connection.");
                return;
            }
            
            // Create and show the Login window
            SwingUtilities.invokeLater(() -> {
                new Login().setVisible(true);
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }
}