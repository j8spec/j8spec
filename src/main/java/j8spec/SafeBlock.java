package j8spec;

/**
 * Block of code that is safe to execute, i.e. it does not throw checked exceptions.
 *
 * @see j8spec.UnsafeBlock
 * @since 3.0.0
 */
@FunctionalInterface
public interface SafeBlock {

    /**
     * No operation block.
     */
    SafeBlock NOOP = () -> {};

    /**
     * Execute the block of code.
     */
    void execute();
}
