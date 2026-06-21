package org.saahil.policy.eviction;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RandomEvictionPolicy<K> implements EvictionPolicy<K> {

    private static final Logger LOGGER = Logger.getLogger(RandomEvictionPolicy.class.getName());

    private final Set<K> keys = ConcurrentHashMap.newKeySet();

    @Override
    public void onKeyAccess(K key) {
        keys.add(key);
        LOGGER.log(Level.INFO, "[RandomEvictionPolicy] onKeyAccess: {0}", key);
    }

    @Override
    public K evictKey() {
        if (keys.isEmpty()) {
            LOGGER.log(Level.INFO, "[RandomEvictionPolicy] evictKey -> null (empty)");
            return null;
        }

        ArrayList<K> snapshot = new ArrayList<>(keys);
        int randomIndex = ThreadLocalRandom.current().nextInt(snapshot.size());
        K evicted = snapshot.get(randomIndex);
        keys.remove(evicted);

        LOGGER.log(Level.INFO, "[RandomEvictionPolicy] evictKey -> {0}", evicted);
        return evicted;
    }
}
