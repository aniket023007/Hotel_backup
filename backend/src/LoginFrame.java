import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

// =============================================================================
// LOGIN FRAME (Swing Desktop Login Window)
// =============================================================================

/**
 * LoginFrame Swing Desktop Application.
 * Entry window for the Grand Horizon Hotel Management Desktop Application.
 * Authenticates users against the MySQL database via the OOP Service Layer (IAuthService / IUserDAO).
 *
 * Demonstrates:
 * - Separation of Concerns: UI delegates authentication entirely to IAuthService
 * - Dependency Inversion: Depends on IAuthService abstraction
 * - Polymorphic State Transfer: Passes authenticated polymorphic User to MainFrame
 */
public class LoginFrame extends JFrame {

    // Service abstraction for authentication
    private final IAuthService authService;

    // UI components
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheck;
    private JButton loginButton;
    private JLabel statusLabel;

    // Color Palette
    private static final Color COLOR_PRIMARY = new Color(25, 55, 90);      // Deep Horizon Navy
    private static final Color COLOR_GOLD = new Color(185, 145, 80);        // Luxury Gold
    private static final Color COLOR_BG = new Color(245, 247, 250);         // Soft Slate Background
    private static final Color COLOR_CARD = Color.WHITE;
    private static final Color COLOR_TEXT_MUTED = new Color(100, 116, 139);
    private static final Color COLOR_ERROR = new Color(220, 38, 38);

    public LoginFrame() {
        this(AuthService.getInstance());
    }

    public LoginFrame(IAuthService authService) {
        this.authService = authService != null ? authService : AuthService.getInstance();

        setTitle("Grand Horizon Hotel — Staff Access Portal");
        setSize(460, 490);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
    }

