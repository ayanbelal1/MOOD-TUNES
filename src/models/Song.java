package models;

import java.sql.Timestamp;

public class Song {
    private int id;
    private String title;
    private String artist;
    private String mood;
    private String url;
    private Timestamp createdAt;
    
    public Song() {}

    public Song(int id, String title, String artist, String mood, String url, Timestamp createdAt) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.mood = mood;
        this.url = url;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }
    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}