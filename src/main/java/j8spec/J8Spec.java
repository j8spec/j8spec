package j8spec;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static j8spec.BlockExecutionFlag.*;
import static j8spec.DescribeBlock.newRootDescribeBlock;
import static java.util.function.Function.identity;

/**
 * @since 1.0.0
 */
public final class J8Spec {

    private static final ThreadLocal<DescribeBlockDefinition> currentDescribeBlockDefinition = new ThreadLocal<>();

    /**
     * @since 1.0.0
     */
    public static synchronized void describe(String description, Runnable body) {
        isValidContext("describe");
        currentDescribeBlockDefinition.get().describe(description, body);
    }

    /**
     * @since 1.1.0
     */
    public static synchronized void xdescribe(String description, Runnable body) {
        isValidContext("xdescribe");
        currentDescribeBlockDefinition.get().xdescribe(description, body);
    }

    /**
     * @since 1.1.0
     */
    public static synchronized void fdescribe(String description, Runnable body) {
        isValidContext("fdescribe");
        currentDescribeBlockDefinition.get().fdescribe(description, body);
    }

    /**
     * @since 1.1.0
     */
    public static synchronized void beforeAll(Runnable body) {
        isValidContext("beforeAll");
        currentDescribeBlockDefinition.get().beforeAll(body);
    }

    /**
     * @since 1.0.0
     */
    public static synchronized void beforeEach(Runnable body) {
        isValidContext("beforeEach");
        currentDescribeBlockDefinition.get().beforeEach(body);
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
        currentDescribeBlockDefinition.get().it(description, itBlockDefinition);
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
        currentDescribeBlockDefinition.get().it(description, itBlockDefinition);
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
        currentDescribeBlockDefinition.get().it(description, itBlockDefinition);
    }

    private static void isValidContext(final String methodName) {
        if (currentDescribeBlockDefinition.get() == null) {
            throw new IllegalContextException(
                "'" + methodName + "' should not be invoked from outside a spec definition."
            );
        }
    }

    /**
     * @since 1.0.0
     */
    public static synchronized DescribeBlock read(Class<?> specClass) {
        currentDescribeBlockDefinition.set(new DescribeBlockDefinition(specClass));
        try {
            return currentDescribeBlockDefinition.get().buildDescribeBlock();
        } finally {
            currentDescribeBlockDefinition.set(null);
        }
    }

    private static class DescribeBlockDefinition {

        private final Class<?> specClass;
        private final String description;
        private final Runnable body;
        private final BlockExecutionFlag executionFlag;
        private final List<DescribeBlockDefinition> describeBlockDefinitions = new LinkedList<>();
        private final Map<String, ItBlockDefinition> itBlockDefinitions = new HashMap<>();
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
            describeBlockDefinitions.add(new DescribeBlockDefinition(specClass, description, body, DEFAULT));
        }

        public void xdescribe(String description, Runnable body) {
            describeBlockDefinitions.add(new DescribeBlockDefinition(specClass, description, body, IGNORED));
        }

        public void fdescribe(String description, Runnable body) {
            describeBlockDefinitions.add(new DescribeBlockDefinition(specClass, description, body, FOCUSED));
        }

        public void beforeAll(Runnable beforeAllBlock) {
            this.beforeAllBlocks.add(beforeAllBlock);
        }

        public void beforeEach(Runnable beforeEachBlock) {
            this.beforeEachBlocks.add(beforeEachBlock);
        }

        public void it(String description, ItBlockDefinition itBlockDefinition) {
            ensureIsNotAlreadyDefined(description, itBlockDefinitions.containsKey(description));
            itBlockDefinitions.put(description, itBlockDefinition);
        }

        private void ensureIsNotAlreadyDefined(String blockName, boolean result) {
            if (result) {
                throw new BlockAlreadyDefinedException(blockName + " block already defined");
            }
        }

        public DescribeBlock buildDescribeBlock() {
            return buildDescribeBlock(null);
        }

        private DescribeBlock buildDescribeBlock(DescribeBlock parent) {
            DescribeBlockDefinition previousDescribeBlockDefinition = J8Spec.currentDescribeBlockDefinition.get();
            J8Spec.currentDescribeBlockDefinition.set(this);

            this.body.run();

            DescribeBlock describeBlock = newDescribeBlock(parent);
            this.describeBlockDefinitions.stream().forEach(block -> block.buildDescribeBlock(describeBlock));

            J8Spec.currentDescribeBlockDefinition.set(previousDescribeBlockDefinition);

            return describeBlock;
        }

        private DescribeBlock newDescribeBlock(DescribeBlock parent) {
            if (parent == null) {
                return newRootDescribeBlock(specClass, beforeAllBlocks, beforeEachBlocks, itBlockDefinitions);
            }
            return addDescribeBlockTo(parent);
        }

        private DescribeBlock addDescribeBlockTo(DescribeBlock parent) {
            if (IGNORED.equals(executionFlag)) {
                return parent.addIgnoredDescribeBlock(description, beforeAllBlocks, beforeEachBlocks, itBlockDefinitions);
            }

            if (FOCUSED.equals(executionFlag)) {
                return parent.addFocusedDescribeBlock(description, beforeAllBlocks, beforeEachBlocks, itBlockDefinitions);
            }

            return parent.addDescribeBlock(description, beforeAllBlocks, beforeEachBlocks, itBlockDefinitions);
        }
    }

    private J8Spec() {}
}
