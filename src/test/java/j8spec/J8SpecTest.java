package j8spec;

import org.junit.Test;

import java.util.Collections;

import static j8spec.J8Spec.*;
import static j8spec.Var.var;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class J8SpecTest {

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

    static class EmptySpec {}

    static class BadSpec {
        private BadSpec() {}
    }

    static class BeforeAllBlockOverwrittenSpec {{
        beforeAll(() -> {});
        beforeAll(() -> {});
    }}

    static class ItBlockOverwrittenSpec {{
        it("some text", () -> {});
        it("some text", () -> {});
    }}

    static class ItBlockWithCollectorOverwrittenSpec {{
        it("some text", c -> c, () -> {});
        it("some text", () -> {});
    }}

    static class XitBlockOverwrittenSpec {{
        xit("some text", () -> {});
        xit("some text", () -> {});
    }}

    static class XitBlockWithCollectorOverwrittenSpec {{
        xit("some text", c -> c, () -> {});
        xit("some text", () -> {});
    }}

    static class FitBlockOverwrittenSpec {{
        fit("some text", () -> {});
        fit("some text", () -> {});
    }}

    static class FitBlockWithCollectorOverwrittenSpec {{
        fit("some text", c -> c, () -> {});
        fit("some text", () -> {});
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

    static class XdescribeSpec {{
        beforeAll(BEFORE_ALL_BLOCK);
        beforeEach(BEFORE_EACH_BLOCK);

        it("block 1", IT_BLOCK_1);
        it("block 2", IT_BLOCK_2);

        xdescribe("describe A", () -> {
            beforeAll(BEFORE_ALL_A_BLOCK);
            beforeEach(BEFORE_EACH_A_BLOCK);

            it("block A.1", IT_BLOCK_A1);
            it("block A.2", IT_BLOCK_A2);
        });
    }}

    static class FdescribeSpec {{
        it("block 1", IT_BLOCK_1);
        it("block 2", IT_BLOCK_2);

        fdescribe("describe A", () -> {
            it("block A.1", IT_BLOCK_A1);
            it("block A.2", IT_BLOCK_A2);
        });
    }}

    static class FitSpec {{
        fit("block 1", IT_BLOCK_1);
    }}

    static class ExpectedExceptionSpec {{
        it("block 1", c -> c.expected(Exception.class), IT_BLOCK_1);
        xit("block 2", c -> c.expected(Exception.class), IT_BLOCK_2);
        fit("block 3", c -> c.expected(Exception.class), IT_BLOCK_3);
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
    public void buildsAnExecutionPlanUsingTheGivenSpecAsDescription() {
        ExecutionPlan plan = executionPlanFor(EmptySpec.class);

        assertThat(plan.specClass(), equalTo(EmptySpec.class));
        assertThat(plan.description(), is("j8spec.J8SpecTest$EmptySpec"));
    }

    @Test
    public void buildsAnExecutionPlanBasedOnAnEmptySpec() {
        ExecutionPlan plan = executionPlanFor(EmptySpec.class);

        assertThat(plan.allItBlocks(), is(emptyList()));
    }

    @Test
    public void buildsAnExecutionPlanWhereAllInnerPlansHaveTheSameSpecClass() {
        ExecutionPlan plan = executionPlanFor(SampleSpec.class);

        ExecutionPlan planA = plan.plans().get(0);
        assertThat(planA.specClass(), equalTo(SampleSpec.class));

        ExecutionPlan planAA = planA.plans().get(0);
        assertThat(planAA.specClass(), equalTo(SampleSpec.class));

        ExecutionPlan planB = plan.plans().get(1);
        assertThat(planB.specClass(), equalTo(SampleSpec.class));
    }

    @Test
    public void buildsAnExecutionPlanUsingTheDescriptionFromTheSpecDefinition() {
        ExecutionPlan plan = executionPlanFor(SampleSpec.class);

        ExecutionPlan planA = plan.plans().get(0);
        assertThat(planA.description(), is("describe A"));

        ExecutionPlan planAA = planA.plans().get(0);
        assertThat(planAA.description(), is("describe A.A"));

        ExecutionPlan planB = plan.plans().get(1);
        assertThat(planB.description(), is("describe B"));
    }

    @Test
    public void buildsAnExecutionPlanUsingBeforeAllBlocksFromTheSpecDefinition() {
        ExecutionPlan plan = executionPlanFor(SampleSpec.class);
        assertThat(plan.beforeAllBlock(), is(BEFORE_ALL_BLOCK));

        ExecutionPlan planA = plan.plans().get(0);
        assertThat(planA.beforeAllBlock(), is(BEFORE_ALL_A_BLOCK));

        ExecutionPlan planAA = planA.plans().get(0);
        assertThat(planAA.beforeAllBlock(), is(BEFORE_ALL_AA_BLOCK));

        ExecutionPlan planB = plan.plans().get(1);
        assertThat(planB.beforeAllBlock(), is(BEFORE_ALL_B_BLOCK));
    }

    @Test
    public void buildsAnExecutionPlanUsingItBlocksFromTheSpecDefinition() {
        ExecutionPlan plan = executionPlanFor(SampleSpec.class);

        assertThat(plan.itBlock("block 1").body(), is(IT_BLOCK_1));
        assertThat(plan.itBlock("block 2").body(), is(IT_BLOCK_2));
        assertThat(plan.itBlock("block 3").body(), is(IT_BLOCK_3));

        ExecutionPlan planA = plan.plans().get(0);
        assertThat(planA.itBlock("block A.1").body(), is(IT_BLOCK_A1));
        assertThat(planA.itBlock("block A.2").body(), is(IT_BLOCK_A2));

        ExecutionPlan planAA = planA.plans().get(0);
        assertThat(planAA.itBlock("block A.A.1").body(), is(IT_BLOCK_AA1));
        assertThat(planAA.itBlock("block A.A.2").body(), is(IT_BLOCK_AA2));

        ExecutionPlan planB = plan.plans().get(1);
        assertThat(planB.itBlock("block B.1").body(), is(IT_BLOCK_B1));
        assertThat(planB.itBlock("block B.2").body(), is(IT_BLOCK_B2));
    }

    @Test
    public void buildsAnExecutionPlanMarkingXdescribeBlocksFromTheSpecDefinitionAsIgnored() {
        ExecutionPlan plan = executionPlanFor(XdescribeSpec.class);

        assertThat(plan.ignored(), is(false));
        assertThat(plan.plans().get(0).ignored(), is(true));
    }

    @Test
    public void buildsAnExecutionPlanMarkingFdescribeBlocksFromTheSpecDefinitionAsFocused() {
        ExecutionPlan plan = executionPlanFor(FdescribeSpec.class);

        assertThat(plan.focused(), is(false));
        assertThat(plan.plans().get(0).focused(), is(true));
    }

    @Test
    public void buildsAnExecutionPlanMarkingXitBlocksFromTheSpecDefinitionAsIgnored() {
        ExecutionPlan plan = executionPlanFor(SampleSpec.class);

        assertThat(plan.itBlock("block 3").ignored(), is(true));
    }

    @Test
    public void buildsAnExecutionPlanMarkingFitBlocksFromTheSpecDefinitionAsFocused() {
        ExecutionPlan plan = executionPlanFor(FitSpec.class);

        assertThat(plan.itBlock("block 1").focused(), is(true));
    }

    @Test
    public void buildsAnExecutionPlanUsingExceptedExceptionsFromTheSpecDefinition() {
        ExecutionPlan plan = executionPlanFor(ExpectedExceptionSpec.class);

        assertThat(plan.itBlock("block 1").expected(), is(equalTo(Exception.class)));
        assertThat(plan.itBlock("block 2").expected(), is(equalTo(Exception.class)));
        assertThat(plan.itBlock("block 3").expected(), is(equalTo(Exception.class)));
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
    public void doesNotAllowXdescribeMethodDirectInvocation() {
        J8Spec.xdescribe("some text", () -> {});
    }

    @Test(expected = IllegalContextException.class)
    public void doesNotAllowFdescribeMethodDirectInvocation() {
        J8Spec.fdescribe("some text", () -> {});
    }

    @Test(expected = IllegalContextException.class)
    public void doesNotAllowBeforeAllMethodDirectInvocation() {
        J8Spec.beforeAll(() -> {});
    }

    @Test(expected = IllegalContextException.class)
    public void doesNotAllowItMethodDirectInvocation() {
        J8Spec.it("some text", () -> {});
    }

    @Test(expected = IllegalContextException.class)
    public void doesNotAllowItMethodDirectInvocationWithCollector() {
        J8Spec.it("some text", c -> c, () -> {});
    }

    @Test(expected = IllegalContextException.class)
    public void doesNotAllowXitMethodDirectInvocation() {
        J8Spec.xit("some text", () -> {});
    }

    @Test(expected = IllegalContextException.class)
    public void doesNotAllowXitMethodDirectInvocationWithCollector() {
        J8Spec.xit("some text", c -> c, () -> {});
    }

    @Test(expected = IllegalContextException.class)
    public void doesNotAllowFitMethodDirectInvocation() {
        J8Spec.fit("some text", () -> {});
    }

    @Test(expected = IllegalContextException.class)
    public void doesNotAllowFitMethodWithCollectorDirectInvocation() {
        J8Spec.fit("some text", c -> c, () -> {});
    }

    @Test(expected = BlockAlreadyDefinedException.class)
    public void doesNotAllowBeforeAllBlockToBeReplaced() {
        executionPlanFor(BeforeAllBlockOverwrittenSpec.class);
    }

    @Test(expected = BlockAlreadyDefinedException.class)
    public void doesNotAllowItBlockToBeReplaced() {
        executionPlanFor(ItBlockOverwrittenSpec.class);
    }

    @Test(expected = BlockAlreadyDefinedException.class)
    public void doesNotAllowItBlockWithCollectorToBeReplaced() {
        executionPlanFor(ItBlockWithCollectorOverwrittenSpec.class);
    }

    @Test(expected = BlockAlreadyDefinedException.class)
    public void doesNotAllowXitBlockToBeReplaced() {
        executionPlanFor(XitBlockOverwrittenSpec.class);
    }

    @Test(expected = BlockAlreadyDefinedException.class)
    public void doesNotAllowXitBlockWithCollectorToBeReplaced() {
        executionPlanFor(XitBlockWithCollectorOverwrittenSpec.class);
    }

    @Test(expected = BlockAlreadyDefinedException.class)
    public void doesNotAllowFitBlockToBeReplaced() {
        executionPlanFor(FitBlockOverwrittenSpec.class);
    }

    @Test(expected = BlockAlreadyDefinedException.class)
    public void doesNotAllowFitBlockWithCollectorToBeReplaced() {
        executionPlanFor(FitBlockWithCollectorOverwrittenSpec.class);
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
