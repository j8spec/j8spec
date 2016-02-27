package j8spec;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static j8spec.BlockExecutionFlag.DEFAULT;
import static j8spec.BlockExecutionFlag.FOCUSED;
import static j8spec.BlockExecutionFlag.IGNORED;
import static j8spec.DescribeBlock.newRootDescribeBlock;
import static j8spec.ItBlockDefinition.newFocusedItBlockDefinition;
import static j8spec.ItBlockDefinition.newIgnoredItBlockDefinition;
import static j8spec.ItBlockDefinition.newItBlockDefinition;
import static java.lang.String.join;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DescribeBlockTest {

    private static final String LS = System.getProperty("line.separator");

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

    static class SampleSpec {}

    @Test
    public void has_a_string_representation_when_empty() {
        assertThat(anEmptyDescribeBlock().toString(), is("j8spec.DescribeBlockTest$SampleSpec"));
    }

    @Test
    public void has_a_string_representation_when_it_contains_child_describe_blocks() {
        assertThat(
            aDescribeBlockWithNoBeforeBlocks().toString(),
            is(join(
                LS,
                "j8spec.DescribeBlockTest$SampleSpec",
                "  block 1",
                "  block 2",
                "  child 1",
                "    block 1",
                "    block 2",
                "  child 2",
                "    block 1",
                "    block 2"
            ))
        );
    }

    @Test
    public void builds_it_blocks_with_given_description() {
        DescribeBlock describeBlock = aDescribeBlockWithInnerDescribeBlocks();

        List<ItBlock> itBlocks = describeBlock.flattenItBlocks();

        assertThat(itBlocks.get(0).description(), is("block 1"));
        assertThat(itBlocks.get(1).description(), is("block 2"));
        assertThat(itBlocks.get(2).description(), is("block A1"));
        assertThat(itBlocks.get(3).description(), is("block A2"));
    }

    @Test
    public void builds_it_blocks_with_given_container_descriptions() {
        DescribeBlock describeBlock = aDescribeBlockWithInnerDescribeBlocks();

        List<ItBlock> itBlocks = describeBlock.flattenItBlocks();

        assertThat(itBlocks.get(0).containerDescriptions(), is(singletonList("j8spec.DescribeBlockTest$SampleSpec")));
        assertThat(itBlocks.get(1).containerDescriptions(), is(singletonList("j8spec.DescribeBlockTest$SampleSpec")));
        assertThat(itBlocks.get(2).containerDescriptions(), is(asList("j8spec.DescribeBlockTest$SampleSpec", "describe A")));
        assertThat(itBlocks.get(3).containerDescriptions(), is(asList("j8spec.DescribeBlockTest$SampleSpec", "describe A")));
    }

    @Test
    public void builds_it_blocks_marked_to_be_ignored() {
        DescribeBlock describeBlock = aDescribeBlockWithIgnoredItBlocks();

        List<ItBlock> itBlocks = describeBlock.flattenItBlocks();

        assertThat(itBlocks.get(0).shouldBeIgnored(), is(true));
    }

    @Test
    public void builds_it_blocks_marked_to_be_ignored_when_the_describe_block_is_ignored() {
        DescribeBlock describeBlock = aDescribeBlockWithIgnoredDescribeBlocks();

        List<ItBlock> itBlocks = describeBlock.flattenItBlocks();

        assertThat(itBlocks.get(0).shouldBeIgnored(), is(false));
        assertThat(itBlocks.get(1).shouldBeIgnored(), is(true));
        assertThat(itBlocks.get(2).shouldBeIgnored(), is(true));
    }

    @Test
    public void builds_it_blocks_marked_to_be_ignored_when_there_is_it_blocks_focused() {
        DescribeBlock describeBlock = aDescribeBlockWithFocusedItBlocks();

        List<ItBlock> itBlocks = describeBlock.flattenItBlocks();

        assertThat(itBlocks.get(0).shouldBeIgnored(), is(true));
        assertThat(itBlocks.get(1).shouldBeIgnored(), is(true));
        assertThat(itBlocks.get(2).shouldBeIgnored(), is(false));
        assertThat(itBlocks.get(3).shouldBeIgnored(), is(true));
    }

    @Test
    public void builds_it_blocks_marked_to_be_ignored_when_there_is_describe_blocks_focused() {
        DescribeBlock describeBlock = aDescribeBlockWithFocusedDescribeBlocks();

        List<ItBlock> itBlocks = describeBlock.flattenItBlocks();

        assertThat(itBlocks.get(0).shouldBeIgnored(), is(true));
        assertThat(itBlocks.get(1).shouldBeIgnored(), is(false));
        assertThat(itBlocks.get(2).shouldBeIgnored(), is(false));
        assertThat(itBlocks.get(3).shouldBeIgnored(), is(false));
        assertThat(itBlocks.get(4).shouldBeIgnored(), is(false));
    }

    @Test
    public void builds_it_blocks_with_excepted_exception() {
        DescribeBlock describeBlock = aDescribeBlockWithExpectedException();

        List<ItBlock> itBlocks = describeBlock.flattenItBlocks();

        assertThat(itBlocks.get(0).expected(), is(equalTo(Exception.class)));
    }

    private DescribeBlock anEmptyDescribeBlock() {
        return newRootDescribeBlock(SampleSpec.class.getName(), emptyList(), emptyList(), emptyList());
    }

    private DescribeBlock aDescribeBlockWithNoBeforeBlocks() {
        List<ItBlockDefinition> itBlocks = new LinkedList<>();
        itBlocks.add(newItBlockDefinition("block 1", UnsafeBlock.NOOP));
        itBlocks.add(newItBlockDefinition("block 2", UnsafeBlock.NOOP));

        DescribeBlock rootDescribeBlock = newRootDescribeBlock(
            SampleSpec.class.getName(),
            emptyList(),
            emptyList(),
            itBlocks
        );

        rootDescribeBlock.addDescribeBlock("child 1", emptyList(), emptyList(), itBlocks, DEFAULT);
        rootDescribeBlock.addDescribeBlock("child 2", emptyList(), emptyList(), itBlocks, DEFAULT);

        return rootDescribeBlock;
    }

    private DescribeBlock aDescribeBlockWithInnerDescribeBlocks() {
        List<ItBlockDefinition> itBlocks = new LinkedList<>();
        itBlocks.add(newItBlockDefinition("block 1", BLOCK_1));
        itBlocks.add(newItBlockDefinition("block 2", BLOCK_2));

        DescribeBlock rootDescribeBlock = newRootDescribeBlock(
            SampleSpec.class.getName(),
            singletonList(BEFORE_ALL_BLOCK),
            singletonList(BEFORE_EACH_BLOCK),
            itBlocks
        );

        List<ItBlockDefinition> itBlocksA = new LinkedList<>();
        itBlocksA.add(newItBlockDefinition("block A1", BLOCK_A_1));
        itBlocksA.add(newItBlockDefinition("block A2", BLOCK_A_2));

        rootDescribeBlock.addDescribeBlock(
            "describe A",
            singletonList(BEFORE_ALL_BLOCK_A),
            singletonList(BEFORE_EACH_BLOCK_A),
            itBlocksA,
            DEFAULT
        );

        return rootDescribeBlock;
    }

    private DescribeBlock aDescribeBlockWithIgnoredItBlocks() {
        List<ItBlockDefinition> itBlocks = new LinkedList<>();
        itBlocks.add(newIgnoredItBlockDefinition("block 1", BLOCK_1));

        return newRootDescribeBlock(
            SampleSpec.class.getName(),
            singletonList(BEFORE_ALL_BLOCK),
            singletonList(BEFORE_EACH_BLOCK),
            itBlocks
        );
    }

    private DescribeBlock aDescribeBlockWithExpectedException() {
        List<ItBlockDefinition> itBlocks = new LinkedList<>();
        itBlocks.add(newItBlockDefinition("block 1", BLOCK_1, Exception.class));

        return newRootDescribeBlock(
            SampleSpec.class.getName(),
            singletonList(BEFORE_ALL_BLOCK),
            singletonList(BEFORE_EACH_BLOCK),
            itBlocks
        );
    }

    private DescribeBlock aDescribeBlockWithFocusedItBlocks() {
        List<ItBlockDefinition> itBlocks = new LinkedList<>();
        itBlocks.add(newItBlockDefinition("block 1", BLOCK_1));
        itBlocks.add(newItBlockDefinition("block 2", BLOCK_2));

        DescribeBlock rootDescribeBlock = newRootDescribeBlock(
            SampleSpec.class.getName(),
            singletonList(BEFORE_ALL_BLOCK),
            singletonList(BEFORE_EACH_BLOCK),
            itBlocks
        );

        List<ItBlockDefinition> itBlocksA = new LinkedList<>();
        itBlocksA.add(newFocusedItBlockDefinition("block A1", BLOCK_A_1));
        itBlocksA.add(newItBlockDefinition("block A2", BLOCK_A_2));

        rootDescribeBlock.addDescribeBlock(
            "describe A",
            singletonList(BEFORE_ALL_BLOCK),
            singletonList(BEFORE_EACH_BLOCK),
            itBlocksA,
            IGNORED
        );

        return rootDescribeBlock;
    }

    private DescribeBlock aDescribeBlockWithFocusedDescribeBlocks() {
        List<ItBlockDefinition> itBlocks = new LinkedList<>();
        itBlocks.add(newItBlockDefinition("block 1", BLOCK_1));

        DescribeBlock rootDescribeBlock = newRootDescribeBlock(
            SampleSpec.class.getName(),
            emptyList(),
            emptyList(),
            itBlocks
        );

        List<ItBlockDefinition> itBlocksA = new LinkedList<>();
        itBlocksA.add(newItBlockDefinition("block A1", BLOCK_A_1));
        itBlocksA.add(newItBlockDefinition("block A2", BLOCK_A_2));

        DescribeBlock describeA = rootDescribeBlock.addDescribeBlock(
            "describe A",
            emptyList(),
            emptyList(),
            itBlocksA,
            FOCUSED
        );

        List<ItBlockDefinition> itBlocksAA = new LinkedList<>();
        itBlocksAA.add(newItBlockDefinition("block AA1", BLOCK_A_A_1));
        itBlocksAA.add(newItBlockDefinition("block AA2", BLOCK_A_A_2));

        describeA.addDescribeBlock("describe A A", emptyList(), emptyList(), itBlocksAA, DEFAULT);

        return rootDescribeBlock;
    }

    private DescribeBlock aDescribeBlockWithIgnoredDescribeBlocks() {
        List<ItBlockDefinition> itBlocks = new LinkedList<>();
        itBlocks.add(newItBlockDefinition("block 1", BLOCK_1));

        DescribeBlock rootDescribeBlock = newRootDescribeBlock(
            SampleSpec.class.getName(),
            singletonList(BEFORE_ALL_BLOCK),
            singletonList(BEFORE_EACH_BLOCK),
            itBlocks
        );

        List<ItBlockDefinition> itBlocksA = new LinkedList<>();
        itBlocksA.add(newItBlockDefinition("block A1", BLOCK_A_1));
        itBlocksA.add(newItBlockDefinition("block A2", BLOCK_A_2));

        rootDescribeBlock.addDescribeBlock(
            "describe A",
            singletonList(BEFORE_ALL_BLOCK),
            singletonList(BEFORE_EACH_BLOCK),
            itBlocksA,
            IGNORED
        );

        return rootDescribeBlock;
    }
}
