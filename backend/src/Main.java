import java.awt.Desktop;
import java.net.URI;

public class Main {

    public static void main(String[] args) {
        if (args.length > 0 && "--swing".equalsIgnoreCase(args[0])) {
            System.out.println("Launching Swing Desktop Login Portal...");
            LoginFrame.main(args);
        } else if (args.length > 0 && "--test".equalsIgnoreCase(args[0])) {
            System.out.println("Running OOP Validation Suite...");
            OOPTestRunner.main(args);
        } else {
            System.out.println("==================================================");
            System.out.println("   GRAND HORIZON HOTEL MANAGEMENT SYSTEM          ");
            System.out.println("==================================================");
            System.out.println("Starting Java Web Server & Opening Login Portal...");

            // Launch Java backend HTTP server
            HotelWebServer.main(args);

            // Automatically open browser to Login Page
            String loginUrl = "http://localhost:8080/login.html";
            openBrowser(loginUrl);

            System.out.println("\n[INFO] Login Portal opened at   : " + loginUrl);
            System.out.println("[INFO] Desktop Swing GUI option : Run with '--swing'");
            System.out.println("==================================================");
        }
    }

    private static void openBrowser(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                // Windows command fallback using ProcessBuilder
                new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", url).start();
            }
        } catch (Exception e) {
            System.out.println("Please open your browser to: " + url);
        }
    }
}