package j8spec;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static j8spec.J8Spec.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class J8SpecFlowTest {

    public static class SampleSpec {{
        beforeAll(() -> log.add("before all"));
        beforeEach(() -> log.add("before each"));

        it("block 1", () -> log.add("block 1"));
        it("block 2", () -> log.add("block 2"));

        describe("describe A", () -> {
            beforeAll(() -> log.add("describe A before all"));
            beforeEach(() -> log.add("describe A before each"));

            it("block A1", () -> log.add("block A1"));
            it("block A2", () -> log.add("block A2"));
        });
    }}

    private static List<String> log;

    private ExecutionPlan plan;

    @Before
    public void resetExecutionPlan() {
        log = new ArrayList<>();
        plan = executionPlanFor(SampleSpec.class);
        plan.allItBlocks().forEach(Runnable::run);
    }

    @Test
    public void flow() {
        assertThat(log, is(asList(
            "before all",

            "before each",
            "block 1",

            "before each",
            "block 2",

            "describe A before all",

            "before each",
            "describe A before each",
            "block A1",

            "before each",
            "describe A before each",
            "block A2"
        )));
    }
}