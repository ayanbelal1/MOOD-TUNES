package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.UserHistory;
// DatabaseConnection is in the same package, no import needed

public class UserHistoryDAO {
    public void save(UserHistory history) throws SQLException {
        String query = "INSERT INTO user_history (user_id, song_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, history.getUserId());
            pstmt.setInt(2, history.getSongId());
            pstmt.executeUpdate();
        }
    }
    
    public List<UserHistory> findByUserId(int userId) throws SQLException {
        List<UserHistory> history = new ArrayList<>();
        String query = "SELECT * FROM user_history WHERE user_id = ? ORDER BY played_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                UserHistory entry = new UserHistory();
                entry.setId(rs.getInt("id"));
                entry.setUserId(rs.getInt("user_id"));
                entry.setSongId(rs.getInt("song_id"));
                entry.setPlayedAt(rs.getTimestamp("played_at"));
                history.add(entry);
            }
        }
        return history;
    }
}

