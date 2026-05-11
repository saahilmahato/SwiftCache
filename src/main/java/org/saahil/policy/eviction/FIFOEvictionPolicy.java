package org.saahil.policy.eviction;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FIFOEvictionPolicy<K> implements EvictionPolicy<K> {

    private final Queue<K> queue = new ConcurrentLinkedQueue<>();

    @Override
    public void onKeyAccess(K key) {
        queue.offer(key);
    }

    @Override
    public K evictKey() {
        return queue.poll();
    }
}
