package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Song;
// DatabaseConnection is in the same package, no import needed

public class SongDAO {
    public List<Song> findByMood(String mood) throws SQLException {
        List<Song> songs = new ArrayList<>();
        String query = "SELECT * FROM songs WHERE mood = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, mood);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Song song = new Song();
                song.setId(rs.getInt("id"));
                song.setTitle(rs.getString("title"));
                song.setArtist(rs.getString("artist"));
                song.setMood(rs.getString("mood"));
                song.setUrl(rs.getString("url"));
                song.setCreatedAt(rs.getTimestamp("created_at"));
                songs.add(song);
            }
        }
        return songs;
    }
    
    public void save(Song song) throws SQLException {
        String query = "INSERT INTO songs (title, artist, mood, url) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, song.getTitle());
            pstmt.setString(2, song.getArtist());
            pstmt.setString(3, song.getMood());
            pstmt.setString(4, song.getUrl());
            pstmt.executeUpdate();
        }
    }
}