import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * ICheckoutDAO interface defining the data access contract for Checkout & Billing.
 * Demonstrates Abstraction and Interface Segregation Principle (ISP).
 */
public interface ICheckoutDAO {
    BigDecimal checkout(int bookingId) throws SQLException;
    BigDecimal calculateBill(int bookingId) throws SQLException;
    Bill getDetailedBillEntity(int bookingId) throws SQLException;
    Bill checkoutWithBillEntity(int bookingId) throws SQLException;
}
