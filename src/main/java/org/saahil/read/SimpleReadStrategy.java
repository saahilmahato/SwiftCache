package org.saahil.read;

import org.saahil.CacheEntry;
import org.saahil.SwiftCache;

public class SimpleReadStrategy<K, V> implements ReadStrategy<K, V> {

    @Override
    public V get(K key, SwiftCache<K, V> cache) {
        CacheEntry<V> entry = cache.getEntry(key);

        if (entry == null) {
            cache.getStats().recordMiss();

            return null;
        }

        if (entry.isExpired()) {
            cache.removeEntry(key);
            cache.getStats().recordEviction();
            cache.getStats().recordMiss();

            return null;
        }

        cache.getStats().recordHit();

        return entry.getValue();
    }
}
