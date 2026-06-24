package org.saahil.annotation;

import org.saahil.SwiftCache;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

public class CacheableMethodInterceptor implements InvocationHandler {

    private final Object target;
    private final CacheStore cacheStore;

    public CacheableMethodInterceptor(Object target, CacheStore cacheStore) {
        this.target = target;
        this.cacheStore = cacheStore;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Cacheable cacheable = method.getAnnotation(Cacheable.class);

        // If method is not annotated with @Cacheable, call the implementation directly
        if (cacheable == null) {
            Method implementationMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());
            return implementationMethod.invoke(target, args);
        }

        // Get the cache for this annotation
        SwiftCache<String, Object> cache = cacheStore.getCache(cacheable.name());
        if (cache == null) {
            // If cache doesn't exist, call method directly
            return method.invoke(target, args);
        }

        // Generate cache key
        String cacheKey = generateCacheKey(cacheable, method, args);

        // Try to get from cache
        Object cachedValue = cache.get(cacheKey);
        if (cachedValue != null) {
            return cachedValue;
        }

        // Cache miss - call the actual implementation method on the target
        Method implementationMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());
        Object result = implementationMethod.invoke(target, args);

        // Cache the result (if not null or if cacheNullValues is true)
        if (result != null || cacheable.cacheNullValues()) {
            long ttlNanos = convertToNanos(cacheable.ttl(), cacheable.unit());
            cache.put(cacheKey, result, ttlNanos);
        }

        return result;
    }

    private String generateCacheKey(Cacheable cacheable, Method method, Object[] args) {
        String keyExpression = cacheable.key();

        if (keyExpression == null || keyExpression.isEmpty()) {
            // Generate default key from method name and arguments
            return CacheKeyGenerator.generateKey(method.getName(), args);
        } else {
            // Use custom key expression
            return CacheKeyGenerator.generateKeyFromExpression(keyExpression, method, args);
        }
    }

    private long convertToNanos(long ttl, TimeUnit unit) {
        if (ttl < 0) {
            return -1; // No expiration
        }
        return unit.toNanos(ttl);
    }
}

