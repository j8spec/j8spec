package j8spec;

/**
 * Represents a "it" block configuration.
 * @since 2.0.0
 */
public final class ItBlockConfiguration {

    private Runnable body;
    private Class<? extends Throwable> expectedException;

    static ItBlockConfiguration newItBlockConfiguration() {
        return new ItBlockConfiguration();
    }

    private ItBlockConfiguration() {}

    /**
     * Defines the exception class the new "it" block is expected to throw.
     *
     * @param expectedException exception class
     * @return this
     * @since 2.0.0
     */
    public ItBlockConfiguration expected(Class<? extends Throwable> expectedException) {
        this.expectedException = expectedException;
        return this;
    }

    ItBlockConfiguration body(Runnable body) {
        this.body = body;
        return this;
    }

    ItBlockDefinition newItBlockDefinition() {
        return ItBlockDefinition.newItBlockDefinition(body, expectedException);
    }

    ItBlockDefinition newIgnoredItBlockDefinition() {
        return ItBlockDefinition.newIgnoredItBlockDefinition(body, expectedException);
    }

    ItBlockDefinition newFocusedItBlockDefinition() {
        return ItBlockDefinition.newFocusedItBlockDefinition(body, expectedException);
    }
}
