package j8spec;

import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.join;
import static java.util.Collections.emptyList;
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

    @Test
    public void buildsItBlocks() {
        Runnable beforeEachBlock = () -> {};
        Runnable block1 = () -> {};
        Runnable block2 = () -> {};
        Runnable beforeEachBlockA = () -> {};
        Runnable blockA1 = () -> {};
        Runnable blockA2 = () -> {};

        Map<String, Runnable> itBlocks = new HashMap<>();
        itBlocks.put("block 1", block1);
        itBlocks.put("block 2", block2);

        ExecutionPlan planWithInnerPlans = new ExecutionPlan(
            SampleSpec.class,
            beforeEachBlock,
            itBlocks
        );

        Map<String, Runnable> itBlocksA = new HashMap<>();
        itBlocksA.put("block A1", blockA1);
        itBlocksA.put("block A2", blockA2);

        planWithInnerPlans.newChildPlan("describe A", beforeEachBlockA, itBlocksA);

        List<ItBlock> blocks = planWithInnerPlans.allItBlocks();

        assertThat(blocks.get(0).description(), is("block 1"));
        assertThat(blocks.get(0).containerDescriptions().get(0), is("j8spec.ExecutionPlanTest$SampleSpec"));
        assertThat(blocks.get(0).beforeEachBlocks().get(0), is(beforeEachBlock));
        assertThat(blocks.get(0).body(), is(block1));

        assertThat(blocks.get(1).description(), is("block 2"));
        assertThat(blocks.get(1).containerDescriptions().get(0), is("j8spec.ExecutionPlanTest$SampleSpec"));
        assertThat(blocks.get(1).beforeEachBlocks().get(0), is(beforeEachBlock));
        assertThat(blocks.get(1).body(), is(block2));

        assertThat(blocks.get(2).description(), is("block A1"));
        assertThat(blocks.get(2).containerDescriptions().get(0), is("j8spec.ExecutionPlanTest$SampleSpec"));
        assertThat(blocks.get(2).containerDescriptions().get(1), is("describe A"));
        assertThat(blocks.get(2).beforeEachBlocks().get(0), is(beforeEachBlock));
        assertThat(blocks.get(2).beforeEachBlocks().get(1), is(beforeEachBlockA));
        assertThat(blocks.get(2).body(), is(blockA1));
    }

    @Test
    public void supportsNullBeforeEachBlocks() {
        Map<String, Runnable> itBlocks = new HashMap<>();
        itBlocks.put("block 1", () -> {});
        itBlocks.put("block 2", () -> {});

        ExecutionPlan plan = new ExecutionPlan(
            SampleSpec.class,
            null,
            itBlocks
        );

        assertThat(plan.allItBlocks().get(0).beforeEachBlocks(), is(emptyList()));
    }
}
