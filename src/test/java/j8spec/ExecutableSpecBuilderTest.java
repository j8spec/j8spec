package j8spec;

import org.junit.Test;

import java.util.List;

import static j8spec.BlockExecutionFlag.DEFAULT;
import static j8spec.BlockExecutionFlag.FOCUSED;
import static j8spec.BlockExecutionFlag.IGNORED;
import static j8spec.BlockExecutionOrder.DEFINED;
import static j8spec.BlockExecutionStrategy.BLACK_LIST;
import static j8spec.BlockExecutionStrategy.WHITE_LIST;
import static j8spec.UnsafeBlock.NOOP;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ExecutableSpecBuilderTest {

    @Test
    public void builds_examples_with_given_description() {
        ExecutableSpecBuilder builder = new ExecutableSpecBuilder(BLACK_LIST);
        builder
            .startGroup("SampleSpec", DEFAULT, DEFINED)
                .example("block 1", NOOP, DEFAULT, null)
                .example("block 2", NOOP, DEFAULT, null)
                .startGroup("describe A", DEFAULT, DEFINED)
                    .example("block A1", NOOP, DEFAULT, null)
                    .example("block A2", NOOP, DEFAULT, null)
                .endGroup()
            .endGroup();
        List<ItBlock> examples = builder.build();

        assertThat(examples.get(0).description(), is("block 1"));
        assertThat(examples.get(1).description(), is("block 2"));
        assertThat(examples.get(2).description(), is("block A1"));
        assertThat(examples.get(3).description(), is("block A2"));
    }

    @Test
    public void builds_examples_with_given_container_descriptions() {
        ExecutableSpecBuilder builder = new ExecutableSpecBuilder(BLACK_LIST);
        builder
            .startGroup("SampleSpec", DEFAULT, DEFINED)
                .example("block 1", NOOP, DEFAULT, null)
                .example("block 2", NOOP, DEFAULT, null)
                .startGroup("describe A", DEFAULT, DEFINED)
                    .example("block A1", NOOP, DEFAULT, null)
                    .example("block A2", NOOP, DEFAULT, null)
                .endGroup()
            .endGroup();
        List<ItBlock> examples = builder.build();

        assertThat(examples.get(0).containerDescriptions(), is(singletonList("SampleSpec")));
        assertThat(examples.get(1).containerDescriptions(), is(singletonList("SampleSpec")));
        assertThat(examples.get(2).containerDescriptions(), is(asList("SampleSpec", "describe A")));
        assertThat(examples.get(3).containerDescriptions(), is(asList("SampleSpec", "describe A")));
    }

    @Test
    public void builds_before_all_hooks_to_execute_just_once() throws Throwable {
        UnsafeBlock beforeAll = mock(UnsafeBlock.class);
        UnsafeBlock innerBeforeAll = mock(UnsafeBlock.class);

        execute(
            new ExecutableSpecBuilder(BLACK_LIST)
                .startGroup("SampleSpec", DEFAULT, DEFINED)
                    .beforeAll(beforeAll)
                    .example("block 1", NOOP, DEFAULT, null)
                    .example("block 2", NOOP, DEFAULT, null)
                    .startGroup("describe A", DEFAULT, DEFINED)
                        .beforeAll(innerBeforeAll)
                        .example("block A 1", NOOP, DEFAULT, null)
                    .endGroup()
                .endGroup()
        );

        verify(beforeAll, times(1)).tryToExecute();
        verify(innerBeforeAll, times(1)).tryToExecute();
    }

    @Test
    public void builds_before_each_hooks_to_execute_after_each_example() throws Throwable {
        UnsafeBlock beforeEach = mock(UnsafeBlock.class);
        UnsafeBlock innerBeforeEach = mock(UnsafeBlock.class);

        execute(
            new ExecutableSpecBuilder(BLACK_LIST)
                .startGroup("SampleSpec", DEFAULT, DEFINED)
                    .beforeEach(beforeEach)
                    .example("block 1", NOOP, DEFAULT, null)
                    .example("block 2", NOOP, DEFAULT, null)
                    .startGroup("describe A", DEFAULT, DEFINED)
                        .beforeEach(innerBeforeEach)
                        .example("block A 1", NOOP, DEFAULT, null)
                    .endGroup()
                .endGroup()
        );

        verify(beforeEach, times(3)).tryToExecute();
        verify(innerBeforeEach, times(1)).tryToExecute();
    }

    @Test
    public void builds_examples_marked_to_be_ignored() throws Throwable {
        UnsafeBlock ignored = mock(UnsafeBlock.class);

        execute(
            new ExecutableSpecBuilder(BLACK_LIST)
                .startGroup("SampleSpec", DEFAULT, DEFINED)
                    .example("ignored block", ignored, IGNORED, null)
                .endGroup()
        );

        verify(ignored, never()).tryToExecute();
    }

    @Test
    public void builds_examples_marked_to_be_ignored_when_the_group_is_ignored() throws Throwable {
        UnsafeBlock focused = mock(UnsafeBlock.class);
        UnsafeBlock ignored = mock(UnsafeBlock.class);

        execute(
            new ExecutableSpecBuilder(BLACK_LIST)
                .startGroup("SampleSpec", DEFAULT, DEFINED)
                    .example("block 1", focused, DEFAULT, null)
                    .startGroup("describe A", IGNORED, DEFINED)
                        .example("block A1", ignored, DEFAULT, null)
                        .example("block A2", ignored, DEFAULT, null)
                        .startGroup("describe AB", DEFAULT, DEFINED)
                            .example("block AB1", ignored, DEFAULT, null)
                        .endGroup()
                    .endGroup()
                .endGroup()
        );

        verify(focused, times(1)).tryToExecute();
        verify(ignored, never()).tryToExecute();
    }

    @Test
    public void builds_examples_marked_to_be_ignored_when_there_is_examples_focused() throws Throwable {
        UnsafeBlock focused = mock(UnsafeBlock.class);
        UnsafeBlock ignored = mock(UnsafeBlock.class);

        execute(
            new ExecutableSpecBuilder(WHITE_LIST)
                .startGroup("SampleSpec", DEFAULT, DEFINED)
                    .example("block 1", ignored, DEFAULT, null)
                    .example("block 2", ignored, DEFAULT, null)

                    .startGroup("describe A", IGNORED, DEFINED)
                        .example("block A1", focused, FOCUSED, null)
                        .example("block A2", ignored, DEFAULT, null)
                    .endGroup()
                .endGroup()
        );

        verify(focused, times(1)).tryToExecute();
        verify(ignored, never()).tryToExecute();
    }

    @Test
    public void builds_examples_marked_to_be_ignored_when_there_are_focused_groups() throws Throwable {
        UnsafeBlock focused = mock(UnsafeBlock.class);
        UnsafeBlock ignored = mock(UnsafeBlock.class);

        execute(
            new ExecutableSpecBuilder(WHITE_LIST)
                .startGroup("SampleSpec", DEFAULT, DEFINED)
                    .example("block 1", ignored, DEFAULT, null)
                    .startGroup("describe A", FOCUSED, DEFINED)
                        .example("block A1", focused, DEFAULT, null)
                        .example("block A2", focused, DEFAULT, null)
                        .startGroup("describe A A", DEFAULT, DEFINED)
                            .example("block A1", focused, DEFAULT, null)
                            .example("block A2", focused, DEFAULT, null)
                        .endGroup()
                    .endGroup()
                .endGroup()
        );

        verify(focused, times(4)).tryToExecute();
        verify(ignored, never()).tryToExecute();
    }

    @Test
    public void builds_examples_with_excepted_exception() {
        ExecutableSpecBuilder builder = new ExecutableSpecBuilder(BLACK_LIST);
        builder
            .startGroup("SampleSpec", DEFAULT, DEFINED)
                .example("block 1", NOOP, DEFAULT, Exception.class)
            .endGroup();
        List<ItBlock> examples = builder.build();

        assertThat(examples.get(0).expected(), is(equalTo(Exception.class)));
    }

    private void execute(BlockDefinitionVisitor visitor) throws Throwable {
        ExecutableSpecBuilder builder = (ExecutableSpecBuilder) visitor;

        for (ItBlock block : builder.build()) {
            block.tryToExecute();
        }
    }
}