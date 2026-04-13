package org.saahil.write;

import org.saahil.SwiftCache;

public interface WriteStrategy<K, V> {
    void put(K key, V value, long ttlNanos, SwiftCache<K, V> cache);
}
