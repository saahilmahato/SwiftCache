package org.saahil.write;

import org.saahil.SwiftCache;

public class SimpleWriteStrategy<K, V> implements WriteStrategy<K, V> {

    @Override
    public void put(K key, V value, long ttlNanos, SwiftCache<K, V> cache) {
        cache.putEntry(key, value, ttlNanos);
    }
}
