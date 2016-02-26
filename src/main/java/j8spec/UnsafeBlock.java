package j8spec;

/**
 * @since 3.0.0
 */
@FunctionalInterface
public interface UnsafeBlock {

    UnsafeBlock NOOP = () -> {};

    void run() throws Throwable;
}
