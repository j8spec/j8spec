package j8spec;

import java.util.*;

public class Specs {

    private static Description currentDescription;

    public static <T> Var<T> var() {
        return new Var<>();
    }

    public static <T> T var(Var<T> var) {
        return var.value;
    }

    public static <T> T var(Var<T> var, T value) {
        return var.value = value;
    }

    public static void describe(String text, Runnable r) {
        currentDescription.describe(text, r);
    }

    public static void describe(Class clazz, Runnable r) {
        currentDescription.describe(clazz.getName(), r);
    }

    public static void beforeEach(Runnable r) {
        currentDescription.beforeEach(r);
    }

    public static void it(String text, Runnable r) {
        currentDescription.it(text, r);
    }

    public static void run(Class<? extends Object> spec) throws IllegalAccessException, InstantiationException {
        currentDescription = new Description();

        spec.newInstance();

        currentDescription.run("", Collections.emptyList());
    }

    private static class Description {

        private final String text;
        private final Runnable runnable;

        private final List<Description> contexts = new LinkedList<>();
        private final Map<String, Runnable> behaviors = new HashMap<>();
        private final List<Runnable> befores = new LinkedList<>();

        public Description(String text, Runnable r) {
            this.text = text;
            this.runnable = r;
        }

        public Description() {
            this.text = "";
            this.runnable = () -> {};
        }

        public void describe(String text, Runnable r) {
            this.contexts.add(new Description(text, r));
        }

        public void beforeEach(Runnable r) {
            this.befores.add(r);
        }

        public void it(String text, Runnable r) {
            this.behaviors.put(text, r);
        }

        public void run(String indentation, List<Runnable> previousBefores) {
            Description previousDescription = Specs.currentDescription;
            Specs.currentDescription = this;

            System.out.println(indentation + text);

            runnable.run();

            List<Runnable> allBefores = new LinkedList<>();
            allBefores.addAll(previousBefores);
            allBefores.addAll(befores);

            for (Description context : contexts) {
                context.run(indentation + "  ", allBefores);
            }

            for (Map.Entry<String, Runnable> entry : behaviors.entrySet()) {

                for(Runnable runnable : allBefores) {
                    runnable.run();
                }

                try {
                    entry.getValue().run();
                    System.out.println(indentation + "  " + entry.getKey() + " [OK]");
                } catch (AssertionError assertionError) {
                    System.out.println(indentation + "  " + entry.getKey() + " [FAILED]");
                    assertionError.printStackTrace(System.out);
                    System.out.println();
                }

            }

            Specs.currentDescription = previousDescription;
        }
    }
}
