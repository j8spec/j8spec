package j8spec;

import static j8spec.BlockExecutionFlag.DEFAULT;
import static j8spec.BlockExecutionFlag.FOCUSED;
import static j8spec.BlockExecutionFlag.IGNORED;

public final class ItBlockDefinition {

    private final Runnable body;
    private final BlockExecutionFlag executionFlag;

    public static ItBlockDefinition newItBlockDefinition(Runnable body) {
        return new ItBlockDefinition(body, DEFAULT);
    }

    public static ItBlockDefinition newIgnoredItBlockDefinition(Runnable body) {
        return new ItBlockDefinition(body, IGNORED);
    }

    public static ItBlockDefinition newFocusedItBlockDefinition(Runnable body) {
        return new ItBlockDefinition(body, FOCUSED);
    }

    private ItBlockDefinition(Runnable body, BlockExecutionFlag executionFlag) {
        this.body = body;
        this.executionFlag = executionFlag;
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
}
