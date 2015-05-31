package j8spec.junit;

import j8spec.ItBlock;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

import java.util.List;

import static j8spec.J8Spec.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class J8SpecRunnerTest {

    public static class SampleSpec {{
        beforeEach(() -> {});

        it("block 1", mock(Runnable.class));
        it("block 2", () -> {});

        describe("describe A", () -> {
            beforeEach(() -> {});

            it("block A.1", () -> {});
            it("block A.2", () -> {});

            describe("describe A.A", () -> {
                beforeEach(() -> {});

                it("block A.A.1", () -> {});
                it("block A.A.2", () -> {});
            });
        });

        describe("describe B", () -> {
            beforeEach(() -> {
            });

            it("block B.1", () -> {
            });
            it("block B.2", () -> {
            });
        });
    }}

    @Test
    public void buildsListOfChildDescriptions() throws InitializationError {
        J8SpecRunner runner = new J8SpecRunner(SampleSpec.class);

        List<ItBlock> itBlocks = runner.getChildren();

        assertThat(itBlocks.get(0).getDescription(), is("block 1"));
        assertThat(itBlocks.get(1).getDescription(), is("block 2"));
    }

    @Test
    public void describesEachChild() throws InitializationError {
        J8SpecRunner runner = new J8SpecRunner(SampleSpec.class);
        List<ItBlock> itBlocks = runner.getChildren();

        Description block1Description = runner.describeChild(itBlocks.get(0));

        assertThat(block1Description.getClassName(), is("j8spec.junit.J8SpecRunnerTest$SampleSpec"));
        assertThat(block1Description.getMethodName(), is("block 1"));

        Description block2Description = runner.describeChild(itBlocks.get(1));

        assertThat(block2Description.getClassName(), is("j8spec.junit.J8SpecRunnerTest$SampleSpec"));
        assertThat(block2Description.getMethodName(), is("block 2"));

        Description blockA1Description = runner.describeChild(itBlocks.get(2));

        assertThat(blockA1Description.getClassName(), is("j8spec.junit.J8SpecRunnerTest$SampleSpec"));
        assertThat(blockA1Description.getMethodName(), is("block A.1, describe A"));
    }

    @Test
    public void notifiesThatEachChildStarted() throws InitializationError {
        J8SpecRunner runner = new J8SpecRunner(SampleSpec.class);
        List<ItBlock> itBlocks = runner.getChildren();

        RunNotifier runNotifier = mock(RunNotifier.class);
        Description description = runner.describeChild(itBlocks.get(0));

        runner.runChild(itBlocks.get(0), runNotifier);

        verify(runNotifier).fireTestStarted(eq(description));
    }

    @Test
    public void runsEachChild() throws InitializationError {
        J8SpecRunner runner = new J8SpecRunner(SampleSpec.class);
        List<ItBlock> itBlocks = runner.getChildren();

        RunNotifier runNotifier = mock(RunNotifier.class);

        runner.runChild(itBlocks.get(0), runNotifier);

        verify(itBlocks.get(0).getBody()).run();
    }

    @Test
    public void notifiesThatEachChildFinishedWhenItFails() throws InitializationError {
        J8SpecRunner runner = new J8SpecRunner(SampleSpec.class);
        List<ItBlock> itBlocks = runner.getChildren();

        RunNotifier runNotifier = new RunNotifier();
        RunListenerHelper listener = new RunListenerHelper();
        runNotifier.addListener(listener);

        RuntimeException runtimeException = new RuntimeException();
        doThrow(runtimeException).when(itBlocks.get(0).getBody()).run();

        runner.runChild(itBlocks.get(0), runNotifier);

        assertThat(listener.getDescription(), is(runner.describeChild(itBlocks.get(0))));
        assertThat(listener.getExpection(), is(runtimeException));
    }

    @Test
    public void notifiesThatEachChildItFinishes() throws InitializationError {
        J8SpecRunner runner = new J8SpecRunner(SampleSpec.class);
        List<ItBlock> itBlocks = runner.getChildren();

        RunNotifier runNotifier = mock(RunNotifier.class);
        Description description = runner.describeChild(itBlocks.get(0));

        runner.runChild(itBlocks.get(0), runNotifier);

        verify(runNotifier).fireTestFinished(eq(description));
    }
}
