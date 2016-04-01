package j8spec;

import java.util.Collection;

/**
 * Block of code that is unsafe to execute, i.e. it could throw checked exceptions.
 *
 * @see j8spec.SafeBlock
 * @since 3.0.0
 */
@FunctionalInterface
public interface UnsafeBlock {

    /**
     * No operation block.
     */
    UnsafeBlock NOOP = () -> {};

    /**
     * Tries to execute a collection of unsafe blocks.
     *
     * @param blocks collection of unsafe blocks to execute
     * @throws Throwable if unable to execute one of the blocks of code
     * @since  3.1.0
     */
    static void tryToExecuteAll(Collection<? extends UnsafeBlock> blocks) throws Throwable {
        for (UnsafeBlock block : blocks) {
            block.tryToExecute();
        }
    }

    /**
     * Try to execute the block of code.
     *
     * @throws Throwable if unable to execute the block of code
     */
    void tryToExecute() throws Throwable;
}
