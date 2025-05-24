import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import java.sql.SQLException; // Add this
import models.Song;
import dao.SongDAO;
import dao.UserDAO; // Add this
import java.net.URI;

public class MoodAppGUI extends JFrame {
    private SongDAO songDAO;
    private String currentMood;
    private JButton selectedMoodButton = null;
    private JLabel lastMoodLabel;
    private String username;
    private MusicPlayer currentPlayer;
    private JPanel controlPanel;
    private JPanel playlistPanel;
    private JList<String> playlistList;
    private DefaultListModel<String> playlistModel;
    private File currentDirectory;
    private JLabel nowPlayingLabel;

    public MoodAppGUI(String username) {
        this.username = username;
        try {
            songDAO = new SongDAO();
            setTitle("MoodTunes - Welcome " + username);
            setSize(600, 400);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);

            // Create main panel with BorderLayout
            JPanel mainPanel = new JPanel(new BorderLayout());
            
            // Create top panel for Profile and Logout buttons
            JPanel topPanel = new JPanel(new BorderLayout());
            
            // Profile button
            JButton profileButton = new JButton("Profile");
            profileButton.setFont(new Font("Arial", Font.PLAIN, 14));
            profileButton.setBackground(new Color(75, 0, 130));
            profileButton.setForeground(Color.WHITE);
            profileButton.setBorderPainted(false);
            profileButton.setFocusPainted(false);
            profileButton.addActionListener(e -> new Profile(username).setVisible(true));
            
            // Logout button
            JButton logoutButton = new JButton("Logout");
            logoutButton.setFont(new Font("Arial", Font.PLAIN, 14));
            logoutButton.setBackground(new Color(178, 34, 34));
            logoutButton.setForeground(Color.WHITE);
            logoutButton.setBorderPainted(false);
            logoutButton.setFocusPainted(false);
            logoutButton.addActionListener(e -> {
                dispose();
                new Login().setVisible(true);
            });
            
            // Add buttons to top panel
            topPanel.add(profileButton, BorderLayout.WEST);
            topPanel.add(logoutButton, BorderLayout.EAST);
            
            // Create center panel for mood selection
            JPanel centerPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            
            // Title label
            JLabel titleLabel = new JLabel("How are you feeling today?");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
            titleLabel.setForeground(new Color(75, 0, 130));
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(20, 0, 30, 0);
            centerPanel.add(titleLabel, gbc);
            
            // Mood buttons panel
            JPanel moodPanel = new JPanel(new GridLayout(2, 2, 20, 20));
            String[] moods = {"Happy", "Sad", "Relaxed", "Energetic"};
            for (String mood : moods) {
                JButton btn = new JButton(mood);
                btn.setFont(new Font("Arial", Font.PLAIN, 18));
                btn.setBackground(new Color(230, 230, 250));  // Lavender
                btn.setBorder(BorderFactory.createLineBorder(new Color(75, 0, 130)));
                btn.setForeground(new Color(75, 0, 130));
                btn.setFocusPainted(false);
                btn.addActionListener(e -> selectMood(btn, mood.toLowerCase()));
                moodPanel.add(btn);
            }
            
            gbc.gridy = 1;
            gbc.insets = new Insets(0, 0, 20, 0);
            centerPanel.add(moodPanel, gbc);
            // YouTube button
            JButton youtubeBtn = new JButton("PLAY ON YOUTUBE");
            youtubeBtn.setFont(new Font("Arial", Font.BOLD, 16));
            youtubeBtn.setBackground(new Color(139, 0, 0));
            youtubeBtn.setForeground(Color.WHITE);
            youtubeBtn.setBorderPainted(false);
            youtubeBtn.setEnabled(false);
            youtubeBtn.addActionListener(e -> openYouTubeSearch());
            
            gbc.gridy = 2;
            gbc.insets = new Insets(0, 0, 10, 0);
            centerPanel.add(youtubeBtn, gbc);
            
            // Offline music button
            JButton offlineBtn = new JButton("Play Offline Music");
            offlineBtn.setFont(new Font("Arial", Font.BOLD, 16));
            offlineBtn.setBackground(new Color(75, 0, 130));  // Indigo
            offlineBtn.setForeground(Color.WHITE);
            offlineBtn.setBorderPainted(false);
            offlineBtn.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                    public boolean accept(File f) {
                        return f.isDirectory() || f.getName().toLowerCase().endsWith(".mp3");
                    }
                    public String getDescription() {
                        return "MP3 Files and Folders";
                    }
                });
                
                int result = fileChooser.showOpenDialog(MoodAppGUI.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selected = fileChooser.getSelectedFile();
                    if (selected.isDirectory()) {
                        currentDirectory = selected;
                        updatePlaylist(selected);
                    } else {
                        playFile(selected);
                    }
                }
            });
            
            gbc.gridy = 3;
            centerPanel.add(offlineBtn, gbc);
            
            // Last mood label
            lastMoodLabel = new JLabel();
            lastMoodLabel.setFont(new Font("Arial", Font.ITALIC, 16));
            lastMoodLabel.setForeground(new Color(108, 117, 125));
            updateLastMoodLabel();
            
            gbc.gridy = 4;
            gbc.insets = new Insets(20, 0, 0, 0);
            centerPanel.add(lastMoodLabel, gbc);
            
            // Add panels to main panel
            mainPanel.add(topPanel, BorderLayout.NORTH);
            mainPanel.add(centerPanel, BorderLayout.CENTER);
            
            // Create and add side panels
            createControlPanel();
            createPlaylistPanel();
            add(mainPanel, BorderLayout.CENTER);    // Main panel in the center
            add(controlPanel, BorderLayout.WEST);   // Control panel on the left
            add(playlistPanel, BorderLayout.EAST);  // Playlist panel on the right
            
            setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void selectMood(JButton btn, String mood) {
        currentMood = mood;
        if (selectedMoodButton != null) {
            selectedMoodButton.setBackground(new Color(230, 230, 250));  // Lavender
            selectedMoodButton.setForeground(new Color(75, 0, 130));  // Indigo
        }
        btn.setBackground(new Color(75, 0, 130));  // Indigo
        btn.setForeground(Color.WHITE);
        selectedMoodButton = btn;
        
        for (Component c : getContentPane().getComponents()) {
            if (c instanceof JPanel) {
                findAndEnableYouTubeButton((JPanel)c);
            }
        }
    }

    private void findAndEnableYouTubeButton(JPanel panel) {
        for (Component c : panel.getComponents()) {
            if (c instanceof JButton && ((JButton)c).getText().equals("PLAY ON YOUTUBE")) {
                ((JButton)c).setEnabled(true);
            } else if (c instanceof JPanel) {
                findAndEnableYouTubeButton((JPanel)c);
            }
        }
    }

    private void openYouTubeSearch() {
        try {
            String searchQuery = currentMood + " music playlist";
            String encodedQuery = java.net.URLEncoder.encode(searchQuery, "UTF-8");
            String youtubeURL = "https://www.youtube.com/results?search_query=" + encodedQuery;
            Desktop.getDesktop().browse(new URI(youtubeURL));
            
            // Move the database update inside the first try block to ensure it only happens if YouTube opens successfully
            new dao.UserDAO().updateLastMood(username, currentMood);
            updateLastMoodLabel();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error opening YouTube: " + e.getMessage());
        }
    }

    private void updateLastMoodLabel() {
        try {
            String lastMood = new UserDAO().getLastMood(username);
            if (lastMood != null && !lastMood.isEmpty()) {
                lastMoodLabel.setText("Last selected mood: " + lastMood);
            } else {
                lastMoodLabel.setText("No previous mood selected");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lastMoodLabel.setText("Error retrieving last mood");
        }
    }

    private void createPlaylistPanel() {
        playlistPanel = new JPanel(new BorderLayout());
        playlistPanel.setBorder(BorderFactory.createTitledBorder("Playlist"));
        playlistPanel.setPreferredSize(new Dimension(200, 0));
        
        // Add folder selection button at the top
        JButton selectFolderBtn = new JButton("Select Music Folder");
        selectFolderBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        selectFolderBtn.setBackground(new Color(75, 0, 130));  // Indigo
        selectFolderBtn.setForeground(Color.WHITE);
        selectFolderBtn.setBorderPainted(false);
        selectFolderBtn.setFocusPainted(false);
        selectFolderBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setDialogTitle("Select Music Folder");
            
            int result = fileChooser.showOpenDialog(MoodAppGUI.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                currentDirectory = fileChooser.getSelectedFile();
                updatePlaylist(currentDirectory);
            }
        });
        
        playlistModel = new DefaultListModel<>();
        playlistList = new JList<>(playlistModel);
        playlistList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        playlistList.setFont(new Font("Arial", Font.PLAIN, 12));
        
        playlistList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && playlistList.getSelectedIndex() != -1) {
                String selectedSong = playlistList.getSelectedValue();
                File songFile = new File(currentDirectory, selectedSong);
                playFile(songFile);
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(playlistList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(selectFolderBtn, BorderLayout.CENTER);
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        playlistPanel.add(topPanel, BorderLayout.NORTH);
        playlistPanel.add(scrollPane, BorderLayout.CENTER);
    }

    private void createControlPanel() {
        controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBorder(BorderFactory.createTitledBorder("Controls"));
        controlPanel.setPreferredSize(new Dimension(200, 0));
        
        // Now Playing label
        nowPlayingLabel = new JLabel("Now Playing: None");
        nowPlayingLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        nowPlayingLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Button panel with GridBagLayout for more control over button placement
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        
        // Create buttons with light blue background
        JButton prevBtn = createControlButton("Previous");
        JButton playPauseBtn = createControlButton("Play/Pause");
        JButton stopBtn = createControlButton("Stop");
        JButton nextBtn = createControlButton("Next");
        
        // Style all buttons
        Component[] buttons = {prevBtn, playPauseBtn, stopBtn, nextBtn};
        for (Component btn : buttons) {
            ((JButton)btn).setBackground(new Color(230, 240, 250));
            ((JButton)btn).setBorder(BorderFactory.createLineBorder(new Color(100, 150, 200)));
            ((JButton)btn).setPreferredSize(new Dimension(100, 30));
        }
        
        // Add Previous and Play/Pause buttons in first row
        gbc.gridx = 0; gbc.gridy = 0;
        buttonPanel.add(prevBtn, gbc);
        gbc.gridx = 1;
        buttonPanel.add(playPauseBtn, gbc);
        
        // Add Stop and Next buttons in second row
        gbc.gridx = 0; gbc.gridy = 1;
        buttonPanel.add(stopBtn, gbc);
        gbc.gridx = 1;
        buttonPanel.add(nextBtn, gbc);
        
        // Add action listeners
        playPauseBtn.addActionListener(e -> {
            if (currentPlayer != null) {
                if (currentPlayer.isPlaying()) {
                    currentPlayer.pause();
                    playPauseBtn.setText("Play");
                } else {
                    currentPlayer.play();
                    playPauseBtn.setText("Pause");
                }
            }
        });
        
        stopBtn.addActionListener(e -> {
            if (currentPlayer != null) {
                currentPlayer.stop();
                nowPlayingLabel.setText("Now Playing: None");
                playPauseBtn.setText("Play/Pause");
            }
        });
        
        prevBtn.addActionListener(e -> {
            int currentIndex = playlistList.getSelectedIndex();
            if (currentIndex > 0) {
                playlistList.setSelectedIndex(currentIndex - 1);
            }
        });
        
        nextBtn.addActionListener(e -> {
            int currentIndex = playlistList.getSelectedIndex();
            if (currentIndex < playlistModel.getSize() - 1) {
                playlistList.setSelectedIndex(currentIndex + 1);
            }
        });
        
        // Add components to control panel
        controlPanel.add(nowPlayingLabel, BorderLayout.NORTH);
        controlPanel.add(buttonPanel, BorderLayout.CENTER);
    }
    
    private JButton createControlButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        return button;
    }

    private void updatePlaylist(File directory) {
        playlistModel.clear();
        File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3"));
        if (files != null) {
            for (File file : files) {
                playlistModel.addElement(file.getName());
            }
        }
    }

    private void playFile(File file) {
        try {
            if (currentPlayer != null) {
                currentPlayer.stop();
            }
            currentPlayer = new MusicPlayer(file.getAbsolutePath());
            currentPlayer.play();
            nowPlayingLabel.setText("Now Playing: " + file.getName());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error playing music: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
// Remove these lines as they are duplicates and outside any method:
// // Profile and Logout button propertie