package org.saahil.stats;

import java.util.concurrent.atomic.AtomicLong;

public class CacheStats {

    private final AtomicLong hits = new AtomicLong();

    private final AtomicLong misses = new AtomicLong();

    private final AtomicLong evictions = new AtomicLong();

    public void recordHit() {
        hits.incrementAndGet();
    }

    public void recordMiss() {
        misses.incrementAndGet();
    }

    public void recordEviction() {
        evictions.incrementAndGet();
    }

    @Override
    public String toString() {

        return "CacheStats{" +
                "hits=" + hits +
                ", misses=" + misses +
                ", evictions=" + evictions +
                '}';
    }
}