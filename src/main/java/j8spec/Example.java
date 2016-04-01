package j8spec;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static j8spec.UnsafeBlock.tryToExecuteAll;

/**
 * Example ready to be executed.
 * @since 3.0.0
 */
public final class Example implements UnsafeBlock, Comparable<Example> {

    static final class Builder {

        private List<String> containerDescriptions = emptyList();
        private String description;
        private List<VarInitializer<?>> varInitializers = emptyList();
        private List<Hook> beforeAllHooks = emptyList();
        private List<Hook> beforeEachHooks = emptyList();
        private List<Hook> afterEachHooks = emptyList();
        private List<Hook> afterAllHooks = emptyList();
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

        Builder beforeAllHooks(List<Hook> beforeAllHooks) {
            this.beforeAllHooks = beforeAllHooks;
            return this;
        }

        Builder beforeEachHooks(List<Hook> beforeHooks) {
            this.beforeEachHooks = beforeHooks;
            return this;
        }

        Builder afterEachHooks(List<Hook> afterHooks) {
            this.afterEachHooks = afterHooks;
            return this;
        }

        Builder afterAllHooks(List<Hook> afterAllHooks) {
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
    private final List<Hook> beforeAllHooks;
    private final List<Hook> beforeEachHooks;
    private final List<Hook> afterEachHooks;
    private final List<Hook> afterAllHooks;
    private final UnsafeBlock block;
    private final Class<? extends Throwable> expectedException;
    private final long timeout;
    private final TimeUnit timeoutUnit;
    private final Rank rank;

    private Example(
        List<String> containerDescriptions,
        String description,
        List<VarInitializer<?>> varInitializers,
        List<Hook> beforeAllHooks,
        List<Hook> beforeEachHooks,
        List<Hook> afterEachHooks,
        List<Hook> afterAllHooks,
        UnsafeBlock block,
        Class<? extends Throwable> expectedException,
        long timeout,
        TimeUnit timeoutUnit,
        Rank rank
    ) {
        this.containerDescriptions = unmodifiableList(containerDescriptions);
        this.description = description;
        this.varInitializers = unmodifiableList(varInitializers);
        this.beforeAllHooks = beforeAllHooks;
        this.beforeEachHooks = unmodifiableList(beforeEachHooks);
        this.afterEachHooks = unmodifiableList(afterEachHooks);
        this.afterAllHooks = afterAllHooks;
        this.block = block;
        this.expectedException = expectedException;
        this.timeout = timeout;
        this.timeoutUnit = timeoutUnit;
        this.rank = rank;
    }

    @Override
    public int compareTo(Example block) {
        return rank.compareTo(block.rank);
    }

    /**
     * Runs this example and associated hooks.
     * @since 2.0.0
     */
    @Override
    public void tryToExecute() throws Throwable {
        tryToExecuteAll(varInitializers);

        tryToExecuteAll(beforeAllHooks);
        tryToExecuteAll(beforeEachHooks);

        block.tryToExecute();

        tryToExecuteAll(afterEachHooks);
        tryToExecuteAll(afterAllHooks);
    }

    /**
     * @return textual description
     * @since 2.0.0
     */
    public String description() {
        return description;
    }

    /**
     * @return textual description of all outer example groups
     * @since 2.0.0
     */
    public List<String> containerDescriptions() {
        return containerDescriptions;
    }

    /**
     * @return <code>true</code> if this example should be ignored, <code>false</code> otherwise
     * @since 2.0.0
     */
    public boolean shouldBeIgnored() {
        return block == NOOP;
    }

    /**
     * @return exception class this example is expected to throw, <code>null</code> otherwise
     * @see #isExpectedToThrowAnException()
     * @since 2.0.0
     */
    public Class<? extends Throwable> expected() {
        return expectedException;
    }

    /**
     * @return <code>true</code> if this example is expected to throw an exception, <code>false</code> otherwise
     * @see #expected()
     * @since 2.0.0
     */
    public boolean isExpectedToThrowAnException() {
        return expectedException != null;
    }

    /**
     * @return <code>true</code> if this example is expected to fail if it takes to long, <code>false</code> otherwise
     * @see #timeout()
     * @since 3.0.0
     */
    public boolean shouldFailOnTimeout() {
        return timeout != 0;
    }

    /**
     * @return time to wait before timing out the example
     * @see #timeoutUnit()
     * @since 3.0.0
     */
    public long timeout() {
        return timeout;
    }

    /**
     * @return the time unit for the {@link #timeout()}
     * @see #timeout()
     * @since 3.0.0
     */
    public TimeUnit timeoutUnit() {
        return timeoutUnit;
    }
}
