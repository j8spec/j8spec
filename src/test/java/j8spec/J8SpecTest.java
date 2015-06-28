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

    static class ItBlockOverwrittenSpec {{
        it("some text", () -> {
        });
        it("some text", () -> {
        });
    }}

    static class ItBlockWithCollectorOverwrittenSpec {{
        it("some text", c -> c, () -> {});
        it("some text", () -> {});
    }}

    static class XitBlockOverwrittenSpec {{
        xit("some text", () -> {
        });
        xit("some text", () -> {
        });
    }}

    static class XitBlockWithCollectorOverwrittenSpec {{
        xit("some text", c -> c, () -> {});
        xit("some text", () -> {});
    }}

    static class FitBlockOverwrittenSpec {{
        fit("some text", () -> {
        });
        fit("some text", () -> {
        });
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

            it("block", () -> {
            });
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
    public void buildsADescribeBlockUsingTheGivenSpecAsDescription() {
        DescribeBlock describeBlock = read(EmptySpec.class);

        assertThat(describeBlock.specClass(), equalTo(EmptySpec.class));
        assertThat(describeBlock.description(), is("j8spec.J8SpecTest$EmptySpec"));
    }

    @Test
    public void buildsADescribeBlockBasedOnAnEmptySpec() {
        DescribeBlock describeBlock = read(EmptySpec.class);

        assertThat(describeBlock.flattenItBlocks(), is(emptyList()));
    }

    @Test
    public void buildsADescribeBlockWhereAllInnerDescribeBlocksHaveTheSameSpecClass() {
        DescribeBlock rootDescribeBlock = read(SampleSpec.class);

        DescribeBlock describeA = rootDescribeBlock.describeBlocks().get(0);
        assertThat(describeA.specClass(), equalTo(SampleSpec.class));

        DescribeBlock describeAA = describeA.describeBlocks().get(0);
        assertThat(describeAA.specClass(), equalTo(SampleSpec.class));

        DescribeBlock describeB = rootDescribeBlock.describeBlocks().get(1);
        assertThat(describeB.specClass(), equalTo(SampleSpec.class));
    }

    @Test
    public void buildsADescribeBlockUsingTheDescriptionFromTheSpecDefinition() {
        DescribeBlock rootDescribeBlock = read(SampleSpec.class);

        DescribeBlock describeA = rootDescribeBlock.describeBlocks().get(0);
        assertThat(describeA.description(), is("describe A"));

        DescribeBlock describeAA = describeA.describeBlocks().get(0);
        assertThat(describeAA.description(), is("describe A.A"));

        DescribeBlock describeB = rootDescribeBlock.describeBlocks().get(1);
        assertThat(describeB.description(), is("describe B"));
    }

    @Test
    public void buildsADescribeBlockUsingItBlocksFromTheSpecDefinition() {
        DescribeBlock rootDescribeBlock = read(SampleSpec.class);

        assertThat(rootDescribeBlock.itBlock("block 1").body(), is(IT_BLOCK_1));
        assertThat(rootDescribeBlock.itBlock("block 2").body(), is(IT_BLOCK_2));
        assertThat(rootDescribeBlock.itBlock("block 3").body(), is(IT_BLOCK_3));

        DescribeBlock describeA = rootDescribeBlock.describeBlocks().get(0);
        assertThat(describeA.itBlock("block A.1").body(), is(IT_BLOCK_A1));
        assertThat(describeA.itBlock("block A.2").body(), is(IT_BLOCK_A2));

        DescribeBlock describeAA = describeA.describeBlocks().get(0);
        assertThat(describeAA.itBlock("block A.A.1").body(), is(IT_BLOCK_AA1));
        assertThat(describeAA.itBlock("block A.A.2").body(), is(IT_BLOCK_AA2));

        DescribeBlock describeB = rootDescribeBlock.describeBlocks().get(1);
        assertThat(describeB.itBlock("block B.1").body(), is(IT_BLOCK_B1));
        assertThat(describeB.itBlock("block B.2").body(), is(IT_BLOCK_B2));
    }

    @Test
    public void buildsADescribeBlockMarkingXdescribeBlocksFromTheSpecDefinitionAsIgnored() {
        DescribeBlock describeBlock = read(XdescribeSpec.class);

        assertThat(describeBlock.ignored(), is(false));
        assertThat(describeBlock.describeBlocks().get(0).ignored(), is(true));
    }

    @Test
    public void buildsADescribeBlockMarkingFdescribeBlocksFromTheSpecDefinitionAsFocused() {
        DescribeBlock describeBlock = read(FdescribeSpec.class);

        assertThat(describeBlock.focused(), is(false));
        assertThat(describeBlock.describeBlocks().get(0).focused(), is(true));
    }

    @Test
    public void buildsADescribeBlockMarkingXitBlocksFromTheSpecDefinitionAsIgnored() {
        DescribeBlock describeBlock = read(SampleSpec.class);

        assertThat(describeBlock.itBlock("block 3").ignored(), is(true));
    }

    @Test
    public void buildsADescribeBlockMarkingFitBlocksFromTheSpecDefinitionAsFocused() {
        DescribeBlock describeBlock = read(FitSpec.class);

        assertThat(describeBlock.itBlock("block 1").focused(), is(true));
    }

    @Test
    public void buildsADescribeBlockUsingExceptedExceptionsFromTheSpecDefinition() {
        DescribeBlock describeBlock = read(ExpectedExceptionSpec.class);

        assertThat(describeBlock.itBlock("block 1").expected(), is(equalTo(Exception.class)));
        assertThat(describeBlock.itBlock("block 2").expected(), is(equalTo(Exception.class)));
        assertThat(describeBlock.itBlock("block 3").expected(), is(equalTo(Exception.class)));
    }

    @Test(expected = SpecInitializationException.class)
    public void throwsExceptionWhenFailsToEvaluateSpec() {
        read(BadSpec.class);
    }

    @Test(expected = IllegalContextException.class)
    public void doesNotAllowDescribeMethodDirectInvocation() {
        J8Spec.describe("some text", () -> {
        });
    }

    @Test(expected = IllegalContextException.class)
    public void doesNotAllowXdescribeMethodDirectInvocation() {
        J8Spec.xdescribe("some text", () -> {
        });
    }

    @Test(expected = IllegalContextException.class)
    public void doesNotAllowFdescribeMethodDirectInvocation() {
        J8Spec.fdescribe("some text", () -> {
        });
    }

    @Test(expected = IllegalContextException.class)
    public void doesNotAllowItMethodDirectInvocation() {
        J8Spec.it("some text", () -> {});
    }

    @Test(expected = IllegalContextException.class)
    public void doesNotAllowItMethodDirectInvocationWithCollector() {
        J8Spec.it("some text", c -> c, () -> {
        });
    }

    @Test(expected = IllegalContextException.class)
    public void doesNotAllowXitMethodDirectInvocation() {
        J8Spec.xit("some text", () -> {});
    }

    @Test(expected = IllegalContextException.class)
    public void doesNotAllowXitMethodDirectInvocationWithCollector() {
        J8Spec.xit("some text", c -> c, () -> {
        });
    }

    @Test(expected = IllegalContextException.class)
    public void doesNotAllowFitMethodDirectInvocation() {
        J8Spec.fit("some text", () -> {});
    }

    @Test(expected = IllegalContextException.class)
    public void doesNotAllowFitMethodWithCollectorDirectInvocation() {
        J8Spec.fit("some text", c -> c, () -> {
        });
    }

    @Test(expected = BlockAlreadyDefinedException.class)
    public void doesNotAllowItBlockToBeReplaced() {
        read(ItBlockOverwrittenSpec.class);
    }

    @Test(expected = BlockAlreadyDefinedException.class)
    public void doesNotAllowItBlockWithCollectorToBeReplaced() {
        read(ItBlockWithCollectorOverwrittenSpec.class);
    }

    @Test(expected = BlockAlreadyDefinedException.class)
    public void doesNotAllowXitBlockToBeReplaced() {
        read(XitBlockOverwrittenSpec.class);
    }

    @Test(expected = BlockAlreadyDefinedException.class)
    public void doesNotAllowXitBlockWithCollectorToBeReplaced() {
        read(XitBlockWithCollectorOverwrittenSpec.class);
    }

    @Test(expected = BlockAlreadyDefinedException.class)
    public void doesNotAllowFitBlockToBeReplaced() {
        read(FitBlockOverwrittenSpec.class);
    }

    @Test(expected = BlockAlreadyDefinedException.class)
    public void doesNotAllowFitBlockWithCollectorToBeReplaced() {
        read(FitBlockWithCollectorOverwrittenSpec.class);
    }

    @Test(expected = IllegalContextException.class)
    public void forgetsLastSpec() {
        read(SampleSpec.class);
        J8Spec.describe("some text", () -> {});
    }

    @Test(expected = IllegalContextException.class)
    public void forgetsLastSpecAfterTheLastSpecEvaluationFails() {
        try {
            read(ItBlockOverwrittenSpec.class);
        } catch (BlockAlreadyDefinedException e) {
        }

        J8Spec.it("some text", () -> {});
    }

    @Test()
    public void allowsMultipleThreadsToBuildDescribeBlocks() throws InterruptedException {
        final Var<DescribeBlock> sleepDescribe = var();

        Thread anotherDescribeBlockThread = new Thread(() -> {
            var(sleepDescribe, read(ThreadThatSleeps2sSpec.class));
        });
        anotherDescribeBlockThread.start();

        Thread.sleep(1000);

        DescribeBlock emptyDescribeBlock = read(EmptySpec.class);

        anotherDescribeBlockThread.join();

        assertThat(emptyDescribeBlock.flattenItBlocks(), is(Collections.<ItBlock>emptyList()));

        assertThat(var(sleepDescribe).flattenItBlocks().size(), is(1));
        assertThat(var(sleepDescribe).flattenItBlocks().get(0).description(), is("block"));
    }
}
