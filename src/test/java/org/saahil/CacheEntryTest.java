package org.saahil;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CacheEntryTest {

    @Test
    void shouldStoreValueAndNotExpireWhenTtlIsNegative() {
        CacheEntry<String> entry = new CacheEntry<>("value", -1);

        assertEquals("value", entry.getValue());
        assertFalse(entry.isExpired());
    }

    @Test
    void shouldExpireAfterPositiveTtl() throws InterruptedException {
        CacheEntry<String> entry = new CacheEntry<>("value", 1_000_000);

        Thread.sleep(5);

        assertTrue(entry.isExpired());
    }
}
