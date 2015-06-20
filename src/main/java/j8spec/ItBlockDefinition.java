package j8spec;

import static j8spec.SelectionFlag.DEFAULT;
import static j8spec.SelectionFlag.FOCUSED;
import static j8spec.SelectionFlag.IGNORED;

public final class ItBlockDefinition {

    private final Runnable body;
    private final SelectionFlag selectionFlag;

    public static ItBlockDefinition newItBlockDefinition(Runnable body) {
        return new ItBlockDefinition(body, DEFAULT);
    }

    public static ItBlockDefinition newIgnoredItBlockDefinition(Runnable body) {
        return new ItBlockDefinition(body, IGNORED);
    }

    public static ItBlockDefinition newFocusedItBlockDefinition(Runnable body) {
        return new ItBlockDefinition(body, FOCUSED);
    }

    private ItBlockDefinition(Runnable body, SelectionFlag selectionFlag) {
        this.body = body;
        this.selectionFlag = selectionFlag;
    }

    Runnable body() {
        return body;
    }

    boolean ignored() {
        return IGNORED.equals(selectionFlag);
    }

    boolean focused() {
        return FOCUSED.equals(selectionFlag);
    }
}
