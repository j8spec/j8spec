package j8spec;

import static j8spec.BlockExecutionFlag.FOCUSED;
import static j8spec.BlockExecutionFlag.IGNORED;

final class ItBlockDefinition implements BlockDefinition {

    private final String description;
    private final UnsafeBlock block;
    private final BlockExecutionFlag executionFlag;
    private final Class<? extends Throwable> expectedException;

    static ItBlockDefinition newItBlockDefinition(
        String description,
        UnsafeBlock block,
        BlockExecutionFlag executionFlag,
        Class<? extends Throwable> expectedException
    ) {
        return new ItBlockDefinition(description, block, executionFlag, expectedException);
    }

    static ItBlockDefinition newItBlockDefinition(
        String description,
        UnsafeBlock block,
        BlockExecutionFlag executionFlag
    ) {
        return newItBlockDefinition(description, block, executionFlag, null);
    }

    private ItBlockDefinition(
        String description,
        UnsafeBlock block,
        BlockExecutionFlag executionFlag,
        Class<? extends Throwable> expectedException
    ) {
        this.description = description;
        this.block = block;
        this.executionFlag = executionFlag;
        this.expectedException = expectedException;
    }

    String description() {
        return description;
    }

    UnsafeBlock block() {
        return block;
    }

    boolean ignored() {
        return IGNORED.equals(executionFlag);
    }

    boolean focused() {
        return FOCUSED.equals(executionFlag);
    }

    Class<? extends Throwable> expected() {
        return expectedException;
    }

    @Override
    public void accept(BlockDefinitionVisitor visitor) {
        visitor.example(description, block, executionFlag, expectedException);
    }
}
