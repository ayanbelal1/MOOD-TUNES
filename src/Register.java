import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import dao.DatabaseConnection; // Add this

public class Register extends JFrame {
    public Register() {
        setTitle("MoodTunes - Register");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel titleLabel = new JLabel("MoodTunes Registration", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(34, 139, 34));

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(15);
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(15);
        JButton registerBtn = new JButton("Register");
        JButton backBtn = new JButton("Back");

        JPanel fieldsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        fieldsPanel.add(userLabel); fieldsPanel.add(userField);
        fieldsPanel.add(passLabel); fieldsPanel.add(passField);
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(registerBtn);
        buttonPanel.add(backBtn);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(fieldsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        registerBtn.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both username and password", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement checkStmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?");
                 PreparedStatement pstmt = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)")) {
                
                 // Check if username already exists
                 checkStmt.setString(1, username);
                 ResultSet rs = checkStmt.executeQuery();

                 if (rs.next()) {
                     JOptionPane.showMessageDialog(this, 
                         "Username already exists. Please choose another.", 
                         "Error", 
                         JOptionPane.ERROR_MESSAGE);
                     return;
                 }

                 // Insert new user
                 pstmt.setString(1, username);
                 pstmt.setString(2, password);
                 pstmt.executeUpdate();

                 JOptionPane.showMessageDialog(this, "Registration completed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                 dispose();
                 new Login();

             } catch (SQLException ex) {
                 JOptionPane.showMessageDialog(this,
                     "Database error: " + ex.getMessage(),
                     "Error",
                     JOptionPane.ERROR_MESSAGE);
                 ex.printStackTrace();
             }
         });

        backBtn.addActionListener(e -> {
            dispose();
            new Login();
        });

        setVisible(true);
    }
}