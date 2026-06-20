package org.saahil.example;

import org.saahil.store.PersistentStore;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

public class InMemoryStore<K, V> implements PersistentStore<K, V> {

    private static final Logger LOGGER = Logger.getLogger(InMemoryStore.class.getName());
    private final Map<K, V> store = new HashMap<>();

    @Override
    public void save(K key, V value) {
        store.put(key, value);
        LOGGER.log(Level.INFO, "[InMemoryStore] Saved: {0} -> {1}", new Object[]{key, value});
    }

    @Override
    public V load(K key) {
        V value = store.get(key);
        LOGGER.log(Level.INFO, "[InMemoryStore] Loaded: {0} -> {1}", new Object[]{key, value});
        return value;
    }

    @Override
    public void delete(K key) {
        store.remove(key);
        LOGGER.log(Level.INFO, "[InMemoryStore] Deleted: {0}", key);
    }

    @Override
    public boolean exists(K key) {
        return store.containsKey(key);
    }
}
