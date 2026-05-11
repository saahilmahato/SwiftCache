package org.saahil.policy.eviction;

public interface EvictionPolicy<K> {

    void onKeyAccess(K key);

    K evictKey();
}
