package j8spec;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

public final class J8Spec {

    private static final ThreadLocal<Spec> currentSpec = new ThreadLocal<>();

    public static synchronized void describe(String description, Runnable body) {
        isValidContext("describe");
        currentSpec.get().describe(description, body);
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
        private final List<Spec> describeBlocks = new LinkedList<>();
        private final Map<String, Runnable> itBlocks = new HashMap<>();
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
        }

        private Spec(Class<?> specClass, String description, Runnable body) {
            this.specClass = specClass;
            this.description = description;
            this.body = body;
        }

        public void describe(String description, Runnable body) {
            describeBlocks.add(new Spec(specClass, description, body));
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
            itBlocks.put(description, body);
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
                newPlan = parentPlan.newChildPlan(description, beforeAllBlock, beforeEachBlock, itBlocks);
            }

            for (Spec spec : describeBlocks) {
                spec.populateExecutionPlan(newPlan);
            }

            J8Spec.currentSpec.set(previousSpec);

            return newPlan;
        }
    }

    private J8Spec() {}
}
