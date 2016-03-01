package j8spec;

import org.junit.Test;

import java.util.List;

import static j8spec.BlockExecutionFlag.DEFAULT;
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

    private static final UnsafeBlock BEFORE_ALL_BLOCK = () -> {};
    private static final UnsafeBlock BEFORE_EACH_BLOCK = () -> {};
    private static final UnsafeBlock BLOCK_1 = () -> {};
    private static final UnsafeBlock BLOCK_2 = () -> {};
    private static final UnsafeBlock BEFORE_ALL_BLOCK_A = () -> {};
    private static final UnsafeBlock BEFORE_EACH_BLOCK_A = () -> {};
    private static final UnsafeBlock BLOCK_A_1 = () -> {};
    private static final UnsafeBlock BLOCK_A_2 = () -> {};
    private static final UnsafeBlock BLOCK_A_A_1 = () -> {};
    private static final UnsafeBlock BLOCK_A_A_2 = () -> {};
    private static final UnsafeBlock BLOCK_A_B_1 = () -> {};

    static class SampleSpec {}

    @Test
    public void builds_it_blocks_with_given_description() {
        List<ItBlock> itBlocks = aSpecWithInnerDescribeBlocks();

        assertThat(itBlocks.get(0).description(), is("block 1"));
        assertThat(itBlocks.get(1).description(), is("block 2"));
        assertThat(itBlocks.get(2).description(), is("block A1"));
        assertThat(itBlocks.get(3).description(), is("block A2"));
    }

    @Test
    public void builds_it_blocks_with_given_container_descriptions() {
        List<ItBlock> itBlocks = aSpecWithInnerDescribeBlocks();

        assertThat(itBlocks.get(0).containerDescriptions(), is(singletonList("j8spec.ExecutableSpecBuilderTest$SampleSpec")));
        assertThat(itBlocks.get(1).containerDescriptions(), is(singletonList("j8spec.ExecutableSpecBuilderTest$SampleSpec")));
        assertThat(itBlocks.get(2).containerDescriptions(), is(asList("j8spec.ExecutableSpecBuilderTest$SampleSpec", "describe A")));
        assertThat(itBlocks.get(3).containerDescriptions(), is(asList("j8spec.ExecutableSpecBuilderTest$SampleSpec", "describe A")));
    }

    @Test
    public void builds_before_all_hooks_to_execute_just_once() throws Throwable {
        UnsafeBlock beforeAll = mock(UnsafeBlock.class);
        UnsafeBlock innerBeforeAll = mock(UnsafeBlock.class);

        execute(
            new ExecutableSpecBuilder(BLACK_LIST)
                .describe("SampleSpec", DEFAULT)
                    .beforeAll(beforeAll)
                    .it("block 1", NOOP, DEFAULT, null)
                    .it("block 2", NOOP, DEFAULT, null)
                    .describe("describe A", DEFAULT)
                        .beforeAll(innerBeforeAll)
                        .it("block A 1", NOOP, DEFAULT, null)
                    .describe()
                .describe()
        );

        verify(beforeAll, times(1)).tryToExecute();
        verify(innerBeforeAll, times(1)).tryToExecute();
    }

    @Test
    public void builds_before_each_hooks_to_execute_after_each_it_block() throws Throwable {
        UnsafeBlock beforeEach = mock(UnsafeBlock.class);
        UnsafeBlock innerBeforeEach = mock(UnsafeBlock.class);

        execute(
            new ExecutableSpecBuilder(BLACK_LIST)
                .describe("SampleSpec", DEFAULT)
                    .beforeEach(beforeEach)
                    .it("block 1", NOOP, DEFAULT, null)
                    .it("block 2", NOOP, DEFAULT, null)
                    .describe("describe A", DEFAULT)
                        .beforeEach(innerBeforeEach)
                        .it("block A 1", NOOP, DEFAULT, null)
                    .describe()
                .describe()
        );

        verify(beforeEach, times(3)).tryToExecute();
        verify(innerBeforeEach, times(1)).tryToExecute();
    }

    @Test
    public void builds_it_blocks_marked_to_be_ignored() throws Throwable {
        UnsafeBlock ignoredBlock = mock(UnsafeBlock.class);

        execute(
            new ExecutableSpecBuilder(BLACK_LIST)
                .describe("SampleSpec", DEFAULT)
                    .it("ignored block", ignoredBlock, IGNORED, null)
                .describe()
        );

        verify(ignoredBlock, never()).tryToExecute();
    }

    @Test
    public void builds_it_blocks_marked_to_be_ignored_when_the_describe_block_is_ignored() {
        List<ItBlock> itBlocks = aSpecWithIgnoredDescribeBlocks();

        assertThat(itBlocks.get(0).shouldBeIgnored(), is(false));
        assertThat(itBlocks.get(1).shouldBeIgnored(), is(true));
        assertThat(itBlocks.get(2).shouldBeIgnored(), is(true));
        assertThat(itBlocks.get(3).shouldBeIgnored(), is(true));
    }

    @Test
    public void builds_it_blocks_marked_to_be_ignored_when_there_is_it_blocks_focused() {
        List<ItBlock> itBlocks = aSpecWithFocusedItBlocks();

        assertThat(itBlocks.get(0).shouldBeIgnored(), is(true));
        assertThat(itBlocks.get(1).shouldBeIgnored(), is(true));
        assertThat(itBlocks.get(2).shouldBeIgnored(), is(false));
        assertThat(itBlocks.get(3).shouldBeIgnored(), is(true));
    }

    @Test
    public void builds_it_blocks_marked_to_be_ignored_when_there_is_describe_blocks_focused() {
        List<ItBlock> itBlocks = aSpecWithFocusedDescribeBlocks();

        assertThat(itBlocks.get(0).shouldBeIgnored(), is(true));
        assertThat(itBlocks.get(1).shouldBeIgnored(), is(false));
        assertThat(itBlocks.get(2).shouldBeIgnored(), is(false));
        assertThat(itBlocks.get(3).shouldBeIgnored(), is(false));
        assertThat(itBlocks.get(4).shouldBeIgnored(), is(false));
    }

    @Test
    public void builds_it_blocks_with_excepted_exception() {
        List<ItBlock> itBlocks = aSpecWithExpectedException();

        assertThat(itBlocks.get(0).expected(), is(equalTo(Exception.class)));
    }

    private List<ItBlock> aSpecWithInnerDescribeBlocks() {
        ExecutableSpecBuilder builder = new ExecutableSpecBuilder(BLACK_LIST);

        builder
            .describe(SampleSpec.class.getName(), DEFAULT)
                .beforeAll(BEFORE_ALL_BLOCK)
                .beforeEach(BEFORE_EACH_BLOCK)
                .it("block 1", BLOCK_1, DEFAULT, null)
                .it("block 2", BLOCK_2, DEFAULT, null)

                .describe("describe A", DEFAULT)
                    .beforeAll(BEFORE_ALL_BLOCK_A)
                    .beforeEach(BEFORE_EACH_BLOCK_A)
                    .it("block A1", BLOCK_A_1, DEFAULT, null)
                    .it("block A2", BLOCK_A_2, DEFAULT, null)
                .describe()
            .describe();

        return builder.build();
    }

    private List<ItBlock> aSpecWithIgnoredDescribeBlocks() {
        ExecutableSpecBuilder builder = new ExecutableSpecBuilder(BLACK_LIST);

        builder
            .describe(SampleSpec.class.getName(), DEFAULT)
                .beforeAll(BEFORE_ALL_BLOCK)
                .beforeEach(BEFORE_EACH_BLOCK)
                .it("block 1", BLOCK_1, DEFAULT, null)

                .describe("describe A", IGNORED)
                    .beforeAll(BEFORE_ALL_BLOCK_A)
                    .beforeEach(BEFORE_EACH_BLOCK_A)
                    .it("block A1", BLOCK_A_1, DEFAULT, null)
                    .it("block A2", BLOCK_A_2, DEFAULT, null)
                    .describe("describe AB", DEFAULT)
                        .it("block AB1", BLOCK_A_B_1, DEFAULT, null)
                    .describe()
                .describe()
            .describe();

        return builder.build();
    }

    private List<ItBlock> aSpecWithFocusedItBlocks() {
        ExecutableSpecBuilder builder = new ExecutableSpecBuilder(WHITE_LIST);

        builder
            .describe(SampleSpec.class.getName(), DEFAULT)
                .beforeAll(BEFORE_ALL_BLOCK)
                .beforeEach(BEFORE_EACH_BLOCK)
                .it("block 1", BLOCK_1, DEFAULT, null)
                .it("block 2", BLOCK_1, DEFAULT, null)

                .describe("describe A", IGNORED)
                    .beforeAll(BEFORE_ALL_BLOCK_A)
                    .beforeEach(BEFORE_EACH_BLOCK_A)
                    .it("block A1", BLOCK_A_1, FOCUSED, null)
                    .it("block A2", BLOCK_A_2, DEFAULT, null)
                .describe()
            .describe();

        return builder.build();
   }

    private List<ItBlock> aSpecWithFocusedDescribeBlocks() {
        ExecutableSpecBuilder builder = new ExecutableSpecBuilder(WHITE_LIST);

        builder
            .describe(SampleSpec.class.getName(), DEFAULT)
                .it("block 1", BLOCK_1, DEFAULT, null)
                .describe("describe A", FOCUSED)
                    .it("block A1", BLOCK_A_1, DEFAULT, null)
                    .it("block A2", BLOCK_A_2, DEFAULT, null)
                    .describe("describe A A", DEFAULT)
                        .it("block A1", BLOCK_A_A_1, DEFAULT, null)
                        .it("block A2", BLOCK_A_A_2, DEFAULT, null)
                    .describe()
                .describe()
            .describe();

        return builder.build();
    }

    private List<ItBlock> aSpecWithExpectedException() {
        ExecutableSpecBuilder builder = new ExecutableSpecBuilder(BLACK_LIST);

        builder
            .describe(SampleSpec.class.getName(), DEFAULT)
                .it("block 1", BLOCK_1, DEFAULT, Exception.class)
            .describe();

        return builder.build();
    }

    private void execute(BlockDefinitionVisitor visitor) throws Throwable {
        ExecutableSpecBuilder builder = (ExecutableSpecBuilder) visitor;

        for (ItBlock block : builder.build()) {
            block.tryToExecute();
        }
    }
}