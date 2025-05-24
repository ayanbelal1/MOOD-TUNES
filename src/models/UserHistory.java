package models;

import java.sql.Timestamp;

public class UserHistory {
    private int id;
    private int userId;
    private int songId;
    private Timestamp playedAt;
    
    public UserHistory() {}
    
    public UserHistory(int id, int userId, int songId, Timestamp playedAt) {
        this.id = id;
        this.userId = userId;
        this.songId = songId;
        this.playedAt = playedAt;
    }
    
    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public int getSongId() { return songId; }
    public void setSongId(int songId) { this.songId = songId; }
    
    public Timestamp getPlayedAt() { return playedAt; }
    public void setPlayedAt(Timestamp playedAt) { this.playedAt = playedAt; }
}