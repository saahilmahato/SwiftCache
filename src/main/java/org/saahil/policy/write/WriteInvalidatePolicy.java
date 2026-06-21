package org.saahil.policy.write;

import org.saahil.SwiftCache;
import org.saahil.store.PersistentStore;

import java.util.logging.Level;
import java.util.logging.Logger;

public class WriteInvalidatePolicy<K, V> implements WritePolicy<K, V> {

    private static final Logger LOGGER = Logger.getLogger(WriteInvalidatePolicy.class.getName());

    @Override
    public void write(
            K key,
            V value,
            long ttlNanos,
            SwiftCache<K, V> cache,
            PersistentStore<K, V> store
    ) {
        LOGGER.log(Level.INFO, "[WriteInvalidatePolicy] Writing to DB: {0}", key);
        store.save(key, value);

        LOGGER.log(Level.INFO, "[WriteInvalidatePolicy] Invalidating cache for key: {0}", key);
        cache.removeCacheEntry(key);
    }
}
