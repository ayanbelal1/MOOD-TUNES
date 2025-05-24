import java.awt.Desktop;
import java.net.*;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.JOptionPane;

public class WebSearch {
    private static final int TIMEOUT = 3000; // 3 seconds timeout

    public static boolean isInternetAvailable() {
        try {
            final URL url = new URL("http://www.google.com");
            final URLConnection conn = url.openConnection();
            conn.setConnectTimeout(TIMEOUT);
            conn.setReadTimeout(TIMEOUT);
            conn.connect();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void openYouTubeForMood(String mood) {
        if (!isInternetAvailable()) {
            JOptionPane.showMessageDialog(null, "No internet connection available.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String url = "https://www.youtube.com/results?search_query=" + URLEncoder.encode(mood + " songs", "UTF-8");
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Could not open browser: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (URISyntaxException e) {
            JOptionPane.showMessageDialog(null, "Invalid URL format", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}