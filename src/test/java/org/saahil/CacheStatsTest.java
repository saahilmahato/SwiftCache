package org.saahil;

import org.junit.jupiter.api.Test;
import org.saahil.stats.CacheStats;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CacheStatsTest {

    @Test
    void shouldRecordHitsMissesAndEvictions() {
        CacheStats stats = new CacheStats();

        stats.recordHit();
        stats.recordHit();
        stats.recordMiss();
        stats.recordEviction();

        assertEquals(2, stats.getHits());
        assertEquals(1, stats.getMisses());
        assertEquals(1, stats.getEvictions());
        assertTrue(stats.toString().contains("hits="));
    }
}
