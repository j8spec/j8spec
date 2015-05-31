package j8spec;

import org.junit.Test;

import static j8spec.J8Spec.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class J8SpecTest {

    private static final Runnable BEFORE_EACH_BLOCK = () -> {};
    private static final Runnable IT_BLOCK_1 = () -> {};
    private static final Runnable IT_BLOCK_2 = () -> {};

    private static final Runnable BEFORE_EACH_A_BLOCK = () -> {};
    private static final Runnable IT_BLOCK_A1 = () -> {};
    private static final Runnable IT_BLOCK_A2 = () -> {};

    private static final Runnable BEFORE_EACH_AA_BLOCK = () -> {};
    private static final Runnable IT_BLOCK_AA1 = () -> {};
    private static final Runnable IT_BLOCK_AA2 = () -> {};

    private static final Runnable BEFORE_EACH_B_BLOCK = () -> {};
    private static final Runnable IT_BLOCK_B1 = () -> {};
    private static final Runnable IT_BLOCK_B2 = () -> {};

    static class EmptySpec {}

    static class BadSpec {
        private BadSpec() {}
    }

    static class BeforeEachBlockOverwrittenSpec {{
        beforeEach(() -> {});
        beforeEach(() -> {});
    }}

    static class ItBlockOverwrittenSpec {{
        it("some text", () -> {});
        it("some text", () -> {});
    }}

    static class SampleSpec {{
        beforeEach(BEFORE_EACH_BLOCK);

        it("block 1", IT_BLOCK_1);
        it("block 2", IT_BLOCK_2);

        describe("describe A", () -> {
            beforeEach(BEFORE_EACH_A_BLOCK);

            it("block A.1", IT_BLOCK_A1);
            it("block A.2", IT_BLOCK_A2);

            describe("describe A.A", () -> {
                beforeEach(BEFORE_EACH_AA_BLOCK);

                it("block A.A.1", IT_BLOCK_AA1);
                it("block A.A.2", IT_BLOCK_AA2);
            });
        });

        describe("describe B", () -> {
            beforeEach(BEFORE_EACH_B_BLOCK);

            it("block B.1", IT_BLOCK_B1);
            it("block B.2", IT_BLOCK_B2);
        });
    }}

    @Test
    public void buildsAnExecutionPlanUsingTheGivenSpecAsDescription() {
        ExecutionPlan plan = executionPlanFor(EmptySpec.class);

        assertThat(plan.specClass(), equalTo(EmptySpec.class));
        assertThat(plan.getDescription(), is("j8spec.J8SpecTest$EmptySpec"));
    }

    @Test
    public void buildsAnExecutionPlanBasedOnAnEmptySpec() {
        ExecutionPlan plan = executionPlanFor(EmptySpec.class);

        assertThat(plan.hasItBlocks(), is(false));
    }

    @Test
    public void buildsAnExecutionPlanBasedOnASpecThatContainsInnerDescribeBlocks() {
        ExecutionPlan plan = executionPlanFor(SampleSpec.class);

        assertThat(plan.beforeEachBlock(), is(BEFORE_EACH_BLOCK));
        assertThat(plan.itBlock("block 1"), is(IT_BLOCK_1));
        assertThat(plan.itBlock("block 2"), is(IT_BLOCK_2));

        ExecutionPlan planA = plan.getPlans().get(0);
        assertThat(planA.specClass(), equalTo(SampleSpec.class));
        assertThat(planA.getDescription(), is("describe A"));
        assertThat(planA.beforeEachBlock(), is(BEFORE_EACH_A_BLOCK));
        assertThat(planA.itBlock("block A.1"), is(IT_BLOCK_A1));
        assertThat(planA.itBlock("block A.2"), is(IT_BLOCK_A2));

        ExecutionPlan planAA = planA.getPlans().get(0);
        assertThat(planAA.specClass(), equalTo(SampleSpec.class));
        assertThat(planAA.getDescription(), is("describe A.A"));
        assertThat(planAA.beforeEachBlock(), is(BEFORE_EACH_AA_BLOCK));
        assertThat(planAA.itBlock("block A.A.1"), is(IT_BLOCK_AA1));
        assertThat(planAA.itBlock("block A.A.2"), is(IT_BLOCK_AA2));

        ExecutionPlan planB = plan.getPlans().get(1);
        assertThat(planB.specClass(), equalTo(SampleSpec.class));
        assertThat(planB.getDescription(), is("describe B"));
        assertThat(planB.beforeEachBlock(), is(BEFORE_EACH_B_BLOCK));
        assertThat(planB.itBlock("block B.1"), is(IT_BLOCK_B1));
        assertThat(planB.itBlock("block B.2"), is(IT_BLOCK_B2));
    }

    @Test(expected = SpecInitializationException.class)
    public void throwsExceptionWhenFailsToEvaluateSpec() {
        executionPlanFor(BadSpec.class);
    }

    @Test(expected = IllegalContextException.class)
    public void doesNotAllowDescribeMethodDirectInvocation() {
        J8Spec.describe("some text", () -> {});
    }

    @Test(expected = IllegalContextException.class)
    public void forgetsLastSpec() {
        executionPlanFor(SampleSpec.class);
        J8Spec.describe("some text", () -> {});
    }

    @Test(expected = IllegalContextException.class)
    public void forgetsLastSpecAfterTheLastSpecEvaluationFails() {
        try {
            executionPlanFor(ItBlockOverwrittenSpec.class);
        } catch (BlockAlreadyDefinedException e) {
        }

        J8Spec.it("some text", () -> {});
    }

    @Test(expected = IllegalContextException.class)
    public void doesNotAllowBeforeEachMethodDirectInvocation() {
        J8Spec.beforeEach(() -> {});
    }

    @Test(expected = IllegalContextException.class)
    public void doesNotAllowItMethodDirectInvocation() {
        J8Spec.it("some text", () -> {});
    }

    @Test(expected = BlockAlreadyDefinedException.class)
    public void doesNotAllowBeforeEachBlockToBeReplaced() {
        executionPlanFor(BeforeEachBlockOverwrittenSpec.class);
    }

    @Test(expected = BlockAlreadyDefinedException.class)
    public void doesNotAllowItBlockToBeReplaced() {
        executionPlanFor(ItBlockOverwrittenSpec.class);
    }
}
