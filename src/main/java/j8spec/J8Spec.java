package j8spec;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class J8Spec {

    private static final ThreadLocal<Spec> currentSpec = new ThreadLocal<>();

    public static synchronized void describe(String description, Runnable body) {
        isValidContext("describe");
        currentSpec.get().describe(description, body);
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
            throw new IllegalContextException("'" + methodName + "' should not be invoked from outside a spec definition.");
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

        public void beforeEach(Runnable body) {
            if (beforeEachBlock != null) {
                throw new BlockAlreadyDefinedException("beforeEach block already defined");
            }
            beforeEachBlock = body;
        }

        public void it(String description, Runnable body) {
            if (itBlocks.containsKey(description)) {
                throw new BlockAlreadyDefinedException("'" + description + "' block already defined");
            }
            itBlocks.put(description, body);
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
                newPlan = new ExecutionPlan(specClass, beforeEachBlock, itBlocks);
            } else {
                newPlan = parentPlan.newChildPlan(description, beforeEachBlock, itBlocks);
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
