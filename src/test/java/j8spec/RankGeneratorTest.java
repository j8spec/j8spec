package j8spec;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RankGeneratorTest {

    private final RankGenerator generator = new RankGenerator();

    @Test
    public void initialize_new_level_with_rank_zero() {
        generator.pushLevel();

        assertThat(generator.generate(), is(new Rank(0)));
    }

    @Test
    public void calculates_next_value_for_current_rank_level() {
        generator.pushLevel();
        generator.next();

        assertThat(generator.generate(), is(new Rank(1)));
    }

    @Test
    public void adds_new_rank_level() {
        generator.pushLevel();
        generator.pushLevel();

        assertThat(generator.generate(), is(new Rank(0, 0)));
    }

    @Test
    public void calculates_next_value_for_rank_after_dropping_level() {
        generator.pushLevel();
        generator.pushLevel();
        generator.popLevel();

        assertThat(generator.generate(), is(new Rank(1)));
    }
}