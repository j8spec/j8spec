package j8spec;

import org.junit.Test;

import static j8spec.J8Spec.*;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class J8SpecBeforeAllTest {

    private static final Runnable NOOP = () -> {};

    private static final Runnable BEFORE_ALL_0_BLOCK = () -> {};
    private static final Runnable BEFORE_ALL_1_BLOCK = () -> {};
    private static final Runnable BEFORE_ALL_A_BLOCK = () -> {};
    private static final Runnable BEFORE_ALL_AA_BLOCK = () -> {};

    static class NoBeforeAllSpec {{
        it("block 1", NOOP);
    }}

    static class SeveralBeforeAllSpec {{
        beforeAll(BEFORE_ALL_0_BLOCK);
        beforeAll(BEFORE_ALL_1_BLOCK);

        it("block 1", NOOP);
    }}

    static class InnerContextSpec {{
        beforeAll(BEFORE_ALL_0_BLOCK);

        it("block 1", NOOP);

        describe("describe A", () -> {
            beforeAll(BEFORE_ALL_A_BLOCK);

            it("block A.1", NOOP);

            describe("describe A.A", () -> {
                beforeAll(BEFORE_ALL_AA_BLOCK);

                it("block A.A.1", NOOP);
            });
        });
    }}

    @Test(expected = IllegalContextException.class)
    public void does_not_allow_before_all_method_direct_invocation() {
        beforeAll(NOOP);
    }

    @Test
    public void builds_a_describe_block_when_there_is_no_before_all_block() {
        DescribeBlock describeBlock = read(NoBeforeAllSpec.class);

        assertThat(describeBlock.beforeAllBlocks(), is(emptyList()));
    }

    @Test
    public void builds_a_describe_block_when_there_are_several_before_all_blocks_in_the_same_context() {
        DescribeBlock describeBlock = read(SeveralBeforeAllSpec.class);

        assertThat(describeBlock.beforeAllBlocks().get(0), is(BEFORE_ALL_0_BLOCK));
        assertThat(describeBlock.beforeAllBlocks().get(1), is(BEFORE_ALL_1_BLOCK));
    }

    @Test
    public void builds_a_describe_block_when_spec_has_inner_contexts() {
        DescribeBlock rootDescribeBlock = read(InnerContextSpec.class);
        DescribeBlock describeA = rootDescribeBlock.describeBlocks().get(0);
        DescribeBlock describeAA = describeA.describeBlocks().get(0);

        assertThat(rootDescribeBlock.beforeAllBlocks().get(0), is(BEFORE_ALL_0_BLOCK));
        assertThat(describeA.beforeAllBlocks().get(0), is(BEFORE_ALL_A_BLOCK));
        assertThat(describeAA.beforeAllBlocks().get(0), is(BEFORE_ALL_AA_BLOCK));
    }
}
