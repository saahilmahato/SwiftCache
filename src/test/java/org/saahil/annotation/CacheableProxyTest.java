package org.saahil.annotation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.saahil.CacheConfig;
import org.saahil.SwiftCache;
import org.saahil.policy.eviction.LRUEvictionPolicy;
import org.saahil.policy.write.WriteThroughPolicy;
import org.saahil.strategy.read.ReadOnlyStrategy;
import org.saahil.testutil.H2PersistentStore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CacheableProxyTest {

    @BeforeEach
    void resetCounters() {
        UserServiceImpl.COUNTER.set(0);
        UserServiceImpl.PROFILE_COUNTER.set(0);
        UserServiceImpl.NULL_COUNTER.set(0);
        UserServiceImpl.TTL_COUNTER.set(0);
    }

    @Test
    void shouldCacheAnnotatedMethodResult() {
        LocalCacheStore store = new LocalCacheStore();
        store.register("users", createCache());
        UserService service = CacheableProxy.createProxy(new UserServiceImpl(), store);

        assertEquals("user-1-1", service.findById(1L));
        assertEquals("user-1-1", service.findById(1L));
        assertEquals(1, UserServiceImpl.COUNTER.get());
    }

    @Test
    void shouldRespectCustomCacheKeyExpression() {
        LocalCacheStore store = new LocalCacheStore();
        store.register("users", createCache());
        UserService service = CacheableProxy.createProxy(new UserServiceImpl(), store);

        assertEquals("profile:10:1", service.profile(10L));
        assertEquals("profile:10:1", service.profile(10L));
        assertEquals("profile:11:2", service.profile(11L));
    }

    @Test
    void shouldCacheNullWhenEnabled() {
        LocalCacheStore store = new LocalCacheStore();
        store.register("users", createCache());
        UserService service = CacheableProxy.createProxy(new UserServiceImpl(), store);

        assertNull(service.maybeNull("x"));
        assertNull(service.maybeNull("x"));
        assertEquals(2, UserServiceImpl.NULL_COUNTER.get());
    }

    @Test
    void shouldExpireEntryBasedOnTtl() throws InterruptedException {
        LocalCacheStore store = new LocalCacheStore();
        store.register("users", createCache());
        UserService service = CacheableProxy.createProxy(new UserServiceImpl(), store);

        assertEquals("ttl-1-1", service.ttlValue(1L));
        Thread.sleep(5);
        assertEquals("ttl-1-2", service.ttlValue(1L));
    }

    private static SwiftCache<String, Object> createCache() {
        H2PersistentStore<String, Object> persistentStore = new H2PersistentStore<>();
        CacheConfig<String, Object> config = new CacheConfig<>(
                100,
                new ReadOnlyStrategy<>(),
                new WriteThroughPolicy<>(),
                new LRUEvictionPolicy<>(),
                persistentStore
        );
        return new SwiftCache<>(config);
    }

    private static final class LocalCacheStore implements CacheStore {
        private final Map<String, SwiftCache<String, Object>> cacheMap = new ConcurrentHashMap<>();

        void register(String name, SwiftCache<String, Object> cache) {
            cacheMap.put(name, cache);
        }

        @Override
        public SwiftCache<String, Object> getCache(String name) {
            return cacheMap.get(name);
        }
    }

    interface UserService {
        @Cacheable(name = "users")
        String findById(Long id);

        @Cacheable(name = "users", key = "profile:#p0")
        String profile(Long id);

        @Cacheable(name = "users", cacheNullValues = true)
        String maybeNull(String key);

        @Cacheable(name = "users", ttl = 1, unit = TimeUnit.MILLISECONDS)
        String ttlValue(Long id);
    }

    static class UserServiceImpl implements UserService {
        private static final AtomicInteger COUNTER = new AtomicInteger(0);
        private static final AtomicInteger PROFILE_COUNTER = new AtomicInteger(0);
        private static final AtomicInteger NULL_COUNTER = new AtomicInteger(0);
        private static final AtomicInteger TTL_COUNTER = new AtomicInteger(0);

        @Override
        public String findById(Long id) {
            return "user-" + id + "-" + COUNTER.incrementAndGet();
        }

        @Override
        public String profile(Long id) {
            return "profile:" + id + ":" + PROFILE_COUNTER.incrementAndGet();
        }

        @Override
        public String maybeNull(String key) {
            NULL_COUNTER.incrementAndGet();
            return null;
        }

        @Override
        public String ttlValue(Long id) {
            return "ttl-" + id + "-" + TTL_COUNTER.incrementAndGet();
        }
    }
}
