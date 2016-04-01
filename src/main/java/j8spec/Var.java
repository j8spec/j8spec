package j8spec;

/**
 * Wrapper object to allow "final" variables to have their value modified.
 * @since 1.0.0
 */
public final class Var<T> {

    T value;

    /**
     * Creates a wrapper object to allow "final" variables to have their value modified. The initial
     * value is <code>null</code>.
     *
     * @param <T> type of value the variable object can store
     * @return new variable object
     * @deprecated use instead {@link J8Spec#var()}
     */
    @Deprecated
    public static <T> Var<T> var() {
        return new Var<>();
    }

    /**
     * Access the value stored in the given variable object.
     *
     * @param var variable object
     * @param <T> type of value the variable object can store
     * @return value stored in the variable object
     * @deprecated use instead {@link J8Spec#var(Var)}
     */
    @Deprecated
    public static <T> T var(Var<T> var) {
        return var.value;
    }

    /**
     * Stores the given value in the provided variable object.
     *
     * @param var variable object
     * @param value value to be stored
     * @param <T> type of value the variable object can store
     * @return value stored in the variable object
     * @deprecated use instead {@link J8Spec#var(Var, Object)}
     */
    @Deprecated
    public static <T> T var(Var<T> var, T value) {
        return var.value = value;
    }

    Var() {}
}
