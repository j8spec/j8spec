package j8spec;

/**
 * Block of code that is unsafe to execute, i.e. it could throw checked exceptions.
 *
 * @see j8spec.SafeBlock
 * @since 3.0.0
 */
@FunctionalInterface
public interface UnsafeBlock {

    UnsafeBlock NOOP = () -> {};

    /**
     * Try to execute the block of code.
     *
     * @throws Throwable if unable to execute the block of code
     */
    void tryToExecute() throws Throwable;
}
