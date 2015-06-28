package j8spec;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static j8spec.BlockExecutionFlag.*;
import static j8spec.ItBlock.newIgnoredItBlock;
import static j8spec.ItBlock.newItBlock;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

/**
 * @since 1.0.0
 */
public final class DescribeBlock {

    @FunctionalInterface
    private interface ShouldBeIgnoredPredicate {
        boolean test(DescribeBlock describeBlock, ItBlockDefinition itBlockDefinition);
    }

    private static final String LS = System.getProperty("line.separator");

    private final DescribeBlock parent;
    private final String description;
    private final List<Runnable> beforeAllBlocks;
    private final List<Runnable> beforeEachBlocks;
    private final Map<String, ItBlockDefinition> itBlocks;
    private final List<DescribeBlock> describeBlocks = new LinkedList<>();
    private final Class<?> specClass;
    private final BlockExecutionFlag executionFlag;

    static DescribeBlock newRootDescribeBlock(
        Class<?> specClass,
        List<Runnable> beforeAllBlocks,
        List<Runnable> beforeEachBlocks,
        Map<String, ItBlockDefinition> itBlocks
    ) {
        return new DescribeBlock(specClass, beforeAllBlocks, beforeEachBlocks, itBlocks);
    }

    private DescribeBlock(
        Class<?> specClass,
        List<Runnable> beforeAllBlocks,
        List<Runnable> beforeEachBlocks,
        Map<String, ItBlockDefinition> itBlocks
    ) {
        this.parent = null;
        this.specClass = specClass;
        this.description = specClass.getName();
        this.beforeAllBlocks = unmodifiableList(beforeAllBlocks);
        this.beforeEachBlocks = unmodifiableList(beforeEachBlocks);
        this.itBlocks = unmodifiableMap(itBlocks);
        this.executionFlag = DEFAULT;
    }

    private DescribeBlock(
        DescribeBlock parent,
        String description,
        List<Runnable> beforeAllBlocks,
        List<Runnable> beforeEachBlocks,
        Map<String, ItBlockDefinition> itBlocks,
        BlockExecutionFlag executionFlag
    ) {
        this.parent = parent;
        this.specClass = parent.specClass;
        this.description = description;
        this.beforeAllBlocks = unmodifiableList(beforeAllBlocks);
        this.beforeEachBlocks = unmodifiableList(beforeEachBlocks);
        this.itBlocks = unmodifiableMap(itBlocks);
        this.executionFlag = executionFlag;
    }

    DescribeBlock addDescribeBlock(
        String description,
        List<Runnable> beforeAllBlocks,
        List<Runnable> beforeEachBlocks,
        Map<String, ItBlockDefinition> itBlocks
    ) {
        DescribeBlock describeBlock = new DescribeBlock(this, description, beforeAllBlocks, beforeEachBlocks, itBlocks, DEFAULT);
        describeBlocks.add(describeBlock);
        return describeBlock;
    }

    DescribeBlock addIgnoredDescribeBlock(
        String description,
        List<Runnable> beforeAllBlocks,
        List<Runnable> beforeEachBlocks,
        Map<String, ItBlockDefinition> itBlocks
    ) {
        DescribeBlock describeBlock = new DescribeBlock(this, description, beforeAllBlocks, beforeEachBlocks, itBlocks, IGNORED);
        describeBlocks.add(describeBlock);
        return describeBlock;
    }

    DescribeBlock addFocusedDescribeBlock(
        String description,
        List<Runnable> beforeAllBlocks,
        List<Runnable> beforeEachBlocks,
        Map<String, ItBlockDefinition> itBlocks
    ) {
        DescribeBlock describeBlock = new DescribeBlock(this, description, beforeAllBlocks, beforeEachBlocks, itBlocks, FOCUSED);
        describeBlocks.add(describeBlock);
        return describeBlock;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        toString(sb, "");
        return sb.toString();
    }

    private void toString(StringBuilder sb, String indentation) {
        sb.append(indentation).append(description);

        for (Map.Entry<String, ItBlockDefinition> behavior : itBlocks.entrySet()) {
            sb.append(LS).append(indentation).append("  ").append(behavior.getKey());
        }

        for (DescribeBlock describeBlock : describeBlocks) {
            sb.append(LS);
            describeBlock.toString(sb, indentation + "  ");
        }
    }

