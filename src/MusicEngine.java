import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import models.Song;
// ... existing imports ...

public class MusicEngine {
    public static void handleMood(String mood, Song song) {
        try {
            // Create MusicPlayer instance and play the song
            MusicPlayer player = new MusicPlayer(song.getUrl());
            player.play();
            
            // Log the played song
            logPlayedSong(song);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void logPlayedSong(Song song) {
        try (FileWriter fw = new FileWriter("visited_songs.txt", true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(String.format("%s - %s (%s) | %s\n", 
                song.getTitle(), song.getArtist(), song.getMood(), 
                song.getCreatedAt()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}