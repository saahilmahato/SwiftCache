package org.saahil.annotation;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CacheKeyGeneratorTest {

    @Test
    void shouldGenerateDefaultKeyFromMethodAndArgs() {
        String key = CacheKeyGenerator.generateKey("findUser", 10L, "active");

        assertEquals("findUser:10:active", key);
    }

    @Test
    void shouldGenerateKeyFromExpression() throws NoSuchMethodException {
        Method method = Dummy.class.getMethod("work", String.class, int.class);
        String key = CacheKeyGenerator.generateKeyFromExpression(
                "#root.method:#p0:#p1:#root.args",
                method,
                new Object[]{"alpha", 7}
        );

        assertEquals("work:alpha:7:alpha:7", key);
    }

    static class Dummy {
        public void work(String a, int b) {
        }
    }
}
