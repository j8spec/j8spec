package j8spec;

import org.junit.Test;

import static j8spec.J8Spec.read;
import static j8spec.J8Spec.xcontext;
import static j8spec.J8Spec.xdescribe;
import static j8spec.J8Spec.xit;

public class J8SpecIgnoreTest {

    static class XitBlockOverwrittenSpec {{
        xit("some text", UnsafeBlock.NOOP);
        xit("some text", UnsafeBlock.NOOP);
    }}

    static class XitBlockWithCollectorOverwrittenSpec {{
        xit("some text", c -> c, UnsafeBlock.NOOP);
        xit("some text", UnsafeBlock.NOOP);
    }}

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
