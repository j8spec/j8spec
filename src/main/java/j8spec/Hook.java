package j8spec;

final class Hook implements UnsafeBlock {

    private final UnsafeBlock block;
    private final boolean justOnce;
    private boolean onceAlready;

    static Hook newOneTimeHook(UnsafeBlock block) {
        return new Hook(block, true);
    }

    static Hook newHook(UnsafeBlock block) {
        return new Hook(block, false);
    }

    private Hook(UnsafeBlock block, boolean justOnce) {
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
