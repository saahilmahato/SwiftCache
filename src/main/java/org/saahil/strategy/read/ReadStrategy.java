package org.saahil.strategy.read;

import org.saahil.SwiftCache;

public interface ReadStrategy<K, V> {

    V read(K key, SwiftCache<K, V> cache);
}
