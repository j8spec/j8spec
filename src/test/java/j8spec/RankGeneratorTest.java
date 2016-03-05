package j8spec;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RankGeneratorTest {

    private final RankGenerator generator = new RankGenerator();

    @Test
    public void initialize_new_level_with_rank_zero_when_using_incremental_strategy() {
        generator.pushLevel(groupConfig().definedOrder().build());

        assertThat(generator.generate(), is(new Rank(0)));
    }

    private ExampleGroupConfiguration.Builder groupConfig() {
        return new ExampleGroupConfiguration.Builder();
    }

    @Test
    public void calculates_next_value_for_current_rank_level_when_using_incremental_strategy() {
        generator.pushLevel(groupConfig().definedOrder().build());
        generator.next();

        assertThat(generator.generate(), is(new Rank(1)));
    }

    @Test
    public void adds_new_rank_level_using_previous_level_strategy() {
        generator.pushLevel(groupConfig().definedOrder().build());
        generator.pushLevel(groupConfig().build());

        assertThat(generator.generate(), is(new Rank(0, 0)));
    }

    @Test
    public void calculates_next_value_for_rank_after_dropping_level_when_using_incremental_strategy() {
        generator.pushLevel(groupConfig().definedOrder().build());
        generator.pushLevel(groupConfig().build());
        generator.popLevel();

        assertThat(generator.generate(), is(new Rank(1)));
    }

    @Test
    public void initialize_new_level_with_rank_zero_when_using_random_strategy() {
        generator.pushLevel(groupConfig().randomOrder().seed(0L).build());

        assertThat(generator.generate(), is(new Rank(-1155484576)));
    }

    @Test
    public void calculates_next_value_for_current_rank_level_when_using_random_strategy() {
        generator.pushLevel(groupConfig().randomOrder().seed(0L).build());
        generator.next();

        assertThat(generator.generate(), is(new Rank(-723955400)));
    }

    @Test
    public void adds_new_rank_level_when_using_random_strategy() {
        generator.pushLevel(groupConfig().randomOrder().seed(0L).build());
        generator.pushLevel(groupConfig().build());

        assertThat(generator.generate(), is(new Rank(-1155484576, -723955400)));
    }

    @Test
    public void calculates_next_value_for_rank_after_dropping_level_when_using_random_strategy() {
        generator.pushLevel(groupConfig().randomOrder().seed(0L).build());
        generator.pushLevel(groupConfig().build());
        generator.popLevel();

        assertThat(generator.generate(), is(new Rank(1033096058)));
    }

    @Test(expected = IllegalStateException.class)
    public void does_not_accept_default_execution_order_in_the_root_level() {
        generator.pushLevel(groupConfig().build());
    }
}