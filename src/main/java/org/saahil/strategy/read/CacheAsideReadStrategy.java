package org.saahil.strategy.read;

import org.saahil.CacheEntry;
import org.saahil.SwiftCache;
import java.util.logging.Logger;
import java.util.logging.Level;

public class CacheAsideReadStrategy<K, V> implements ReadStrategy<K, V> {

    private static final Logger LOGGER = Logger.getLogger(CacheAsideReadStrategy.class.getName());

    @Override
    public V read(K key, SwiftCache<K, V> cache) {

        CacheEntry<V> entry = cache.getInternalEntry(key);

        if (entry == null) {
            cache.getStats().recordMiss();
            LOGGER.log(Level.INFO, "[CacheAsideReadStrategy] MISS for key: {0}", key);
            return null;
        }

        if (entry.isExpired()) {
            cache.removeInternal(key);
            cache.getStats().recordMiss();
            LOGGER.log(Level.INFO, "[CacheAsideReadStrategy] EXPIRED for key: {0}", key);
            return null;
        }

        cache.getStats().recordHit();
        LOGGER.log(Level.INFO, "[CacheAsideReadStrategy] HIT for key: {0}", key);

        return entry.getValue();
    }
}
