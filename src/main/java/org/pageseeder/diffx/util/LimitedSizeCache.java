/*
 * Copyright 2010-2025 Allette Systems (Australia)
 * http://www.allette.com.au
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pageseeder.diffx.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * A limited size cache that stores key-value pairs and follows the Least Recently Used (LRU) eviction policy.
 *
 * <p>This class provides a mechanism to store a limited number of entries, and evicts the least recently used entry
 * when the number of entries exceeds the specified maximum size. A factory function is used to generate values
 * when they are not present in the cache.
 *
 * @param <K> the type of keys maintained by this cache
 * @param <V> the type of mapped values
 *
 * @author Christophe Lauret
 *
 * @since 1.3.0
 * @version 1.3.0
 */
public class LimitedSizeCache<K, V> {
  private final int maxSize;
  private final Map<K, V> cache;

  /**
   * Constructs a limited size cache with a specified maximum size and a factory function to create values.
   * The cache automatically evicts the least recently used entry when the number of entries exceeds the maximum size.
   *
   * @param maxSize      the maximum number of entries the cache can hold
   */
  public LimitedSizeCache(int maxSize) {
    this.maxSize = maxSize;

    // Use LinkedHashMap with access-order to implement LRU behavior
    this.cache = new LinkedHashMap<>(16, 0.75f, true) {
      @Override
      protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > LimitedSizeCache.this.maxSize;
      }
    };
  }

  /**
   * Retrieves a value associated with the given key from the cache, or creates it using the provided
   * factory function if it is not present. If the cache has reached its maximum size and the key is
   * not present, the value will be created using the factory but will not be stored in the cache.
   *
   * @param key          The key whose associated value is to be retrieved or created.
   * @param valueFactory A function that generates a value for the key if it is not already present.
   *
   * @return The value associated with the key, either retrieved from the cache or created using the factory function.
   */
  public V getOrCreate(K key, Function<K, V> valueFactory) {
    // Check if the cache is at capacity and key is not present
    if (cache.size() >= maxSize && !cache.containsKey(key)) {
      // At capacity and key not present - create but don't store
      return valueFactory.apply(key);
    } else {
      // Either the cache has space or key is already present
      return cache.computeIfAbsent(key, valueFactory);
    }
  }

  /**
   * Get the current size of the cache
   */
  public int size() {
    return cache.size();
  }

  /**
   * Clear all entries from the cache
   */
  public void clear() {
    cache.clear();
  }
}

