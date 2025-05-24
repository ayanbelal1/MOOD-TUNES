import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.prefs.Preferences;
import models.User;
import dao.UserDAO;
import dao.DatabaseConnection;

public class Login extends JFrame {
    private Preferences prefs;
    private JTextField userField;
    private JPasswordField passField;
    private JCheckBox rememberMeBox;
    private JCheckBox showPasswordBox;
    private JButton loginBtn;    // Add this field
    private JButton registerBtn; // Add this field

    public Login() {
        setTitle("MoodTunes - Login");
        setSize(450, 350);  // Increased window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        prefs = Preferences.userNodeForPackage(Login.class);

        JLabel titleLabel = new JLabel("MoodTunes Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(30, 144, 255));

        JLabel userLabel = new JLabel("Username:");
        userField = new JTextField(20);
        userField.setPreferredSize(new Dimension(250, 30));  // Increased text field size
        
        JLabel passLabel = new JLabel("Password:");
        passField = new JPasswordField(20);
        passField.setPreferredSize(new Dimension(250, 30));  // Increased text field size
        passField.setEchoChar('*');
        rememberMeBox = new JCheckBox("Remember Me");
        showPasswordBox = new JCheckBox("Show Password");
        showPasswordBox.addActionListener(e -> {
            if (showPasswordBox.isSelected()) {
                passField.setEchoChar((char) 0); // Show password
            } else {
                passField.setEchoChar('•'); // Hide password
            }
        });

        // Initialize the buttons
        loginBtn = new JButton("Login");
        registerBtn = new JButton("Register");

        // Update the fieldsPanel layout
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);  // Increased padding
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;  // Make components fill horizontal space

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.3;  // Give less space to labels
        fieldsPanel.add(userLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;  // Give more space to text fields
        fieldsPanel.add(userField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0.3;
        fieldsPanel.add(passLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        fieldsPanel.add(passField, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        gbc.insets = new Insets(5, 10, 5, 10);  // Smaller padding for checkboxes
        fieldsPanel.add(showPasswordBox, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        fieldsPanel.add(rememberMeBox, gbc);

        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(fieldsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        loginBtn.addActionListener(e -> {
            handleLogin();
        });

        registerBtn.addActionListener(e -> {
            handleRegister();
        });

        setVisible(true);
    }
    
    private void handleLogin() {
        String username = userField.getText();
        String password = new String(passField.getPassword());
        
        try {
            UserDAO userDAO = new UserDAO();
            User user = userDAO.findByUsername(username);
            
            if (user != null && user.getPassword().equals(password)) {
                if (rememberMeBox.isSelected()) {
                    prefs.put("username", username);
                } else {
                    prefs.remove("username");
                }
                
                new MoodAppGUI(username);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }
    
    private void handleRegister() {
        String username = userField.getText();
        String password = new String(passField.getPassword());
        
        try {
            UserDAO userDAO = new UserDAO();
            User existingUser = userDAO.findByUsername(username);
            
            if (existingUser != null) {
                JOptionPane.showMessageDialog(this, "Username already exists");
                return;
            }
            
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password);
            userDAO.save(newUser);
            
            JOptionPane.showMessageDialog(this, "Registration successful! Please login.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }
}