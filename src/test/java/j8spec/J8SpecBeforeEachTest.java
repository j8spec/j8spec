package j8spec;

import org.junit.Test;

import static j8spec.J8Spec.*;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class J8SpecBeforeEachTest {

    private static final Runnable NOOP = () -> {};
    
    private static final Runnable BEFORE_EACH_0_BLOCK = NOOP;
    private static final Runnable BEFORE_EACH_1_BLOCK = NOOP;
    private static final Runnable BEFORE_EACH_A_BLOCK = NOOP;
    private static final Runnable BEFORE_EACH_AA_BLOCK = NOOP;

    static class NoBeforeEachSpec {{
        it("block 1", NOOP);
    }}

    static class SeveralBeforeEachSpec {{
        beforeEach(BEFORE_EACH_0_BLOCK);
        beforeEach(BEFORE_EACH_1_BLOCK);

        it("block 1", NOOP);
    }}

    static class InnerContextSpec {{
        beforeEach(BEFORE_EACH_0_BLOCK);

        it("block 1", NOOP);

        describe("describe A", () -> {
            beforeEach(BEFORE_EACH_A_BLOCK);

            it("block A.1", NOOP);

            describe("describe A.A", () -> {
                beforeEach(BEFORE_EACH_AA_BLOCK);

                it("block A.A.1", NOOP);
            });
        });
    }}

    @Test(expected = IllegalContextException.class)
    public void does_not_allow_before_each_method_direct_invocation() {
        beforeEach(NOOP);
    }

    @Test
    public void builds_a_describe_block_when_there_is_no_before_each_block() {
        DescribeBlock describeBlock = read(NoBeforeEachSpec.class);

        assertThat(describeBlock.beforeEachBlocks(), is(emptyList()));
    }

    @Test
    public void builds_a_describe_block_when_there_are_several_before_each_blocks_in_the_same_context() {
        DescribeBlock describeBlock = read(SeveralBeforeEachSpec.class);

        assertThat(describeBlock.beforeEachBlocks().get(0), is(BEFORE_EACH_0_BLOCK));
        assertThat(describeBlock.beforeEachBlocks().get(1), is(BEFORE_EACH_1_BLOCK));
    }

    @Test
    public void builds_a_describe_block_when_spec_has_inner_contexts() {
        DescribeBlock rootDescribeBlock = read(InnerContextSpec.class);
        DescribeBlock describeA = rootDescribeBlock.describeBlocks().get(0);
        DescribeBlock describeAA = describeA.describeBlocks().get(0);

        assertThat(rootDescribeBlock.beforeEachBlocks().get(0), is(BEFORE_EACH_0_BLOCK));
        assertThat(describeA.beforeEachBlocks().get(0), is(BEFORE_EACH_A_BLOCK));
        assertThat(describeAA.beforeEachBlocks().get(0), is(BEFORE_EACH_AA_BLOCK));
    }
}
