package j8spec;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

/**
 * Example ready to be executed.
 * @since 3.0.0
 */
public final class Example implements UnsafeBlock, Comparable<Example> {

    private static final Predicate<UnsafeBlock> ALL = whatever -> true;

    static final class Builder {

        private List<String> containerDescriptions = emptyList();
        private String description;
        private List<VarInitializer<?>> varInitializers = emptyList();
        private List<UnsafeBlock> beforeAllHooks = emptyList();
        private List<UnsafeBlock> beforeEachHooks = emptyList();
        private List<UnsafeBlock> afterEachHooks = emptyList();
        private List<UnsafeBlock> afterAllHooks = emptyList();
        private UnsafeBlock block;
        private Class<? extends Throwable> expectedException;
        private long timeout;
        private TimeUnit timeoutUnit;
        private Rank rank;

        Builder containerDescriptions(List<String> containerDescriptions) {
            this.containerDescriptions = containerDescriptions;
            return this;
        }

        Builder description(String description) {
            this.description = description;
            return this;
        }

        Builder varInitializers(List<VarInitializer<?>> varInitializers) {
            this.varInitializers = varInitializers;
            return this;
        }

        Builder beforeAllHooks(List<UnsafeBlock> beforeAllHooks) {
            this.beforeAllHooks = beforeAllHooks;
            return this;
        }

        Builder beforeEachHooks(List<UnsafeBlock> beforeHooks) {
            this.beforeEachHooks = beforeHooks;
            return this;
        }

        Builder afterEachHooks(List<UnsafeBlock> afterHooks) {
            this.afterEachHooks = afterHooks;
            return this;
        }

        Builder afterAllHooks(List<UnsafeBlock> afterAllHooks) {
            this.afterAllHooks = afterAllHooks;
            return this;
        }

        Builder block(UnsafeBlock block) {
            this.block = block;
            return this;
        }

        Builder expectedException(Class<? extends Throwable> expectedException) {
            this.expectedException = expectedException;
            return this;
        }

        Builder rank(Rank rank) {
            this.rank = rank;
            return this;
        }

        Builder ignored() {
            this.block = NOOP;
            return this;
        }

        Builder timeout(long timeout, TimeUnit unit) {
            this.timeout = timeout;
            this.timeoutUnit = unit;
            return this;
        }

        Example build() {
            return new Example(
                containerDescriptions,
                description,
                varInitializers,
                beforeAllHooks,
                beforeEachHooks,
                afterEachHooks,
                afterAllHooks,
                block,
                expectedException,
                timeout,
                timeoutUnit,
                rank
            );
        }
    }

    private final List<String> containerDescriptions;
    private final String description;
    private final List<VarInitializer<?>> varInitializers;
    private final List<UnsafeBlock> beforeAllHooks;
    private final List<UnsafeBlock> beforeEachHooks;
    private final List<UnsafeBlock> afterEachHooks;
    private final List<UnsafeBlock> afterAllHooks;
    private final UnsafeBlock block;
    private final Class<? extends Throwable> expectedException;
    private final long timeout;
    private final TimeUnit timeoutUnit;
    private final Rank rank;

    private Example previous;
    private Example next;
    private boolean beforeAllHookFailed = false;

    private Example(
        List<String> containerDescriptions,
        String description,
        List<VarInitializer<?>> varInitializers,
        List<UnsafeBlock> beforeAllHooks,
        List<UnsafeBlock> beforeEachHooks,
        List<UnsafeBlock> afterEachHooks,
        List<UnsafeBlock> afterAllHooks,
        UnsafeBlock block,
        Class<? extends Throwable> expectedException,
        long timeout,
        TimeUnit timeoutUnit,
        Rank rank
    ) {
        this.containerDescriptions = unmodifiableList(containerDescriptions);
        this.description = description;
        this.varInitializers = unmodifiableList(varInitializers);
        this.beforeAllHooks = unmodifiableList(beforeAllHooks);
        this.beforeEachHooks = unmodifiableList(beforeEachHooks);
        this.afterEachHooks = unmodifiableList(afterEachHooks);
        this.afterAllHooks = unmodifiableList(afterAllHooks);
        this.block = block;
        this.expectedException = expectedException;
        this.timeout = timeout;
        this.timeoutUnit = timeoutUnit;
        this.rank = rank;
    }

    void previous(Example example) { previous = example; }

    void next(Example example) { next = example; }

    @Override
    public int compareTo(Example block) { return rank.compareTo(block.rank); }

    /**
     * Runs this example and associated hooks.
     * @since 2.0.0
     */
    @Override
    public void tryToExecute() throws Throwable {
        Exceptions.Collector collector = new Exceptions.Collector();

        varInitializers.forEach(collector::executeOrSkip);
        collector.haltOnFailure();

        beforeAllHooks.stream().filter(firstChance()).forEach(collector::executeOrSkip);
        beforeAllHookFailed = !collector.isEmpty();
        collector.haltOnFailure();

        beforeEachHooks.forEach(collector::executeOrSkip);
        collector.haltOnFailure();

        collector.execute(block);
        afterEachHooks.forEach(collector::execute);
        afterAllHooks.stream().filter(lastChance()).forEach(collector::execute);
        collector.haltOnFailure();
    }

    private Predicate<UnsafeBlock> firstChance() {
        return previous == null ? ALL : hook -> !previous.hasBeforeAllHook(hook);
    }

    private Predicate<UnsafeBlock> lastChance() {
        return next == null ? ALL : hook -> !next.hasAfterAllHook(hook);
    }

    private boolean hasBeforeAllHook(UnsafeBlock hook) {
        return beforeAllHooks.contains(hook) || previous != null && previous.hasBeforeAllHook(hook);
    }

    private boolean hasAfterAllHook(UnsafeBlock hook) {
        return afterAllHooks.contains(hook) || next != null && next.hasAfterAllHook(hook);
    }

    /**
     * @return textual description
     * @since 2.0.0
     */
    public String description() { return description; }

    /**
     * @return textual description of all outer example groups
     * @since 2.0.0
     */
    public List<String> containerDescriptions() { return containerDescriptions; }

    /**
     * @return <code>true</code> if this example should be ignored, <code>false</code> otherwise
     * @since 2.0.0
     */
    public boolean shouldBeIgnored() { return block == NOOP || hasAnyBeforeAllHookFailed(); }

    private boolean hasAnyBeforeAllHookFailed() {
        return beforeAllHookFailed || previous != null && previous.hasAnyBeforeAllHookFailed();
    }

    /**
     * @return exception class this example is expected to throw, <code>null</code> otherwise
     * @see #isExpectedToThrowAnException()
     * @since 2.0.0
     */
    public Class<? extends Throwable> expected() { return expectedException; }

    /**
     * @return <code>true</code> if this example is expected to throw an exception, <code>false</code> otherwise
     * @see #expected()
     * @since 2.0.0
     */
    public boolean isExpectedToThrowAnException() { return expectedException != null; }

    /**
     * @return <code>true</code> if this example is expected to fail if it takes to long, <code>false</code> otherwise
     * @see #timeout()
     * @since 3.0.0
     */
    public boolean shouldFailOnTimeout() { return timeout != 0; }

    /**
     * @return time to wait before timing out the example
     * @see #timeoutUnit()
     * @since 3.0.0
     */
    public long timeout() { return timeout; }

    /**
     * @return the time unit for the {@link #timeout()}
     * @see #timeout()
     * @since 3.0.0
     */
    public TimeUnit timeoutUnit() { return timeoutUnit; }
}
