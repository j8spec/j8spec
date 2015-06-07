package j8spec;

public final class BeforeBlock implements Runnable {

    private final Runnable body;
    private final boolean justOnce;
    private boolean onceAlready;

    static BeforeBlock newBeforeAllBlock(Runnable body) {
        return new BeforeBlock(body, true);
    }

    static BeforeBlock newBeforeEachBlock(Runnable body) {
        return new BeforeBlock(body, false);
    }

    private BeforeBlock(Runnable body, boolean justOnce) {
        this.body = body;
        this.justOnce = justOnce;
        this.onceAlready = false;
    }

    @Override
    public void run() {
        if (this.justOnce && this.onceAlready) {
            return;
        }

        onceAlready = true;
        body.run();
    }

    boolean justOnce() {
        return justOnce;
    }

    Runnable body() {
        return body;
    }
}
