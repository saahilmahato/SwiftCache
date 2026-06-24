package org.saahil.example;

import org.junit.jupiter.api.Test;
import org.saahil.CacheConfig;
import org.saahil.SwiftCache;
import org.saahil.annotation.CacheStore;
import org.saahil.annotation.CacheableProxy;
import org.saahil.policy.eviction.LFUEvictionPolicy;
import org.saahil.policy.eviction.LRUEvictionPolicy;
import org.saahil.policy.write.WriteAroundPolicy;
import org.saahil.policy.write.WriteThroughPolicy;
import org.saahil.strategy.read.ReadOnlyStrategy;
import org.saahil.strategy.read.ReadThroughStrategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PolicyDemonstrationTest {

    @Test
    void testWriteThroughPolicyWritesToBothCacheAndDB() {
        H2InMemoryStore<String, String> store = new H2InMemoryStore<>();

        CacheConfig<String, String> config = new CacheConfig<>(
                10,
                new ReadOnlyStrategy<>(),
                new WriteThroughPolicy<>(),
                new LRUEvictionPolicy<>(),
                store
        );

        SwiftCache<String, String> cache = new SwiftCache<>(config);

        cache.put("user:1", "John Doe", -1);

        assertEquals("John Doe", cache.get("user:1"));
        assertEquals("John Doe", store.load("user:1"));
    }

    @Test
    void testWriteAroundPolicyBypassesCache() {
        H2InMemoryStore<String, String> store = new H2InMemoryStore<>();

        CacheConfig<String, String> config = new CacheConfig<>(
                10,
                new ReadOnlyStrategy<>(),
                new WriteAroundPolicy<>(),
                new LRUEvictionPolicy<>(),
                store
        );

        SwiftCache<String, String> cache = new SwiftCache<>(config);

        cache.put("product:1", "Widget", -1);

        assertNull(cache.get("product:1"));
        assertEquals("Widget", store.load("product:1"));
    }

    @Test
    void testReadThroughStrategyLoadsFromDBOnMiss() {
        H2InMemoryStore<String, String> store = new H2InMemoryStore<>();
        store.save("user:5", "Alice Smith");

        CacheConfig<String, String> config = new CacheConfig<>(
                10,
                new ReadThroughStrategy<>(store),
                new WriteThroughPolicy<>(),
                new LRUEvictionPolicy<>(),
                store
        );

        SwiftCache<String, String> cache = new SwiftCache<>(config);

        assertEquals("Alice Smith", cache.get("user:5"));
        assertEquals(1, cache.getStats().getHits() + cache.getStats().getMisses());
    }

    @Test
    void testReadOnlyStrategyNeverLoadsFromDB() {
        H2InMemoryStore<String, String> store = new H2InMemoryStore<>();
        store.save("user:10", "Bob Wilson");

        CacheConfig<String, String> config = new CacheConfig<>(
                10,
                new ReadOnlyStrategy<>(),
                new WriteThroughPolicy<>(),
                new LRUEvictionPolicy<>(),
                store
        );

        SwiftCache<String, String> cache = new SwiftCache<>(config);

        assertNull(cache.get("user:10"));
        assertEquals(1, cache.getStats().getMisses());
    }

    @Test
    void testLRUEvictionPolicy() {
        H2InMemoryStore<String, Integer> store = new H2InMemoryStore<>();

        CacheConfig<String, Integer> config = new CacheConfig<>(
                3,
                new ReadOnlyStrategy<>(),
                new WriteThroughPolicy<>(),
                new LRUEvictionPolicy<>(),
                store
        );

        SwiftCache<String, Integer> cache = new SwiftCache<>(config);

        cache.put("key1", 1, -1);
        cache.put("key2", 2, -1);
        cache.put("key3", 3, -1);

        cache.get("key1");

        cache.put("key4", 4, -1);

        assertEquals(1, cache.getStats().getEvictions());
    }

    @Test
    void testLFUEvictionPolicy() {
        H2InMemoryStore<String, Integer> store = new H2InMemoryStore<>();

        CacheConfig<String, Integer> config = new CacheConfig<>(
                3,
                new ReadOnlyStrategy<>(),
                new WriteThroughPolicy<>(),
                new LFUEvictionPolicy<>(),
                store
        );

        SwiftCache<String, Integer> cache = new SwiftCache<>(config);

        cache.put("key1", 1, -1);
        cache.put("key2", 2, -1);
        cache.put("key3", 3, -1);

        cache.get("key1");
        cache.get("key1");
        cache.get("key2");

        cache.put("key4", 4, -1);

        assertEquals(1, cache.getStats().getEvictions());
    }

    @Test
    void testCachingWithProxies() {
        CacheStore cacheStore = new ApplicationCacheStore();
        UserService userService = new UserServiceImpl();

        UserService cachedUserService = CacheableProxy.createProxy(userService, cacheStore);

        cachedUserService.getUserById(1L);
        cachedUserService.getUserById(1L);
        cachedUserService.getUserById(2L);
        cachedUserService.getUserByName("John Doe");
        cachedUserService.getUserByName("John Doe");
        cachedUserService.getAllUsers();
        cachedUserService.getAllUsers();

        SwiftCache<String, Object> usersCache = cacheStore.getCache("users");
        assertEquals(3, usersCache.getStats().getHits(), "Expected 3 cache hits");
        assertEquals(4, usersCache.getStats().getMisses(), "Expected 4 cache misses");
    }
}


