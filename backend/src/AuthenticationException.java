/**
 * Domain-specific exception thrown when authentication or credential validation fails.
 * Demonstrates:
 * - Exception Encapsulation
 * - Domain-Driven Design (DDD) Error Modeling
 */
public class AuthenticationException extends Exception {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
