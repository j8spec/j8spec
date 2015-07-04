package j8spec;

/**
 * Thrown when a block definition method is called outside the context of the {@link J8Spec#read(Class)} method.
 * @since 1.0.0
 */
public class IllegalContextException extends J8SpecException {
    IllegalContextException(String message) {
        super(message);
    }
}
