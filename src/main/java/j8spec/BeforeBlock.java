package j8spec;

final class BeforeBlock implements UnsafeBlock {

    private final UnsafeBlock block;
    private final boolean justOnce;
    private boolean onceAlready;

    static BeforeBlock newBeforeAllBlock(UnsafeBlock block) {
        return new BeforeBlock(block, true);
    }

    static BeforeBlock newBeforeEachBlock(UnsafeBlock block) {
        return new BeforeBlock(block, false);
    }

    private BeforeBlock(UnsafeBlock block, boolean justOnce) {
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
