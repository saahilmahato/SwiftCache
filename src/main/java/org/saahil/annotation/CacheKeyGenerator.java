package org.saahil.annotation;

import java.lang.reflect.Method;

/**
 * Generates cache keys from method information and arguments.
 */
public class CacheKeyGenerator {

    private CacheKeyGenerator() {
        /* This utility class should not be instantiated */
    }

    private static final String CACHE_KEY_SEPARATOR = ":";

    /**
     * Generate a cache key based on method signature and arguments.
     *
     * @param methodName the name of the method
     * @param args the method arguments
     * @return a cache key string
     */
    public static String generateKey(String methodName, Object... args) {
        StringBuilder keyBuilder = new StringBuilder(methodName);

        if (args != null && args.length > 0) {
            keyBuilder.append(CACHE_KEY_SEPARATOR);
            for (int i = 0; i < args.length; i++) {
                if (i > 0) {
                    keyBuilder.append(CACHE_KEY_SEPARATOR);
                }
                keyBuilder.append(getArgString(args[i]));
            }
        }

        return keyBuilder.toString();
    }

    /**
     * Generate a cache key based on a custom expression.
     *
     * @param keyExpression the key expression (supports #p0, #p1, etc., #root.method, #root.args)
     * @param method the method being invoked
     * @param args the method arguments
     * @return a cache key string
     */
    public static String generateKeyFromExpression(
            String keyExpression, Method method, Object[] args) {

        String key = keyExpression;

        // Replace #root.method
        key = key.replace("#root.method", method.getName());

        // Replace #root.args
        if (key.contains("#root.args")) {
            StringBuilder argsStr = new StringBuilder();
            if (args != null && args.length > 0) {
                for (int i = 0; i < args.length; i++) {
                    if (i > 0) {
                        argsStr.append(CACHE_KEY_SEPARATOR);
                    }
                    argsStr.append(getArgString(args[i]));
                }
            }
            key = key.replace("#root.args", argsStr.toString());
        }

        // Replace #p0, #p1, etc.
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                key = key.replace("#p" + i, getArgString(args[i]));
            }
        }

        return key;
    }

    private static String getArgString(Object arg) {
        if (arg == null) {
            return "null";
        }

        return arg.toString();
    }
}

