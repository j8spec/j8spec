package j8spec;

import org.junit.Before;
import org.junit.Test;

import static j8spec.J8Spec.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class J8SpecBeforeEachTest {

    private static final Runnable NOOP = () -> {};
    
    private static final Runnable BEFORE_EACH_BLOCK = NOOP;
    private static final Runnable BEFORE_EACH_A_BLOCK = NOOP;
    private static final Runnable BEFORE_EACH_AA_BLOCK = NOOP;
    private static final Runnable BEFORE_EACH_B_BLOCK = NOOP;

    static class BeforeEachBlockOverwrittenSpec {{
        beforeEach(NOOP);
        beforeEach(NOOP);
    }}

    static class NoBeforeEachSpec {{
        it("block 1", NOOP);
    }}

    static class SampleSpec {{
        beforeEach(BEFORE_EACH_BLOCK);

        it("block 1", NOOP);

        describe("describe A", () -> {
            beforeEach(BEFORE_EACH_A_BLOCK);

            it("block A.1", NOOP);

            describe("describe A.A", () -> {
                beforeEach(BEFORE_EACH_AA_BLOCK);

                it("block A.A.1", NOOP);
            });
        });

        describe("describe B", () -> {
            beforeEach(BEFORE_EACH_B_BLOCK);

            it("block B.1", NOOP);
        });
    }}

    private ExecutionPlan sampleSpecPlan;
    private ExecutionPlan describeAPlan;
    private ExecutionPlan describeAAPlan;
    private ExecutionPlan describeBPlan;

    @Before
    public void buildExecutionPlanForSampleSpec() {
        sampleSpecPlan = executionPlanFor(SampleSpec.class);
        describeAPlan = sampleSpecPlan.plans().get(0);
        describeAAPlan = describeAPlan.plans().get(0);
        describeBPlan = sampleSpecPlan.plans().get(1);
    }

    @Test(expected = IllegalContextException.class)
    public void does_not_allow_before_each_method_direct_invocation() {
        beforeEach(NOOP);
    }

    @Test(expected = BlockAlreadyDefinedException.class)
    public void does_not_allow_before_each_block_to_be_replaced() {
        executionPlanFor(BeforeEachBlockOverwrittenSpec.class);
    }

    @Test
    public void builds_an_execution_plan_when_there_is_no_before_each_block() {
        ExecutionPlan plan = executionPlanFor(NoBeforeEachSpec.class);

        assertThat(plan.beforeEachBlock(), is(nullValue()));
    }

    @Test
    public void builds_an_execution_plan_using_before_each_blocks_from_the_spec_definition() {
        assertThat(sampleSpecPlan.beforeEachBlock(), is(BEFORE_EACH_BLOCK));
        assertThat(describeAPlan.beforeEachBlock(), is(BEFORE_EACH_A_BLOCK));
        assertThat(describeAAPlan.beforeEachBlock(), is(BEFORE_EACH_AA_BLOCK));
        assertThat(describeBPlan.beforeEachBlock(), is(BEFORE_EACH_B_BLOCK));
    }
}
