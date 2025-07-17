package org.saahil;

public class CacheEntry<V> {
  private final V value;
  private final long timestamp;
  private final long ttl;

  public CacheEntry(V value, long ttl) {
    this.value = value;
    this.timestamp = System.currentTimeMillis();
    this.ttl = ttl;
  }

  public V getValue() {
    return value;
  }

  public boolean isExpired() {
    return ttl > 0 && (System.currentTimeMillis() - timestamp) > ttl;
  }

  public long getTimestamp() {
    return timestamp;
  }
}
