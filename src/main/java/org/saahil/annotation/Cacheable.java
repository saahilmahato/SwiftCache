package org.saahil.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cacheable {

    /**
     * Name of the cache store to use.
     * This should correspond to a SwiftCache instance available in the application.
     */
    String name();

    /**
     * Time-to-live value for cached entries.
     * Defaults to no expiration (-1).
     */
    long ttl() default -1;

    /**
     * Time unit for the TTL value.
     * Defaults to NANOSECONDS.
     */
    TimeUnit unit() default TimeUnit.NANOSECONDS;

    /**
     * Custom key expression. If not provided, generates key from method name and parameters.
     * Supported expressions:
     * - "#root.method" - method name
     * - "#p0", "#p1", etc - method parameter at index 0, 1, etc
     * - "#root.args" - all arguments as comma-separated string
     */
    String key() default "";

    /**
     * Whether to cache null values.
     */
    boolean cacheNullValues() default false;
}

