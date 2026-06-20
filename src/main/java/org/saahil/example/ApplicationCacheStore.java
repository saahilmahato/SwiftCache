package org.saahil.example;

import org.saahil.CacheConfig;
import org.saahil.SwiftCache;
import org.saahil.annotation.CacheStore;
import org.saahil.policy.eviction.FIFOEvictionPolicy;
import org.saahil.policy.write.WriteThroughPolicy;
import org.saahil.strategy.read.CacheAsideReadStrategy;

import java.util.concurrent.ConcurrentHashMap;

public class ApplicationCacheStore implements CacheStore {

    private final ConcurrentHashMap<String, SwiftCache<String, Object>> caches;

    public ApplicationCacheStore() {
        this.caches = new ConcurrentHashMap<>();
        initializeCaches();
    }

    private void initializeCaches() {
        // Configure the "users" cache
        CacheConfig<String, Object> usersCacheConfig = new CacheConfig<>(
                100,  // max size
                new CacheAsideReadStrategy<>(),  // read strategy
                new WriteThroughPolicy<>(),      // write policy
                new FIFOEvictionPolicy<>()       // eviction policy
        );

        SwiftCache<String, Object> usersCache = new SwiftCache<>(usersCacheConfig);
        caches.put("users", usersCache);
    }

    @Override
    public SwiftCache<String, Object> getCache(String name) {
        return caches.get(name);
    }
}

