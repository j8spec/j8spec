package j8spec;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

final class Rank implements Comparable<Rank> {
    private final List<Integer> values;

    Rank(List<Integer> values) {
        this.values = unmodifiableList(values);
    }

    Rank(Integer ... values) {
        this(asList(values));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rank rank = (Rank) o;

        return values.equals(rank.values);

    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }

    @Override
    public String toString() {
        return values.toString();
    }

    @Override
    public int compareTo(Rank rank) {
        int size = Math.min(values.size(), rank.values.size());

        for (int i = 0; i < size; i++) {
            int result = values.get(i) - rank.values.get(i);
            if (result != 0) {
                return result;
            }
        }

        return values.size() - rank.values.size();
    }
}
