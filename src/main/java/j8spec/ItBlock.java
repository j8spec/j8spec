package j8spec;

import java.util.List;

import static java.util.Collections.unmodifiableList;

public final class ItBlock implements Runnable {

    private final List<String> containerDescriptions;
    private final String description;
    private final List<Runnable> beforeEachBlocks;
    private final Runnable body;

    public static ItBlock newItBlock(List<String> containerDescriptions, String description, List<Runnable> beforeEachBlocks, Runnable body) {
        return new ItBlock(containerDescriptions, description, beforeEachBlocks, body);
    }

    private ItBlock(List<String> containerDescriptions, String description, List<Runnable> beforeEachBlocks, Runnable body) {
        this.containerDescriptions = unmodifiableList(containerDescriptions);
        this.description = description;
        this.beforeEachBlocks = unmodifiableList(beforeEachBlocks);
        this.body = body;
    }

    public String getDescription() {
        return description;
    }

    public Runnable getBody() {
        return body;
    }

    public List<Runnable> beforeEachBlocks() {
        return beforeEachBlocks;
    }

    public List<String> containerDescriptions() {
        return containerDescriptions;
    }

    @Override
    public void run() {
        body.run();
    }
}
