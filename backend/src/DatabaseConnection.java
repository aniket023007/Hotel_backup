import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/hotel_management";

    private static final String USER = "root";

    private static final String PASSWORD = "SIB@17aniket";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {

        try (Connection connection = getConnection()) {

            System.out.println("MySQL connected successfully!");
            System.out.println(
                "Database: " + connection.getCatalog()
            );

        } catch (SQLException e) {

            System.out.println("MySQL connection failed!");
            System.out.println(e.getMessage());
        }
    }
}