import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import models.User;
import dao.UserDAO;

public class Profile extends JFrame {
    private User currentUser;
    private UserDAO userDAO;
    private JTextField usernameField;
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JLabel lastMoodLabel;

    public Profile(String username) {
        userDAO = new UserDAO();
        try {
            currentUser = userDAO.findByUsername(username);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading profile: " + e.getMessage());
            dispose();
            return;
        }

        setTitle("MoodTunes - Profile");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username section
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        usernameField = new JTextField(currentUser.getUsername());
        usernameField.setEditable(false);
        mainPanel.add(usernameField, gbc);

        // Current password section
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Current Password:"), gbc);
        
        gbc.gridx = 1;
        currentPasswordField = new JPasswordField();
        mainPanel.add(currentPasswordField, gbc);

        // New password section
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("New Password:"), gbc);
        
        gbc.gridx = 1;
        newPasswordField = new JPasswordField();
        mainPanel.add(newPasswordField, gbc);

        // Last mood section
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(new JLabel("Last Mood:"), gbc);
        
        gbc.gridx = 1;
        lastMoodLabel = new JLabel(currentUser.getLastMood() != null ? currentUser.getLastMood() : "Not set");
        mainPanel.add(lastMoodLabel, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        JButton updateButton = new JButton("Update Password");
        JButton closeButton = new JButton("Close");

        updateButton.addActionListener(e -> updatePassword());
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(updateButton);
        buttonPanel.add(closeButton);

        // Add panels to frame
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void updatePassword() {
        String currentPassword = new String(currentPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());

        if (currentPassword.isEmpty() || newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all password fields");
            return;
        }

        if (!currentPassword.equals(currentUser.getPassword())) {
            JOptionPane.showMessageDialog(this, "Current password is incorrect");
            return;
        }

        try {
            currentUser.setPassword(newPassword);
            userDAO.updatePassword(currentUser);
            JOptionPane.showMessageDialog(this, "Password updated successfully");
            currentPasswordField.setText("");
            newPasswordField.setText("");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating password: " + e.getMessage());
        }
    }
}