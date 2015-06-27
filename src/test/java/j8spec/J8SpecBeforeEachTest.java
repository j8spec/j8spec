package j8spec;

import org.junit.Test;

import static j8spec.J8Spec.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class J8SpecBeforeEachTest {

    private static final Runnable BEFORE_ALL_BLOCK = () -> {};
    private static final Runnable BEFORE_EACH_BLOCK = () -> {};
    private static final Runnable IT_BLOCK_1 = () -> {};
    private static final Runnable IT_BLOCK_2 = () -> {};
    private static final Runnable IT_BLOCK_3 = () -> {};

    private static final Runnable BEFORE_ALL_A_BLOCK = () -> {};
    private static final Runnable BEFORE_EACH_A_BLOCK = () -> {};
    private static final Runnable IT_BLOCK_A1 = () -> {};
    private static final Runnable IT_BLOCK_A2 = () -> {};

    private static final Runnable BEFORE_ALL_AA_BLOCK = () -> {};
    private static final Runnable BEFORE_EACH_AA_BLOCK = () -> {};
    private static final Runnable IT_BLOCK_AA1 = () -> {};
    private static final Runnable IT_BLOCK_AA2 = () -> {};

    private static final Runnable BEFORE_ALL_B_BLOCK = () -> {};
    private static final Runnable BEFORE_EACH_B_BLOCK = () -> {};
    private static final Runnable IT_BLOCK_B1 = () -> {};
    private static final Runnable IT_BLOCK_B2 = () -> {};

    static class BeforeEachBlockOverwrittenSpec {{
        beforeEach(() -> {});
        beforeEach(() -> {});
    }}

    static class SampleSpec {{
        beforeAll(BEFORE_ALL_BLOCK);
        beforeEach(BEFORE_EACH_BLOCK);

        it("block 1", IT_BLOCK_1);
        it("block 2", IT_BLOCK_2);
        xit("block 3", IT_BLOCK_3);

        describe("describe A", () -> {
            beforeAll(BEFORE_ALL_A_BLOCK);
            beforeEach(BEFORE_EACH_A_BLOCK);

            it("block A.1", IT_BLOCK_A1);
            it("block A.2", IT_BLOCK_A2);

            describe("describe A.A", () -> {
                beforeAll(BEFORE_ALL_AA_BLOCK);
                beforeEach(BEFORE_EACH_AA_BLOCK);

                it("block A.A.1", IT_BLOCK_AA1);
                it("block A.A.2", IT_BLOCK_AA2);
            });
        });

        describe("describe B", () -> {
            beforeAll(BEFORE_ALL_B_BLOCK);
            beforeEach(BEFORE_EACH_B_BLOCK);

            it("block B.1", IT_BLOCK_B1);
            it("block B.2", IT_BLOCK_B2);
        });
    }}

    @Test
    public void buildsAnExecutionPlanUsingBeforeEachBlocksFromTheSpecDefinition() {
        ExecutionPlan plan = executionPlanFor(SampleSpec.class);
        assertThat(plan.beforeEachBlock(), is(BEFORE_EACH_BLOCK));

        ExecutionPlan planA = plan.plans().get(0);
        assertThat(planA.beforeEachBlock(), is(BEFORE_EACH_A_BLOCK));

        ExecutionPlan planAA = planA.plans().get(0);
        assertThat(planAA.beforeEachBlock(), is(BEFORE_EACH_AA_BLOCK));

        ExecutionPlan planB = plan.plans().get(1);
        assertThat(planB.beforeEachBlock(), is(BEFORE_EACH_B_BLOCK));
    }

    @Test(expected = IllegalContextException.class)
    public void doesNotAllowBeforeEachMethodDirectInvocation() {
        J8Spec.beforeEach(() -> {});
    }

    @Test(expected = BlockAlreadyDefinedException.class)
    public void doesNotAllowBeforeEachBlockToBeReplaced() {
        executionPlanFor(BeforeEachBlockOverwrittenSpec.class);
    }

}
