package j8spec;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static j8spec.BeforeBlock.newBeforeEachBlock;
import static j8spec.ItBlock.newIgnoredItBlock;
import static j8spec.ItBlock.newItBlock;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ItBlockTest {

    @Test
    public void runs_before_blocks_and_then_body() throws Throwable {
        final List<String> executionOrder = new ArrayList<>();

        newItBlock(
            emptyList(),
            "it block",
            asList(
                newBeforeEachBlock(() -> executionOrder.add("beforeEach1")),
                newBeforeEachBlock(() -> executionOrder.add("beforeEach2"))
            ),
            () -> executionOrder.add("body")
        ).run();

        assertThat(executionOrder.get(0), is("beforeEach1"));
        assertThat(executionOrder.get(1), is("beforeEach2"));
        assertThat(executionOrder.get(2), is("body"));
    }

    @Test
    public void indicates_if_it_should_be_ignored() {
        ItBlock block = newIgnoredItBlock(emptyList(), "it block");

        assertThat(block.shouldBeIgnored(), is(true));
    }

    @Test
    public void indicates_if_it_should_not_be_ignored() {
        ItBlock block = newItBlock(emptyList(), "it block", emptyList(), () -> {});

        assertThat(block.shouldBeIgnored(), is(false));
    }
}
