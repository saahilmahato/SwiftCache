package org.saahil.strategy.read;

import org.junit.jupiter.api.Test;
import org.saahil.CacheConfig;
import org.saahil.SwiftCache;
import org.saahil.policy.eviction.FIFOEvictionPolicy;
import org.saahil.policy.write.WriteThroughPolicy;
import org.saahil.testutil.H2PersistentStore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ReadStrategiesTest {

    @Test
    void readOnlyShouldReturnNullOnMissAndValueOnHit() {
        H2PersistentStore<String, Object> store = new H2PersistentStore<>();
        SwiftCache<String, Object> cache = new SwiftCache<>(new CacheConfig<>(
                10,
                new ReadOnlyStrategy<>(),
                new WriteThroughPolicy<>(),
                new FIFOEvictionPolicy<>(),
                store
        ));

        assertNull(cache.get("missing"));
        cache.put("k1", "v1", -1);
        assertEquals("v1", cache.get("k1"));
        assertEquals(1, cache.getStats().getHits());
        assertEquals(1, cache.getStats().getMisses());
    }

    @Test
    void readThroughShouldLoadFromStoreOnMissAndPopulateCache() {
        H2PersistentStore<String, Object> store = new H2PersistentStore<>();
        store.save("k1", "db-v1");
        SwiftCache<String, Object> cache = new SwiftCache<>(new CacheConfig<>(
                10,
                new ReadThroughStrategy<>(store),
                new WriteThroughPolicy<>(),
                new FIFOEvictionPolicy<>(),
                store
        ));

        assertEquals("db-v1", cache.get("k1"));
        assertEquals("db-v1", cache.get("k1"));
        assertEquals(1, cache.getStats().getHits());
        assertEquals(1, cache.getStats().getMisses());
    }

    @Test
    void readThroughWithTtlShouldExpireAndReloadFromStore() throws InterruptedException {
        H2PersistentStore<String, Object> store = new H2PersistentStore<>();
        store.save("k1", "db-v1");
        SwiftCache<String, Object> cache = new SwiftCache<>(new CacheConfig<>(
                10,
                new ReadThroughWithTTLStrategy<>(store, 1_000_000),
                new WriteThroughPolicy<>(),
                new FIFOEvictionPolicy<>(),
                store
        ));

        assertEquals("db-v1", cache.get("k1"));
        Thread.sleep(5);
        assertEquals("db-v1", cache.get("k1"));
        assertEquals(0, cache.getStats().getHits());
        assertEquals(2, cache.getStats().getMisses());
    }
}
