package org.saahil.policy.write;

import org.saahil.SwiftCache;
import org.saahil.store.PersistentStore;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.logging.Level;

public class WriteBackPolicy<K, V> implements WritePolicy<K, V> {

    private static final Logger LOGGER = Logger.getLogger(WriteBackPolicy.class.getName());
    private final long delayMillis;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public WriteBackPolicy(long delayMillis) {
        this.delayMillis = delayMillis;
    }

    @Override
    public void write(
            K key,
            V value,
            long ttlNanos,
            SwiftCache<K, V> cache,
            PersistentStore<K, V> store
    ) {
        LOGGER.log(Level.INFO, "[WriteBackPolicy] Writing to cache immediately: {0}", key);
        cache.putInternal(key, value, ttlNanos);

        LOGGER.log(Level.INFO, "[WriteBackPolicy] Deferring DB write with delay: {0}ms for key: {1}",
            new Object[]{delayMillis, key});

        executor.schedule(() -> {
            LOGGER.log(Level.INFO, "[WriteBackPolicy] Executing delayed DB write: {0}", key);
            store.save(key, value);
        }, delayMillis, TimeUnit.MILLISECONDS);
    }
}

