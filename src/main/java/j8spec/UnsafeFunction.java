package j8spec;

/**
 * Represents a zero parameter function.
 *
 * @param <T> the type of results returned by this "function"
 * @see java.util.function.Supplier
 * @since 3.1.0
 */
@FunctionalInterface
public interface UnsafeFunction<T> {
    T tryToGet() throws Throwable;
}
