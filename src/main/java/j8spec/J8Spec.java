package j8spec;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static j8spec.BlockExecutionFlag.*;
import static j8spec.ItBlockDefinition.*;

public final class J8Spec {

    private static final ThreadLocal<Spec> currentSpec = new ThreadLocal<>();

    public static synchronized void describe(String description, Runnable body) {
        isValidContext("describe");
        currentSpec.get().describe(description, body);
    }

    public static synchronized void xdescribe(String description, Runnable body) {
        isValidContext("xdescribe");
        currentSpec.get().xdescribe(description, body);
    }

    public static synchronized void fdescribe(String description, Runnable body) {
        isValidContext("fdescribe");
        currentSpec.get().fdescribe(description, body);
    }

    public static synchronized void beforeAll(Runnable body) {
        isValidContext("beforeAll");
        currentSpec.get().beforeAll(body);
    }

    public static synchronized void beforeEach(Runnable body) {
        isValidContext("beforeEach");
        currentSpec.get().beforeEach(body);
    }

    public static synchronized void it(String description, Runnable body) {
        isValidContext("it");
        currentSpec.get().it(description, body);
    }

    public static synchronized void xit(String description, Runnable body) {
        isValidContext("xit");
        currentSpec.get().xit(description, body);
    }

    public static synchronized void fit(String description, Runnable body) {
        isValidContext("fit");
        currentSpec.get().fit(description, body);
    }

    private static void isValidContext(final String methodName) {
        if (currentSpec.get() == null) {
            throw new IllegalContextException(
                "'" + methodName + "' should not be invoked from outside a spec definition."
            );
        }
    }

    public static synchronized ExecutionPlan executionPlanFor(Class<?> testClass) {
        currentSpec.set(new Spec(testClass));
        try {
            return currentSpec.get().buildExecutionPlan();
        } finally {
            currentSpec.set(null);
        }
    }

    private static class Spec {

        private final Class<?> specClass;
        private final String description;
        private final Runnable body;
        private final BlockExecutionFlag executionFlag;
        private final List<Spec> describeBlocks = new LinkedList<>();
        private final Map<String, ItBlockDefinition> itBlocks = new HashMap<>();
        private Runnable beforeAllBlock;
        private Runnable beforeEachBlock;

        public Spec(Class<?> specClass) {
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

        private Spec(Class<?> specClass, String description, Runnable body, BlockExecutionFlag executionFlag) {
            this.specClass = specClass;
            this.description = description;
            this.body = body;
            this.executionFlag = executionFlag;
        }

        public void describe(String description, Runnable body) {
            describeBlocks.add(new Spec(specClass, description, body, DEFAULT));
        }

        public void xdescribe(String description, Runnable body) {
            describeBlocks.add(new Spec(specClass, description, body, IGNORED));
        }

        public void fdescribe(String description, Runnable body) {
            describeBlocks.add(new Spec(specClass, description, body, FOCUSED));
        }

        public void beforeAll(Runnable beforeAllBlock) {
            ensureIsNotAlreadyDefined("beforeAll", this.beforeAllBlock != null);
            this.beforeAllBlock = beforeAllBlock;
        }

        public void beforeEach(Runnable beforeEachBlock) {
            ensureIsNotAlreadyDefined("beforeEach", this.beforeEachBlock != null);
            this.beforeEachBlock = beforeEachBlock;
        }

        public void it(String description, Runnable body) {
            ensureIsNotAlreadyDefined(description, itBlocks.containsKey(description));
            itBlocks.put(description, newItBlockDefinition(body));
        }

        public void xit(String description, Runnable body) {
            ensureIsNotAlreadyDefined(description, itBlocks.containsKey(description));
            itBlocks.put(description, newIgnoredItBlockDefinition(body));
        }

        public void fit(String description, Runnable body) {
            ensureIsNotAlreadyDefined(description, itBlocks.containsKey(description));
            itBlocks.put(description, newFocusedItBlockDefinition(body));
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
            Spec previousSpec = J8Spec.currentSpec.get();
            J8Spec.currentSpec.set(this);

            body.run();

            ExecutionPlan newPlan;
            if (parentPlan == null) {
                newPlan = new ExecutionPlan(specClass, beforeAllBlock, beforeEachBlock, itBlocks);
            } else {
                newPlan = newChildPlan(parentPlan);
            }

            describeBlocks.stream().forEach(block -> block.populateExecutionPlan(newPlan));

            J8Spec.currentSpec.set(previousSpec);

            return newPlan;
        }

        private ExecutionPlan newChildPlan(ExecutionPlan parentPlan) {
            if (IGNORED.equals(executionFlag)) {
                return parentPlan.newIgnoredChildPlan(description, beforeAllBlock, beforeEachBlock, itBlocks);
            }

            if (FOCUSED.equals(executionFlag)) {
                return parentPlan.newFocusedChildPlan(description, beforeAllBlock, beforeEachBlock, itBlocks);
            }

            return parentPlan.newChildPlan(description, beforeAllBlock, beforeEachBlock, itBlocks);
        }
    }

    private J8Spec() {}
}
