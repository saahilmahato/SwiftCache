package org.saahil;

import java.util.concurrent.ConcurrentHashMap;

public class SwiftCache<K, V> {
    private final ConcurrentHashMap<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();
    private final CacheStats stats;

    public SwiftCache() {
        this.stats = new CacheStats();
    }
}
