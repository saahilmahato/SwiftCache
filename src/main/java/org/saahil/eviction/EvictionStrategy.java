package org.saahil.eviction;

import java.util.concurrent.ConcurrentHashMap;

public interface EvictionStrategy<K, V> {
  void recordAccess(K key);

  void recordWrite(K key);

  K evictNext(ConcurrentHashMap<K, V> cache);

  void remove(K key);

  boolean shouldEvict(int currentSize, int maxSize);
}