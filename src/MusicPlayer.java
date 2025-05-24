
import javazoom.jl.player.Player;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class MusicPlayer {
    private Player player;
    private String source;
    private boolean isPlaying;
    private Thread playerThread;
    private boolean isPaused;
    private long pauseLocation;
    private long songLength;
    private FileInputStream fis;
    
    public MusicPlayer(String source) {
        this.source = source;
        this.isPlaying = false;
        this.isPaused = false;
        this.pauseLocation = 0;
        this.songLength = 0;
    }

    public void play() {
        if (isPaused) {
            resumePlay();
            return;
        }
        
        if (isPlaying) {
            stop();
        }
        
        playerThread = new Thread(() -> {
            try {
                if (source.startsWith("http")) {
                    // Online streaming
                    URL url = new URL(source);
                    URLConnection conn = url.openConnection();
                    InputStream inputStream = conn.getInputStream();
                    player = new Player(inputStream);
                } else {
                    // Local file playback
                    File file = new File(source);
                    if (!file.exists()) {
                        throw new FileNotFoundException("MP3 file not found: " + source);
                    }
                    fis = new FileInputStream(file);
                    songLength = file.length();
                    player = new Player(fis);
                }
                
                isPlaying = true;
                player.play();
            } catch (Exception e) {
                e.printStackTrace();
                isPlaying = false;
                throw new RuntimeException("Error playing music: " + e.getMessage());
            } finally {
                isPlaying = false;
                isPaused = false;
                if (player != null) {
                    player.close();
                }
            }
        });
        playerThread.start();
    }

    public void pause() {
        if (player != null && isPlaying && !isPaused) {
            try {
                pauseLocation = fis.available();
                stop();
                isPaused = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void resumePlay() {
        if (isPaused) {
            playerThread = new Thread(() -> {
                try {
                    fis = new FileInputStream(source);
                    fis.skip(songLength - pauseLocation);
                    player = new Player(fis);
                    isPlaying = true;
                    isPaused = false;
                    player.play();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    isPlaying = false;
                    isPaused = false;
                    if (player != null) {
                        player.close();
                    }
                }
            });
            playerThread.start();
        }
    }

    public void stop() {
        if (player != null) {
            player.close();
            isPlaying = false;
        }
        if (playerThread != null) {
            playerThread.interrupt();
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public boolean isPaused() {
        return isPaused;
    }
}
