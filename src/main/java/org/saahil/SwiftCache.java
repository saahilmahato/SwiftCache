package org.saahil;

import java.util.concurrent.ConcurrentHashMap;

public class SwiftCache<K, V> {
    private final ConcurrentHashMap<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();
    private final CacheStats stats;

    public SwiftCache() {
        this.stats = new CacheStats();
    }

    public CacheStats getStats() {
        return this.stats;
    }

    public void printStats() {
        System.out.println(this.stats);
    }

    public CacheEntry<V> getEntry(K key) {
        return this.cache.get(key);
    }

    public void removeEntry(K key) {
        this.cache.remove(key);
    }

    public void putEntry(K key, V value, Long ttlNanos) {
        this.cache.put(key, new CacheEntry<V>(value, ttlNanos));
    }
}
