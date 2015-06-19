package j8spec;

public final class ItBlockDefinition {

    private final Runnable body;
    private final boolean ignored;
    private final boolean focused;

    public static ItBlockDefinition newItBlockDefinition(Runnable body) {
        return new ItBlockDefinition(body, false, false);
    }

    public static ItBlockDefinition newIgnoredItBlockDefinition(Runnable body) {
        return new ItBlockDefinition(body, true, false);
    }

    public static ItBlockDefinition newFocusedItBlockDefinition(Runnable body) {
        return new ItBlockDefinition(body, false, true);
    }

    private ItBlockDefinition(Runnable body, boolean ignored, boolean focused) {
        this.body = body;
        this.ignored = ignored;
        this.focused = focused;
    }

    Runnable body() {
        return body;
    }

    public boolean ignored() {
        return ignored;
    }

    public boolean focused() {
        return focused;
    }
}
