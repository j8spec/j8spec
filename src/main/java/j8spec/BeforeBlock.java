package j8spec;

final class BeforeBlock implements UnsafeBlock {

    private final UnsafeBlock body;
    private final boolean justOnce;
    private boolean onceAlready;

    static BeforeBlock newBeforeAllBlock(UnsafeBlock body) {
        return new BeforeBlock(body, true);
    }

    static BeforeBlock newBeforeEachBlock(UnsafeBlock body) {
        return new BeforeBlock(body, false);
    }

    private BeforeBlock(UnsafeBlock body, boolean justOnce) {
        this.body = body;
        this.justOnce = justOnce;
        this.onceAlready = false;
    }

    @Override
    public void run() throws Throwable {
        if (this.justOnce && this.onceAlready) {
            return;
        }

        onceAlready = true;
        body.run();
    }
}
