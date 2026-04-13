package org.saahil.read;

import org.saahil.SwiftCache;

public interface ReadStrategy<K, V> {
    V get(K key, SwiftCache<K, V> cache);
}
