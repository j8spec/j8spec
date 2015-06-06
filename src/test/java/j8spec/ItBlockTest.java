package j8spec;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static j8spec.ItBlock.newItBlock;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ItBlockTest {

    @Test
    public void runsBeforeBlocksAndThenBody() {
        final List<String> executionOrder = new ArrayList<>();

        newItBlock(
            Collections.<String>emptyList(),
            "it block",
            asList(
                () -> executionOrder.add("beforeEach1"),
                () -> executionOrder.add("beforeEach2")
            ),
            () -> executionOrder.add("body")
        ).run();

        assertThat(executionOrder.get(0), is("beforeEach1"));
        assertThat(executionOrder.get(1), is("beforeEach2"));
        assertThat(executionOrder.get(2), is("body"));
    }
}
