package j8spec;

import static j8spec.BlockExecutionFlag.DEFAULT;
import static j8spec.BlockExecutionFlag.FOCUSED;
import static j8spec.BlockExecutionFlag.IGNORED;

final class ItBlockDefinition {

    private final UnsafeBlock body;
    private final BlockExecutionFlag executionFlag;
    private final Class<? extends Throwable> expectedException;

    static ItBlockDefinition newItBlockDefinition(UnsafeBlock body) {
        return new ItBlockDefinition(body, DEFAULT, null);
    }

    static ItBlockDefinition newItBlockDefinition(UnsafeBlock body, Class<? extends Throwable> expectedException) {
        return new ItBlockDefinition(body, DEFAULT, expectedException);
    }

    static ItBlockDefinition newIgnoredItBlockDefinition(UnsafeBlock body) {
        return new ItBlockDefinition(body, IGNORED, null);
    }

    static ItBlockDefinition newIgnoredItBlockDefinition(UnsafeBlock body, Class<? extends Throwable> expectedException) {
        return new ItBlockDefinition(body, IGNORED, expectedException);
    }

    static ItBlockDefinition newFocusedItBlockDefinition(UnsafeBlock body) {
        return new ItBlockDefinition(body, FOCUSED, null);
    }

    static ItBlockDefinition newFocusedItBlockDefinition(UnsafeBlock body, Class<? extends Throwable> expectedException) {
        return new ItBlockDefinition(body, FOCUSED, expectedException);
    }

    private ItBlockDefinition(UnsafeBlock body, BlockExecutionFlag executionFlag, Class<? extends Throwable> expectedException) {
        this.body = body;
        this.executionFlag = executionFlag;
        this.expectedException = expectedException;
    }

    UnsafeBlock body() {
        return body;
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
