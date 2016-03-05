package j8spec;

import j8spec.annotation.DefinedOrder;
import j8spec.annotation.RandomOrder;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static j8spec.J8Spec.beforeAll;
import static j8spec.J8Spec.beforeEach;
import static j8spec.J8Spec.describe;
import static j8spec.J8Spec.it;
import static j8spec.J8Spec.read;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class J8SpecFlowRandomOrderTest {

    static class RandomAsDefaultOrderSpec {{
        it("block 1", () -> log.add("block 1"));
        it("block 2", () -> log.add("block 2"));
        it("block 3", () -> log.add("block 3"));
        it("block 4", () -> log.add("block 4"));
        it("block 5", () -> log.add("block 5"));
        it("block 6", () -> log.add("block 6"));
        it("block 7", () -> log.add("block 7"));
        it("block 8", () -> log.add("block 8"));
        it("block 9", () -> log.add("block 9"));
    }}

    @RandomOrder(seed = 0)
    static class SingleExampleGroupWithRandomOrderSpec {{
        beforeAll(() -> log.add("before all 1"));
        beforeEach(() -> log.add("before each 1"));

        it("block 1", () -> log.add("block 1"));
        it("block 2", () -> log.add("block 2"));
        it("block 3", () -> log.add("block 3"));
        it("block 4", () -> log.add("block 4"));
        it("block 5", () -> log.add("block 5"));
    }}

    @DefinedOrder
    static class InnerExampleGroupWithRandomOrderSpec {{
        beforeAll(() -> log.add("before all 1"));
        beforeEach(() -> log.add("before each 1"));

        it("block 1", () -> log.add("block 1"));
        it("block 2", () -> log.add("block 2"));

        describe("describe A", c -> c.randomOrder().seed(0L), () -> {
            beforeAll(() -> log.add("before all A1"));
            beforeEach(() -> log.add("before each A1"));

            it("block A1", () -> log.add("block A1"));
            it("block A2", () -> log.add("block A2"));
            it("block A3", () -> log.add("block A3"));
        });

        it("block 3", () -> log.add("block 3"));
    }}

    @RandomOrder(seed = 0)
    static class InnerExampleGroupWithDefinedOrderSpec {{
        beforeAll(() -> log.add("before all 1"));
        beforeEach(() -> log.add("before each 1"));

        it("block 1", () -> log.add("block 1"));
        it("block 2", () -> log.add("block 2"));

        describe("describe A", c -> c.definedOrder(), () -> {
            beforeAll(() -> log.add("before all A1"));
            beforeEach(() -> log.add("before each A1"));

            it("block A1", () -> log.add("block A1"));
            it("block A2", () -> log.add("block A2"));
            it("block A3", () -> log.add("block A3"));
        });

        it("block 3", () -> log.add("block 3"));
    }}

    @RandomOrder(seed = 0)
    static class SuperSpec {}

    static class SubSpec extends SuperSpec {{
        it("block 1", () -> log.add("block 1"));
        it("block 2", () -> log.add("block 2"));
        it("block 3", () -> log.add("block 3"));
    }}

    private static List<String> log;

    private List<String> executeSpec(Class<?> specClass) throws Throwable {
        log = new ArrayList<>();
        for (Example example : read(specClass)) {
            example.tryToExecute();
        }
        return unmodifiableList(log);
    }

    @Test
    public void uses_a_different_seed_each_execution() throws Throwable {
        List<String> firstExecution = executeSpec(RandomAsDefaultOrderSpec.class);
        List<String> secondExecution = executeSpec(RandomAsDefaultOrderSpec.class);

        assertThat(firstExecution, is(not(secondExecution)));
    }

    @Test
    public void single_example_group_with_random_order_flow() throws Throwable {
        executeSpec(SingleExampleGroupWithRandomOrderSpec.class);

        assertThat(log, is(asList(
            "before all 1",

            "before each 1",
            "block 3",

            "before each 1",
            "block 4",

            "before each 1",
            "block 5",

            "before each 1",
            "block 1",

            "before each 1",
            "block 2"
        )));
    }

    @Test
    public void inner_example_group_with_random_order_flow() throws Throwable {
        executeSpec(InnerExampleGroupWithRandomOrderSpec.class);

        assertThat(log, is(asList(
            "before all 1",

            "before each 1",
            "block 1",

            "before each 1",
            "block 2",

                "before all A1",

                "before each 1",
                "before each A1",
                "block A3",

                "before each 1",
                "before each A1",
                "block A1",

                "before each 1",
                "before each A1",
                "block A2",

            "before each 1",
            "block 3"
        )));
    }

    @Test
    public void inner_example_group_with_defined_order_flow() throws Throwable {
        executeSpec(InnerExampleGroupWithDefinedOrderSpec.class);

        assertThat(log, is(asList(
            "before all 1",

                "before all A1",

                "before each 1",
                "before each A1",
                "block A1",

                "before each 1",
                "before each A1",
                "block A2",

                "before each 1",
                "before each A1",
                "block A3",

            "before each 1",
            "block 3",

            "before each 1",
            "block 1",

            "before each 1",
            "block 2"
        )));
    }

    @Test
    public void inherits_order_from_super_spec() throws Throwable {
        executeSpec(SubSpec.class);

        assertThat(log, is(asList(
            "block 3",
            "block 1",
            "block 2"
        )));
    }
}
