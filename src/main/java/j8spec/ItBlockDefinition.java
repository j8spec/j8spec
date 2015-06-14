package j8spec;

public final class ItBlockDefinition {

    private final Runnable body;
    private final boolean ignored;

    public static ItBlockDefinition newItBlockDefinition(Runnable body) {
        return new ItBlockDefinition(body, false);
    }

    public static ItBlockDefinition newIgnoredItBlockDefinition(Runnable body) {
        return new ItBlockDefinition(body, true);
    }

    private ItBlockDefinition(Runnable body, boolean ignored) {
        this.body = body;
        this.ignored = ignored;
    }

    Runnable body() {
        return body;
    }

    public boolean ignored() {
        return ignored;
    }
}
