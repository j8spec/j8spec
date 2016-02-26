package j8spec;

/**
 * @see j8spec.SafeBlock
 * @since 3.0.0
 */
@FunctionalInterface
public interface UnsafeBlock {

    UnsafeBlock NOOP = () -> {};

    void tryToExecute() throws Throwable;
}
