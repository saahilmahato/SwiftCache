package org.saahil.policy.write;

import org.saahil.SwiftCache;

public interface WritePolicy<K, V> {

    void write(
            K key,
            V value,
            long ttlNanos,
            SwiftCache<K, V> cache
    );
}
