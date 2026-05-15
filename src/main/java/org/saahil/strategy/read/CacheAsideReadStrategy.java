package org.saahil.strategy.read;

import org.saahil.CacheEntry;
import org.saahil.SwiftCache;

public class CacheAsideReadStrategy<K, V> implements ReadStrategy<K, V> {

    @Override
    public V read(K key, SwiftCache<K, V> cache) {

        CacheEntry<V> entry = cache.getInternalEntry(key);

        if (entry == null) {
            cache.getStats().recordMiss();
            return null;
        }

        if (entry.isExpired()) {
            cache.removeInternal(key);
            cache.getStats().recordMiss();
            return null;
        }

        cache.getStats().recordHit();

        return entry.getValue();
    }
}
