package org.saahil.policy.eviction;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EvictionPoliciesTest {

    @Test
    void fifoShouldEvictInInsertionOrder() {
        FIFOEvictionPolicy<String> policy = new FIFOEvictionPolicy<>();

        policy.onKeyAccess("k1");
        policy.onKeyAccess("k2");
        policy.onKeyAccess("k3");

        assertEquals("k1", policy.evictKey());
        assertEquals("k2", policy.evictKey());
        assertEquals("k3", policy.evictKey());
        assertNull(policy.evictKey());
    }

    @Test
    void lruShouldEvictLeastRecentlyUsedKey() {
        LRUEvictionPolicy<String> policy = new LRUEvictionPolicy<>();

        policy.onKeyAccess("k1");
        policy.onKeyAccess("k2");
        policy.onKeyAccess("k1");

        assertEquals("k2", policy.evictKey());
    }

    @Test
    void lfuShouldEvictLeastFrequentlyUsedKey() {
        LFUEvictionPolicy<String> policy = new LFUEvictionPolicy<>();

        policy.onKeyAccess("k1");
        policy.onKeyAccess("k1");
        policy.onKeyAccess("k2");

        assertEquals("k2", policy.evictKey());
    }

    @Test
    void randomShouldEvictOneOfTrackedKeys() {
        RandomEvictionPolicy<String> policy = new RandomEvictionPolicy<>();

        policy.onKeyAccess("k1");
        policy.onKeyAccess("k2");
        policy.onKeyAccess("k3");

        String evicted = policy.evictKey();
        assertTrue(Set.of("k1", "k2", "k3").contains(evicted));
    }
}
