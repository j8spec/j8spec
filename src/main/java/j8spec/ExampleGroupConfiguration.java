package j8spec;

import static j8spec.BlockExecutionFlag.DEFAULT;

/**
 * Represents a example group configuration.
 * @since 3.0.0
 */
public final class ExampleGroupConfiguration {

    static final class Builder {
        private String description;
        private BlockExecutionFlag executionFlag = DEFAULT;

        Builder description(String description) {
            this.description = description;
            return this;
        }

        Builder executionFlag(BlockExecutionFlag executionFlag) {
            this.executionFlag = executionFlag;
            return this;
        }

        ExampleGroupConfiguration build() {
            return new ExampleGroupConfiguration(
                description,
                executionFlag
            );
        }
    }

    private ExampleGroupConfiguration(String description, BlockExecutionFlag executionFlag) {
        this.description = description;
        this.executionFlag = executionFlag;
    }

    private final String description;
    private final BlockExecutionFlag executionFlag;

    String description() {
        return description;
    }

    BlockExecutionFlag executionFlag() {
        return executionFlag;
    }
}
