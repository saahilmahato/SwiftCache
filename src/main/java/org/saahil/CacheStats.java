package org.saahil;

import java.util.concurrent.atomic.LongAdder;

public class CacheStats {
    private final LongAdder hits = new LongAdder();
    private final LongAdder misses = new LongAdder();
    private final LongAdder evictions = new LongAdder();
    private final LongAdder puts = new LongAdder();

    public void recordHit() {
        hits.increment();
    }

    public void recordMiss() {
        misses.increment();
    }

    public void recordEviction() {
        evictions.increment();
    }

    public void recordPut() {
        puts.increment();
    }

    public double getHitRate() {
        long total = hits.longValue() + misses.longValue();
        return total == 0 ? 0.0 : (double) hits.longValue() / total;
    }

    public long getHits() {
        return hits.longValue();
    }

    public long getMisses() {
        return misses.longValue();
    }

    public long getEvictions() {
        return evictions.longValue();
    }

    public long getPuts() {
        return puts.longValue();
    }

    @Override
    public synchronized String toString() {
        return String.format("CacheStats{ hits=%d, misses=%d, evictions=%d, puts=%d, hitRate=%.2f }",
                hits.longValue(),
                misses.longValue(),
                evictions.longValue(),
                puts.longValue(),
                getHitRate()
        );
    }
}
