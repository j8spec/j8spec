package j8spec;

public class Main {

    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        J8Spec.ExecutionPlan plan = J8Spec.executionPlanFor(MyObjectSpec.class);
        System.out.println(plan);
    }

}

