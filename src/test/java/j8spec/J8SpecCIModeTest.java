package j8spec;

import j8spec.annotation.RandomOrder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static j8spec.J8Spec.*;
import static j8spec.UnsafeBlock.NOOP;

public class J8SpecCIModeTest {

    static class FocusedExampleSpec {{
        fit("block 1", NOOP);
    }}

    static class FdescribeSpec {{
        fdescribe("describe 1", () -> {
            it("block 1", NOOP);
        });
    }}

    static class FcontextSpec {{
        fcontext("describe 1", () -> {
            it("block 1", NOOP);
        });
    }}

    static class IgnoredExampleSpec {{
        xit("block 1", NOOP);
    }}

    static class XdescribeSpec {{
        xdescribe("describe 1", () -> {
            it("block 1", NOOP);
        });
    }}

    static class XcontextSpec {{
        xcontext("describe 1", () -> {
            it("block 1", NOOP);
        });
    }}

    @RandomOrder(seed = 0)
    static class HardCodedSeedSpec {{
        it("block 1", NOOP);
    }}

    @Before
    public void setCIModeOn() {
        System.setProperty("j8spec.ci.mode", "true");
    }

    @After
    public void setCIModeOff() {
        System.setProperty("j8spec.ci.mode", "false");
    }

    @Test(expected = CIModeEnabledException.class)
    public void does_not_allow_focused_examples_when_ci_mode_enabled() {
        read(FocusedExampleSpec.class);
    }

    @Test(expected = CIModeEnabledException.class)
    public void does_not_allow_focused_example_group_when_ci_mode_enabled() {
        read(FdescribeSpec.class);
    }

    @Test(expected = CIModeEnabledException.class)
    public void does_not_allow_fcontext_blocks_when_ci_mode_enabled() {
        read(FcontextSpec.class);
    }

    @Test(expected = CIModeEnabledException.class)
    public void does_not_allow_ignored_examples_when_ci_mode_enabled() {
        read(IgnoredExampleSpec.class);
    }

    @Test(expected = CIModeEnabledException.class)
    public void does_not_allow_ignored_example_group_when_ci_mode_enabled() {
        read(XdescribeSpec.class);
    }

    @Test(expected = CIModeEnabledException.class)
    public void does_not_allow_xcontext_blocks_when_ci_mode_enabled() {
        read(XcontextSpec.class);
    }

    @Test(expected = CIModeEnabledException.class)
    public void does_not_allow_hard_coded_seed_when_ci_mode_enabled() {
        read(HardCodedSeedSpec.class);
    }
}
