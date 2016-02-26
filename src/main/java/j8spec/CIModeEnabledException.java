package j8spec;

/**
 * Thrown when a method that ignores or focuses a block
 * (like {@link J8Spec#xit(String, UnsafeBlock) xit} or {@link J8Spec#fit(String, UnsafeBlock) fit})
 * is used while the system property <code>j8spec.ci.mode</code> is <code>true</code>.
 * @since 2.0.0
 */
public class CIModeEnabledException extends J8SpecException {
    CIModeEnabledException(String message) {
        super(message);
    }
}
