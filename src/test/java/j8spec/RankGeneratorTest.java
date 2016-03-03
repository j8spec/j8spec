package j8spec;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RankGeneratorTest {

    private final RankGenerator generator = new RankGenerator();

    @Test
    public void initialize_new_level_with_rank_zero_when_using_incremental_strategy() {
        generator.pushLevel(RankGenerator.Incremental.INSTANCE);

        assertThat(generator.generate(), is(new Rank(0)));
    }

    @Test
    public void calculates_next_value_for_current_rank_level_when_using_incremental_strategy() {
        generator.pushLevel(RankGenerator.Incremental.INSTANCE);
        generator.next();

        assertThat(generator.generate(), is(new Rank(1)));
    }

    @Test
    public void adds_new_rank_level_when_using_incremental_strategy() {
        generator.pushLevel(RankGenerator.Incremental.INSTANCE);
        generator.pushLevel(RankGenerator.Incremental.INSTANCE);

        assertThat(generator.generate(), is(new Rank(0, 0)));
    }

    @Test
    public void calculates_next_value_for_rank_after_dropping_level_when_using_incremental_strategy() {
        generator.pushLevel(RankGenerator.Incremental.INSTANCE);
        generator.pushLevel(RankGenerator.Incremental.INSTANCE);
        generator.popLevel();

        assertThat(generator.generate(), is(new Rank(1)));
    }
}