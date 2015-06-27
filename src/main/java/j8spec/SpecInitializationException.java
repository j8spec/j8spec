package j8spec;

/**
 * @since 1.0.0
 */
public class SpecInitializationException extends J8SpecException {
    SpecInitializationException(String message, Exception e) {
        super(message, e);
    }
}
