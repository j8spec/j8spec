package j8spec;

import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;

final class RankGenerator {

    private interface Strategy {
        Strategy DEFINED = new Strategy() {
            @Override
            public Integer initialValue() {
                return 0;
            }

            @Override
            public Integer nextValue(Integer currentValue) {
                return currentValue + 1;
            }
        };

        Integer initialValue();
        Integer nextValue(Integer currentValue);
    }

    private final Deque<Strategy> strategies = new LinkedList<>();
    private final Deque<Integer> ranks = new LinkedList<>();

    void pushLevel() {
        strategies.push(Strategy.DEFINED);
        ranks.push(strategies.peek().initialValue());
    }

    void next() {
        ranks.push(strategies.peek().nextValue(ranks.pop()));
    }

    void popLevel() {
        strategies.pop();
        ranks.pop();

        if (!ranks.isEmpty()) {
            next();
        }
    }

    Rank generate() {
        LinkedList<Integer> values = new LinkedList<>(ranks);
        Collections.reverse(values);
        return new Rank(values);
    }
}
