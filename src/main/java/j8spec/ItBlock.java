package j8spec;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

public final class ItBlock implements Runnable {

    private static final Runnable NOOP = () -> {};

    private final List<String> containerDescriptions;
    private final String description;
    private final List<BeforeBlock> beforeBlocks;
    private final Runnable body;
    private final Class<? extends Throwable> expectedException;

    static ItBlock newItBlock(
        List<String> containerDescriptions,
        String description,
        List<BeforeBlock> beforeBlocks,
        Runnable body
    ) {
        return new ItBlock(containerDescriptions, description, beforeBlocks, body, null);
    }

    static ItBlock newItBlock(
        List<String> containerDescriptions,
        String description,
        List<BeforeBlock> beforeBlocks,
        Runnable body,
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
        Runnable body,
        Class<? extends Throwable> expectedException
    ) {
        this.containerDescriptions = unmodifiableList(containerDescriptions);
        this.description = description;
        this.beforeBlocks = unmodifiableList(beforeBlocks);
        this.body = body;
        this.expectedException = expectedException;
    }

    public String description() {
        return description;
    }

    public List<String> containerDescriptions() {
        return containerDescriptions;
    }

    @Override
    public void run() {
        beforeBlocks.forEach(Runnable::run);
        body.run();
    }

    public boolean shouldBeIgnored() {
        return body == NOOP;
    }

    public Class<? extends Throwable> expected() {
        return expectedException;
    }

    public boolean isExpectedToThrowAnException() {
        return expectedException != null;
    }

    List<BeforeBlock> beforeBlocks() {
        return beforeBlocks;
    }

    Runnable body() {
        return body;
    }
}
