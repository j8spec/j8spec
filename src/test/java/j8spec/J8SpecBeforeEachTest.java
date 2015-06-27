package j8spec;

import org.junit.Test;

import static j8spec.J8Spec.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class J8SpecBeforeEachTest {

    private static final Runnable NOOP = () -> {};
    
    private static final Runnable BEFORE_EACH_0_BLOCK = NOOP;
    private static final Runnable BEFORE_EACH_1_BLOCK = NOOP;
    private static final Runnable BEFORE_EACH_A_BLOCK = NOOP;
    private static final Runnable BEFORE_EACH_AA_BLOCK = NOOP;

    static class NoBeforeEachSpec {{
        it("block 1", NOOP);
    }}

    static class SeveralBeforeEachSpec {{
        beforeEach(BEFORE_EACH_0_BLOCK);
        beforeEach(BEFORE_EACH_1_BLOCK);

        it("block 1", NOOP);
    }}

    static class InnerContextSpec {{
        beforeEach(BEFORE_EACH_0_BLOCK);

        it("block 1", NOOP);

        describe("describe A", () -> {
            beforeEach(BEFORE_EACH_A_BLOCK);

            it("block A.1", NOOP);

            describe("describe A.A", () -> {
                beforeEach(BEFORE_EACH_AA_BLOCK);

                it("block A.A.1", NOOP);
            });
        });
    }}

    @Test(expected = IllegalContextException.class)
    public void does_not_allow_before_each_method_direct_invocation() {
        beforeEach(NOOP);
    }

    @Test
    public void builds_an_execution_plan_when_there_is_no_before_each_block() {
        ExecutionPlan plan = executionPlanFor(NoBeforeEachSpec.class);

        assertThat(plan.beforeEachBlock(), is(nullValue()));
    }

    @Test
    public void builds_an_execution_plan_when_there_are_several_before_each_blocks_in_the_same_context() {
        ExecutionPlan plan = executionPlanFor(SeveralBeforeEachSpec.class);

        assertThat(plan.beforeEachBlockAt(0), is(BEFORE_EACH_0_BLOCK));
        assertThat(plan.beforeEachBlockAt(1), is(BEFORE_EACH_1_BLOCK));
    }

    @Test
    public void builds_an_execution_plan_when_spec_has_inner_contexts() {
        ExecutionPlan sampleSpecPlan = executionPlanFor(InnerContextSpec.class);
        ExecutionPlan describeAPlan = sampleSpecPlan.plans().get(0);
        ExecutionPlan describeAAPlan = describeAPlan.plans().get(0);

        assertThat(sampleSpecPlan.beforeEachBlock(), is(BEFORE_EACH_0_BLOCK));
        assertThat(describeAPlan.beforeEachBlock(), is(BEFORE_EACH_A_BLOCK));
        assertThat(describeAAPlan.beforeEachBlock(), is(BEFORE_EACH_AA_BLOCK));
    }
}
