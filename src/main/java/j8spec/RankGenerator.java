package j8spec;

import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Logger;

final class RankGenerator {

    private static final Logger LOG = Logger.getLogger("j8spec.RankGenerator");

    private interface Strategy {
        Integer initialValue();
        Integer nextValue(Integer currentValue);
    }

    private static final class IncrementalStrategy implements Strategy {
        static final IncrementalStrategy INSTANCE = new IncrementalStrategy();

        private IncrementalStrategy() {}

        @Override
        public Integer initialValue() {
            return 0;
        }

        @Override
        public Integer nextValue(Integer currentValue) {
            return currentValue + 1;
        }
    }

    private static final class RandomStrategy implements Strategy {
        private final Random random;

        RandomStrategy(Long seed) {
            this.random = new Random(seed);
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
                pushLevel(IncrementalStrategy.INSTANCE);
                break;
            case RANDOM:
                Long seed = config.seed();
                if (seed == null) {
                    seed = RandomOrderSeedProvider.seed();
                } else {
                    LOG.info("overriding random order seed for '" + config.description() + "': " + seed);
                }
                pushLevel(new RandomStrategy(seed));
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

    private void next() {
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

        next();

        return new Rank(values);
    }
}
