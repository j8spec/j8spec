package j8spec;

import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;

final class RankGenerator {

    private interface Strategy {
        Integer initialValue();
        Integer nextValue(Integer currentValue);
    }

    private static final class Incremental implements Strategy {
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

    private static class Random implements Strategy {
        private final java.util.Random random;

        Random(Long seed) {
            if (seed == null) {
                this.random = new java.util.Random();
            } else {
                this.random = new java.util.Random(seed);
            }
        }

        @Override
        public Integer initialValue() {
            return random.nextInt();
        }

        @Override
        public Integer nextValue(Integer currentValue) {
            return random.nextInt();
        }
    }

    private final Deque<Strategy> strategies = new LinkedList<>();
    private final Deque<Integer> ranks = new LinkedList<>();

    void pushLevel(ExampleGroupConfiguration config) {
        switch (config.executionOrder()) {
            case DEFINED:
                pushLevel(Incremental.INSTANCE);
                break;
            case RANDOM:
                pushLevel(new Random(config.seed()));
                break;
            case DEFAULT:
                if (strategies.isEmpty()) {
                    throw new IllegalStateException();
                }

                pushLevel();
                break;
        }
    }

    private void pushLevel(Strategy strategy) {
        strategies.push(strategy);
        ranks.push(strategies.peek().initialValue());
    }

    private void pushLevel() {
        pushLevel(strategies.peek());
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
