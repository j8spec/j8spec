package j8spec;

/**
 * @see j8spec.UnsafeBlock
 * @since 3.0.0
 */
@FunctionalInterface
public interface SafeBlock {

    SafeBlock NOOP = () -> {};

    void execute();
}
