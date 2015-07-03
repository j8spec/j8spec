package j8spec;

/**
 * {@link ItBlockDefinition} builder.
 *
 * @since 2.0.0
 */
public final class ItBlockDefinitionBuilder {

    private Runnable body;
    private Class<? extends Throwable> expectedException;

    /**
     * Defines the exception class the new "it" block is expected to throw.
     *
     * @param expectedException exception class
     * @return this
     * @since 2.0.0
     */
    public ItBlockDefinitionBuilder expected(Class<? extends Throwable> expectedException) {
        this.expectedException = expectedException;
        return this;
    }

    ItBlockDefinitionBuilder body(Runnable body) {
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
