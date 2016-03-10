package j8spec;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static j8spec.Example.newExample;
import static j8spec.Hook.newHook;
import static j8spec.Example.newIgnoredExample;
import static j8spec.UnsafeBlock.NOOP;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ExampleTest {

    @Test
    public void runs_before_blocks_and_then_block() throws Throwable {
        final List<String> executionOrder = new ArrayList<>();

        newExample(
            emptyList(),
            "it block",
            asList(
                newHook(() -> executionOrder.add("beforeEach1")),
                newHook(() -> executionOrder.add("beforeEach2"))
            ),
            new LinkedList<>(), () -> executionOrder.add("block"),
            new Rank(0)
        ).tryToExecute();

        assertThat(executionOrder.get(0), is("beforeEach1"));
        assertThat(executionOrder.get(1), is("beforeEach2"));
        assertThat(executionOrder.get(2), is("block"));
    }

    @Test
    public void runs_block_and_then_after_blocks() throws Throwable {
        final List<String> executionOrder = new ArrayList<>();

        newExample(
            emptyList(),
            "it block",
            new LinkedList<>(),
            asList(
                newHook(() -> executionOrder.add("afterEach1")),
                newHook(() -> executionOrder.add("afterEach2"))
            ),
            () -> executionOrder.add("block"),
            new Rank(0)
        ).tryToExecute();

        assertThat(executionOrder.get(0), is("block"));
        assertThat(executionOrder.get(1), is("afterEach1"));
        assertThat(executionOrder.get(2), is("afterEach2"));
    }

    @Test
    public void indicates_if_it_should_be_ignored() {
        Example block = newIgnoredExample(emptyList(), "it block", new Rank(0));

        assertThat(block.shouldBeIgnored(), is(true));
    }

    @Test
    public void indicates_if_it_should_not_be_ignored() {
        Example block = newExample(emptyList(), "it block", emptyList(), new LinkedList<>(), () -> {}, new Rank(0));

        assertThat(block.shouldBeIgnored(), is(false));
    }

    @Test
    public void is_sortable_by_rank() {
        Example block1 = newExample(emptyList(), "block 1", emptyList(), new LinkedList<>(), NOOP, new Rank(0));
        Example block2 = newExample(emptyList(), "block 2", emptyList(), new LinkedList<>(), NOOP, new Rank(1));

        LinkedList<Example> blocks = new LinkedList<>();
        blocks.add(block2);
        blocks.add(block1);

        Collections.sort(blocks);

        assertThat(blocks, is(asList(
            block1,
            block2
        )));
    }
}
