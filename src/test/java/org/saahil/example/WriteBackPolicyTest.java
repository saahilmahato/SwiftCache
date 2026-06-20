package org.saahil.example;

import org.junit.jupiter.api.Test;
import org.saahil.CacheConfig;
import org.saahil.SwiftCache;
import org.saahil.policy.eviction.LRUEvictionPolicy;
import org.saahil.policy.write.WriteBackPolicy;
import org.saahil.store.PersistentStore;
import org.saahil.strategy.read.ReadOnlyStrategy;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WriteBackPolicyTest {

    @Test
    void testWriteBackPolicyDefersDBWrite() throws InterruptedException {
        CountDownLatchStore<String, String> store = new CountDownLatchStore<>();

        CacheConfig<String, String> config = new CacheConfig<>(
                10,
                new ReadOnlyStrategy<>(),
                new WriteBackPolicy<>(100),
                new LRUEvictionPolicy<>(),
                store
        );

        SwiftCache<String, String> cache = new SwiftCache<>(config);

        cache.put("user:1", "John Doe", -1);

        assertEquals("John Doe", cache.get("user:1"));

        assertNull(store.load("user:1"));

        boolean writeCompleted = store.waitForSave();
        assertTrue(writeCompleted, "Write-back should complete within 500ms");
        assertEquals("John Doe", store.load("user:1"));
    }

    private static class CountDownLatchStore<K, V> implements PersistentStore<K, V> {
        private final InMemoryStore<K, V> delegate = new InMemoryStore<>();
        private final CountDownLatch latch = new CountDownLatch(1);

        @Override
        public void save(K key, V value) {
            delegate.save(key, value);
            latch.countDown();
        }

        @Override
        public V load(K key) {
            return delegate.load(key);
        }

        @Override
        public void delete(K key) {
            delegate.delete(key);
        }

        @Override
        public boolean exists(K key) {
            return delegate.exists(key);
        }

        boolean waitForSave() throws InterruptedException {
            return latch.await(500, TimeUnit.MILLISECONDS);
        }
    }
}
