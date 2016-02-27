package j8spec;

/**
 * Represents a "it" block configuration.
 * @since 2.0.0
 */
public final class ItBlockConfiguration {

    private String description;
    private UnsafeBlock block;
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

    ItBlockConfiguration description(String description) {
        this.description = description;
        return this;
    }

    ItBlockConfiguration block(UnsafeBlock block) {
        this.block = block;
        return this;
    }

    ItBlockDefinition newItBlockDefinition() {
        return ItBlockDefinition.newItBlockDefinition(description, block, expectedException);
    }

    ItBlockDefinition newIgnoredItBlockDefinition() {
        return ItBlockDefinition.newIgnoredItBlockDefinition(description, block, expectedException);
    }

    ItBlockDefinition newFocusedItBlockDefinition() {
        return ItBlockDefinition.newFocusedItBlockDefinition(description, block, expectedException);
    }
}
