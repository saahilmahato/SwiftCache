package org.saahil.example;

import org.saahil.CacheConfig;
import org.saahil.SwiftCache;
import org.saahil.annotation.CacheStore;
import org.saahil.policy.eviction.FIFOEvictionPolicy;
import org.saahil.policy.write.WriteThroughPolicy;
import org.saahil.strategy.read.ReadOnlyStrategy;

import java.util.concurrent.ConcurrentHashMap;

public class ApplicationCacheStore implements CacheStore {

    private final ConcurrentHashMap<String, SwiftCache<String, Object>> caches;

    public ApplicationCacheStore() {
        this.caches = new ConcurrentHashMap<>();
        initializeCaches();
    }

    private void initializeCaches() {
        H2InMemoryStore<String, Object> store = new H2InMemoryStore<>();

        CacheConfig<String, Object> usersCacheConfig = new CacheConfig<>(
                100,
                new ReadOnlyStrategy<>(),
                new WriteThroughPolicy<>(),
                new FIFOEvictionPolicy<>(),
                store
        );

        SwiftCache<String, Object> usersCache = new SwiftCache<>(usersCacheConfig);
        caches.put("users", usersCache);
    }

    @Override
    public SwiftCache<String, Object> getCache(String name) {
        return caches.get(name);
    }
}

