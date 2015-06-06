package j8spec;

public final class BeforeBlock implements Runnable {

    private final Runnable body;

    static BeforeBlock newBeforeEachBlock(Runnable body) {
        return new BeforeBlock(body);
    }

    private BeforeBlock(Runnable body) {
        this.body = body;
    }

    @Override
    public void run() {
        body.run();
    }

    Runnable body() {
        return body;
    }
}
