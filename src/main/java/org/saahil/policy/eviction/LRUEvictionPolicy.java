package org.saahil.policy.eviction;

import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import java.util.logging.Level;

public class LRUEvictionPolicy<K> implements EvictionPolicy<K> {

    private static final Logger LOGGER = Logger.getLogger(LRUEvictionPolicy.class.getName());
    private final ConcurrentHashMap<K, Long> accessTimes = new ConcurrentHashMap<>();
    private final AtomicLong currentTime = new AtomicLong(0);

    @Override
    public void onKeyAccess(K key) {
        long time = currentTime.incrementAndGet();
        accessTimes.put(key, time);
        LOGGER.log(Level.INFO, "[LRUEvictionPolicy] onKeyAccess: {0} at time {1}",
            new Object[]{key, time});
    }

    @Override
    public K evictKey() {
        if (accessTimes.isEmpty()) {
            LOGGER.log(Level.INFO, "[LRUEvictionPolicy] evictKey -> null (empty)");
            return null;
        }

        K lruKey = accessTimes.entrySet().stream()
                .min(Comparator.comparingLong(java.util.Map.Entry::getValue))
                .map(entry -> {
                    accessTimes.remove(entry.getKey());
                    return entry.getKey();
                })
                .orElse(null);

        if (lruKey != null) {
            LOGGER.log(Level.INFO, "[LRUEvictionPolicy] evictKey -> {0}", lruKey);
        } else {
            LOGGER.log(Level.INFO, "[LRUEvictionPolicy] evictKey -> null");
        }
        return lruKey;
    }
}

