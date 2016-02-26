package j8spec;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

/**
 * Representation of a "it" block ready to be executed.
 * @since 1.0.0
 */
public final class ItBlock implements UnsafeBlock {

    private final List<String> containerDescriptions;
    private final String description;
    private final List<BeforeBlock> beforeBlocks;
    private final UnsafeBlock body;
    private final Class<? extends Throwable> expectedException;

    static ItBlock newItBlock(
        List<String> containerDescriptions,
        String description,
        List<BeforeBlock> beforeBlocks,
        UnsafeBlock body
    ) {
        return new ItBlock(containerDescriptions, description, beforeBlocks, body, null);
    }

    static ItBlock newItBlock(
        List<String> containerDescriptions,
        String description,
        List<BeforeBlock> beforeBlocks,
        UnsafeBlock body,
        Class<? extends Throwable> expectedException
    ) {
        return new ItBlock(containerDescriptions, description, beforeBlocks, body, expectedException);
    }

    static ItBlock newIgnoredItBlock(List<String> containerDescriptions, String description) {
        return new ItBlock(containerDescriptions, description, emptyList(), NOOP, null);
    }

    private ItBlock(
        List<String> containerDescriptions,
        String description,
        List<BeforeBlock> beforeBlocks,
        UnsafeBlock body,
        Class<? extends Throwable> expectedException
    ) {
        this.containerDescriptions = unmodifiableList(containerDescriptions);
        this.description = description;
        this.beforeBlocks = unmodifiableList(beforeBlocks);
        this.body = body;
        this.expectedException = expectedException;
    }

    /**
     * @return textual description
     * @since 2.0.0
     */
    public String description() {
        return description;
    }

    /**
     * @return textual description of all outer "describe" blocks
     * @since 2.0.0
     */
    public List<String> containerDescriptions() {
        return containerDescriptions;
    }

    /**
     * Runs this block and associated setup code.
     * @since 2.0.0
     */
    @Override
    public void run() throws Throwable {
        for (BeforeBlock beforeBlock : beforeBlocks) {
            beforeBlock.run();
        }
        body.run();
    }

    /**
     * @return <code>true</code> if this block should be ignored, <code>false</code> otherwise
     * @since 2.0.0
     */
    public boolean shouldBeIgnored() {
        return body == NOOP;
    }

    /**
     * @return exception class this block is expected to throw, <code>null</code> otherwise
     * @see #isExpectedToThrowAnException()
     * @since 2.0.0
     */
    public Class<? extends Throwable> expected() {
        return expectedException;
    }

    /**
     * @return <code>true</code> if this block is expected to throw an exception, <code>false</code> otherwise
     * @see #expected()
     * @since 2.0.0
     */
    public boolean isExpectedToThrowAnException() {
        return expectedException != null;
    }
}
