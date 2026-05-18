package org.saahil;

import org.saahil.policy.eviction.EvictionPolicy;
import org.saahil.policy.write.WritePolicy;
import org.saahil.stats.CacheStats;
import org.saahil.strategy.read.ReadStrategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SwiftCache<K, V> {

    private final ConcurrentHashMap<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();

    private final CacheStats stats = new CacheStats();

    private final int maxSize;

    private final ReadStrategy<K, V> readStrategy;

    private final WritePolicy<K, V> writePolicy;

    private final EvictionPolicy<K> evictionPolicy;

    private final ScheduledExecutorService cleaner = Executors.newSingleThreadScheduledExecutor();

    public SwiftCache(CacheConfig<K, V> config) {

        this.maxSize = config.maxSize();
        this.readStrategy = config.readStrategy();
        this.writePolicy = config.writePolicy();
        this.evictionPolicy = config.evictionPolicy();
        startCleanupTask();
    }

    public V get(K key) {

        evictionPolicy.onKeyAccess(key);

        return readStrategy.read(key, this);
    }

    public void put(K key, V value, long ttlNanos) {

        if (cache.size() >= maxSize) {
            K evictKey = evictionPolicy.evictKey();
            if (evictKey != null) {
                cache.remove(evictKey);
                stats.recordEviction();
            }
        }

        writePolicy.write(key, value, ttlNanos, this);

        evictionPolicy.onKeyAccess(key);
    }

    public void remove(K key) {
        cache.remove(key);
    }

    public CacheStats getStats() {
        return stats;
    }

    public void printStats() {
        System.out.println(stats);
    }

    public void shutdown() {
        cleaner.shutdown();
    }

    public CacheEntry<V> getInternalEntry(K key) {
        return cache.get(key);
    }

    public void putInternal(K key, V value, long ttlNanos) {
        cache.put(key, new CacheEntry<>(value, ttlNanos));
    }

    public void removeInternal(K key) {
        cache.remove(key);
    }

    private void startCleanupTask() {
        cleaner.scheduleAtFixedRate(this::cleanupExpiredEntries, 1, 1, TimeUnit.MINUTES);
    }

    private void cleanupExpiredEntries() {

        for (Map.Entry<K, CacheEntry<V>> entry : cache.entrySet()) {
            if (entry.getValue().isExpired()) {
                cache.remove(entry.getKey());
            }
        }
    }
}