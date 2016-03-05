package j8spec;

final class BeforeHook implements UnsafeBlock {

    private final UnsafeBlock block;
    private final boolean justOnce;
    private boolean onceAlready;

    static BeforeHook newBeforeAllBlock(UnsafeBlock block) {
        return new BeforeHook(block, true);
    }

    static BeforeHook newBeforeEachBlock(UnsafeBlock block) {
        return new BeforeHook(block, false);
    }

    private BeforeHook(UnsafeBlock block, boolean justOnce) {
        this.block = block;
        this.justOnce = justOnce;
        this.onceAlready = false;
    }

    @Override
    public void tryToExecute() throws Throwable {
        if (this.justOnce && this.onceAlready) {
            return;
        }

        onceAlready = true;
        block.tryToExecute();
    }
}
