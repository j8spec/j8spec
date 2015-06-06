package j8spec;

public final class BeforeBlock implements Runnable {

    private final Runnable body;
    private final boolean justOnce;

    static BeforeBlock newBeforeAllBlock(Runnable body) {
        return new BeforeBlock(body, true);
    }

    static BeforeBlock newBeforeEachBlock(Runnable body) {
        return new BeforeBlock(body, false);
    }

    private BeforeBlock(Runnable body, boolean justOnce) {
        this.body = body;
        this.justOnce = justOnce;
    }

    @Override
    public void run() {
        body.run();
    }

    public boolean justOnce() {
        return justOnce;
    }

    Runnable body() {
        return body;
    }
}
