package org.saahil.store;

public interface PersistentStore<K, V> {

    void save(K key, V value);

    V load(K key);
}

