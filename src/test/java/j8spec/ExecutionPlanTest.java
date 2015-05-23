package j8spec;

import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.join;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ExecutionPlanTest {

    private static final String LS = System.getProperty("line.separator");

    private static final Runnable NOOP = () -> {};

    static class SampleSpec {}

    @Test
    public void hasAStringRepresentationWhenEmpty() {
        ExecutionPlan emptyPlan = new ExecutionPlan(
            SampleSpec.class,
            NOOP,
            Collections.emptyMap()
        );

        assertThat(emptyPlan.toString(), is("j8spec.ExecutionPlanTest$SampleSpec"));
    }

    @Test
    public void hasAStringRepresentationWhenItContainsItBlocks() {
        Map<String, Runnable> itBlocks = new HashMap<>();
        itBlocks.put("block 1", NOOP);
        itBlocks.put("block 2", NOOP);

        ExecutionPlan planWithItBlocks = new ExecutionPlan(
            SampleSpec.class,
            NOOP,
            itBlocks
        );

        assertThat(
            planWithItBlocks.toString(),
            is(join(
                LS,
                "j8spec.ExecutionPlanTest$SampleSpec",
                "  block 1",
                "  block 2"
            ))
        );
    }

    @Test
    public void hasAStringRepresentationWhenItContainsChildPlans() {
        Map<String, Runnable> itBlocks = new HashMap<>();
        itBlocks.put("block 1", NOOP);
        itBlocks.put("block 2", NOOP);

        ExecutionPlan planWithInnerPlans = new ExecutionPlan(
            SampleSpec.class,
            NOOP,
            itBlocks
        );

        planWithInnerPlans.newChildPlan("child 1", NOOP, itBlocks);
        planWithInnerPlans.newChildPlan("child 2", NOOP, itBlocks);

        assertThat(
            planWithInnerPlans.toString(),
            is(join(
                LS,
                "j8spec.ExecutionPlanTest$SampleSpec",
                "  block 1",
                "  block 2",
                "  child 1",
                "    block 1",
                "    block 2",
                "  child 2",
                "    block 1",
                "    block 2"
            ))
        );
    }
}
