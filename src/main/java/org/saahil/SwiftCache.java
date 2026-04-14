package org.saahil;

import org.saahil.read.ReadStrategy;

import java.util.concurrent.ConcurrentHashMap;

public class SwiftCache<K, V> {
    private final ConcurrentHashMap<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();
    private final CacheStats stats;
    private final ReadStrategy<K, V> readStrategy;

    public SwiftCache(ReadStrategy<K, V> readStrategy) {
        this.stats = new CacheStats();
        this.readStrategy = readStrategy;
    }

    public CacheStats getStats() {
        return this.stats;
    }

    public void printStats() {
        System.out.println(this.stats);
    }

    public V get(K key) {
        return readStrategy.get(key, this);
    }

    public CacheEntry<V> getEntry(K key) {
        return this.cache.get(key);
    }

    public void removeEntry(K key) {
        this.cache.remove(key);
    }
}
