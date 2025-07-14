package org.saahil;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SwiftCache<K, V> {
    private final ConcurrentHashMap<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();
    private final CacheStats stats;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public SwiftCache() {
        this.stats = new CacheStats();
    }
}