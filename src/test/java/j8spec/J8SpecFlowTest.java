package j8spec;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static j8spec.J8Spec.beforeAll;
import static j8spec.J8Spec.beforeEach;
import static j8spec.J8Spec.describe;
import static j8spec.J8Spec.fcontext;
import static j8spec.J8Spec.fdescribe;
import static j8spec.J8Spec.fit;
import static j8spec.J8Spec.it;
import static j8spec.J8Spec.read;
import static j8spec.J8Spec.xcontext;
import static j8spec.J8Spec.xdescribe;
import static j8spec.J8Spec.xit;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class J8SpecFlowTest {

    static class SingleItBlockSpec {{
        it("block 1", () -> log.add("block 1"));
    }}

    static class SampleSpec {{
        beforeAll(() -> log.add("before all 1"));
        beforeAll(() -> log.add("before all 2"));
        beforeEach(() -> log.add("before each 1"));
        beforeEach(() -> log.add("before each 2"));

        it("block 1", () -> log.add("block 1"));
        it("block 2", () -> log.add("block 2"));

        describe("describe A", () -> {
            beforeAll(() -> log.add("describe A before all 1"));
            beforeAll(() -> log.add("describe A before all 2"));
            beforeEach(() -> log.add("describe A before each 1"));
            beforeEach(() -> log.add("describe A before each 2"));

            it("block A1", () -> log.add("block A1"));
            it("block A2", () -> log.add("block A2"));
        });

        it("block 3", () -> log.add("block 3"));
    }}

    static class OddHookOrderSpec {{
        it("block 1", () -> log.add("block 1"));
        beforeAll(() -> log.add("before all 1"));
        beforeEach(() -> log.add("before each 1"));
    }}

    static class FdescribeSpec {{
        it("block 1", () -> log.add("block 1"));
        it("block 2", () -> log.add("block 2"));

        fdescribe("describe A", () -> {
            it("block A.1", () -> log.add("block A.1"));
            it("block A.2", () -> log.add("block A.2"));
        });
    }}

    static class FcontextSpec {{
        it("block 1", () -> log.add("block 1"));
        it("block 2", () -> log.add("block 2"));

        fcontext("context A", () -> {
            it("block A.1", () -> log.add("block A.1"));
            it("block A.2", () -> log.add("block A.2"));
        });
    }}

    static class FitSpec {{
        it("block 1", () -> log.add("block 1"));
        fit("block 2", () -> log.add("block 2"));
    }}

    static class XdescribeSpec {{
        it("block 1", () -> log.add("block 1"));
        it("block 2", () -> log.add("block 2"));

        xdescribe("describe A", () -> {
            it("block A.1", () -> log.add("block A.1"));
            it("block A.2", () -> log.add("block A.2"));
        });
    }}

    static class XcontextSpec {{
        it("block 1", () -> log.add("block 1"));
        it("block 2", () -> log.add("block 2"));

        xcontext("context A", () -> {
            it("block A.1", () -> log.add("block A.1"));
            it("block A.2", () -> log.add("block A.2"));
        });
    }}

    static class XitSpec {{
        xit("block 1", () -> log.add("block 1"));
        it("block 2", () -> log.add("block 2"));
    }}

    private static List<String> log;

    @Before
    public void resetLog() throws Throwable {
        log = new ArrayList<>();
    }

    @Test
    public void single_it_block_spec_flow() throws Throwable {
        executeSpec(SingleItBlockSpec.class);

        assertThat(log, is(asList(
            "block 1"
        )));
    }

    @Test
    public void focused_describe_spec_flow() throws Throwable {
        executeSpec(FdescribeSpec.class);

        assertThat(log, is(asList(
            "block A.1",
            "block A.2"
        )));
    }

    @Test
    public void focused_context_spec_flow() throws Throwable {
        executeSpec(FcontextSpec.class);

        assertThat(log, is(asList(
            "block A.1",
            "block A.2"
        )));
    }

    @Test
    public void focused_it_spec_flow() throws Throwable {
        executeSpec(FitSpec.class);

        assertThat(log, is(asList(
            "block 2"
        )));
    }


    @Test
    public void ignored_describe_spec_flow() throws Throwable {
        executeSpec(XdescribeSpec.class);

        assertThat(log, is(asList(
            "block 1",
            "block 2"
        )));
    }

    @Test
    public void ignored_context_spec_flow() throws Throwable {
        executeSpec(XcontextSpec.class);

        assertThat(log, is(asList(
            "block 1",
            "block 2"
        )));
    }

    @Test
    public void ignored_it_spec_flow() throws Throwable {
        executeSpec(XitSpec.class);

        assertThat(log, is(asList(
            "block 2"
        )));
    }

    @Test
    public void respects_hook_order_even_when_they_are_defined_after() throws Throwable {
        executeSpec(OddHookOrderSpec.class);

        assertThat(log, is(asList(
            "before all 1",
            "before each 1",
            "block 1"
        )));
    }

    @Test
    public void full_spec_flow() throws Throwable {
        executeSpec(SampleSpec.class);

        assertThat(log, is(asList(
            "before all 1",
            "before all 2",

            "before each 1",
            "before each 2",
            "block 1",

            "before each 1",
            "before each 2",
            "block 2",

            "describe A before all 1",
            "describe A before all 2",

            "before each 1",
            "before each 2",
            "describe A before each 1",
            "describe A before each 2",
            "block A1",

            "before each 1",
            "before each 2",
            "describe A before each 1",
            "describe A before each 2",
            "block A2",

            "before each 1",
            "before each 2",
            "block 3"
        )));
    }

    private void executeSpec(Class<?> specClass) throws Throwable {
        for (Example example : read(specClass)) {
            example.tryToExecute();
        }
    }
}
