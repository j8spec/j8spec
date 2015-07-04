package j8spec;

/**
 * Thrown when a block with the same description has already been defined in the same context.
 * @since 1.0.0
 */
public class BlockAlreadyDefinedException extends J8SpecException {
    BlockAlreadyDefinedException(String message) {
        super(message);
    }
}
