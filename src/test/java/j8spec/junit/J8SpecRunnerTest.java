package j8spec.junit;

import j8spec.ItBlock;
import j8spec.UnsafeBlock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static j8spec.J8Spec.*;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class J8SpecRunnerTest {

    private static final String BLOCK_1 = "block 1";
    private static final String BLOCK_2 = "block 2";
    private static final String BLOCK_3 = "block 3";
    private static final String BLOCK_4 = "block 4";

    public static class CustomException extends Exception {}

    public static class SampleSpec {{
        it(BLOCK_1, newBlock(BLOCK_1));
        it(BLOCK_2, () -> {});
        xit(BLOCK_3, newBlock(BLOCK_3));
        it(BLOCK_4, c -> c.expected(CustomException.class), newBlock(BLOCK_4));

        describe("describe A", () -> it("block A.1", () -> {}));
    }}

    private static Map<String, UnsafeBlock> blocks;

    private static UnsafeBlock newBlock(String id) {
        UnsafeBlock block = mock(UnsafeBlock.class);
        blocks.put(id, block);
        return block;
    }

    private static UnsafeBlock block(String id) {
        return blocks.get(id);
    }

    @Before
    public void resetBlocks() {
        blocks = new HashMap<>();
    }

    @Test
    public void builds_list_of_child_descriptions() throws InitializationError {
        J8SpecRunner runner = new J8SpecRunner(SampleSpec.class);

        List<ItBlock> itBlocks = runner.getChildren();

        assertThat(itBlocks.get(0).description(), is(BLOCK_1));
        assertThat(itBlocks.get(1).description(), is(BLOCK_2));
    }

    @Test
    public void describes_each_child() throws InitializationError {
        J8SpecRunner runner = new J8SpecRunner(SampleSpec.class);
        List<ItBlock> itBlocks = runner.getChildren();

        Description block1Description = runner.describeChild(itBlocks.get(0));

        assertThat(block1Description.getClassName(), is("j8spec.junit.J8SpecRunnerTest$SampleSpec"));
        assertThat(block1Description.getMethodName(), is(BLOCK_1));

        Description block2Description = runner.describeChild(itBlocks.get(1));

        assertThat(block2Description.getClassName(), is("j8spec.junit.J8SpecRunnerTest$SampleSpec"));
        assertThat(block2Description.getMethodName(), is(BLOCK_2));

        Description block3Description = runner.describeChild(itBlocks.get(2));

        assertThat(block3Description.getClassName(), is("j8spec.junit.J8SpecRunnerTest$SampleSpec"));
        assertThat(block3Description.getMethodName(), is(BLOCK_3));

        Description block4Description = runner.describeChild(itBlocks.get(3));

        assertThat(block4Description.getClassName(), is("j8spec.junit.J8SpecRunnerTest$SampleSpec"));
        assertThat(block4Description.getMethodName(), is(BLOCK_4));

        Description blockA1Description = runner.describeChild(itBlocks.get(4));

        assertThat(blockA1Description.getClassName(), is("j8spec.junit.J8SpecRunnerTest$SampleSpec"));
        assertThat(blockA1Description.getMethodName(), is("block A.1, describe A"));
    }

    @Test
    public void notifies_when_a_child_starts() throws InitializationError {
        J8SpecRunner runner = new J8SpecRunner(SampleSpec.class);
        List<ItBlock> itBlocks = runner.getChildren();

        RunNotifier runNotifier = mock(RunNotifier.class);
        Description description = runner.describeChild(itBlocks.get(0));

        runner.runChild(itBlocks.get(0), runNotifier);

        verify(runNotifier).fireTestStarted(eq(description));
    }

    @Test
    public void runs_the_given_it_block() throws Throwable {
        J8SpecRunner runner = new J8SpecRunner(SampleSpec.class);
        List<ItBlock> itBlocks = runner.getChildren();

        runner.runChild(itBlocks.get(0), mock(RunNotifier.class));

        verify(block(BLOCK_1)).tryToExecute();
    }

    @Test
    public void notifies_when_a_child_fails() throws Throwable {
        J8SpecRunner runner = new J8SpecRunner(SampleSpec.class);
        List<ItBlock> itBlocks = runner.getChildren();

        RunNotifier runNotifier = new RunNotifier();
        RunListenerHelper listener = new RunListenerHelper();
        runNotifier.addListener(listener);

        RuntimeException runtimeException = new RuntimeException();
        doThrow(runtimeException).when(block(BLOCK_1)).tryToExecute();

        runner.runChild(itBlocks.get(0), runNotifier);

        assertThat(listener.getDescription(), is(runner.describeChild(itBlocks.get(0))));
        assertThat(listener.getException(), is(runtimeException));
    }

    @Test
    public void notifies_when_a_child_is_ignored() throws Throwable {
        J8SpecRunner runner = new J8SpecRunner(SampleSpec.class);
        List<ItBlock> itBlocks = runner.getChildren();

        RunNotifier runNotifier = new RunNotifier();
        RunListenerHelper listener = new RunListenerHelper();
        runNotifier.addListener(listener);

        runner.runChild(itBlocks.get(2), runNotifier);

        assertThat(listener.getDescription(), is(runner.describeChild(itBlocks.get(2))));
        assertThat(listener.isIgnored(), is(true));

        verify(block(BLOCK_3), never()).tryToExecute();
    }

    @Test
    public void notifies_only_test_ignored_event_when_a_child_is_ignored() throws InitializationError {
        J8SpecRunner runner = new J8SpecRunner(SampleSpec.class);
        List<ItBlock> itBlocks = runner.getChildren();

        RunNotifier runNotifier = mock(RunNotifier.class);

        runner.runChild(itBlocks.get(2), runNotifier);

        verify(runNotifier, never()).fireTestStarted(any());
        verify(runNotifier, never()).fireTestFinished(any());
    }

    @Test
    public void notifies_when_a_child_finishes() throws InitializationError {
        J8SpecRunner runner = new J8SpecRunner(SampleSpec.class);
        List<ItBlock> itBlocks = runner.getChildren();

        RunNotifier runNotifier = mock(RunNotifier.class);
        Description description = runner.describeChild(itBlocks.get(0));

        runner.runChild(itBlocks.get(0), runNotifier);

        verify(runNotifier).fireTestFinished(eq(description));
    }

    @Test
    public void notifies_when_a_child_finishes_even_when_it_fails() throws Throwable {
        J8SpecRunner runner = new J8SpecRunner(SampleSpec.class);
        List<ItBlock> itBlocks = runner.getChildren();
        doThrow(new RuntimeException()).when(block(BLOCK_1)).tryToExecute();
        RunNotifier runNotifier = mock(RunNotifier.class);

        runner.runChild(itBlocks.get(0), runNotifier);

        verify(runNotifier).fireTestFinished(eq(runner.describeChild(itBlocks.get(0))));
    }

    @Test
    public void notifies_success_when_expected_exception_occurs() throws Throwable {
        J8SpecRunner runner = new J8SpecRunner(SampleSpec.class);
        List<ItBlock> itBlocks = runner.getChildren();

        RunNotifier runNotifier = mock(RunNotifier.class);
        doThrow(new CustomException()).when(block(BLOCK_4)).tryToExecute();

        runner.runChild(itBlocks.get(3), runNotifier);

        verify(runNotifier, never()).fireTestFailure(any());
    }

    @Test
    public void notifies_failure_when_no_exception_occurs() throws InitializationError {
        J8SpecRunner runner = new J8SpecRunner(SampleSpec.class);
        List<ItBlock> itBlocks = runner.getChildren();

        RunNotifier runNotifier = new RunNotifier();
        RunListenerHelper listener = new RunListenerHelper();
        runNotifier.addListener(listener);

        runner.runChild(itBlocks.get(3), runNotifier);

        assertThat(listener.getDescription(), is(runner.describeChild(itBlocks.get(3))));
        assertThat(listener.getException(), instanceOf(AssertionError.class));
    }

    @Test
    public void notifies_failure_when_different_exception_occurs() throws Throwable {
        J8SpecRunner runner = new J8SpecRunner(SampleSpec.class);
        List<ItBlock> itBlocks = runner.getChildren();

        doThrow(new RuntimeException()).when(block(BLOCK_4)).tryToExecute();

        RunNotifier runNotifier = new RunNotifier();
        RunListenerHelper listener = new RunListenerHelper();
        runNotifier.addListener(listener);

        runner.runChild(itBlocks.get(3), runNotifier);

        assertThat(listener.getDescription(), is(runner.describeChild(itBlocks.get(3))));
        assertThat(listener.getException(), instanceOf(Exception.class));
    }
}
