package org.saahil.annotation;

import java.lang.reflect.Proxy;

public class CacheableProxy {

    private CacheableProxy() {
        /* This utility class should not be instantiated */
    }

    @SuppressWarnings("unchecked")
    public static <T> T createProxy(T target, CacheStore cacheStore) {
        CacheableMethodInterceptor interceptor = new CacheableMethodInterceptor(target, cacheStore);

        return (T) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                interceptor
        );
    }
}

