package org.saahil.example;

import org.saahil.annotation.Cacheable;

/**
 * Example service interface.
 * Methods in this interface can be annotated with @Cacheable
 * to enable automatic caching when a proxy is used.
 */
public interface UserService {

    /**
     * Get a user by ID.
     * This method will be cached with key: "getUserById:123"
     */
    @Cacheable(name = "users", ttl = 5, unit = java.util.concurrent.TimeUnit.MINUTES)
    User getUserById(Long id);

    /**
     * Get a user by name.
     * Uses custom key expression: "getUserByName:john"
     */
    @Cacheable(name = "users", key = "getUserByName:#p0", ttl = 10, unit = java.util.concurrent.TimeUnit.MINUTES)
    User getUserByName(String name);

    /**
     * Get all users.
     * Caches the list of users with key: "getAllUsers"
     */
    @Cacheable(name = "users", ttl = 30, unit = java.util.concurrent.TimeUnit.MINUTES)
    java.util.List<User> getAllUsers();
}

