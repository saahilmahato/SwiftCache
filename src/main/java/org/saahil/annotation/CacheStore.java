package org.saahil.annotation;

import org.saahil.SwiftCache;

public interface CacheStore {

    SwiftCache<String, Object> getCache(String name);
}

