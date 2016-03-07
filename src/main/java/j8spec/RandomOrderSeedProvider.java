package j8spec;

import java.util.Random;
import java.util.logging.Logger;

final class RandomOrderSeedProvider {

    private static Logger LOG = Logger.getLogger("j8spec.RandomOrderSeedProvider");

    private static Long seed;

    static synchronized Long seed() {
        if (seed == null) {
            String seedFromProperty = System.getProperty("j8spec.seed", "");
            if ("".equals(seedFromProperty)) {
                seed = new Random().nextLong();
                LOG.info("random order seed (generated): " + seed);
            } else {
                try {
                    seed = new Long(seedFromProperty);
                    LOG.info("random order seed (from system property): " + seed);
                } catch (NumberFormatException e) {
                    throw new IllegalSeedPropertyException(e);
                }
            }
        }

        return seed;
    }

    static synchronized void reset() {
        seed = null;
    }

    private RandomOrderSeedProvider() {}
}
