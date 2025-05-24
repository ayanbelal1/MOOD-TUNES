import java.awt.Desktop;
import java.net.URI;
public class DesktopTest {
    public static void main(String[] args) throws Exception {
        Desktop.getDesktop().browse(new URI("https://www.youtube.com"));
    }
}