    /**
     * @since 1.1.0
     */
    public Class<?> specClass() {
        return specClass;
    }

    /**
     * @since 1.0.0
     */
    public List<ItBlock> flattenItBlocks() {
        LinkedList<ItBlock> blocks = new LinkedList<>();
        collectItBlocks(blocks, new LinkedList<>(), shouldBeIgnoredPredicate());
        return blocks;
    }

    private ShouldBeIgnoredPredicate shouldBeIgnoredPredicate() {
        if (thereIsAtLeastOneFocusedBlock()) {
            return (p, b) -> !b.focused() && !p.containerFocused();
        }
        return (p, b) -> b.ignored() || p.containerIgnored();
    }

    private boolean thereIsAtLeastOneFocusedBlock() {
        return itBlocks.values().stream().anyMatch(ItBlockDefinition::focused)
            || describeBlocks.stream().anyMatch(b -> b.focused() || b.thereIsAtLeastOneFocusedBlock());
    }

    String description() {
        return description;
    }

    List<DescribeBlock> describeBlocks() {
        return new LinkedList<>(describeBlocks);
    }

    List<Runnable> beforeAllBlocks() {
        return beforeAllBlocks;
    }

    List<Runnable> beforeEachBlocks() {
        return beforeEachBlocks;
    }

    ItBlockDefinition itBlock(String itBlockDescription) {
        return itBlocks.get(itBlockDescription);
    }

    boolean ignored() {
        return IGNORED.equals(executionFlag);
    }

    boolean focused() {
        return FOCUSED.equals(executionFlag);
    }

    private void collectItBlocks(
        List<ItBlock> blocks,
        List<BeforeBlock> parentBeforeAllBlocks,
        ShouldBeIgnoredPredicate shouldBeIgnored
    ) {
        this.beforeAllBlocks.stream().map(BeforeBlock::newBeforeAllBlock).forEach(parentBeforeAllBlocks::add);

        List<BeforeBlock> beforeBlocks = new LinkedList<>();
        beforeBlocks.addAll(parentBeforeAllBlocks);
        beforeBlocks.addAll(collectBeforeEachBlocks());

        for (Map.Entry<String, ItBlockDefinition> entry : this.itBlocks.entrySet()) {
            String description = entry.getKey();
            ItBlockDefinition itBlock = entry.getValue();

            if (shouldBeIgnored.test(this, itBlock)) {
                blocks.add(newIgnoredItBlock(allContainerDescriptions(), description));
            } else {
                blocks.add(newItBlock(allContainerDescriptions(), description, beforeBlocks, itBlock.body(), itBlock.expected()));
            }
        }

        for (DescribeBlock describeBlock : this.describeBlocks) {
            describeBlock.collectItBlocks(blocks, parentBeforeAllBlocks, shouldBeIgnored);
        }
    }

    private boolean containerIgnored() {
        if (isRootDescribeBlock()) {
            return ignored();
        }
        return ignored() || parent.containerIgnored();
    }

    private boolean containerFocused() {
        if (isRootDescribeBlock()) {
            return focused();
        }
        return focused() || parent.containerFocused();
    }

    private List<String> allContainerDescriptions() {
        List<String> containerDescriptions;

        if (isRootDescribeBlock()) {
            containerDescriptions = new LinkedList<>();
        } else {
            containerDescriptions = parent.allContainerDescriptions();
        }

        containerDescriptions.add(description);
        return containerDescriptions;
    }

    private List<BeforeBlock> collectBeforeEachBlocks() {
        List<BeforeBlock> beforeEachBlocks;

        if (isRootDescribeBlock()) {
            beforeEachBlocks = new LinkedList<>();
        } else {
            beforeEachBlocks = parent.collectBeforeEachBlocks();
        }

        this.beforeEachBlocks.stream().map(BeforeBlock::newBeforeEachBlock).forEach(beforeEachBlocks::add);

        return beforeEachBlocks;
    }

    private boolean isRootDescribeBlock() {
        return parent == null;
    }
}