package j8spec;

/**
 * Represents a "it" block configuration.
 * @since 2.0.0
 */
public final class ItBlockConfiguration {

    public static final class Builder {

        private String description;
        private BlockExecutionFlag executionFlag;
        private Class<? extends Throwable> expectedException;

        Builder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * Defines the exception class the new "it" block is expected to throw.
         *
         * @param expectedException exception class
         * @return this
         * @since 2.0.0
         */
        public Builder expected(Class<? extends Throwable> expectedException) {
            this.expectedException = expectedException;
            return this;
        }

        Builder executionFlag(BlockExecutionFlag executionFlag) {
            this.executionFlag = executionFlag;
            return this;
        }

        ItBlockConfiguration build() {
            return new ItBlockConfiguration(
                description,
                executionFlag,
                expectedException
            );
        }
    }

    private final String description;
    private final BlockExecutionFlag executionFlag;
    private final Class<? extends Throwable> expectedException;

    private ItBlockConfiguration(
        String description,
        BlockExecutionFlag executionFlag,
        Class<? extends Throwable> expectedException
    ) {
        this.description = description;
        this.executionFlag = executionFlag;
        this.expectedException = expectedException;
    }

    String description() {
        return description;
    }

    Class<? extends Throwable> expectedException() {
        return expectedException;
    }

    BlockExecutionFlag executionFlag() {
        return executionFlag;
    }
}
