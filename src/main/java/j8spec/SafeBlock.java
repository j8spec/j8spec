package j8spec;

/**
 * @since 3.0.0
 */
@FunctionalInterface
public interface SafeBlock {

    SafeBlock NOOP = () -> {};

    void run();
}
