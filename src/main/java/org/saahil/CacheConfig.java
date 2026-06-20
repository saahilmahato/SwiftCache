package org.saahil;

import org.saahil.policy.eviction.EvictionPolicy;
import org.saahil.strategy.read.ReadStrategy;
import org.saahil.policy.write.WritePolicy;
import org.saahil.store.PersistentStore;

public record CacheConfig<K, V>(
        int maxSize, ReadStrategy<K, V> readStrategy,
        WritePolicy<K, V> writePolicy,
        EvictionPolicy<K> evictionPolicy,
        PersistentStore<K, V> persistentStore) {

}