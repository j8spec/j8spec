package j8spec;

import org.junit.After;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

public class RandomOrderSeedProviderTest {

    @After
    public void resetSeed() {
        System.setProperty("j8spec.seed", "");
        RandomOrderSeedProvider.reset();
    }

    @Test
    public void reads_the_seed_from_system_property_when_available() {
        System.setProperty("j8spec.seed", "1234");

        assertThat(RandomOrderSeedProvider.seed(), is(1234L));
    }

    @Test
    public void generates_a_new_global_seed() {
        assertThat(RandomOrderSeedProvider.seed(), is(RandomOrderSeedProvider.seed()));
    }

    @Test
    public void generates_a_new_seed_once_the_previous_is_removed() {
        Long seed1 = RandomOrderSeedProvider.seed();
        RandomOrderSeedProvider.reset();
        Long seed2 = RandomOrderSeedProvider.seed();

        assertThat(seed1, is(not(seed2)));
    }

    @Test(expected = IllegalSeedPropertyException.class)
    public void indicates_the_system_property_has_an_illegal_value() {
        System.setProperty("j8spec.seed", "illegal value");

        RandomOrderSeedProvider.seed();
    }
}