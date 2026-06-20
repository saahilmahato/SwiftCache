package org.saahil.policy.eviction;

import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import java.util.logging.Level;

public class LFUEvictionPolicy<K> implements EvictionPolicy<K> {

    private static final Logger LOGGER = Logger.getLogger(LFUEvictionPolicy.class.getName());
    private final ConcurrentHashMap<K, AtomicLong> frequencies = new ConcurrentHashMap<>();

    @Override
    public void onKeyAccess(K key) {
        frequencies.computeIfAbsent(key, _ -> new AtomicLong(0)).incrementAndGet();
        LOGGER.log(Level.INFO, "[LFUEvictionPolicy] onKeyAccess: {0} with frequency {1}",
            new Object[]{key, frequencies.get(key).get()});
    }

    @Override
    public K evictKey() {
        if (frequencies.isEmpty()) {
            LOGGER.log(Level.INFO, "[LFUEvictionPolicy] evictKey -> null (empty)");
            return null;
        }

        K lfuKey = frequencies.entrySet().stream()
                .min(Comparator.comparingLong(e -> e.getValue().get()))
                .map(e -> {
                    frequencies.remove(e.getKey());
                    return e.getKey();
                })
                .orElse(null);

        if (lfuKey != null) {
            LOGGER.log(Level.INFO, "[LFUEvictionPolicy] evictKey -> {0}", lfuKey);
        } else {
            LOGGER.log(Level.INFO, "[LFUEvictionPolicy] evictKey -> null");
        }
        return lfuKey;
    }
}


