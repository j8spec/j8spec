package j8spec;

import org.junit.Test;

import static j8spec.J8Spec.fcontext;
import static j8spec.J8Spec.fdescribe;
import static j8spec.J8Spec.fit;
import static j8spec.J8Spec.read;

public class J8SpecFocusTest {

    static class FitBlockOverwrittenSpec {{
        fit("some text", UnsafeBlock.NOOP);
        fit("some text", UnsafeBlock.NOOP);
    }}

    static class FitBlockWithCollectorOverwrittenSpec {{
        fit("some text", c -> c, UnsafeBlock.NOOP);
        fit("some text", UnsafeBlock.NOOP);
    }}

    @Test(expected = IllegalContextException.class)
    public void does_not_allow_fdescribe_method_direct_invocation() {
        fdescribe("some text", SafeBlock.NOOP);
    }

    @Test(expected = IllegalContextException.class)
    public void does_not_allow_fcontext_method_direct_invocation() {
        fcontext("some text", SafeBlock.NOOP);
    }

    @Test(expected = IllegalContextException.class)
    public void does_not_allow_fit_method_direct_invocation() {
        fit("some text", UnsafeBlock.NOOP);
    }

    @Test(expected = IllegalContextException.class)
    public void does_not_allow_fit_method_with_collector_direct_invocation() {
        fit("some text", c -> c, UnsafeBlock.NOOP);
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
