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
        private Long seed;

        Builder description(String description) {
            this.description = description;
            return this;
        }

        Builder executionFlag(BlockExecutionFlag executionFlag) {
            this.executionFlag = executionFlag;
            return this;
        }

        Builder definedOrder() {
            this.executionOrder = DEFINED;
            return this;
        }

        Builder randomOrder() {
            this.executionOrder = RANDOM;
            return this;
        }

        Builder seed(Long seed) {
            if (Boolean.valueOf(System.getProperty("j8spec.ci.mode", "false"))) {
                throw new Exceptions.HardCodedSeedNotAllowedInCIMode();
            }

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
    private final Long seed;

    private ExampleGroupConfiguration(
        String description,
        BlockExecutionFlag executionFlag,
        BlockExecutionOrder executionOrder,
        Long seed
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

    Long seed() {
        return seed;
    }
}
