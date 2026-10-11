public class Main {

    public static void main(String[] args) {
        if (args.length > 0 && "--swing".equalsIgnoreCase(args[0])) {
            System.out.println("Launching Swing Desktop Interface...");
            MainFrame.main(args);
        } else if (args.length > 0 && "--test".equalsIgnoreCase(args[0])) {
            System.out.println("Running OOP Validation Suite...");
            OOPTestRunner.main(args);
        } else {
            System.out.println("Starting Hotel Management Web Application...");
            HotelWebServer.main(args);
        }
    }
}