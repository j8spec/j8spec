package j8spec;

/**
 * Wrapper object to allow "final" variables to have their value modified.
 * @since 1.0.0
 */
public final class Var<T> {

    public static <T> Var<T> var() {
        return new Var<>();
    }

    public static <T> T var(Var<T> var) {
        return var.value;
    }

    public static <T> T var(Var<T> var, T value) {
        return var.value = value;
    }

    private T value;

    private Var() {}
}
