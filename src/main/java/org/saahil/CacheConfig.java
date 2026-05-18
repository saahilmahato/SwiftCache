package org.saahil;

import org.saahil.policy.eviction.EvictionPolicy;
import org.saahil.strategy.read.ReadStrategy;
import org.saahil.policy.write.WritePolicy;

public class CacheConfig<K, V> {

    private final int maxSize;

    private final ReadStrategy<K, V> readStrategy;

    private final WritePolicy<K, V> writePolicy;
 
    private final EvictionPolicy<K> evictionPolicy;

    public CacheConfig(
            int maxSize,
            ReadStrategy<K, V> readStrategy,
            WritePolicy<K, V> writePolicy,
            EvictionPolicy<K> evictionPolicy
    ) {

        this.maxSize = maxSize;
        this.readStrategy = readStrategy;
        this.writePolicy = writePolicy;
        this.evictionPolicy = evictionPolicy;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public ReadStrategy<K, V> getReadStrategy() {
        return readStrategy;
    }

    public WritePolicy<K, V> getWritePolicy() {
        return writePolicy;
    }

    public EvictionPolicy<K> getEvictionPolicy() {
        return evictionPolicy;
    }
}