package j8spec;

public final class ItBlockConfig {

    private final Runnable body;

    public static ItBlockConfig newItBlockConfig(Runnable body) {
        return new ItBlockConfig(body);
    }

    private ItBlockConfig(Runnable body) {
        this.body = body;
    }

    Runnable body() {
        return body;
    }
}
