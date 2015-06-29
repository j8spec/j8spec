package j8spec;

import org.junit.Test;

import static j8spec.J8Spec.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class J8SpecIgnoreTest {

    private static final Runnable NOOP = () -> {};

    private static final Runnable IT_BLOCK_1 = () -> {};
    private static final Runnable IT_BLOCK_2 = () -> {};

    private static final Runnable IT_BLOCK_A1 = () -> {};
    private static final Runnable IT_BLOCK_A2 = () -> {};

    static class XitBlockOverwrittenSpec {{
        xit("some text", NOOP);
        xit("some text", NOOP);
    }}

    static class XitBlockWithCollectorOverwrittenSpec {{
        xit("some text", c -> c, NOOP);
        xit("some text", NOOP);
    }}

    static class XdescribeSpec {{
        it("block 1", IT_BLOCK_1);
        it("block 2", IT_BLOCK_2);

        xdescribe("describe A", () -> {
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
    public void builds_a_describe_block_marking_xit_blocks_from_the_spec_definition_as_ignored() {
        DescribeBlock describeBlock = read(XitSpec.class);

        assertThat(describeBlock.itBlock("block 1").ignored(), is(true));
    }

    @Test(expected = IllegalContextException.class)
    public void does_not_allow_xdescribe_method_direct_invocation() {
        xdescribe("some text", NOOP);
    }

    @Test(expected = IllegalContextException.class)
    public void does_not_allow_xit_method_direct_invocation() {
        xit("some text", NOOP);
    }

    @Test(expected = IllegalContextException.class)
    public void does_not_allow_xit_method_direct_invocation_with_collector() {
        xit("some text", c -> c, NOOP);
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
