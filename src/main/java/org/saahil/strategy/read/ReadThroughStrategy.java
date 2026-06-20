package org.saahil.strategy.read;

import org.saahil.CacheEntry;
import org.saahil.SwiftCache;
import org.saahil.store.PersistentStore;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ReadThroughStrategy<K, V> implements ReadStrategy<K, V> {

    private static final Logger LOGGER = Logger.getLogger(ReadThroughStrategy.class.getName());
    private final PersistentStore<K, V> store;

    public ReadThroughStrategy(PersistentStore<K, V> store) {
        this.store = store;
    }

    @Override
    public V read(K key, SwiftCache<K, V> cache) {
        CacheEntry<V> entry = cache.getInternalEntry(key);

        if (entry != null && !entry.isExpired()) {
            cache.getStats().recordHit();
            LOGGER.log(Level.INFO, "[ReadThroughStrategy] HIT for key: {0}", key);
            return entry.getValue();
        }

        if (entry != null && entry.isExpired()) {
            cache.removeInternal(key);
        }

        cache.getStats().recordMiss();
        LOGGER.log(Level.INFO, "[ReadThroughStrategy] MISS for key: {0}, loading from DB", key);

        V value = store.load(key);

        if (value != null) {
            LOGGER.log(Level.INFO, "[ReadThroughStrategy] Loaded from DB and populating cache: {0}", key);
            cache.putInternal(key, value, -1);
        }

        return value;
    }
}

