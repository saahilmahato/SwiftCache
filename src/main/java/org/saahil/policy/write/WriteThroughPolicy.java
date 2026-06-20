package org.saahil.policy.write;

import org.saahil.SwiftCache;
import java.util.logging.Logger;
import java.util.logging.Level;

public class WriteThroughPolicy<K, V> implements WritePolicy<K, V> {

    private static final Logger LOGGER = Logger.getLogger(WriteThroughPolicy.class.getName());

    @Override
    public void write(
            K key,
            V value,
            long ttlNanos,
            SwiftCache<K, V> cache
    ) {

        LOGGER.log(Level.INFO, "Writing to DB: {0}", key);

        cache.putInternal(key, value, ttlNanos);
    }
}
