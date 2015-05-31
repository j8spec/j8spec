package j8spec;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.*;

public final class ExecutionPlan {

    private static final String LS = System.getProperty("line.separator");

    private final ExecutionPlan parent;
    private final String description;
    private final Runnable beforeEachBlock;
    private final Map<String, Runnable> itBlocks;
    private final List<ExecutionPlan> plans = new LinkedList<>();
    private final Class<?> specClass;

    public ExecutionPlan(Class<?> specClass, Runnable beforeEachBlock, Map<String, Runnable> itBlocks) {
        this.parent = null;
        this.specClass = specClass;
        this.description = specClass.getName();
        this.beforeEachBlock = beforeEachBlock;
        this.itBlocks = unmodifiableMap(itBlocks);
    }

    public ExecutionPlan(ExecutionPlan parent, String description, Runnable beforeEachBlock, Map<String, Runnable> itBlocks) {
        this.parent = parent;
        this.specClass = parent.specClass;
        this.description = description;
        this.beforeEachBlock = beforeEachBlock;
        this.itBlocks = unmodifiableMap(itBlocks);
    }

    ExecutionPlan newChildPlan(String description, Runnable beforeEach, Map<String, Runnable> behaviors) {
        ExecutionPlan plan = new ExecutionPlan(this, description, beforeEach, behaviors);
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

        for (Map.Entry<String, Runnable> behavior : itBlocks.entrySet()) {
            sb.append(LS).append(indentation).append("  ").append(behavior.getKey());
        }

        for (ExecutionPlan plan : plans) {
            sb.append(LS);
            plan.toString(sb, indentation + "  ");
        }
    }

    public Class<?> specClass() {
        return specClass;
    }

    public String getDescription() {
        return description;
    }

    public boolean hasItBlocks() {
        return !itBlocks.isEmpty();
    }

    public List<ExecutionPlan> getPlans() {
        return new LinkedList<>(plans);
    }

    public Runnable beforeEachBlock() {
        return beforeEachBlock;
    }

    public Runnable itBlock(String itBlockDescription) {
        return itBlocks.get(itBlockDescription);
    }

    public List<ItBlock> allItBlocks() {
        LinkedList<ItBlock> blocks = new LinkedList<>();
        collectItBlocks(blocks);
        return blocks;
    }

    private void collectItBlocks(List<ItBlock> blocks) {
        for (Map.Entry<String, Runnable> itBlock : itBlocks.entrySet()) {
            blocks.add(new ItBlock(
                allContainerDescriptions(),
                itBlock.getKey(),
                allBeforeEachBlocks(),
                itBlock.getValue())
            );
        }

        for (ExecutionPlan plan : plans) {
            plan.collectItBlocks(blocks);
        }
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

    private List<Runnable> allBeforeEachBlocks() {
        List<Runnable> beforeEachBlocks;

        if (isRootPlan()) {
            beforeEachBlocks = new LinkedList<>();
        } else {
            beforeEachBlocks = parent.allBeforeEachBlocks();
        }

        beforeEachBlocks.add(beforeEachBlock);
        return beforeEachBlocks;
    }

    private boolean isRootPlan() {
        return parent == null;
    }
}
