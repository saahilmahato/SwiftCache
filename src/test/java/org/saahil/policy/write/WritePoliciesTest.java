package org.saahil.policy.write;

import org.junit.jupiter.api.Test;
import org.saahil.CacheConfig;
import org.saahil.SwiftCache;
import org.saahil.policy.eviction.FIFOEvictionPolicy;
import org.saahil.strategy.read.ReadOnlyStrategy;
import org.saahil.testutil.H2PersistentStore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WritePoliciesTest {

    @Test
    void writeThroughShouldWriteToStoreAndCache() {
        H2PersistentStore<String, Object> store = new H2PersistentStore<>();
        SwiftCache<String, Object> cache = new SwiftCache<>(new CacheConfig<>(
                10,
                new ReadOnlyStrategy<>(),
                new WriteThroughPolicy<>(),
                new FIFOEvictionPolicy<>(),
                store
        ));

        cache.put("k1", "v1", -1);

        assertTrue(store.exists("k1"));
        assertEquals("v1", cache.get("k1"));
    }

    @Test
    void writeAroundShouldWriteOnlyToStore() {
        H2PersistentStore<String, Object> store = new H2PersistentStore<>();
        SwiftCache<String, Object> cache = new SwiftCache<>(new CacheConfig<>(
                10,
                new ReadOnlyStrategy<>(),
                new WriteAroundPolicy<>(),
                new FIFOEvictionPolicy<>(),
                store
        ));

        cache.put("k1", "v1", -1);

        assertTrue(store.exists("k1"));
        assertNull(cache.get("k1"));
    }

    @Test
    void writeInvalidateShouldInvalidateCacheAfterStoreWrite() {
        H2PersistentStore<String, Object> store = new H2PersistentStore<>();
        SwiftCache<String, Object> cache = new SwiftCache<>(new CacheConfig<>(
                10,
                new ReadOnlyStrategy<>(),
                new WriteThroughPolicy<>(),
                new FIFOEvictionPolicy<>(),
                store
        ));
        cache.put("k1", "old", -1);

        SwiftCache<String, Object> invalidateCache = new SwiftCache<>(new CacheConfig<>(
                10,
                new ReadOnlyStrategy<>(),
                new WriteInvalidatePolicy<>(),
                new FIFOEvictionPolicy<>(),
                store
        ));
        invalidateCache.putCacheEntry("k1", "old", -1);

        invalidateCache.put("k1", "new", -1);

        assertTrue(store.exists("k1"));
        assertEquals("new", store.load("k1"));
        assertNull(invalidateCache.get("k1"));
    }

    @Test
    void writeBackShouldPersistWithDelay() throws InterruptedException {
        H2PersistentStore<String, Object> store = new H2PersistentStore<>();
        SwiftCache<String, Object> cache = new SwiftCache<>(new CacheConfig<>(
                10,
                new ReadOnlyStrategy<>(),
                new WriteBackPolicy<>(50),
                new FIFOEvictionPolicy<>(),
                store
        ));

        cache.put("k1", "v1", -1);

        assertFalse(store.exists("k1"));
        assertEquals("v1", cache.get("k1"));

        long deadline = System.currentTimeMillis() + 1_000;
        while (!store.exists("k1") && System.currentTimeMillis() < deadline) {
            Thread.sleep(20);
        }

        assertTrue(store.exists("k1"));
        assertEquals("v1", store.load("k1"));
    }
}
