package j8spec;

import static j8spec.BlockExecutionFlag.DEFAULT;
import static j8spec.BlockExecutionFlag.FOCUSED;
import static j8spec.BlockExecutionFlag.IGNORED;

final class ItBlockDefinition {

    private final UnsafeBlock block;
    private final BlockExecutionFlag executionFlag;
    private final Class<? extends Throwable> expectedException;

    static ItBlockDefinition newItBlockDefinition(UnsafeBlock block) {
        return new ItBlockDefinition(block, DEFAULT, null);
    }

    static ItBlockDefinition newItBlockDefinition(UnsafeBlock block, Class<? extends Throwable> expectedException) {
        return new ItBlockDefinition(block, DEFAULT, expectedException);
    }

    static ItBlockDefinition newIgnoredItBlockDefinition(UnsafeBlock block) {
        return new ItBlockDefinition(block, IGNORED, null);
    }

    static ItBlockDefinition newIgnoredItBlockDefinition(UnsafeBlock block, Class<? extends Throwable> expectedException) {
        return new ItBlockDefinition(block, IGNORED, expectedException);
    }

    static ItBlockDefinition newFocusedItBlockDefinition(UnsafeBlock block) {
        return new ItBlockDefinition(block, FOCUSED, null);
    }

    static ItBlockDefinition newFocusedItBlockDefinition(UnsafeBlock block, Class<? extends Throwable> expectedException) {
        return new ItBlockDefinition(block, FOCUSED, expectedException);
    }

    private ItBlockDefinition(UnsafeBlock block, BlockExecutionFlag executionFlag, Class<? extends Throwable> expectedException) {
        this.block = block;
        this.executionFlag = executionFlag;
        this.expectedException = expectedException;
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
