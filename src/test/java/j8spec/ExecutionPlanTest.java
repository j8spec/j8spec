package j8spec;

import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static j8spec.ItBlockDefinition.newItBlockDefinition;
import static java.lang.String.join;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ExecutionPlanTest {

    private static final String LS = System.getProperty("line.separator");

    private static final Runnable NOOP = () -> {};

    private static final Runnable BEFORE_ALL_BLOCK = () -> {};
    private static final Runnable BEFORE_EACH_BLOCK = () -> {};
    private static final Runnable BLOCK_1 = () -> {};
    private static final Runnable BLOCK_2 = () -> {};
    private static final Runnable BEFORE_ALL_BLOCK_A = () -> {};
    private static final Runnable BEFORE_EACH_BLOCK_A = () -> {};
    private static final Runnable BLOCK_A_1 = () -> {};
    private static final Runnable BLOCK_A_2 = () -> {};

    static class SampleSpec {}

    @Test
    public void hasAStringRepresentationWhenEmpty() {
        assertThat(anEmptyExecutionPlan().toString(), is("j8spec.ExecutionPlanTest$SampleSpec"));
    }

    @Test
    public void hasAStringRepresentationWhenItContainsChildPlans() {
        assertThat(
            anExecutionPlanWithNoBeforeBlocks().toString(),
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
    public void supportsNullBeforeBlocks() {
        assertThat(anExecutionPlanWithNoBeforeBlocks().allItBlocks().get(0).beforeBlocks(), is(emptyList()));
    }

    @Test
    public void buildsItBlocksWithGivenDescription() {
        ExecutionPlan planWithInnerPlans = anExecutionPlanWithInnerPlan();

        List<ItBlock> itBlocks = planWithInnerPlans.allItBlocks();

        assertThat(itBlocks.get(0).description(), is("block 1"));
        assertThat(itBlocks.get(1).description(), is("block 2"));
        assertThat(itBlocks.get(2).description(), is("block A1"));
        assertThat(itBlocks.get(3).description(), is("block A2"));
    }

    @Test
    public void buildsItBlocksWithGivenContainerDescriptions() {
        ExecutionPlan planWithInnerPlans = anExecutionPlanWithInnerPlan();

        List<ItBlock> itBlocks = planWithInnerPlans.allItBlocks();

        assertThat(itBlocks.get(0).containerDescriptions(), is(asList("j8spec.ExecutionPlanTest$SampleSpec")));
        assertThat(itBlocks.get(1).containerDescriptions(), is(asList("j8spec.ExecutionPlanTest$SampleSpec")));
        assertThat(itBlocks.get(2).containerDescriptions(), is(asList("j8spec.ExecutionPlanTest$SampleSpec", "describe A")));
        assertThat(itBlocks.get(3).containerDescriptions(), is(asList("j8spec.ExecutionPlanTest$SampleSpec", "describe A")));
    }

    @Test
    public void buildsItBlocksWithGivenBodies() {
        ExecutionPlan planWithInnerPlans = anExecutionPlanWithInnerPlan();

        List<ItBlock> itBlocks = planWithInnerPlans.allItBlocks();

        assertThat(itBlocks.get(0).body(), is(BLOCK_1));
        assertThat(itBlocks.get(1).body(), is(BLOCK_2));
        assertThat(itBlocks.get(2).body(), is(BLOCK_A_1));
        assertThat(itBlocks.get(3).body(), is(BLOCK_A_2));
    }

    @Test
    public void ensuresBeforeAllBlocksAreConfiguredToRunJustOnce() {
        ExecutionPlan planWithInnerPlans = anExecutionPlanWithInnerPlan();

        List<ItBlock> itBlocks = planWithInnerPlans.allItBlocks();

        assertThat(itBlocks.get(0).beforeBlocks().get(0).body(), is(BEFORE_ALL_BLOCK));
        assertThat(itBlocks.get(0).beforeBlocks().get(0).justOnce(), is(true));

        assertThat(itBlocks.get(1).beforeBlocks().get(0).body(), is(BEFORE_ALL_BLOCK));
        assertThat(itBlocks.get(1).beforeBlocks().get(0).justOnce(), is(true));

        assertThat(itBlocks.get(2).beforeBlocks().get(0).body(), is(BEFORE_ALL_BLOCK));
        assertThat(itBlocks.get(2).beforeBlocks().get(0).justOnce(), is(true));
        assertThat(itBlocks.get(2).beforeBlocks().get(1).body(), is(BEFORE_ALL_BLOCK_A));
        assertThat(itBlocks.get(2).beforeBlocks().get(1).justOnce(), is(true));

        assertThat(itBlocks.get(3).beforeBlocks().get(0).body(), is(BEFORE_ALL_BLOCK));
        assertThat(itBlocks.get(3).beforeBlocks().get(0).justOnce(), is(true));
        assertThat(itBlocks.get(3).beforeBlocks().get(1).body(), is(BEFORE_ALL_BLOCK_A));
        assertThat(itBlocks.get(3).beforeBlocks().get(1).justOnce(), is(true));
    }

    @Test
    public void ensuresBeforeAllBlocksAreReusedAcrossItBlocks() {
        ExecutionPlan planWithInnerPlans = anExecutionPlanWithInnerPlan();

        List<ItBlock> itBlocks = planWithInnerPlans.allItBlocks();

        BeforeBlock beforeAllBlock = itBlocks.get(0).beforeBlocks().get(0);
        assertThat(itBlocks.get(1).beforeBlocks().get(0), is(beforeAllBlock));
        assertThat(itBlocks.get(2).beforeBlocks().get(0), is(beforeAllBlock));
        assertThat(itBlocks.get(3).beforeBlocks().get(0), is(beforeAllBlock));

        BeforeBlock beforeAllBlockA = itBlocks.get(2).beforeBlocks().get(1);
        assertThat(itBlocks.get(3).beforeBlocks().get(1), is(beforeAllBlockA));
    }

    @Test
    public void ensuresBeforeEachBlocksAreConfiguredToAlwaysRun() {
        ExecutionPlan planWithInnerPlans = anExecutionPlanWithInnerPlan();

        List<ItBlock> itBlocks = planWithInnerPlans.allItBlocks();

        assertThat(itBlocks.get(0).beforeBlocks().get(1).body(), is(BEFORE_EACH_BLOCK));
        assertThat(itBlocks.get(0).beforeBlocks().get(1).justOnce(), is(false));

        assertThat(itBlocks.get(1).beforeBlocks().get(1).body(), is(BEFORE_EACH_BLOCK));
        assertThat(itBlocks.get(1).beforeBlocks().get(1).justOnce(), is(false));

        assertThat(itBlocks.get(2).beforeBlocks().get(2).body(), is(BEFORE_EACH_BLOCK));
        assertThat(itBlocks.get(2).beforeBlocks().get(2).justOnce(), is(false));
        assertThat(itBlocks.get(2).beforeBlocks().get(3).body(), is(BEFORE_EACH_BLOCK_A));
        assertThat(itBlocks.get(2).beforeBlocks().get(3).justOnce(), is(false));

        assertThat(itBlocks.get(3).beforeBlocks().get(2).body(), is(BEFORE_EACH_BLOCK));
        assertThat(itBlocks.get(3).beforeBlocks().get(2).justOnce(), is(false));
        assertThat(itBlocks.get(3).beforeBlocks().get(3).body(), is(BEFORE_EACH_BLOCK_A));
        assertThat(itBlocks.get(3).beforeBlocks().get(3).justOnce(), is(false));
    }

    private ExecutionPlan anEmptyExecutionPlan() {
        return new ExecutionPlan(SampleSpec.class, null, null, emptyMap());
    }

    private ExecutionPlan anExecutionPlanWithNoBeforeBlocks() {
        Map<String, ItBlockDefinition> itBlocks = new HashMap<>();
        itBlocks.put("block 1", newItBlockDefinition(NOOP));
        itBlocks.put("block 2", newItBlockDefinition(NOOP));

        ExecutionPlan planWithInnerPlans = new ExecutionPlan(SampleSpec.class, null, null, itBlocks);

        planWithInnerPlans.newChildPlan("child 1", null, null, itBlocks);
        planWithInnerPlans.newChildPlan("child 2", null, null, itBlocks);

        return planWithInnerPlans;
    }

    private ExecutionPlan anExecutionPlanWithInnerPlan() {
        Map<String, ItBlockDefinition> itBlocks = new HashMap<>();
        itBlocks.put("block 1", newItBlockDefinition(BLOCK_1));
        itBlocks.put("block 2", newItBlockDefinition(BLOCK_2));

        ExecutionPlan planWithInnerPlans = new ExecutionPlan(
            SampleSpec.class,
            BEFORE_ALL_BLOCK,
            BEFORE_EACH_BLOCK,
            itBlocks
        );

        Map<String, ItBlockDefinition> itBlocksA = new HashMap<>();
        itBlocksA.put("block A1", newItBlockDefinition(BLOCK_A_1));
        itBlocksA.put("block A2", newItBlockDefinition(BLOCK_A_2));

        planWithInnerPlans.newChildPlan(
            "describe A",
            BEFORE_ALL_BLOCK_A,
            BEFORE_EACH_BLOCK_A,
            itBlocksA
        );

        return planWithInnerPlans;
    }
}
