package org.saahil.example;

import org.junit.jupiter.api.Test;
import org.saahil.CacheConfig;
import org.saahil.SwiftCache;
import org.saahil.policy.eviction.RandomEvictionPolicy;
import org.saahil.policy.write.WriteInvalidatePolicy;
import org.saahil.store.PersistentStore;
import org.saahil.strategy.read.ReadOnlyStrategy;
import org.saahil.strategy.read.ReadThroughWithTTLStrategy;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdditionalStrategiesAndPoliciesTest {

    @Test
    void testReadThroughWithTTLStrategyReloadsAfterExpiry() throws InterruptedException {
        CountingStore<String, String> store = new CountingStore<>();
        store.save("item:1", "initial");

        CacheConfig<String, String> config = new CacheConfig<>(
                10,
                new ReadThroughWithTTLStrategy<>(store, Duration.ofMillis(40).toNanos()),
                new WriteInvalidatePolicy<>(),
                new RandomEvictionPolicy<>(),
                store
        );

        SwiftCache<String, String> cache = new SwiftCache<>(config);

        assertEquals("initial", cache.get("item:1"));
        assertEquals(1, store.getLoadCount("item:1"));

        assertEquals("initial", cache.get("item:1"));
        assertEquals(1, store.getLoadCount("item:1"));

        Thread.sleep(60);

        assertEquals("initial", cache.get("item:1"));
        assertEquals(2, store.getLoadCount("item:1"));
    }

    @Test
    void testWriteInvalidatePolicyUpdatesStoreAndClearsCache() {
        H2InMemoryStore<String, String> store = new H2InMemoryStore<>();

        CacheConfig<String, String> config = new CacheConfig<>(
                10,
                new ReadOnlyStrategy<>(),
                new WriteInvalidatePolicy<>(),
                new RandomEvictionPolicy<>(),
                store
        );

        SwiftCache<String, String> cache = new SwiftCache<>(config);
        cache.putCacheEntry("user:1", "stale", -1);

        cache.put("user:1", "fresh", -1);

        assertEquals("fresh", store.load("user:1"));
        assertNull(cache.getCacheEntry("user:1"));
        assertNull(cache.get("user:1"));
    }

    @Test
    void testRandomEvictionPolicyEvictsARealKey() {
        H2InMemoryStore<String, Integer> store = new H2InMemoryStore<>();

        CacheConfig<String, Integer> config = new CacheConfig<>(
                3,
                new ReadOnlyStrategy<>(),
                new org.saahil.policy.write.WriteThroughPolicy<>(),
                new RandomEvictionPolicy<>(),
                store
        );

        SwiftCache<String, Integer> cache = new SwiftCache<>(config);

        cache.put("a", 1, -1);
        cache.put("b", 2, -1);
        cache.put("c", 3, -1);
        cache.put("d", 4, -1);

        assertEquals(1, cache.getStats().getEvictions());

        int presentEntries = 0;
        if (cache.getCacheEntry("a") != null) presentEntries++;
        if (cache.getCacheEntry("b") != null) presentEntries++;
        if (cache.getCacheEntry("c") != null) presentEntries++;
        if (cache.getCacheEntry("d") != null) presentEntries++;

        assertEquals(3, presentEntries);
        assertNotNull(cache.getCacheEntry("d"));
        assertTrue(
                cache.getCacheEntry("a") == null ||
                        cache.getCacheEntry("b") == null ||
                        cache.getCacheEntry("c") == null,
                "One of the first three keys should be evicted"
        );
    }

    private static class CountingStore<K, V> implements PersistentStore<K, V> {
        private final H2InMemoryStore<K, V> delegate = new H2InMemoryStore<>();
        private final ConcurrentHashMap<K, AtomicInteger> loads = new ConcurrentHashMap<>();

        @Override
        public void save(K key, V value) {
            delegate.save(key, value);
        }

        @Override
        public V load(K key) {
            loads.computeIfAbsent(key, _ -> new AtomicInteger()).incrementAndGet();
            return delegate.load(key);
        }

        @Override
        public void delete(K key) {
            delegate.delete(key);
        }

        @Override
        public boolean exists(K key) {
            return delegate.exists(key);
        }

        int getLoadCount(K key) {
            AtomicInteger count = loads.get(key);
            return count == null ? 0 : count.get();
        }
    }
}
