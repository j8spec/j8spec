package j8spec;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static j8spec.BlockExecutionFlag.*;
import static j8spec.ExecutionPlan.newExecutionPlan;
import static java.util.function.Function.identity;

/**
 * @since 1.0.0
 */
public final class J8Spec {

    private static final ThreadLocal<DescribeBlockDefinition> currentDescribeBlock = new ThreadLocal<>();

    /**
     * @since 1.0.0
     */
    public static synchronized void describe(String description, Runnable body) {
        isValidContext("describe");
        currentDescribeBlock.get().describe(description, body);
    }

    /**
     * @since 1.1.0
     */
    public static synchronized void xdescribe(String description, Runnable body) {
        isValidContext("xdescribe");
        currentDescribeBlock.get().xdescribe(description, body);
    }

    /**
     * @since 1.1.0
     */
    public static synchronized void fdescribe(String description, Runnable body) {
        isValidContext("fdescribe");
        currentDescribeBlock.get().fdescribe(description, body);
    }

    /**
     * @since 1.1.0
     */
    public static synchronized void beforeAll(Runnable body) {
        isValidContext("beforeAll");
        currentDescribeBlock.get().beforeAll(body);
    }

    /**
     * @since 1.0.0
     */
    public static synchronized void beforeEach(Runnable body) {
        isValidContext("beforeEach");
        currentDescribeBlock.get().beforeEach(body);
    }

    /**
     * @since 1.0.0
     */
    public static synchronized void it(String description, Runnable body) {
        it(description, identity(), body);
    }

    /**
     * @since 1.1.0
     */
    public static synchronized void it(
        String description,
        Function<ItBlockDefinitionBuilder, ItBlockDefinitionBuilder> collector,
        Runnable body
    ) {
        isValidContext("it");
        ItBlockDefinition itBlockDefinition = collector.apply(new ItBlockDefinitionBuilder())
            .body(body)
            .newItBlockDefinition();
        currentDescribeBlock.get().it(description, itBlockDefinition);
    }

    /**
     * @since 1.1.0
     */
    public static synchronized void xit(String description, Runnable body) {
        xit(description, identity(), body);
    }

    /**
     * @since 1.1.0
     */
    public static synchronized void xit(
        String description,
        Function<ItBlockDefinitionBuilder, ItBlockDefinitionBuilder> collector,
        Runnable body
    ) {
        isValidContext("xit");
        ItBlockDefinition itBlockDefinition = collector.apply(new ItBlockDefinitionBuilder())
            .body(body)
            .newIgnoredItBlockDefinition();
        currentDescribeBlock.get().it(description, itBlockDefinition);
    }

    /**
     * @since 1.1.0
     */
    public static synchronized void fit(String description, Runnable body) {
        fit(description, identity(), body);
    }

    /**
     * @since 1.1.0
     */
    public static synchronized void fit(
        String description,
        Function<ItBlockDefinitionBuilder, ItBlockDefinitionBuilder> collector,
        Runnable body
    ) {
        isValidContext("fit");
        ItBlockDefinition itBlockDefinition = collector.apply(new ItBlockDefinitionBuilder())
            .body(body)
            .newFocusedItBlockDefinition();
        currentDescribeBlock.get().it(description, itBlockDefinition);
    }

    private static void isValidContext(final String methodName) {
        if (currentDescribeBlock.get() == null) {
            throw new IllegalContextException(
                "'" + methodName + "' should not be invoked from outside a spec definition."
            );
        }
    }

    /**
     * @since 1.0.0
     */
    public static synchronized ExecutionPlan executionPlanFor(Class<?> testClass) {
        currentDescribeBlock.set(new DescribeBlockDefinition(testClass));
        try {
            return currentDescribeBlock.get().buildExecutionPlan();
        } finally {
            currentDescribeBlock.set(null);
        }
    }

    private static class DescribeBlockDefinition {

        private final Class<?> specClass;
        private final String description;
        private final Runnable body;
        private final BlockExecutionFlag executionFlag;
        private final List<DescribeBlockDefinition> describeBlocks = new LinkedList<>();
        private final Map<String, ItBlockDefinition> itBlocks = new HashMap<>();
        private final List<Runnable> beforeAllBlocks = new LinkedList<>();
        private final List<Runnable> beforeEachBlocks = new LinkedList<>();

        public DescribeBlockDefinition(Class<?> specClass) {
            this.specClass = specClass;
            this.description = specClass.getName();
            this.body = () -> {
                try {
                    specClass.newInstance();
                } catch (J8SpecException e) {
                    throw e;
                } catch (Exception e) {
                    throw new SpecInitializationException("Failed to create instance of " + specClass + ".", e);
                }
            };
            this.executionFlag = DEFAULT;
        }

        private DescribeBlockDefinition(Class<?> specClass, String description, Runnable body, BlockExecutionFlag executionFlag) {
            this.specClass = specClass;
            this.description = description;
            this.body = body;
            this.executionFlag = executionFlag;
        }

        public void describe(String description, Runnable body) {
            describeBlocks.add(new DescribeBlockDefinition(specClass, description, body, DEFAULT));
        }

        public void xdescribe(String description, Runnable body) {
            describeBlocks.add(new DescribeBlockDefinition(specClass, description, body, IGNORED));
        }

        public void fdescribe(String description, Runnable body) {
            describeBlocks.add(new DescribeBlockDefinition(specClass, description, body, FOCUSED));
        }

        public void beforeAll(Runnable beforeAllBlock) {
            this.beforeAllBlocks.add(beforeAllBlock);
        }

        public void beforeEach(Runnable beforeEachBlock) {
            this.beforeEachBlocks.add(beforeEachBlock);
        }

        public void it(String description, ItBlockDefinition itBlockDefinition) {
            ensureIsNotAlreadyDefined(description, itBlocks.containsKey(description));
            itBlocks.put(description, itBlockDefinition);
        }

        private void ensureIsNotAlreadyDefined(String blockName, boolean result) {
            if (result) {
                throw new BlockAlreadyDefinedException(blockName + " block already defined");
            }
        }

        public ExecutionPlan buildExecutionPlan() {
            return populateExecutionPlan(null);
        }

        private ExecutionPlan populateExecutionPlan(ExecutionPlan parentPlan) {
            DescribeBlockDefinition previousDescribeBlock = J8Spec.currentDescribeBlock.get();
            J8Spec.currentDescribeBlock.set(this);

            this.body.run();

            ExecutionPlan newPlan = newPlan(parentPlan);
            this.describeBlocks.stream().forEach(block -> block.populateExecutionPlan(newPlan));

            J8Spec.currentDescribeBlock.set(previousDescribeBlock);

            return newPlan;
        }

        private ExecutionPlan newPlan(ExecutionPlan parentPlan) {
            if (parentPlan == null) {
                return newExecutionPlan(specClass, beforeAllBlocks, beforeEachBlocks, itBlocks);
            }
            return newChildPlan(parentPlan);
        }

        private ExecutionPlan newChildPlan(ExecutionPlan parentPlan) {
            if (IGNORED.equals(executionFlag)) {
                return parentPlan.newIgnoredChildPlan(description, beforeAllBlocks, beforeEachBlocks, itBlocks);
            }

            if (FOCUSED.equals(executionFlag)) {
                return parentPlan.newFocusedChildPlan(description, beforeAllBlocks, beforeEachBlocks, itBlocks);
            }

            return parentPlan.newChildPlan(description, beforeAllBlocks, beforeEachBlocks, itBlocks);
        }
    }

    private J8Spec() {}
}
