package j8spec;

import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static j8spec.ExecutionPlan.newExecutionPlan;
import static j8spec.ItBlockDefinition.*;
import static java.lang.String.join;
import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static org.hamcrest.CoreMatchers.equalTo;
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
    private static final Runnable BLOCK_A_A_1 = () -> {};
    private static final Runnable BLOCK_A_A_2 = () -> {};

    static class SampleSpec {}

    @Test
    public void has_a_string_representation_when_empty() {
        assertThat(anEmptyExecutionPlan().toString(), is("j8spec.ExecutionPlanTest$SampleSpec"));
    }

    @Test
    public void has_a_string_representation_when_it_contains_child_plans() {
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
    public void supports_null_before_blocks() {
        assertThat(anExecutionPlanWithNoBeforeBlocks().allItBlocks().get(0).beforeBlocks(), is(emptyList()));
    }

    @Test
    public void builds_it_blocks_with_given_description() {
        ExecutionPlan planWithInnerPlans = anExecutionPlanWithInnerPlan();

        List<ItBlock> itBlocks = planWithInnerPlans.allItBlocks();

        assertThat(itBlocks.get(0).description(), is("block 1"));
        assertThat(itBlocks.get(1).description(), is("block 2"));
        assertThat(itBlocks.get(2).description(), is("block A1"));
        assertThat(itBlocks.get(3).description(), is("block A2"));
    }

    @Test
    public void builds_it_blocks_with_given_container_descriptions() {
        ExecutionPlan planWithInnerPlans = anExecutionPlanWithInnerPlan();

        List<ItBlock> itBlocks = planWithInnerPlans.allItBlocks();

        assertThat(itBlocks.get(0).containerDescriptions(), is(singletonList("j8spec.ExecutionPlanTest$SampleSpec")));
        assertThat(itBlocks.get(1).containerDescriptions(), is(singletonList("j8spec.ExecutionPlanTest$SampleSpec")));
        assertThat(itBlocks.get(2).containerDescriptions(), is(asList("j8spec.ExecutionPlanTest$SampleSpec", "describe A")));
        assertThat(itBlocks.get(3).containerDescriptions(), is(asList("j8spec.ExecutionPlanTest$SampleSpec", "describe A")));
    }

    @Test
    public void builds_it_blocks_marked_to_be_ignored() {
        ExecutionPlan plan = anExecutionPlanWithIgnoredItBlocks();

        List<ItBlock> itBlocks = plan.allItBlocks();

        assertThat(itBlocks.get(0).shouldBeIgnored(), is(true));
    }

    @Test
    public void builds_it_blocks_marked_to_be_ignored_when_the_describe_block_is_ignored() {
        ExecutionPlan plan = anExecutionPlanWithIgnoredDescribeBlocks();

        List<ItBlock> itBlocks = plan.allItBlocks();

        assertThat(itBlocks.get(0).shouldBeIgnored(), is(false));
        assertThat(itBlocks.get(1).shouldBeIgnored(), is(true));
        assertThat(itBlocks.get(2).shouldBeIgnored(), is(true));
    }

    @Test
    public void builds_it_blocks_marked_to_be_ignored_when_there_is_it_blocks_focused() {
        ExecutionPlan plan = anExecutionPlanWithFocusedItBlocks();

        List<ItBlock> itBlocks = plan.allItBlocks();

        assertThat(itBlocks.get(0).shouldBeIgnored(), is(true));
        assertThat(itBlocks.get(1).shouldBeIgnored(), is(true));
        assertThat(itBlocks.get(2).shouldBeIgnored(), is(false));
        assertThat(itBlocks.get(3).shouldBeIgnored(), is(true));
    }

    @Test
    public void builds_it_blocks_marked_to_be_ignored_when_there_is_describe_blocks_focused() {
        ExecutionPlan plan = anExecutionPlanWithFocusedDescribeBlocks();

        List<ItBlock> itBlocks = plan.allItBlocks();

        assertThat(itBlocks.get(0).shouldBeIgnored(), is(true));
        assertThat(itBlocks.get(1).shouldBeIgnored(), is(false));
        assertThat(itBlocks.get(2).shouldBeIgnored(), is(false));
        assertThat(itBlocks.get(3).shouldBeIgnored(), is(false));
        assertThat(itBlocks.get(4).shouldBeIgnored(), is(false));
    }

    @Test
    public void builds_it_blocks_with_excepted_exception() {
        ExecutionPlan plan = anExecutionPlanWithExpectedException();

        List<ItBlock> itBlocks = plan.allItBlocks();

        assertThat(itBlocks.get(0).expected(), is(equalTo(Exception.class)));
    }

    private ExecutionPlan anEmptyExecutionPlan() {
        return newExecutionPlan(SampleSpec.class, null, emptyList(), emptyMap());
    }

    private ExecutionPlan anExecutionPlanWithNoBeforeBlocks() {
        Map<String, ItBlockDefinition> itBlocks = new HashMap<>();
        itBlocks.put("block 1", newItBlockDefinition(NOOP));
        itBlocks.put("block 2", newItBlockDefinition(NOOP));

        ExecutionPlan planWithInnerPlans = newExecutionPlan(SampleSpec.class, null, emptyList(), itBlocks);

        planWithInnerPlans.newChildPlan("child 1", null, emptyList(), itBlocks);
        planWithInnerPlans.newChildPlan("child 2", null, emptyList(), itBlocks);

        return planWithInnerPlans;
    }

    private ExecutionPlan anExecutionPlanWithInnerPlan() {
        Map<String, ItBlockDefinition> itBlocks = new HashMap<>();
        itBlocks.put("block 1", newItBlockDefinition(BLOCK_1));
        itBlocks.put("block 2", newItBlockDefinition(BLOCK_2));

        ExecutionPlan planWithInnerPlans = newExecutionPlan(
            SampleSpec.class,
            BEFORE_ALL_BLOCK,
            singletonList(BEFORE_EACH_BLOCK),
            itBlocks
        );

        Map<String, ItBlockDefinition> itBlocksA = new HashMap<>();
        itBlocksA.put("block A1", newItBlockDefinition(BLOCK_A_1));
        itBlocksA.put("block A2", newItBlockDefinition(BLOCK_A_2));

        planWithInnerPlans.newChildPlan(
            "describe A",
            BEFORE_ALL_BLOCK_A,
            singletonList(BEFORE_EACH_BLOCK_A),
            itBlocksA
        );

        return planWithInnerPlans;
    }

    private ExecutionPlan anExecutionPlanWithIgnoredItBlocks() {
        Map<String, ItBlockDefinition> itBlocks = new HashMap<>();
        itBlocks.put("block 1", newIgnoredItBlockDefinition(BLOCK_1));

        return newExecutionPlan(
            SampleSpec.class,
            BEFORE_ALL_BLOCK,
            singletonList(BEFORE_EACH_BLOCK),
            itBlocks
        );
    }

    private ExecutionPlan anExecutionPlanWithExpectedException() {
        Map<String, ItBlockDefinition> itBlocks = new HashMap<>();
        itBlocks.put("block 1", newItBlockDefinition(BLOCK_1, Exception.class));

        return newExecutionPlan(SampleSpec.class, BEFORE_ALL_BLOCK, singletonList(BEFORE_EACH_BLOCK), itBlocks);
    }

    private ExecutionPlan anExecutionPlanWithFocusedItBlocks() {
        Map<String, ItBlockDefinition> itBlocks = new HashMap<>();
        itBlocks.put("block 1", newItBlockDefinition(BLOCK_1));
        itBlocks.put("block 2", newItBlockDefinition(BLOCK_2));

        ExecutionPlan plan = newExecutionPlan(
            SampleSpec.class,
            BEFORE_ALL_BLOCK,
            singletonList(BEFORE_EACH_BLOCK),
            itBlocks
        );

        Map<String, ItBlockDefinition> itBlocksA = new HashMap<>();
        itBlocksA.put("block A1", newFocusedItBlockDefinition(BLOCK_A_1));
        itBlocksA.put("block A2", newItBlockDefinition(BLOCK_A_2));

        plan.newIgnoredChildPlan("describe A", BEFORE_ALL_BLOCK, singletonList(BEFORE_EACH_BLOCK), itBlocksA);

        return plan;
    }

    private ExecutionPlan anExecutionPlanWithFocusedDescribeBlocks() {
        Map<String, ItBlockDefinition> itBlocks = new HashMap<>();
        itBlocks.put("block 1", newItBlockDefinition(BLOCK_1));

        ExecutionPlan plan = newExecutionPlan(SampleSpec.class, null, emptyList(), itBlocks);

        Map<String, ItBlockDefinition> itBlocksA = new HashMap<>();
        itBlocksA.put("block A1", newItBlockDefinition(BLOCK_A_1));
        itBlocksA.put("block A2", newItBlockDefinition(BLOCK_A_2));

        ExecutionPlan planA = plan.newFocusedChildPlan("describe A", null, emptyList(), itBlocksA);

        Map<String, ItBlockDefinition> itBlocksAA = new HashMap<>();
        itBlocksAA.put("block AA1", newItBlockDefinition(BLOCK_A_A_1));
        itBlocksAA.put("block AA2", newItBlockDefinition(BLOCK_A_A_2));

        planA.newChildPlan("describe A A", null, emptyList(), itBlocksAA);

        return plan;
    }

    private ExecutionPlan anExecutionPlanWithIgnoredDescribeBlocks() {
        Map<String, ItBlockDefinition> itBlocks = new HashMap<>();
        itBlocks.put("block 1", newItBlockDefinition(BLOCK_1));

        ExecutionPlan plan = newExecutionPlan(
            SampleSpec.class,
            BEFORE_ALL_BLOCK,
            singletonList(BEFORE_EACH_BLOCK),
            itBlocks
        );

        Map<String, ItBlockDefinition> itBlocksA = new HashMap<>();
        itBlocksA.put("block A1", newItBlockDefinition(BLOCK_A_1));
        itBlocksA.put("block A2", newItBlockDefinition(BLOCK_A_2));

        plan.newIgnoredChildPlan("describe A", BEFORE_ALL_BLOCK, singletonList(BEFORE_EACH_BLOCK), itBlocksA);

        return plan;
    }
}
