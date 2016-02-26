package j8spec;

import org.junit.Test;

import static j8spec.J8Spec.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class J8SpecIgnoreTest {

    private static final UnsafeBlock IT_BLOCK_1 = () -> {};
    private static final UnsafeBlock IT_BLOCK_2 = () -> {};

    private static final UnsafeBlock IT_BLOCK_A1 = () -> {};
    private static final UnsafeBlock IT_BLOCK_A2 = () -> {};

    static class XitBlockOverwrittenSpec {{
        xit("some text", UnsafeBlock.NOOP);
        xit("some text", UnsafeBlock.NOOP);
    }}

    static class XitBlockWithCollectorOverwrittenSpec {{
        xit("some text", c -> c, UnsafeBlock.NOOP);
        xit("some text", UnsafeBlock.NOOP);
    }}

    static class XdescribeSpec {{
        it("block 1", IT_BLOCK_1);
        it("block 2", IT_BLOCK_2);

        xdescribe("describe A", () -> {
            it("block A.1", IT_BLOCK_A1);
            it("block A.2", IT_BLOCK_A2);
        });
    }}

    static class XcontextSpec {{
        it("block 1", IT_BLOCK_1);
        it("block 2", IT_BLOCK_2);

        xcontext("context A", () -> {
            it("block A.1", IT_BLOCK_A1);
            it("block A.2", IT_BLOCK_A2);
        });
    }}

    static class XitSpec {{
        xit("block 1", IT_BLOCK_1);
    }}

    @Test
    public void builds_a_describe_block_marking_xdescribe_blocks_from_the_spec_definition_as_ignored() {
        DescribeBlock describeBlock = read(XdescribeSpec.class);

        assertThat(describeBlock.ignored(), is(false));
        assertThat(describeBlock.describeBlocks().get(0).ignored(), is(true));
    }

    @Test
    public void builds_a_describe_block_marking_xcontext_blocks_from_the_spec_definition_as_ignored() {
        DescribeBlock describeBlock = read(XcontextSpec.class);

        assertThat(describeBlock.ignored(), is(false));
        assertThat(describeBlock.describeBlocks().get(0).ignored(), is(true));
    }

    @Test
    public void builds_a_describe_block_marking_xit_blocks_from_the_spec_definition_as_ignored() {
        DescribeBlock describeBlock = read(XitSpec.class);

        assertThat(describeBlock.itBlock("block 1").ignored(), is(true));
    }

    @Test(expected = IllegalContextException.class)
    public void does_not_allow_xdescribe_method_direct_invocation() {
        xdescribe("some text", SafeBlock.NOOP);
    }

    @Test(expected = IllegalContextException.class)
    public void does_not_allow_xcontext_method_direct_invocation() {
        xcontext("some text", SafeBlock.NOOP);
    }

    @Test(expected = IllegalContextException.class)
    public void does_not_allow_xit_method_direct_invocation() {
        xit("some text", UnsafeBlock.NOOP);
    }

    @Test(expected = IllegalContextException.class)
    public void does_not_allow_xit_method_direct_invocation_with_collector() {
        xit("some text", c -> c, UnsafeBlock.NOOP);
    }

    @Test(expected = BlockAlreadyDefinedException.class)
    public void does_not_allow_xit_block_to_be_replaced() {
        read(XitBlockOverwrittenSpec.class);
    }

    @Test(expected = BlockAlreadyDefinedException.class)
    public void does_not_allow_xit_block_with_collector_to_be_replaced() {
        read(XitBlockWithCollectorOverwrittenSpec.class);
    }
}
