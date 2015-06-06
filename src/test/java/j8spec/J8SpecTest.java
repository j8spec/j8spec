package j8spec;

import org.junit.Test;

import java.util.Collections;

import static j8spec.J8Spec.*;
import static j8spec.Var.var;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class J8SpecTest {

    private static final Runnable BEFORE_ALL_BLOCK = () -> {};
    private static final Runnable BEFORE_EACH_BLOCK = () -> {};
    private static final Runnable IT_BLOCK_1 = () -> {};
    private static final Runnable IT_BLOCK_2 = () -> {};

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

    static class EmptySpec {}

    static class BadSpec {
        private BadSpec() {}
    }

    static class BeforeAllBlockOverwrittenSpec {{
        beforeAll(() -> {});
        beforeAll(() -> {});
    }}

    static class BeforeEachBlockOverwrittenSpec {{
        beforeEach(() -> {});
        beforeEach(() -> {});
    }}

    static class ItBlockOverwrittenSpec {{
        it("some text", () -> {});
        it("some text", () -> {});
    }}

    static class ThreadThatSleeps2sSpec {{
        describe("forces thread to sleep", () -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            it("block", () -> {});
        });
    }}

    static class SampleSpec {{
        beforeAll(BEFORE_ALL_BLOCK);
        beforeEach(BEFORE_EACH_BLOCK);

        it("block 1", IT_BLOCK_1);
        it("block 2", IT_BLOCK_2);

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
    public void buildsAnExecutionPlanUsingTheGivenSpecAsDescription() {
        ExecutionPlan plan = executionPlanFor(EmptySpec.class);

        assertThat(plan.specClass(), equalTo(EmptySpec.class));
        assertThat(plan.description(), is("j8spec.J8SpecTest$EmptySpec"));
    }

    @Test
    public void buildsAnExecutionPlanBasedOnAnEmptySpec() {
        ExecutionPlan plan = executionPlanFor(EmptySpec.class);

        assertThat(plan.hasItBlocks(), is(false));
    }

    @Test
    public void buildsAnExecutionPlanBasedOnASpecThatContainsInnerDescribeBlocks() {
        ExecutionPlan plan = executionPlanFor(SampleSpec.class);

        assertThat(plan.beforeAllBlock(), is(BEFORE_ALL_BLOCK));
        assertThat(plan.beforeEachBlock(), is(BEFORE_EACH_BLOCK));
        assertThat(plan.itBlock("block 1"), is(IT_BLOCK_1));
        assertThat(plan.itBlock("block 2"), is(IT_BLOCK_2));

        ExecutionPlan planA = plan.plans().get(0);
        assertThat(planA.specClass(), equalTo(SampleSpec.class));
        assertThat(planA.description(), is("describe A"));
        assertThat(planA.beforeAllBlock(), is(BEFORE_ALL_A_BLOCK));
        assertThat(planA.beforeEachBlock(), is(BEFORE_EACH_A_BLOCK));
        assertThat(planA.itBlock("block A.1"), is(IT_BLOCK_A1));
        assertThat(planA.itBlock("block A.2"), is(IT_BLOCK_A2));

        ExecutionPlan planAA = planA.plans().get(0);
        assertThat(planAA.specClass(), equalTo(SampleSpec.class));
        assertThat(planAA.description(), is("describe A.A"));
        assertThat(planAA.beforeAllBlock(), is(BEFORE_ALL_AA_BLOCK));
        assertThat(planAA.beforeEachBlock(), is(BEFORE_EACH_AA_BLOCK));
        assertThat(planAA.itBlock("block A.A.1"), is(IT_BLOCK_AA1));
        assertThat(planAA.itBlock("block A.A.2"), is(IT_BLOCK_AA2));

        ExecutionPlan planB = plan.plans().get(1);
        assertThat(planB.specClass(), equalTo(SampleSpec.class));
        assertThat(planB.description(), is("describe B"));
        assertThat(planB.beforeAllBlock(), is(BEFORE_ALL_B_BLOCK));
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
    public void doesNotAllowBeforeAllMethodDirectInvocation() {
        J8Spec.beforeAll(() -> {});
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
    public void doesNotAllowBeforeAllBlockToBeReplaced() {
        executionPlanFor(BeforeAllBlockOverwrittenSpec.class);
    }

    @Test(expected = BlockAlreadyDefinedException.class)
    public void doesNotAllowBeforeEachBlockToBeReplaced() {
        executionPlanFor(BeforeEachBlockOverwrittenSpec.class);
    }

    @Test(expected = BlockAlreadyDefinedException.class)
    public void doesNotAllowItBlockToBeReplaced() {
        executionPlanFor(ItBlockOverwrittenSpec.class);
    }

    @Test()
    public void allowsMultipleThreadsToBuildPlans() throws InterruptedException {
        final Var<ExecutionPlan> sleepPlan = var();

        Thread anotherExecutionPlanThread = new Thread(() -> {
            var(sleepPlan, executionPlanFor(ThreadThatSleeps2sSpec.class));
        });
        anotherExecutionPlanThread.start();

        Thread.sleep(1000);

        ExecutionPlan emptyExecutionPlan = executionPlanFor(EmptySpec.class);

        anotherExecutionPlanThread.join();

        assertThat(emptyExecutionPlan.allItBlocks(), is(Collections.<ItBlock>emptyList()));

        assertThat(var(sleepPlan).allItBlocks().size(), is(1));
        assertThat(var(sleepPlan).allItBlocks().get(0).description(), is("block"));
    }
}