    private void initUI() {
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(COLOR_BG);
        rootPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        // =====================================================================
        // HEADER PANEL
        // =====================================================================
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel brandIcon = new JLabel("GH", SwingConstants.CENTER);
        brandIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        brandIcon.setFont(new Font("Serif", Font.BOLD, 28));
        brandIcon.setForeground(COLOR_GOLD);
        brandIcon.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_GOLD, 2, true),
            new EmptyBorder(4, 12, 4, 12)
        ));

        JLabel titleLabel = new JLabel("GRAND HORIZON", SwingConstants.CENTER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 22));
        titleLabel.setForeground(COLOR_PRIMARY);

        JLabel subTitleLabel = new JLabel("STAFF ACCESS PORTAL", SwingConstants.CENTER);
        subTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subTitleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        subTitleLabel.setForeground(COLOR_GOLD);

        JLabel descLabel = new JLabel("Authenticate with MySQL Backend & OOP Service", SwingConstants.CENTER);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setForeground(COLOR_TEXT_MUTED);

        headerPanel.add(brandIcon);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(subTitleLabel);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(descLabel);
        headerPanel.add(Box.createVerticalStrut(20));

        // =====================================================================
        // CARD / FORM PANEL
        // =====================================================================
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(COLOR_CARD);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Username Field
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Arial", Font.BOLD, 13));
        userLabel.setForeground(COLOR_PRIMARY);

        usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setPreferredSize(new Dimension(380, 36));
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        // Password Field
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Arial", Font.BOLD, 13));
        passLabel.setForeground(COLOR_PRIMARY);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(380, 36));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        // Show/Hide Password Checkbox
        showPasswordCheck = new JCheckBox("Show Password");
        showPasswordCheck.setFont(new Font("Arial", Font.PLAIN, 12));
        showPasswordCheck.setForeground(COLOR_TEXT_MUTED);
        showPasswordCheck.setOpaque(false);
        showPasswordCheck.addActionListener(e -> {
            if (showPasswordCheck.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }
        });

        // Status / Error message
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statusLabel.setForeground(COLOR_ERROR);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Sign In Button
        loginButton = new JButton("SIGN IN TO DASHBOARD");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(COLOR_PRIMARY);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setPreferredSize(new Dimension(380, 42));
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        loginButton.addActionListener(this::handleLogin);

        // Press Enter to submit
        KeyAdapter enterListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin(null);
                }
            }
        };
        usernameField.addKeyListener(enterListener);
        passwordField.addKeyListener(enterListener);

        cardPanel.add(userLabel);
        cardPanel.add(Box.createVerticalStrut(5));
        cardPanel.add(usernameField);
        cardPanel.add(Box.createVerticalStrut(12));
        cardPanel.add(passLabel);
        cardPanel.add(Box.createVerticalStrut(5));
        cardPanel.add(passwordField);
        cardPanel.add(Box.createVerticalStrut(6));
        cardPanel.add(showPasswordCheck);
        cardPanel.add(Box.createVerticalStrut(10));
        cardPanel.add(statusLabel);
        cardPanel.add(Box.createVerticalStrut(10));
        cardPanel.add(loginButton);

        // Combine into root
        rootPanel.add(headerPanel, BorderLayout.NORTH);
        rootPanel.add(cardPanel, BorderLayout.CENTER);

        setContentPane(rootPanel);
    }

    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty()) {
            showError("Please enter your username.");
            usernameField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            showError("Please enter your password.");
            passwordField.requestFocus();
            return;
        }

        // Disable button and indicate progress
        loginButton.setEnabled(false);
        loginButton.setText("AUTHENTICATING...");
        statusLabel.setForeground(COLOR_PRIMARY);
        statusLabel.setText("Verifying with MySQL backend...");

        // Perform authentication asynchronously to keep UI responsive
        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() throws Exception {
                // Demonstrates static polymorphism (accepts LoginCredentials DTO)
                LoginCredentials credentials = new LoginCredentials(username, password);
                return authService.login(credentials);
            }

            @Override
            protected void done() {
                try {
                    User authenticatedUser = get();

                    // Success: Dispose LoginFrame and open MainFrame
                    dispose();

                    SwingUtilities.invokeLater(() -> {
                        MainFrame mainFrame = new MainFrame(authenticatedUser);
                        mainFrame.setVisible(true);
                    });

                } catch (Exception ex) {
                    loginButton.setEnabled(true);
                    loginButton.setText("SIGN IN TO DASHBOARD");
                    String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    if (msg == null || msg.isEmpty()) msg = "Invalid username or password.";
                    showError(msg);
                }
            }
        };

        worker.execute();
    }

    private void showError(String message) {
        statusLabel.setForeground(COLOR_ERROR);
        statusLabel.setText(message);
    }

    public static void main(String[] args) {
        // Set native look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}

// =============================================================================
// MAIN FRAME (Swing Desktop Receptionist Dashboard)
// =============================================================================

/**
 * MainFrame Swing Desktop Application.
 * Receptionist Dashboard providing a desktop graphical user interface.
 * Adheres strictly to OOP Principles: Dependency Inversion, Service Delegation, and Domain Modeling.
 */
class MainFrame extends JFrame {

    private final IHotelService hotelService = new HotelService();
    private final IRoomDAO roomDAO = new RoomDAO();
    private final IGuestDAO guestDAO = new GuestDAO();
    private final IBookingDAO bookingDAO = new BookingDAO();
    private final ICheckoutDAO checkoutDAO = new CheckoutDAO();

    private User currentUser;

    public MainFrame() {
        this(null);
    }

    public MainFrame(User user) {
        this.currentUser = user;

        setTitle("Hotel Management System — Grand Horizon");
        setSize(850, 580);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        );

        JLabel heading = new JLabel(
            "HOTEL MANAGEMENT SYSTEM",
            SwingConstants.CENTER
        );

        heading.setFont(
            new Font("Times New Roman", Font.BOLD, 26)
        );
        heading.setForeground(new Color(25, 55, 90));

        String subtitleText = (user != null)
            ? "Logged in as: " + user.getName() + " (" + user.getRole() + ")"
            : "Receptionist Dashboard (Pure Java OOP)";

        JLabel subtitle = new JLabel(
            subtitleText,
            SwingConstants.CENTER
        );

        subtitle.setFont(new Font("Arial", Font.PLAIN, 15));
        if (user != null) {
            subtitle.setForeground(new Color(185, 145, 80)); // Luxury gold accent
        }

        JPanel header = new JPanel(new GridLayout(2, 1, 5, 5));
        header.add(heading);
        header.add(subtitle);

        // Dashboard buttons (4 rows x 2 columns = 8 slots)
        JPanel buttonPanel = new JPanel(
            new GridLayout(4, 2, 15, 15)
        );

        JButton roomsButton = new JButton("View Rooms");
        JButton guestsButton = new JButton("View Guests");
        JButton addGuestButton = new JButton("Add Guest");
        JButton bookButton = new JButton("Book Room");
        JButton bookingButton = new JButton("View Bookings");
        JButton billButton = new JButton("Calculate Bill");
        JButton checkoutButton = new JButton("Check Out");
        JButton logoutButton = new JButton("Sign Out / Switch User");

        JButton[] buttons = {
            roomsButton,
            guestsButton,
            addGuestButton,
            bookButton,
            bookingButton,
            billButton,
            checkoutButton,
            logoutButton
        };

        for (JButton button : buttons) {
            button.setFont(new Font("Arial", Font.BOLD, 15));
            button.setFocusPainted(false);
            buttonPanel.add(button);
        }

        logoutButton.setBackground(new Color(254, 242, 242));
        logoutButton.setForeground(new Color(185, 28, 28));

        // Button actions
        roomsButton.addActionListener(e -> showRooms());
        guestsButton.addActionListener(e -> showGuests());
        addGuestButton.addActionListener(e -> addGuest());
        bookButton.addActionListener(e -> bookRoom());
        bookingButton.addActionListener(e -> viewBooking());
        billButton.addActionListener(e -> calculateBill());
        checkoutButton.addActionListener(e -> checkout());
        logoutButton.addActionListener(e -> handleLogout());

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to sign out and return to the login screen?",
            "Confirm Sign Out",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            AuthService.getInstance().logout();
            dispose();
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            });
        }
    }

    private void showRooms() {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            PrintStream oldOut = System.out;

            try (PrintStream newOut = new PrintStream(output)) {
                System.setOut(newOut);
                roomDAO.displayRooms();
            } finally {
                System.setOut(oldOut);
            }

            JTextArea area = new JTextArea(output.toString());
            area.setEditable(false);
            area.setFont(new Font("Monospaced", Font.PLAIN, 13));

            JOptionPane.showMessageDialog(
                this,
                new JScrollPane(area),
                "Room Details",
                JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void showGuests() {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            PrintStream oldOut = System.out;

            try (PrintStream newOut = new PrintStream(output)) {
                System.setOut(newOut);
                guestDAO.displayGuests();
            } finally {
                System.setOut(oldOut);
            }

            JTextArea area = new JTextArea(output.toString());
            area.setEditable(false);
            area.setFont(new Font("Monospaced", Font.PLAIN, 13));

            JOptionPane.showMessageDialog(
                this,
                new JScrollPane(area),
                "Guest Details",
                JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void viewBooking() {
        try {
            javax.swing.table.DefaultTableModel model = bookingDAO.getBookingsTableModel();

            JTable table = new JTable(model);
            table.setFont(new Font("Arial", Font.PLAIN, 13));
            table.setRowHeight(28);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            table.setFillsViewportHeight(true);

            table.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13)
            );

            int[] widths = {90, 160, 120, 90, 120, 70, 110};

            for (int i = 0; i < widths.length; i++) {
                table.getColumnModel()
                     .getColumn(i)
                     .setPreferredWidth(widths[i]);
            }

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(780, 350));

            String message = model.getRowCount() == 0
                ? "No bookings found."
                : "Total bookings: " + model.getRowCount();

            JPanel panel = new JPanel(new BorderLayout(5, 5));
            panel.add(new JLabel(message), BorderLayout.NORTH);
            panel.add(scrollPane, BorderLayout.CENTER);

            JOptionPane.showMessageDialog(
                this,
                panel,
                "Hotel Booking Details",
                JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void addGuest() {
        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField addressField = new JTextField();

        Object[] fields = {
            "Full Name:", nameField,
            "Phone:", phoneField,
            "Email:", emailField,
            "Address:", addressField
        };

        int result = JOptionPane.showConfirmDialog(
            this,
            fields,
            "Add New Guest",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                this, "Guest name is required."
            );
            return;
        }

        try {
            Guest guest = new Guest(
                nameField.getText().trim(),
                phoneField.getText().trim(),
                emailField.getText().trim(),
                addressField.getText().trim()
            );

            int guestId = hotelService.registerGuest(guest);

            JOptionPane.showMessageDialog(
                this, "Guest registered successfully with ID #" + guestId
            );

        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void bookRoom() {
        JTextField bookingIdField = new JTextField();
        JTextField guestIdField = new JTextField();
        JTextField roomIdField = new JTextField();
        JTextField nightsField = new JTextField();

        try {
            bookingIdField.setText(String.valueOf(hotelService.getNextBookingId()));
        } catch (Exception ignored) {}

        Object[] fields = {
            "Booking ID:", bookingIdField,
            "Guest ID:", guestIdField,
            "Room ID:", roomIdField,
            "Number of Nights:", nightsField
        };

        int result = JOptionPane.showConfirmDialog(
            this,
            fields,
            "Book a Room",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            int bookingId = Integer.parseInt(bookingIdField.getText().trim());
            int guestId = Integer.parseInt(guestIdField.getText().trim());
            int roomId = Integer.parseInt(roomIdField.getText().trim());
            int nights = Integer.parseInt(nightsField.getText().trim());

            if (bookingId <= 0 || guestId <= 0 || roomId <= 0 || nights <= 0) {
                throw new IllegalArgumentException("All IDs and nights must be positive.");
            }

            Booking booking = hotelService.bookRoom(bookingId, guestId, roomId, nights);

            JOptionPane.showMessageDialog(
                this,
                "Room booked successfully!\nReservation #" + booking.getBookingId() +
                "\nGuest: " + booking.getGuest().getName() +
                "\nTotal: ₹" + booking.calculateTotalAmount(),
                "Success",
                JOptionPane.INFORMATION_MESSAGE
            );

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Please enter valid numeric IDs and nights.",
                "Input Error",
                JOptionPane.ERROR_MESSAGE
            );

        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void calculateBill() {
        String input = JOptionPane.showInputDialog(
            this, "Enter Booking ID:"
        );

        if (input == null) {
            return;
        }

        try {
            int bookingId = Integer.parseInt(input.trim());

            if (bookingId <= 0) {
                throw new IllegalArgumentException("Booking ID must be positive.");
            }

            Bill bill = hotelService.calculateBill(bookingId);

            String discountLine = bill.getDiscount() > 0 ? "\nDiscount: -₹" + bill.getDiscount() : "";

            JOptionPane.showMessageDialog(
                this,
                "Booking ID: #" + bookingId +
                "\nGuest: " + bill.getBooking().getGuest().getName() +
                "\nRoom: #" + bill.getBooking().getRoom().getRoomNumber() + " (" + bill.getBooking().getRoom().getRoomType() + ")" +
                "\nNights: " + bill.getBooking().getNumberOfNights() +
                "\nSubtotal: ₹" + bill.getSubtotal() +
                discountLine +
                "\nTax (" + (int) bill.getTaxRate() + "% GST): ₹" + bill.getTaxAmount() +
                "\nGrand Total: ₹" + bill.getGrandTotal() +
                "\nRate Scheme: " + bill.getStrategy().getStrategyName(),
                "Bill Details",
                JOptionPane.INFORMATION_MESSAGE
            );

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                this, "Please enter a valid Booking ID."
            );
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void checkout() {
        String input = JOptionPane.showInputDialog(
            this, "Enter Booking ID to check out:"
        );

        if (input == null) {
            return;
        }

        try {
            int bookingId = Integer.parseInt(input.trim());

            if (bookingId <= 0) {
                throw new IllegalArgumentException("Booking ID must be positive.");
            }

            Bill bill = hotelService.processCheckout(bookingId);

            JOptionPane.showMessageDialog(
                this,
                "Checkout successful!\n" +
                "Booking ID: #" + bookingId +
                "\nGuest: " + bill.getBooking().getGuest().getName() +
                "\nFinal Bill: ₹" + bill.getGrandTotal() +
                "\nRoom #" + bill.getBooking().getRoom().getRoomNumber() + " is now available.",
                "Checkout Complete",
                JOptionPane.INFORMATION_MESSAGE
            );

            showRooms();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                this, "Please enter a valid Booking ID."
            );
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(
            this,
            ex.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
