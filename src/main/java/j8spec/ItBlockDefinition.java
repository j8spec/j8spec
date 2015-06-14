package j8spec;

public final class ItBlockDefinition {

    private final Runnable body;

    public static ItBlockDefinition newItBlockDefinition(Runnable body) {
        return new ItBlockDefinition(body);
    }

    private ItBlockDefinition(Runnable body) {
        this.body = body;
    }

    Runnable body() {
        return body;
    }
}
