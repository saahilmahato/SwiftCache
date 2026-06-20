package org.saahil.policy.eviction;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;
import java.util.logging.Level;

public class FIFOEvictionPolicy<K> implements EvictionPolicy<K> {

    private final Queue<K> queue = new ConcurrentLinkedQueue<>();
    private static final Logger LOGGER = Logger.getLogger(FIFOEvictionPolicy.class.getName());

    @Override
    public void onKeyAccess(K key) {
        queue.offer(key);
        LOGGER.log(Level.INFO, "[FIFOEvictionPolicy] onKeyAccess: {0}", key);
    }

    @Override
    public K evictKey() {
        K k = queue.poll();
        if (k != null) {
            LOGGER.log(Level.INFO, "[FIFOEvictionPolicy] evictKey -> {0}", k);
        } else {
            LOGGER.log(Level.INFO, "[FIFOEvictionPolicy] evictKey -> null");
        }
        return k;
    }
}
