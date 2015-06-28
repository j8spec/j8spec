package j8spec;

import org.junit.Test;

import static j8spec.J8Spec.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class J8SpecBeforeAllTest {

    private static final Runnable NOOP = () -> {};

    private static final Runnable BEFORE_ALL_BLOCK = () -> {};
    private static final Runnable BEFORE_ALL_A_BLOCK = () -> {};
    private static final Runnable BEFORE_ALL_AA_BLOCK = () -> {};

    static class BeforeAllBlockOverwrittenSpec {{
        beforeAll(NOOP);
        beforeAll(NOOP);
    }}

    static class InnerContextSpec {{
        beforeAll(BEFORE_ALL_BLOCK);

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

    @Test(expected = BlockAlreadyDefinedException.class)
    public void does_not_allow_before_all_block_to_be_replaced() {
        executionPlanFor(BeforeAllBlockOverwrittenSpec.class);
    }

    @Test
    public void builds_an_execution_plan_using_before_all_blocks_from_the_spec_definition() {
        ExecutionPlan plan = executionPlanFor(InnerContextSpec.class);
        ExecutionPlan planA = plan.plans().get(0);
        ExecutionPlan planAA = planA.plans().get(0);

        assertThat(plan.beforeAllBlock(), is(BEFORE_ALL_BLOCK));
        assertThat(planA.beforeAllBlock(), is(BEFORE_ALL_A_BLOCK));
        assertThat(planAA.beforeAllBlock(), is(BEFORE_ALL_AA_BLOCK));
    }
}
