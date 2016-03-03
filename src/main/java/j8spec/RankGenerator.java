package j8spec;

import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;

final class RankGenerator {

    interface Strategy {
        Integer initialValue();
        Integer nextValue(Integer currentValue);
    }

    static final class Incremental implements Strategy {
        static final Incremental INSTANCE = new Incremental();

        private Incremental() {}

        @Override
        public Integer initialValue() {
            return 0;
        }

        @Override
        public Integer nextValue(Integer currentValue) {
            return currentValue + 1;
        }
    }

    private final Deque<Strategy> strategies = new LinkedList<>();
    private final Deque<Integer> ranks = new LinkedList<>();

    void pushLevel(Strategy strategy) {
        strategies.push(strategy);
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
