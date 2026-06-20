package org.saahil.policy.write;

import org.saahil.SwiftCache;
import org.saahil.store.PersistentStore;
import java.util.logging.Logger;
import java.util.logging.Level;

public class WriteAroundPolicy<K, V> implements WritePolicy<K, V> {

    private static final Logger LOGGER = Logger.getLogger(WriteAroundPolicy.class.getName());

    @Override
    public void write(
            K key,
            V value,
            long ttlNanos,
            SwiftCache<K, V> cache,
            PersistentStore<K, V> store
    ) {
        LOGGER.log(Level.INFO, "[WriteAroundPolicy] Writing to DB (bypassing cache): {0}", key);
        store.save(key, value);
    }
}

