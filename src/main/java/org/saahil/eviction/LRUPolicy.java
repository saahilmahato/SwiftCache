package org.saahil.eviction;

import java.util.Iterator;
import java.util.LinkedHashSet;

public class LRUPolicy<K> implements EvictionPolicy<K> {

    private final LinkedHashSet<K> order = new LinkedHashSet<>();

    @Override
    public void onPut(K key) {
        order.remove(key);
        order.add(key);
    }

    @Override
    public void onGet(K key) {
        order.remove(key);
        order.add(key);
    }

    @Override
    public K evictCandidate() {
        Iterator<K> it = order.iterator();
        if (!it.hasNext()) {
            return null;
        }
        K oldest = it.next();
        it.remove();

        return oldest;
    }
}
