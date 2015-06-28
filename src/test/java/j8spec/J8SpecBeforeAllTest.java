package j8spec;

import org.junit.Test;

import static j8spec.J8Spec.*;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class J8SpecBeforeAllTest {

    private static final Runnable NOOP = () -> {};

    private static final Runnable BEFORE_ALL_0_BLOCK = () -> {};
    private static final Runnable BEFORE_ALL_1_BLOCK = () -> {};
    private static final Runnable BEFORE_ALL_A_BLOCK = () -> {};
    private static final Runnable BEFORE_ALL_AA_BLOCK = () -> {};

    static class NoBeforeAllSpec {{
        it("block 1", NOOP);
    }}

    static class SeveralBeforeAllSpec {{
        beforeAll(BEFORE_ALL_0_BLOCK);
        beforeAll(BEFORE_ALL_1_BLOCK);

        it("block 1", NOOP);
    }}

    static class InnerContextSpec {{
        beforeAll(BEFORE_ALL_0_BLOCK);

        it("block 1", NOOP);

        describe("describe A", () -> {
            beforeAll(BEFORE_ALL_A_BLOCK);

            it("block A.1", NOOP);

            describe("describe A.A", () -> {
                beforeAll(BEFORE_ALL_AA_BLOCK);

                it("block A.A.1", NOOP);
            });
        });
    }}

    @Test(expected = IllegalContextException.class)
    public void does_not_allow_before_all_method_direct_invocation() {
        beforeAll(NOOP);
    }

    @Test
    public void builds_an_execution_plan_when_there_is_no_before_all_block() {
        ExecutionPlan plan = executionPlanFor(NoBeforeAllSpec.class);

        assertThat(plan.beforeAllBlocks(), is(emptyList()));
    }

    @Test
    public void builds_an_execution_plan_when_there_are_several_before_all_blocks_in_the_same_context() {
        ExecutionPlan plan = executionPlanFor(SeveralBeforeAllSpec.class);

        assertThat(plan.beforeAllBlocks().get(0), is(BEFORE_ALL_0_BLOCK));
        assertThat(plan.beforeAllBlocks().get(1), is(BEFORE_ALL_1_BLOCK));
    }

    @Test
    public void builds_an_execution_plan_when_spec_has_inner_contexts() {
        ExecutionPlan plan = executionPlanFor(InnerContextSpec.class);
        ExecutionPlan planA = plan.plans().get(0);
        ExecutionPlan planAA = planA.plans().get(0);

        assertThat(plan.beforeAllBlocks().get(0), is(BEFORE_ALL_0_BLOCK));
        assertThat(planA.beforeAllBlocks().get(0), is(BEFORE_ALL_A_BLOCK));
        assertThat(planAA.beforeAllBlocks().get(0), is(BEFORE_ALL_AA_BLOCK));
    }
}
