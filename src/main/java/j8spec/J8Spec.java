package j8spec;

import java.util.*;

import static java.util.Collections.*;

public class J8Spec {

    private static Specification currentSpecification;

    public static void describe(Class testClass, Runnable body) {
        currentSpecification.describe(testClass.getName(), body);
    }

    public static void describe(String description, Runnable body) {
        currentSpecification.describe(description, body);
    }

    public static void beforeEach(Runnable body) {
        currentSpecification.beforeEach(body);
    }

    public static void it(String description, Runnable body) {
        currentSpecification.it(description, body);
    }

    public static ExecutionPlan executionPlanFor(Class<?> testClass) throws IllegalAccessException, InstantiationException {
        currentSpecification = new Specification();
        testClass.newInstance();

        currentSpecification = currentSpecification.contexts.get(0);

        ExecutionPlan parentPlan = new ExecutionPlan();
        currentSpecification.populateExecutionPlan(parentPlan);
        currentSpecification = null;

        return parentPlan.plans.get(0);
    }

    private static class Specification {

        private final String description;
        private final Runnable body;

        private final List<Specification> contexts = new LinkedList<>();
        private final Map<String, Runnable> behaviors = new HashMap<>();
        private final List<Runnable> beforeEach = new LinkedList<>();

        public Specification(String description, Runnable body) {
            this.description = description;
            this.body = body;
        }

        public Specification() {
            this.description = "root";
            this.body = () -> {};
        }

        public void describe(String description, Runnable body) {
            this.contexts.add(new Specification(description, body));
        }

        public void beforeEach(Runnable body) {
            this.beforeEach.add(body);
        }

        public void it(String description, Runnable body) {
            this.behaviors.put(description, body);
        }

        public void populateExecutionPlan(ExecutionPlan parentPlan) {
            Specification previousSpecification = J8Spec.currentSpecification;
            J8Spec.currentSpecification = this;

            body.run();

            ExecutionPlan childPlan = parentPlan.newChildPlan(
                    description,
                    beforeEach,
                    behaviors
            );

            for (Specification context : contexts) {
                context.populateExecutionPlan(childPlan);
            }

            J8Spec.currentSpecification = previousSpecification;
        }
    }

    public static class ExecutionPlan {

        private final String description;
        private final List<Runnable> beforeEach;
        private final Map<String, Runnable> behaviors;
        private final List<ExecutionPlan> plans = new LinkedList<>();

        public ExecutionPlan(String description, List<Runnable> beforeEach, Map<String, Runnable> behaviors) {
            this.description = description;
            this.beforeEach = unmodifiableList(beforeEach);
            this.behaviors = unmodifiableMap(behaviors);
        }

        public ExecutionPlan() {
            this.description = "";
            this.beforeEach = emptyList();
            this.behaviors = emptyMap();
        }

        private ExecutionPlan newChildPlan(String description, List<Runnable> beforeEach, Map<String, Runnable> behaviors) {
            ExecutionPlan plan = new ExecutionPlan(description, beforeEach, behaviors);
            plans.add(plan);
            return plan;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            toString(sb, "");
            return sb.toString();
        }

        private void toString(StringBuilder sb, String indentation) {
            sb.append(indentation).append(description).append(System.getProperty("line.separator"));

            for (Map.Entry<String, Runnable> behavior : behaviors.entrySet()) {
                sb.append(indentation).append("  ").append(behavior.getKey()).append(System.getProperty("line.separator"));
            }

            for (ExecutionPlan plan : plans) {
                plan.toString(sb, indentation + "  ");
            }
        }
    }

}
