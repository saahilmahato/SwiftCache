package org.saahil.policy.write;

import org.saahil.SwiftCache;

public class WriteThroughPolicy<K, V> implements WritePolicy<K, V> {

    @Override
    public void write(
            K key,
            V value,
            long ttlNanos,
            SwiftCache<K, V> cache
    ) {

        // simulate database write
        System.out.println("Writing to DB: " + key);

        cache.putInternal(key, value, ttlNanos);
    }
}
