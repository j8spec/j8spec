package j8spec;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static j8spec.Hook.newHook;
import static j8spec.Var.var;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class ExampleTest {

    @Test
    public void runs_before_hooks_and_then_block() throws Throwable {
        final List<String> executionOrder = new ArrayList<>();

        new Example.Builder()
            .description("example")
            .beforeEachHooks(singletonList(newHook(() -> executionOrder.add("beforeEach"))))
            .beforeAllHooks(singletonList(newHook(() -> executionOrder.add("beforeAll"))))
            .block(() -> executionOrder.add("block"))
            .rank(new Rank(0))
            .build()
            .tryToExecute();

        assertThat(executionOrder, is(asList(
            "beforeAll",
            "beforeEach",
            "block"
        )));
    }

    @Test
    public void runs_block_and_then_after_hooks() throws Throwable {
        final List<String> executionOrder = new ArrayList<>();

        new Example.Builder()
            .description("example")
            .afterAllHooks(singletonList(newHook(() -> executionOrder.add("afterAll"))))
            .afterEachHooks(singletonList(newHook(() -> executionOrder.add("afterEach"))))
            .block(() -> executionOrder.add("block"))
            .rank(new Rank(0))
            .build()
            .tryToExecute();

        assertThat(executionOrder, is(asList(
            "block",
            "afterEach",
            "afterAll"
        )));
    }

    @Test
    public void indicates_if_example_should_be_ignored() {
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

    @Test
    public void initializes_variables_before_execution() throws Throwable {
        List<Object> values = new ArrayList<>();

        Var<String> stringVar = var();
        Var<Integer> integerVar = var();

        List<VarInitializer<?>> vars = new LinkedList<>();
        vars.add(new VarInitializer<>(stringVar, () -> "value"));
        vars.add(new VarInitializer<>(integerVar, () -> 123));

        new Example.Builder()
            .description("example")
            .vars(vars)
            .block(() -> {
                values.add(var(stringVar));
                values.add(var(integerVar));
            })
            .build()
            .tryToExecute();

        assertThat(values, is(asList(
            "value",
            123
        )));
    }
}
