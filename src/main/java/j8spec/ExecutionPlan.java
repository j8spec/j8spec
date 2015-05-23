package j8spec;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.*;

public class ExecutionPlan {

    public static final String LS = System.getProperty("line.separator");
    private final String description;
    private final Runnable beforeEachBlock;
    private final Map<String, Runnable> itBlocks;
    private final List<ExecutionPlan> plans = new LinkedList<>();
    private final Class<?> testClass;

    public ExecutionPlan(Class<?> testClass, Runnable beforeEachBlock, Map<String, Runnable> itBlocks) {
        this.testClass = testClass;
        this.description = testClass.getName();
        this.beforeEachBlock = beforeEachBlock;
        this.itBlocks = unmodifiableMap(itBlocks);
    }

    public ExecutionPlan(ExecutionPlan parent, String description, Runnable beforeEachBlock, Map<String, Runnable> itBlocks) {
        this.testClass = parent.testClass;
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

    public String getDescription() {
        return description;
    }

    public boolean hasItBlocks() {
        return !itBlocks.isEmpty();
    }

    public List<ExecutionPlan> getPlans() {
        return new LinkedList<>(plans);
    }

    public Class<?> getTestClass() {
        return testClass;
    }

    public Runnable beforeEachBlock() {
        return beforeEachBlock;
    }

    public Runnable itBlock(String itBlockDescription) {
        return itBlocks.get(itBlockDescription);
    }
}
