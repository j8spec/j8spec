package j8spec;

import org.junit.Test;

import static j8spec.BlockExecutionFlag.FOCUSED;
import static j8spec.BlockExecutionStrategy.WHITE_LIST;
import static j8spec.UnsafeBlock.NOOP;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BlockExecutionStrategySelectorTest {

    @Test
    public void selects_black_list_strategy_when_there_is_no_focused_block() {
        BlockExecutionStrategySelector selector = new BlockExecutionStrategySelector();

        selector
            .startGroup(groupConfig().description("group 1").build())
                .example(exampleConfig().description("example 1").build(), NOOP)
            .endGroup();

        assertThat(selector.strategy(), is(BlockExecutionStrategy.BLACK_LIST));
    }

    @Test
    public void selects_white_list_strategy_when_there_is_at_least_one_focused_group() {
        BlockExecutionStrategySelector selector = new BlockExecutionStrategySelector();

        selector
            .startGroup(groupConfig().description("group 1").executionFlag(FOCUSED).build())
                .example(exampleConfig().description("example 1").build(), NOOP)
            .endGroup();

        assertThat(selector.strategy(), is(WHITE_LIST));
    }

    @Test
    public void selects_white_list_strategy_when_there_is_at_least_one_focused_example() {
        BlockExecutionStrategySelector selector = new BlockExecutionStrategySelector();

        selector
            .startGroup(groupConfig().description("group 1").build())
                .example(exampleConfig().description("example 1").executionFlag(FOCUSED).build(), NOOP)
            .endGroup();

        assertThat(selector.strategy(), is(WHITE_LIST));
    }

    private ExampleConfiguration.Builder exampleConfig() {
        return new ExampleConfiguration.Builder();
    }

    private ExampleGroupConfiguration.Builder groupConfig() {
        return new ExampleGroupConfiguration.Builder();
    }
}