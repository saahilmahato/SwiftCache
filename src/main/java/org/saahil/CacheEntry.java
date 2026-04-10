package org.saahil;

public class CacheEntry<V> {
    private final V value;
    private final long expiryTimeNanos;

    public CacheEntry(V value, long ttlNanos) {
        this.value = value;
        this.expiryTimeNanos = ttlNanos > 0
                ? System.nanoTime() + ttlNanos
                : -1;
    }

    public V getValue() {
        return value;
    }

    public boolean isExpired() {
        return expiryTimeNanos != -1 && System.nanoTime() > expiryTimeNanos;
    }
}
