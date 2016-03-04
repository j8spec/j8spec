package j8spec;

import org.junit.Test;

import java.util.List;

import static j8spec.BlockExecutionFlag.FOCUSED;
import static j8spec.BlockExecutionFlag.IGNORED;
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
            .startGroup(groupConfig().description("SampleSpec").definedOrder().build())
                .example(exampleConfig().description("block 1").build(), NOOP)
                .example(exampleConfig().description("block 2").build(), NOOP)
                .startGroup(groupConfig().description("describe A").build())
                    .example(exampleConfig().description("block A1").build(), NOOP)
                    .example(exampleConfig().description("block A2").build(), NOOP)
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
            .startGroup(groupConfig().description("SampleSpec").definedOrder().build())
                .example(exampleConfig().description("block 1").build(), NOOP)
                .example(exampleConfig().description("block 2").build(), NOOP)
                .startGroup(groupConfig().description("describe A").build())
                    .example(exampleConfig().description("block A1").build(), NOOP)
                    .example(exampleConfig().description("block A1").build(), NOOP)
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
                .startGroup(groupConfig().description("SampleSpec").definedOrder().build())
                    .beforeAll(beforeAll)
                    .example(exampleConfig().description("block 1").build(), NOOP)
                    .example(exampleConfig().description("block 2").build(), NOOP)
                    .startGroup(groupConfig().description("describe A").build())
                        .beforeAll(innerBeforeAll)
                        .example(exampleConfig().description("block A 1").build(), NOOP)
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
                .startGroup(groupConfig().description("SampleSpec").definedOrder().build())
                    .beforeEach(beforeEach)
                    .example(exampleConfig().description("block 1").build(), NOOP)
                    .example(exampleConfig().description("block 2").build(), NOOP)
                    .startGroup(groupConfig().description("describe A").build())
                        .beforeEach(innerBeforeEach)
                        .example(exampleConfig().description("block A 1").build(), NOOP)
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
                .startGroup(groupConfig().description("SampleSpec").definedOrder().build())
                    .example(exampleConfig().description("ignored block").executionFlag(IGNORED).build(), ignored)
                .endGroup()
        );

        verify(ignored, never()).tryToExecute();
    }

    @Test
    public void builds_examples_marked_to_be_ignored_when_the_group_is_ignored() throws Throwable {
        UnsafeBlock executed = mock(UnsafeBlock.class);
        UnsafeBlock ignored = mock(UnsafeBlock.class);

        execute(
            new ExecutableSpecBuilder(BLACK_LIST)
                .startGroup(groupConfig().description("SampleSpec").definedOrder().build())
                    .example(exampleConfig().description("block 1").build(), executed)
                    .startGroup(groupConfig().description("describe A").executionFlag(IGNORED).build())
                        .example(exampleConfig().description("block A1").build(), ignored)
                        .example(exampleConfig().description("block A2").build(), ignored)
                        .startGroup(groupConfig().description("describe AB").build())
                            .example(exampleConfig().description("block AB1").build(), ignored)
                        .endGroup()
                    .endGroup()
                .endGroup()
        );

        verify(executed, times(1)).tryToExecute();
        verify(ignored, never()).tryToExecute();
    }

    @Test
    public void builds_examples_marked_to_be_ignored_when_there_is_examples_focused() throws Throwable {
        UnsafeBlock executed = mock(UnsafeBlock.class);
        UnsafeBlock ignored = mock(UnsafeBlock.class);

        execute(
            new ExecutableSpecBuilder(WHITE_LIST)
                .startGroup(groupConfig().description("SampleSpec").definedOrder().build())
                    .example(exampleConfig().description("block 1").build(), ignored)
                    .example(exampleConfig().description("block 2").build(), ignored)
                    .startGroup(groupConfig().description("describe A").build())
                        .example(exampleConfig().description("block A1").executionFlag(FOCUSED).build(), executed)
                        .example(exampleConfig().description("block A2").build(), executed)
                    .endGroup()
                .endGroup()
        );

        verify(executed, times(1)).tryToExecute();
        verify(ignored, never()).tryToExecute();
    }

    @Test
    public void builds_examples_marked_to_be_ignored_when_there_are_focused_groups() throws Throwable {
        UnsafeBlock executed = mock(UnsafeBlock.class);
        UnsafeBlock ignored = mock(UnsafeBlock.class);

        execute(
            new ExecutableSpecBuilder(WHITE_LIST)
                .startGroup(groupConfig().description("SampleSpec").definedOrder().build())
                    .example(exampleConfig().description("block 1").build(), ignored)
                    .startGroup(groupConfig().description("describe A").executionFlag(FOCUSED).build())
                        .example(exampleConfig().description("block A1").build(), executed)
                        .example(exampleConfig().description("block A2").build(), executed)
                        .startGroup(groupConfig().description("describe A A").build())
                            .example(exampleConfig().description("block A A 1").build(), executed)
                            .example(exampleConfig().description("block A A 2").build(), executed)
                        .endGroup()
                    .endGroup()
                .endGroup()
        );

        verify(executed, times(4)).tryToExecute();
        verify(ignored, never()).tryToExecute();
    }

    @Test
    public void builds_examples_with_excepted_exception() {
        ExecutableSpecBuilder builder = new ExecutableSpecBuilder(BLACK_LIST);
        builder
            .startGroup(groupConfig().description("SampleSpec").definedOrder().build())
                .example(exampleConfig().description("block 1").expected(Exception.class).build(), NOOP)
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

    private ExampleGroupConfiguration.Builder groupConfig() {
        return new ExampleGroupConfiguration.Builder();
    }

    private ExampleConfiguration.Builder exampleConfig() {
        return new ExampleConfiguration.Builder();
    }
}