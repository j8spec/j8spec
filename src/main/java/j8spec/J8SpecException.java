package j8spec;

/**
 * @since 1.0.0
 */
public class J8SpecException extends RuntimeException {
    J8SpecException(String message, Exception e) {
        super(message, e);
    }

    J8SpecException(String message) {
        super(message);
    }
}
