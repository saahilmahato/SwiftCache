package org.saahil;

import org.junit.jupiter.api.Test;
import org.saahil.policy.eviction.FIFOEvictionPolicy;
import org.saahil.policy.write.WriteThroughPolicy;
import org.saahil.strategy.read.ReadOnlyStrategy;
import org.saahil.testutil.H2PersistentStore;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SwiftCacheTest {

    @Test
    void shouldEvictWhenMaxSizeReached() {
        H2PersistentStore<String, Object> store = new H2PersistentStore<>();
        CacheConfig<String, Object> config = new CacheConfig<>(
                2,
                new ReadOnlyStrategy<>(),
                new WriteThroughPolicy<>(),
                new FIFOEvictionPolicy<>(),
                store
        );
        SwiftCache<String, Object> cache = new SwiftCache<>(config);

        cache.put("k1", "v1", -1);
        cache.put("k2", "v2", -1);
        cache.put("k3", "v3", -1);

        assertNull(cache.get("k1"));
        assertEquals("v2", cache.get("k2"));
        assertEquals("v3", cache.get("k3"));
        assertEquals(1, cache.getStats().getEvictions());
    }

    @Test
    void shouldRemoveExpiredEntriesWhenCleanupRuns() throws Exception {
        H2PersistentStore<String, Object> store = new H2PersistentStore<>();
        CacheConfig<String, Object> config = new CacheConfig<>(
                10,
                new ReadOnlyStrategy<>(),
                new WriteThroughPolicy<>(),
                new FIFOEvictionPolicy<>(),
                store
        );
        SwiftCache<String, Object> cache = new SwiftCache<>(config);

        cache.put("shortLived", "v", 1_000_000);
        Thread.sleep(5);

        Method cleanupMethod = SwiftCache.class.getDeclaredMethod("cleanupExpiredEntries");
        cleanupMethod.setAccessible(true);
        cleanupMethod.invoke(cache);

        assertNull(cache.get("shortLived"));
    }
}
