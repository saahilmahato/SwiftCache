package org.saahil.example;

import org.junit.jupiter.api.Test;
import org.saahil.SwiftCache;
import org.saahil.annotation.CacheStore;
import org.saahil.annotation.CacheableProxy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApplicationCacheTest {

    @Test
    void testCachingBehavior() {
        // Create cache store and service
        CacheStore cacheStore = new ApplicationCacheStore();
        UserService userService = new UserServiceImpl();

        // Create a cached proxy of the service
        UserService cachedUserService = CacheableProxy.createProxy(userService, cacheStore);

        // Use the cached service (sequence mirrors the example)
        cachedUserService.getUserById(1L); // miss
        cachedUserService.getUserById(1L); // hit
        cachedUserService.getUserById(2L); // miss
        cachedUserService.getUserByName("John Doe"); // miss
        cachedUserService.getUserByName("John Doe"); // hit
        cachedUserService.getAllUsers(); // miss
        cachedUserService.getAllUsers(); // hit

        // Validate stats on the "users" cache
        SwiftCache<String, Object> usersCache = cacheStore.getCache("users");
        assertEquals(3, usersCache.getStats().getHits(), "Expected 3 cache hits");
        assertEquals(4, usersCache.getStats().getMisses(), "Expected 4 cache misses");
    }
}

