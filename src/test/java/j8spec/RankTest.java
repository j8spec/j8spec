package j8spec;

import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

public class RankTest {

    private final Set<Rank> ranks = new HashSet<>();

    @Test
    public void can_be_found_in_a_collection_set_when_it_has_same_values_in_the_same_order() {
        ranks.add(new Rank(0, 1, 2));

        assertThat(ranks, hasItem(new Rank(0, 1, 2)));
    }

    @Test
    public void cannot_be_found_in_a_collection_set_when_it_has_different_values() {
        ranks.add(new Rank(0, 1, 1));

        assertThat(ranks, not(hasItem(new Rank(0, 1, 2))));
    }

    @Test
    public void cannot_be_found_in_a_collection_set_when_it_has_same_values_but_in_different_order() {
        ranks.add(new Rank(0, 2, 1));

        assertThat(ranks, not(hasItem(new Rank(0, 1, 2))));
    }

    @Test
    public void is_sortable_by_value() {
        List<Rank> rankList = new LinkedList<>();

        rankList.add(new Rank(2));
        rankList.add(new Rank(0));
        rankList.add(new Rank(1, 1));
        rankList.add(new Rank(1, 0));
        rankList.add(new Rank(1, 2, 0));
        rankList.add(new Rank(1, 1, 0));
        rankList.add(new Rank(3));
        rankList.add(new Rank(2, 0));
        rankList.add(new Rank(1, 0, 0));

        Collections.sort(rankList);

        assertThat(rankList, is(asList(
            new Rank(0),
            new Rank(1, 0),
            new Rank(1, 0, 0),
            new Rank(1, 1),
            new Rank(1, 1, 0),
            new Rank(1, 2, 0),
            new Rank(2),
            new Rank(2, 0),
            new Rank(3)
        )));
    }

    @Test
    public void is_sortable_by_length_when_values_are_the_same() {
        List<Rank> rankList = new LinkedList<>();

        rankList.add(new Rank(0, 0, 0));
        rankList.add(new Rank(0, 0));
        rankList.add(new Rank(0));

        Collections.sort(rankList);

        assertThat(rankList, is(asList(
            new Rank(0),
            new Rank(0, 0),
            new Rank(0, 0, 0)
        )));
    }
}
