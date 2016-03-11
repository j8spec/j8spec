package j8spec;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static j8spec.Hook.newHook;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ExampleTest {

    @Test
    public void runs_before_blocks_and_then_block() throws Throwable {
        final List<String> executionOrder = new ArrayList<>();

        new Example.Builder()
            .description("example")
            .beforeEachHooks(singletonList(newHook(() -> executionOrder.add("beforeEach"))))
            .beforeAllHooks(singletonList(newHook(() -> executionOrder.add("beforeAll"))))
            .block(() -> executionOrder.add("block"))
            .rank(new Rank(0))
            .build()
            .tryToExecute();

        assertThat(executionOrder.get(0), is("beforeAll"));
        assertThat(executionOrder.get(1), is("beforeEach"));
        assertThat(executionOrder.get(2), is("block"));
    }

    @Test
    public void runs_block_and_then_after_blocks() throws Throwable {
        final List<String> executionOrder = new ArrayList<>();

        new Example.Builder()
            .description("example")
            .afterAllHooks(singletonList(newHook(() -> executionOrder.add("afterAll"))))
            .afterEachHooks(singletonList(newHook(() -> executionOrder.add("afterEach"))))
            .block(() -> executionOrder.add("block"))
            .rank(new Rank(0))
            .build()
            .tryToExecute();

        assertThat(executionOrder.get(0), is("block"));
        assertThat(executionOrder.get(1), is("afterEach"));
        assertThat(executionOrder.get(2), is("afterAll"));
    }

    @Test
    public void indicates_if_it_should_be_ignored() {
        Example example = new Example.Builder()
            .description("example")
            .rank(new Rank(0))
            .ignored()
            .build();

        assertThat(example.shouldBeIgnored(), is(true));
    }

    @Test
    public void indicates_if_example_should_not_be_ignored() {
        Example example = new Example.Builder()
            .description("example")
            .block(() -> {})
            .rank(new Rank(0))
            .build();

        assertThat(example.shouldBeIgnored(), is(false));
    }

    @Test
    public void is_sortable_by_rank() {
        Example example1 = new Example.Builder()
            .description("example 1")
            .block(() -> {})
            .rank(new Rank(0))
            .build();

        Example example2 = new Example.Builder()
            .description("example 2")
            .block(() -> {})
            .rank(new Rank(1))
            .build();

        LinkedList<Example> blocks = new LinkedList<>();
        blocks.add(example2);
        blocks.add(example1);

        Collections.sort(blocks);

        assertThat(blocks, is(asList(
            example1,
            example2
        )));
    }
}
