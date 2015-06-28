package j8spec;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static j8spec.BeforeBlock.newBeforeAllBlock;
import static j8spec.BeforeBlock.newBeforeEachBlock;
import static j8spec.ItBlock.newIgnoredItBlock;
import static j8spec.ItBlock.newItBlock;
import static j8spec.BlockExecutionFlag.DEFAULT;
import static j8spec.BlockExecutionFlag.FOCUSED;
import static j8spec.BlockExecutionFlag.IGNORED;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

/**
 * @since 1.0.0
 */
public final class ExecutionPlan {

    private static final String LS = System.getProperty("line.separator");

    @FunctionalInterface
    private interface ShouldBeIgnoredPredicate {
        boolean test(ExecutionPlan plan, ItBlockDefinition itBlockDefinition);
    }

    static ExecutionPlan newExecutionPlan(
        Class<?> specClass,
        Runnable beforeAllBlock,
        List<Runnable> beforeEachBlocks,
        Map<String, ItBlockDefinition> itBlocks
    ) {
        return new ExecutionPlan(specClass, beforeAllBlock, beforeEachBlocks, itBlocks);
    }

    private final ExecutionPlan parent;
    private final BlockExecutionFlag executionFlag;
    private final String description;
    private final Runnable beforeAllBlock;
    private final List<Runnable> beforeEachBlocks;
    private final Map<String, ItBlockDefinition> itBlocks;
    private final List<ExecutionPlan> plans = new LinkedList<>();
    private final Class<?> specClass;

    private ExecutionPlan(
        Class<?> specClass,
        Runnable beforeAllBlock,
        List<Runnable> beforeEachBlocks,
        Map<String, ItBlockDefinition> itBlocks
    ) {
        this.parent = null;
        this.specClass = specClass;
        this.description = specClass.getName();
        this.beforeAllBlock = beforeAllBlock;
        this.beforeEachBlocks = unmodifiableList(beforeEachBlocks);
        this.itBlocks = unmodifiableMap(itBlocks);
        this.executionFlag = DEFAULT;
    }

    private ExecutionPlan(
        ExecutionPlan parent,
        String description,
        Runnable beforeAllBlock,
        List<Runnable> beforeEachBlocks,
        Map<String, ItBlockDefinition> itBlocks,
        BlockExecutionFlag executionFlag
    ) {
        this.parent = parent;
        this.specClass = parent.specClass;
        this.description = description;
        this.beforeAllBlock = beforeAllBlock;
        this.beforeEachBlocks = unmodifiableList(beforeEachBlocks);
        this.itBlocks = unmodifiableMap(itBlocks);
        this.executionFlag = executionFlag;
    }

    ExecutionPlan newChildPlan(
        String description,
        Runnable beforeAllBlock,
        List<Runnable> beforeEachBlocks,
        Map<String, ItBlockDefinition> itBlocks
    ) {
        ExecutionPlan plan = new ExecutionPlan(this, description, beforeAllBlock, beforeEachBlocks, itBlocks, DEFAULT);
        plans.add(plan);
        return plan;
    }

    ExecutionPlan newIgnoredChildPlan(
        String description,
        Runnable beforeAllBlock,
        List<Runnable> beforeEachBlocks,
        Map<String, ItBlockDefinition> itBlocks
    ) {
        ExecutionPlan plan = new ExecutionPlan(this, description, beforeAllBlock, beforeEachBlocks, itBlocks, IGNORED);
        plans.add(plan);
        return plan;
    }

    ExecutionPlan newFocusedChildPlan(
        String description,
        Runnable beforeAllBlock,
        List<Runnable> beforeEachBlocks,
        Map<String, ItBlockDefinition> itBlocks
    ) {
        ExecutionPlan plan = new ExecutionPlan(this, description, beforeAllBlock, beforeEachBlocks, itBlocks, FOCUSED);
        plans.add(plan);
        return plan;
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

        for (ExecutionPlan plan : plans) {
            sb.append(LS);
            plan.toString(sb, indentation + "  ");
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
    public List<ItBlock> allItBlocks() {
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
            || plans.stream().anyMatch(p -> p.focused() || p.thereIsAtLeastOneFocusedBlock());
    }

    String description() {
        return description;
    }

    List<ExecutionPlan> plans() {
        return new LinkedList<>(plans);
    }

    Runnable beforeAllBlock() {
        return beforeAllBlock;
    }

    Runnable beforeEachBlock() {
        if (beforeEachBlocks.isEmpty()) {
            return null;
        }
        return beforeEachBlocks.get(0);
    }

    Runnable beforeEachBlockAt(int index) {
        return beforeEachBlocks.get(index);
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
        if (this.beforeAllBlock != null) {
            parentBeforeAllBlocks.add(newBeforeAllBlock(this.beforeAllBlock));
        }

        List<BeforeBlock> beforeBlocks = new LinkedList<>();
        beforeBlocks.addAll(parentBeforeAllBlocks);
        beforeBlocks.addAll(collectBeforeEachBlocks());

        for (Map.Entry<String, ItBlockDefinition> entry : itBlocks.entrySet()) {
            String description = entry.getKey();
            ItBlockDefinition itBlock = entry.getValue();

            if (shouldBeIgnored.test(this, itBlock)) {
                blocks.add(newIgnoredItBlock(allContainerDescriptions(), description));
            } else {
                blocks.add(newItBlock(allContainerDescriptions(), description, beforeBlocks, itBlock.body(), itBlock.expected()));
            }
        }

        for (ExecutionPlan plan : plans) {
            plan.collectItBlocks(blocks, parentBeforeAllBlocks, shouldBeIgnored);
        }
    }

    private boolean containerIgnored() {
        if (isRootPlan()) {
            return ignored();
        }
        return ignored() || parent.containerIgnored();
    }

    private boolean containerFocused() {
        if (isRootPlan()) {
            return focused();
        }
        return focused() || parent.containerFocused();
    }

    private List<String> allContainerDescriptions() {
        List<String> containerDescriptions;

        if (isRootPlan()) {
            containerDescriptions = new LinkedList<>();
        } else {
            containerDescriptions = parent.allContainerDescriptions();
        }

        containerDescriptions.add(description);
        return containerDescriptions;
    }

    private List<BeforeBlock> collectBeforeEachBlocks() {
        List<BeforeBlock> beforeEachBlocks;

        if (isRootPlan()) {
            beforeEachBlocks = new LinkedList<>();
        } else {
            beforeEachBlocks = parent.collectBeforeEachBlocks();
        }

        if (this.beforeEachBlock() != null) {
            beforeEachBlocks.add(newBeforeEachBlock(beforeEachBlock()));
        }

        return beforeEachBlocks;
    }

    private boolean isRootPlan() {
        return parent == null;
    }
}
