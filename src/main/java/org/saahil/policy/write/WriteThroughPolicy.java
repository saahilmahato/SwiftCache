package org.saahil.policy.write;

import org.saahil.SwiftCache;
import org.saahil.store.PersistentStore;
import java.util.logging.Logger;
import java.util.logging.Level;

public class WriteThroughPolicy<K, V> implements WritePolicy<K, V> {

    private static final Logger LOGGER = Logger.getLogger(WriteThroughPolicy.class.getName());

    @Override
    public void write(
            K key,
            V value,
            long ttlNanos,
            SwiftCache<K, V> cache,
            PersistentStore<K, V> store
    ) {
        LOGGER.log(Level.INFO, "[WriteThroughPolicy] Writing to DB: {0}", key);
        store.save(key, value);

        LOGGER.log(Level.INFO, "[WriteThroughPolicy] Writing to cache: {0}", key);
        cache.putCacheEntry(key, value, ttlNanos);
    }
}
