package j8spec;

import org.junit.Test;

import static j8spec.J8Spec.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class J8SpecFocusTest {

    private static final Runnable NOOP = () -> {};

    private static final Runnable IT_BLOCK_1 = () -> {};
    private static final Runnable IT_BLOCK_2 = () -> {};

    private static final Runnable IT_BLOCK_A1 = () -> {};
    private static final Runnable IT_BLOCK_A2 = () -> {};

    static class FitBlockOverwrittenSpec {{
        fit("some text", NOOP);
        fit("some text", NOOP);
    }}

    static class FitBlockWithCollectorOverwrittenSpec {{
        fit("some text", c -> c, NOOP);
        fit("some text", NOOP);
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

    @Test
    public void builds_a_describe_block_marking_fdescribe_blocks_from_the_spec_definition_as_focused() {
        DescribeBlock describeBlock = read(FdescribeSpec.class);

        assertThat(describeBlock.focused(), is(false));
        assertThat(describeBlock.describeBlocks().get(0).focused(), is(true));
    }

    @Test
    public void builds_a_describe_block_marking_fit_blocks_from_the_spec_definition_as_focused() {
        DescribeBlock describeBlock = read(FitSpec.class);

        assertThat(describeBlock.itBlock("block 1").focused(), is(true));
    }

    @Test(expected = IllegalContextException.class)
    public void does_not_allow_fdescribe_method_direct_invocation() {
        fdescribe("some text", NOOP);
    }

    @Test(expected = IllegalContextException.class)
    public void does_not_allow_fit_method_direct_invocation() {
        fit("some text", NOOP);
    }

    @Test(expected = IllegalContextException.class)
    public void does_not_allow_fit_method_with_collector_direct_invocation() {
        fit("some text", c -> c, NOOP);
    }

    @Test(expected = BlockAlreadyDefinedException.class)
    public void does_not_allow_fit_block_to_be_replaced() {
        read(FitBlockOverwrittenSpec.class);
    }

    @Test(expected = BlockAlreadyDefinedException.class)
    public void does_not_allow_fit_block_with_collector_to_be_replaced() {
        read(FitBlockWithCollectorOverwrittenSpec.class);
    }
}
