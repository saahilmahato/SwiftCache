package org.saahil;

import java.util.concurrent.ConcurrentHashMap;

public class SwiftCache<K, V> {
    private final ConcurrentHashMap<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();
    private final CacheStats stats;

    public SwiftCache() {
        this.stats = new CacheStats();
    }

    public void printStats() {
        System.out.println(this.stats);
    }

    private CacheEntry<V> getEntry(K key) {
        this.stats.recordHit();
        return cache.get(key);
    }

    private void putEntry(K key, V value, long ttlNanos) {
        cache.put(key, new CacheEntry<V>(value, ttlNanos));
        this.stats.getPuts();
    }

    private void removeEntry(K key) {
        cache.remove(key);
        this.stats.recordEviction();
    }
}
