package j8spec;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class J8Spec {

    private static Spec currentSpec;

    public static void describe(String description, Runnable body) {
        currentSpec.describe(description, body); // TODO check if currentSpec is null
    }

    public static void beforeEach(Runnable body) {
        currentSpec.beforeEach(body);
    }

    public static void it(String description, Runnable body) {
        currentSpec.it(description, body);
    }

    public static ExecutionPlan executionPlanFor(Class<?> testClass) {
        currentSpec = new Spec(testClass);
        ExecutionPlan plan = currentSpec.buildExecutionPlan();
        currentSpec = null;
        return plan;
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
                } catch (Exception e) {
                    throw new RuntimeException(e); // FIXME RT exception
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
            beforeEachBlock = body; // TODO warning about replace
        }

        public void it(String description, Runnable body) {
            itBlocks.put(description, body); // TODO warning about replace
        }

        public ExecutionPlan buildExecutionPlan() {
            return populateExecutionPlan(null);
        }

        private ExecutionPlan populateExecutionPlan(ExecutionPlan parentPlan) {
            Spec previousSpec = J8Spec.currentSpec;
            J8Spec.currentSpec = this;

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

            J8Spec.currentSpec = previousSpec;

            return newPlan;
        }
    }
}
