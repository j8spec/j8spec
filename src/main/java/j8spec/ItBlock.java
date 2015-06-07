package j8spec;

import java.util.List;

import static java.util.Collections.unmodifiableList;

public final class ItBlock implements Runnable {

    private final List<String> containerDescriptions;
    private final String description;
    private final List<BeforeBlock> beforeBlocks;
    private final Runnable body;

    static ItBlock newItBlock(
        List<String> containerDescriptions,
        String description,
        List<BeforeBlock> beforeBlocks,
        Runnable body
    ) {
        return new ItBlock(containerDescriptions, description, beforeBlocks, body);
    }

    private ItBlock(
        List<String> containerDescriptions,
        String description,
        List<BeforeBlock> beforeBlocks,
        Runnable body
    ) {
        this.containerDescriptions = unmodifiableList(containerDescriptions);
        this.description = description;
        this.beforeBlocks = unmodifiableList(beforeBlocks);
        this.body = body;
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

    List<BeforeBlock> beforeBlocks() {
        return beforeBlocks;
    }

    Runnable body() {
        return body;
    }
}
