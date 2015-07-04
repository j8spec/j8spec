package j8spec;

/**
 * Thrown when an instance of the class that contains the spec definition cannot be instantiated.
 * @since 1.0.0
 */
public class SpecInitializationException extends J8SpecException {
    SpecInitializationException(String message, Exception e) {
        super(message, e);
    }
}
