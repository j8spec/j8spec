package j8spec;

/**
 * Represents a "it" block configuration.
 * @since 2.0.0
 */
public final class ItBlockConfiguration {

    private String description;
    private UnsafeBlock block;
    private BlockExecutionFlag executionFlag;
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

    Class<? extends Throwable> expected() {
        return expectedException;
    }

    ItBlockConfiguration description(String description) {
        this.description = description;
        return this;
    }

    String description() {
        return description;
    }

    ItBlockConfiguration block(UnsafeBlock block) {
        this.block = block;
        return this;
    }

    UnsafeBlock block() {
        return block;
    }

    ItBlockConfiguration executionFlag(BlockExecutionFlag executionFlag) {
        this.executionFlag = executionFlag;
        return this;
    }

    BlockExecutionFlag executionFlag() {
        return executionFlag;
    }
}
