package j8spec;

import static j8spec.BlockExecutionFlag.DEFAULT;
import static j8spec.BlockExecutionFlag.FOCUSED;
import static j8spec.BlockExecutionFlag.IGNORED;

final class ItBlockDefinition {

    private final Runnable body;
    private final BlockExecutionFlag executionFlag;
    private final Class<? extends Throwable> expectedException;

    static ItBlockDefinition newItBlockDefinition(Runnable body) {
        return new ItBlockDefinition(body, DEFAULT, null);
    }

    static ItBlockDefinition newItBlockDefinition(Runnable body, Class<? extends Throwable> expectedException) {
        return new ItBlockDefinition(body, DEFAULT, expectedException);
    }

    static ItBlockDefinition newIgnoredItBlockDefinition(Runnable body) {
        return new ItBlockDefinition(body, IGNORED, null);
    }

    static ItBlockDefinition newIgnoredItBlockDefinition(Runnable body, Class<? extends Throwable> expectedException) {
        return new ItBlockDefinition(body, IGNORED, expectedException);
    }

    static ItBlockDefinition newFocusedItBlockDefinition(Runnable body) {
        return new ItBlockDefinition(body, FOCUSED, null);
    }

    static ItBlockDefinition newFocusedItBlockDefinition(Runnable body, Class<? extends Throwable> expectedException) {
        return new ItBlockDefinition(body, FOCUSED, expectedException);
    }

    private ItBlockDefinition(Runnable body, BlockExecutionFlag executionFlag, Class<? extends Throwable> expectedException) {
        this.body = body;
        this.executionFlag = executionFlag;
        this.expectedException = expectedException;
    }

    Runnable body() {
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
