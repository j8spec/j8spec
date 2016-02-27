package j8spec;

import static j8spec.BlockExecutionFlag.DEFAULT;
import static j8spec.BlockExecutionFlag.FOCUSED;
import static j8spec.BlockExecutionFlag.IGNORED;

final class ItBlockDefinition {

    private final String description;
    private final UnsafeBlock block;
    private final BlockExecutionFlag executionFlag;
    private final Class<? extends Throwable> expectedException;

    static ItBlockDefinition newItBlockDefinition(
        String description,
        UnsafeBlock block,
        Class<? extends Throwable> expectedException
    ) {
        return new ItBlockDefinition(description, block, DEFAULT, expectedException);
    }

    static ItBlockDefinition newItBlockDefinition(
        String description,
        UnsafeBlock block
    ) {
        return newItBlockDefinition(description, block, null);
    }

    static ItBlockDefinition newIgnoredItBlockDefinition(
        String description,
        UnsafeBlock block,
        Class<? extends Throwable> expectedException
    ) {
        return new ItBlockDefinition(description, block, IGNORED, expectedException);
    }

    static ItBlockDefinition newIgnoredItBlockDefinition(
        String description,
        UnsafeBlock block
    ) {
        return newIgnoredItBlockDefinition(description, block, null);
    }

    static ItBlockDefinition newFocusedItBlockDefinition(
        String description,
        UnsafeBlock block,
        Class<? extends Throwable> expectedException
    ) {
        return new ItBlockDefinition(description, block, FOCUSED, expectedException);
    }

    static ItBlockDefinition newFocusedItBlockDefinition(
        String description,
        UnsafeBlock block
    ) {
        return newFocusedItBlockDefinition(description, block, null);
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
}
