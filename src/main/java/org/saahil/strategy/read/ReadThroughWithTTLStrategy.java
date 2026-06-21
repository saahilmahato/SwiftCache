package org.saahil.strategy.read;

import org.saahil.CacheEntry;
import org.saahil.SwiftCache;
import org.saahil.store.PersistentStore;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ReadThroughWithTTLStrategy<K, V> implements ReadStrategy<K, V> {

    private static final Logger LOGGER = Logger.getLogger(ReadThroughWithTTLStrategy.class.getName());

    private final PersistentStore<K, V> store;
    private final long ttlNanos;

    public ReadThroughWithTTLStrategy(PersistentStore<K, V> store, long ttlNanos) {
        this.store = store;
        this.ttlNanos = ttlNanos;
    }

    @Override
    public V read(K key, SwiftCache<K, V> cache) {
        CacheEntry<V> entry = cache.getCacheEntry(key);

        if (entry != null && !entry.isExpired()) {
            cache.getStats().recordHit();
            LOGGER.log(Level.INFO, "[ReadThroughWithTTLStrategy] HIT for key: {0}", key);
            return entry.getValue();
        }

        if (entry != null && entry.isExpired()) {
            cache.removeCacheEntry(key);
        }

        cache.getStats().recordMiss();
        LOGGER.log(Level.INFO, "[ReadThroughWithTTLStrategy] MISS for key: {0}, loading from DB", key);

        V value = store.load(key);

        if (value != null) {
            cache.putCacheEntry(key, value, ttlNanos);
            LOGGER.log(Level.INFO, "[ReadThroughWithTTLStrategy] Loaded from DB and cached key: {0}", key);
        }

        return value;
    }
}
