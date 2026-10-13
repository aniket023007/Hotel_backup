import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

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
