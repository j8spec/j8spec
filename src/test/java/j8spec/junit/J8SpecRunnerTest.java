package j8spec.junit;

import j8spec.ItBlock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static j8spec.J8Spec.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class J8SpecRunnerTest {

    private static final String BLOCK_1 = "block 1";
    private static final String BLOCK_2 = "block 2";

    public static class SampleSpec {{
        it(BLOCK_1, newBlock(BLOCK_1));
        it(BLOCK_2, () -> {});

        describe("describe A", () -> {
            it("block A.1", () -> {});
        });
    }}

    private static Map<String, Runnable> blocks;

    private static Runnable newBlock(String id) {
        Runnable block = mock(Runnable.class);
        blocks.put(id, block);
        return block;
    }

    private static Runnable block(String id) {
        return blocks.get(id);
    }

    @Before
    public void resetBlocks() {
        blocks = new HashMap<>();
    }

    @Test
    public void buildsListOfChildDescriptions() throws InitializationError {
        J8SpecRunner runner = new J8SpecRunner(SampleSpec.class);

        List<ItBlock> itBlocks = runner.getChildren();

        assertThat(itBlocks.get(0).description(), is(BLOCK_1));
        assertThat(itBlocks.get(1).description(), is(BLOCK_2));
    }

    @Test
    public void describesEachChild() throws InitializationError {
        J8SpecRunner runner = new J8SpecRunner(SampleSpec.class);
        List<ItBlock> itBlocks = runner.getChildren();

        Description block1Description = runner.describeChild(itBlocks.get(0));

        assertThat(block1Description.getClassName(), is("j8spec.junit.J8SpecRunnerTest$SampleSpec"));
        assertThat(block1Description.getMethodName(), is(BLOCK_1));

        Description block2Description = runner.describeChild(itBlocks.get(1));

        assertThat(block2Description.getClassName(), is("j8spec.junit.J8SpecRunnerTest$SampleSpec"));
        assertThat(block2Description.getMethodName(), is(BLOCK_2));

        Description blockA1Description = runner.describeChild(itBlocks.get(2));

        assertThat(blockA1Description.getClassName(), is("j8spec.junit.J8SpecRunnerTest$SampleSpec"));
        assertThat(blockA1Description.getMethodName(), is("block A.1, describe A"));
    }

    @Test
    public void notifiesWhenAChildStarts() throws InitializationError {
        J8SpecRunner runner = new J8SpecRunner(SampleSpec.class);
        List<ItBlock> itBlocks = runner.getChildren();

        RunNotifier runNotifier = mock(RunNotifier.class);
        Description description = runner.describeChild(itBlocks.get(0));

        runner.runChild(itBlocks.get(0), runNotifier);

        verify(runNotifier).fireTestStarted(eq(description));
    }

    @Test
    public void runsTheGivenItBlock() throws InitializationError {
        J8SpecRunner runner = new J8SpecRunner(SampleSpec.class);
        List<ItBlock> itBlocks = runner.getChildren();

        runner.runChild(itBlocks.get(0), mock(RunNotifier.class));

        verify(block(BLOCK_1)).run();
    }

    @Test
    public void notifiesWhenAChildFails() throws InitializationError {
        J8SpecRunner runner = new J8SpecRunner(SampleSpec.class);
        List<ItBlock> itBlocks = runner.getChildren();

        RunNotifier runNotifier = new RunNotifier();
        RunListenerHelper listener = new RunListenerHelper();
        runNotifier.addListener(listener);

        RuntimeException runtimeException = new RuntimeException();
        doThrow(runtimeException).when(block(BLOCK_1)).run();

        runner.runChild(itBlocks.get(0), runNotifier);

        assertThat(listener.getDescription(), is(runner.describeChild(itBlocks.get(0))));
        assertThat(listener.getException(), is(runtimeException));
    }

    @Test
    public void notifiesWhenAChildFinishes() throws InitializationError {
        J8SpecRunner runner = new J8SpecRunner(SampleSpec.class);
        List<ItBlock> itBlocks = runner.getChildren();

        RunNotifier runNotifier = mock(RunNotifier.class);
        Description description = runner.describeChild(itBlocks.get(0));

        runner.runChild(itBlocks.get(0), runNotifier);

        verify(runNotifier).fireTestFinished(eq(description));
    }

    @Test
    public void notifiesWhenAChildFinishesEvenWhenItFails() throws InitializationError {
        J8SpecRunner runner = new J8SpecRunner(SampleSpec.class);
        List<ItBlock> itBlocks = runner.getChildren();
        doThrow(new RuntimeException()).when(block(BLOCK_1)).run();
        RunNotifier runNotifier = mock(RunNotifier.class);

        runner.runChild(itBlocks.get(0), runNotifier);

        verify(runNotifier).fireTestFinished(eq(runner.describeChild(itBlocks.get(0))));
    }
}
