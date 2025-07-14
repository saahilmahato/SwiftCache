package org.saahil;

public class CacheStats {
    private long hits = 0;
    private long misses = 0;
    private long evictions = 0;
    private long puts = 0;

    public synchronized void recordHit() {
        hits++;
    }

    public synchronized void recordMiss() {
        misses++;
    }

    public synchronized void recordEviction() {
        evictions++;
    }

    public synchronized void recordPut() {
        puts++;
    }

    public synchronized double getHitRate() {
        long total = hits + misses;
        return total == 0 ? 0.0 : (double) hits/total;
    }

    public synchronized long getHits() {
        return hits;
    }

    public synchronized long getMisses() {
        return misses;
    }

    public synchronized long getEvictions() {
        return evictions;
    }

    public synchronized long getPuts() {
        return puts;
    }

    @Override
    public synchronized String toString() {
        return String.format("CacheStats{ hits=%d, misses=%d, evictions=%d, puts=%d, hitRate=%.2f }", hits, misses, evictions, puts, getHitRate());
    }
}
