package j8spec;

import java.util.concurrent.TimeUnit;

import static j8spec.BlockExecutionFlag.DEFAULT;

/**
 * Represents an example block configuration.
 * @since 3.0.0
 */
public final class ExampleConfiguration {

    public static final class Builder {

        private String description;
        private BlockExecutionFlag executionFlag = DEFAULT;
        private Class<? extends Throwable> expectedException;
        private int timeout;
        private TimeUnit timeoutUnit;

        Builder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * Defines the exception class the new example is expected to throw.
         *
         * @param expectedException exception class
         * @return this
         * @since 3.0.0
         */
        public Builder expected(Class<? extends Throwable> expectedException) {
            this.expectedException = expectedException;
            return this;
        }

        /**
         * Specifies the time to wait before timing out the example.
         *
         * @param timeout the maximum time to wait
         * @param unit the time unit of the {@code timeout} argument
         * @return this
         * @since 3.0.0
         */
        public Builder timeout(int timeout, TimeUnit unit) {
            this.timeout = timeout;
            this.timeoutUnit = unit;
            return this;
        }

        Builder executionFlag(BlockExecutionFlag executionFlag) {
            this.executionFlag = executionFlag;
            return this;
        }

        ExampleConfiguration build() {
            return new ExampleConfiguration(
                description,
                executionFlag,
                expectedException,
                timeout,
                timeoutUnit
            );
        }
    }

    private final String description;
    private final BlockExecutionFlag executionFlag;
    private final Class<? extends Throwable> expectedException;
    private final long timeout;
    private final TimeUnit timeoutUnit;

    private ExampleConfiguration(
        String description,
        BlockExecutionFlag executionFlag,
        Class<? extends Throwable> expectedException,
        long timeout,
        TimeUnit timeoutUnit
    ) {
        this.description = description;
        this.executionFlag = executionFlag;
        this.expectedException = expectedException;
        this.timeout = timeout;
        this.timeoutUnit = timeoutUnit;
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

    long timeout() {
        return timeout;
    }

    TimeUnit timeoutUnit() {
        return timeoutUnit;
    }
}
