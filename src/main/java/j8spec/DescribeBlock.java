package j8spec;

import java.util.LinkedList;
import java.util.List;

import static j8spec.BlockExecutionFlag.DEFAULT;
import static j8spec.BlockExecutionFlag.FOCUSED;
import static j8spec.BlockExecutionFlag.IGNORED;
import static j8spec.ItBlock.newIgnoredItBlock;
import static j8spec.ItBlock.newItBlock;
import static java.util.Collections.unmodifiableList;

final class DescribeBlock {

    @FunctionalInterface
    private interface ShouldBeIgnoredPredicate {
        boolean test(DescribeBlock describeBlock, ItBlockDefinition itBlockDefinition);
    }

    private final DescribeBlock parent;
    private final String description;
    private final List<UnsafeBlock> beforeAllBlocks;
    private final List<UnsafeBlock> beforeEachBlocks;
    private final List<ItBlockDefinition> itBlockDefinitions;
    private final List<DescribeBlock> describeBlocks = new LinkedList<>();
    private final BlockExecutionFlag executionFlag;

    static DescribeBlock newRootDescribeBlock(
        String description,
        List<UnsafeBlock> beforeAllBlocks,
        List<UnsafeBlock> beforeEachBlocks,
        List<ItBlockDefinition> itBlocks
    ) {
        return new DescribeBlock(null, description, beforeAllBlocks, beforeEachBlocks, itBlocks, DEFAULT);
    }

    private DescribeBlock(
        DescribeBlock parent,
        String description,
        List<UnsafeBlock> beforeAllBlocks,
        List<UnsafeBlock> beforeEachBlocks,
        List<ItBlockDefinition> itBlockDefinitions,
        BlockExecutionFlag executionFlag
    ) {
        this.parent = parent;
        this.description = description;
        this.beforeAllBlocks = unmodifiableList(beforeAllBlocks);
        this.beforeEachBlocks = unmodifiableList(beforeEachBlocks);
        this.itBlockDefinitions = unmodifiableList(itBlockDefinitions);
        this.executionFlag = executionFlag;
    }

    DescribeBlock addDescribeBlock(
        String description,
        List<UnsafeBlock> beforeAllBlocks,
        List<UnsafeBlock> beforeEachBlocks,
        List<ItBlockDefinition> itBlocks,
        BlockExecutionFlag executionFlag
    ) {
        DescribeBlock describeBlock = new DescribeBlock(
            this,
            description,
            beforeAllBlocks,
            beforeEachBlocks,
            itBlocks,
            executionFlag
        );
        describeBlocks.add(describeBlock);
        return describeBlock;
    }

    List<ItBlock> flattenItBlocks() {
        LinkedList<ItBlock> blocksCollector = new LinkedList<>();
        collectItBlocks(blocksCollector, new LinkedList<>(), shouldBeIgnoredPredicate());
        return blocksCollector;
    }

    private ShouldBeIgnoredPredicate shouldBeIgnoredPredicate() {
        if (thereIsAtLeastOneFocusedBlock()) {
            return (p, b) -> !b.focused() && !p.containerFocused();
        }
        return (p, b) -> b.ignored() || p.containerIgnored();
    }

    private boolean thereIsAtLeastOneFocusedBlock() {
        return itBlockDefinitions.stream().anyMatch(ItBlockDefinition::focused)
            || describeBlocks.stream().anyMatch(b -> b.focused() || b.thereIsAtLeastOneFocusedBlock());
    }

    boolean ignored() {
        return IGNORED.equals(executionFlag);
    }

    boolean focused() {
        return FOCUSED.equals(executionFlag);
    }

    private void collectItBlocks(
        List<ItBlock> blocksCollector,
        List<BeforeBlock> parentBeforeAllBlocks,
        ShouldBeIgnoredPredicate shouldBeIgnored
    ) {
        this.beforeAllBlocks.stream().map(BeforeBlock::newBeforeAllBlock).forEach(parentBeforeAllBlocks::add);

        List<BeforeBlock> beforeBlocks = new LinkedList<>();
        beforeBlocks.addAll(parentBeforeAllBlocks);
        beforeBlocks.addAll(collectBeforeEachBlocks());

        for (ItBlockDefinition itBlock : this.itBlockDefinitions) {
            if (shouldBeIgnored.test(this, itBlock)) {
                blocksCollector.add(newIgnoredItBlock(allContainerDescriptions(), itBlock.description()));
            } else {
                blocksCollector.add(newItBlock(allContainerDescriptions(), itBlock.description(), beforeBlocks, itBlock.block(), itBlock.expected()));
            }
        }

        for (DescribeBlock describeBlock : this.describeBlocks) {
            describeBlock.collectItBlocks(blocksCollector, parentBeforeAllBlocks, shouldBeIgnored);
        }
    }

    private boolean containerIgnored() {
        if (isRoot()) {
            return ignored();
        }
        return ignored() || parent.containerIgnored();
    }

    private boolean containerFocused() {
        if (isRoot()) {
            return focused();
        }
        return focused() || parent.containerFocused();
    }

    private List<String> allContainerDescriptions() {
        List<String> containerDescriptions;

        if (isRoot()) {
            containerDescriptions = new LinkedList<>();
        } else {
            containerDescriptions = parent.allContainerDescriptions();
        }

        containerDescriptions.add(description);
        return containerDescriptions;
    }

    private List<BeforeBlock> collectBeforeEachBlocks() {
        List<BeforeBlock> beforeEachBlocks;

        if (isRoot()) {
            beforeEachBlocks = new LinkedList<>();
        } else {
            beforeEachBlocks = parent.collectBeforeEachBlocks();
        }

        this.beforeEachBlocks.stream().map(BeforeBlock::newBeforeEachBlock).forEach(beforeEachBlocks::add);

        return beforeEachBlocks;
    }

    private boolean isRoot() {
        return parent == null;
    }
}
