package org.saahil.example;

import org.saahil.CacheConfig;
import org.saahil.SwiftCache;
import org.saahil.policy.eviction.FIFOEvictionPolicy;
import org.saahil.policy.eviction.LFUEvictionPolicy;
import org.saahil.policy.eviction.LRUEvictionPolicy;
import org.saahil.policy.write.WriteAroundPolicy;
import org.saahil.policy.write.WriteBackPolicy;
import org.saahil.policy.write.WriteThroughPolicy;
import org.saahil.strategy.read.ReadOnlyStrategy;
import org.saahil.strategy.read.ReadThroughStrategy;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CachePoliciesDemonstration {

    private static final String EVICTIONS_LABEL = "   Evictions: ";

    private static final Logger LOGGER = Logger.getLogger(CachePoliciesDemonstration.class.getName());


    static void main() {
        log("=== SwiftCache Policies Demonstration ===\n");

        demonstrateWritePolicies();
        log("\n" + "=".repeat(50) + "\n");

        demonstrateReadStrategies();
        log("\n" + "=".repeat(50) + "\n");

        demonstrateEvictionPolicies();
    }

    private static void log(String msg) {
        LOGGER.log(Level.INFO, msg);
    }

    private static void demonstrateWritePolicies() {
        log("WRITE POLICIES DEMONSTRATION:\n");

        log("1. WriteThroughPolicy - Writes to DB first, then cache");
        demonstrateWriteThrough();

        log("\n2. WriteBackPolicy - Writes to cache immediately, defers DB write");
        demonstrateWriteBack();

        log("\n3. WriteAroundPolicy - Writes to DB, bypasses cache");
        demonstrateWriteAround();
    }

    private static void demonstrateWriteThrough() {
        InMemoryStore<String, String> store = new InMemoryStore<>();
        CacheConfig<String, String> config = new CacheConfig<>(
                10,
                new ReadOnlyStrategy<>(),
                new WriteThroughPolicy<>(),
                new FIFOEvictionPolicy<>(),
                store
        );

        SwiftCache<String, String> cache = new SwiftCache<>(config);
        cache.put("key1", "value1", -1);
        log("   Put key1=value1");
        log("   Cache get key1: " + cache.get("key1"));
        log("   Store get key1: " + store.load("key1"));
    }

    private static void demonstrateWriteBack() {
        InMemoryStore<String, String> store = new InMemoryStore<>();
        CacheConfig<String, String> config = new CacheConfig<>(
                10,
                new ReadOnlyStrategy<>(),
                new WriteBackPolicy<>(500),
                new FIFOEvictionPolicy<>(),
                store
        );

        SwiftCache<String, String> cache = new SwiftCache<>(config);
        cache.put("key2", "value2", -1);
        log("   Put key2=value2 (async DB write after 500ms)");
        log("   Cache get key2 immediately: " + cache.get("key2"));
        log("   Store get key2 immediately: " + store.load("key2"));
        log("   Waiting 600ms for async write...");
        try {
            Thread.sleep(600);
        } catch (InterruptedException _) {
            Thread.currentThread().interrupt();
        }
        log("   Store get key2 after delay: " + store.load("key2"));
    }

    private static void demonstrateWriteAround() {
        InMemoryStore<String, String> store = new InMemoryStore<>();
        CacheConfig<String, String> config = new CacheConfig<>(
                10,
                new ReadOnlyStrategy<>(),
                new WriteAroundPolicy<>(),
                new FIFOEvictionPolicy<>(),
                store
        );

        SwiftCache<String, String> cache = new SwiftCache<>(config);
        cache.put("key3", "value3", -1);
        log("   Put key3=value3 (skips cache)");
        log("   Cache get key3: " + cache.get("key3"));
        log("   Store get key3: " + store.load("key3"));
    }

    private static void demonstrateReadStrategies() {
        log("READ STRATEGIES DEMONSTRATION:\n");

        log("1. ReadThroughStrategy - Loads from DB on miss, populates cache");
        demonstrateReadThrough();

        log("\n2. ReadOnlyStrategy - Only reads from cache, no DB fallback");
        demonstrateReadOnly();

        log("\n(Cache-Aside pattern is implemented at application layer)");
    }

    private static void demonstrateReadThrough() {
        final String key = "key_b";
        InMemoryStore<String, String> store = new InMemoryStore<>();
        store.save(key, "value_b");

        CacheConfig<String, String> config = new CacheConfig<>(
                10,
                new ReadThroughStrategy<>(store),
                new WriteThroughPolicy<>(),
                new FIFOEvictionPolicy<>(),
                store
        );

        SwiftCache<String, String> cache = new SwiftCache<>(config);
        log("   Get key_b (not in cache, loads from DB): " + cache.get(key));
        log("   Get key_b again (now in cache): " + cache.get(key));
        log("   Stats - Hits: " + cache.getStats().getHits() + ", Misses: " + cache.getStats().getMisses());
    }

    private static void demonstrateReadOnly() {
        InMemoryStore<String, String> store = new InMemoryStore<>();
        store.save("key_c", "value_c");

        CacheConfig<String, String> config = new CacheConfig<>(
                10,
                new ReadOnlyStrategy<>(),
                new WriteThroughPolicy<>(),
                new FIFOEvictionPolicy<>(),
                store
        );

        SwiftCache<String, String> cache = new SwiftCache<>(config);
        log("   Get key_c (not in cache, DB not consulted): " + cache.get("key_c"));
        log("   Stats - Misses: " + cache.getStats().getMisses());
    }

    private static void demonstrateEvictionPolicies() {
        log("EVICTION POLICIES DEMONSTRATION:\n");

        log("1. FIFOEvictionPolicy - First In, First Out");
        demonstrateFIFO();

        log("\n2. LRUEvictionPolicy - Least Recently Used");
        demonstrateLRU();

        log("\n3. LFUEvictionPolicy - Least Frequently Used");
        demonstrateLFU();
    }

    private static void demonstrateFIFO() {
        InMemoryStore<String, Integer> store = new InMemoryStore<>();
        CacheConfig<String, Integer> config = new CacheConfig<>(
                3,
                new ReadOnlyStrategy<>(),
                new WriteThroughPolicy<>(),
                new FIFOEvictionPolicy<>(),
                store
        );

        SwiftCache<String, Integer> cache = new SwiftCache<>(config);
        cache.put("a", 1, -1);
        cache.put("b", 2, -1);
        cache.put("c", 3, -1);
        log("   Added a, b, c (max size = 3)");
        cache.put("d", 4, -1);
        log("   Added d, evicted earliest (a)");
        log(EVICTIONS_LABEL + cache.getStats().getEvictions());
    }

    private static void demonstrateLRU() {
        InMemoryStore<String, Integer> store = new InMemoryStore<>();
        CacheConfig<String, Integer> config = new CacheConfig<>(
                3,
                new ReadOnlyStrategy<>(),
                new WriteThroughPolicy<>(),
                new LRUEvictionPolicy<>(),
                store
        );

        SwiftCache<String, Integer> cache = new SwiftCache<>(config);
        cache.put("x", 1, -1);
        cache.put("y", 2, -1);
        cache.put("z", 3, -1);
        log("   Added x, y, z (max size = 3)");
        cache.get("x");
        log("   Accessed x (marks as recently used)");
        cache.put("w", 4, -1);
        log("   Added w, evicted least recently used (y)");
        log(EVICTIONS_LABEL + cache.getStats().getEvictions());
    }

    private static void demonstrateLFU() {
        InMemoryStore<String, Integer> store = new InMemoryStore<>();
        CacheConfig<String, Integer> config = new CacheConfig<>(
                3,
                new ReadOnlyStrategy<>(),
                new WriteThroughPolicy<>(),
                new LFUEvictionPolicy<>(),
                store
        );

        SwiftCache<String, Integer> cache = new SwiftCache<>(config);
        cache.put("p", 1, -1);
        cache.put("q", 2, -1);
        cache.put("r", 3, -1);
        log("   Added p, q, r (max size = 3)");
        cache.get("p");
        cache.get("p");
        cache.get("q");
        log("   Accessed p twice, q once (r never accessed)");
        cache.put("s", 4, -1);
        log("   Added s, evicted least frequently used (r)");
        log(EVICTIONS_LABEL + cache.getStats().getEvictions());
    }
}



