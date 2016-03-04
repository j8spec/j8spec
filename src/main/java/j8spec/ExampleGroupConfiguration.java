package j8spec;

import static j8spec.BlockExecutionOrder.DEFINED;
import static j8spec.BlockExecutionOrder.RANDOM;

/**
 * Represents a example group configuration.
 * @since 3.0.0
 */
public final class ExampleGroupConfiguration {

    static final class Builder {
        private String description;
        private BlockExecutionFlag executionFlag = BlockExecutionFlag.DEFAULT;
        private BlockExecutionOrder executionOrder = BlockExecutionOrder.DEFAULT;
        private long seed;

        Builder description(String description) {
            this.description = description;
            return this;
        }

        Builder executionFlag(BlockExecutionFlag executionFlag) {
            this.executionFlag = executionFlag;
            return this;
        }

        public Builder definedOrder() {
            this.executionOrder = DEFINED;
            return this;
        }

        public Builder randomOrder() {
            this.executionOrder = RANDOM;
            return this;
        }

        public Builder seed(long seed) {
            this.seed = seed;
            return this;
        }

        ExampleGroupConfiguration build() {
            return new ExampleGroupConfiguration(
                description,
                executionFlag,
                executionOrder,
                seed
            );
        }
    }

    private final String description;
    private final BlockExecutionFlag executionFlag;
    private final BlockExecutionOrder executionOrder;
    private final long seed;

    private ExampleGroupConfiguration(
        String description,
        BlockExecutionFlag executionFlag,
        BlockExecutionOrder executionOrder,
        long seed
    ) {
        this.description = description;
        this.executionFlag = executionFlag;
        this.executionOrder = executionOrder;
        this.seed = seed;
    }

    String description() {
        return description;
    }

    BlockExecutionFlag executionFlag() {
        return executionFlag;
    }

    BlockExecutionOrder executionOrder() {
        return executionOrder;
    }

    long seed() {
        return seed;
    }
}
