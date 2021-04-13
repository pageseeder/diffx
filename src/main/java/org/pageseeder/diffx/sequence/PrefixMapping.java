/*
 * Copyright 2010-2015 Allette Systems (Australia)
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
package org.pageseeder.diffx.sequence;

import java.util.*;

/**
 * Provides a mapping of namespace URIs to prefixes.
 *
 * <p>This class can be used to reconstruct the qualified element or attribute names.
 *
 * <p>Note that for each namespace URI there can only be one prefix.
 *
 * @author Christophe Lauret
 *
 * @version 0.9.0
 * @since 0.7
 */
public final class PrefixMapping extends AbstractCollection<Namespace> implements Collection<Namespace> {

  /**
   * Maps namespace URIs to namespace instances.
   */
  private final Map<String, Namespace> namespacesByUri = new HashMap<>();

  /**
   * Maps prefixes to namespace instances.
   */
  private final Map<String, Namespace> namespacesByPrefix = new HashMap<>();

  public PrefixMapping() {
  }

  /**
   * Create a new prefix mapping with the specified namespace.
   */
  public PrefixMapping(Namespace namespace) {
    this.namespacesByUri.put(namespace.getUri(), namespace);
    this.namespacesByPrefix.put(namespace.getPrefix(), namespace);
  }

  /**
   * Add the specified mapping if the namespace URI has not been mapped before.
   *
   * <p>This method will ensure that the mapping is actually unique, that is that
   * the namespace URI correspond to one and only one prefix and that the prefix only
   * corresponds to one and only one namespace URI.
   *
   * @param uri    The namespace URI to map.
   * @param prefix The prefix to use.
   *
   * @throws NullPointerException if the URI or prefix is <code>null</code>
   */
  public boolean add(String uri, String prefix) throws NullPointerException {
    assert uri != null;
    assert prefix != null;
    if (!this.namespacesByUri.containsKey(uri)) {
      int count = 1;
      String actualPrefix = prefix;
      while (this.namespacesByPrefix.containsKey(actualPrefix)) {
        actualPrefix = (prefix.isEmpty() ? "ns" : prefix) + count++;
      }
      Namespace namespace = new Namespace(uri, actualPrefix);
      this.namespacesByUri.put(uri, namespace);
      this.namespacesByPrefix.put(actualPrefix, namespace);
      return true;
    }
    return false;
  }

  /**
   * Add the specified mappings if the namespace URI has not been mapped before.
   */
  @Override
  public boolean add(Namespace namespace) {
    return add(namespace.getUri(), namespace.getPrefix());
  }

  /**
   * Clears the prefix mapping.
   */
  @Override
  public void clear() {
    this.namespacesByUri.clear();
    this.namespacesByPrefix.clear();
  }

  /**
   * Returns the size of the
   */
  @Override
  public int size() {
    return this.namespacesByUri.size();
  }

  /**
   * Add the specified mappings if the namespace URI has not been mapped before.
   *
   * <p>This method will ensure that the mappings are actually unique, that is that
   * the namespace URI correspond to one and only one prefix and that the prefix only
   * corresponds to one and only one namespace URI.
   *
   * @param other more mappings
   */
  public void add(PrefixMapping other) {
    this.addAll(other);
  }

  /**
   * @return An iterator over the URIs used in this mapping.
   */
  @Override
  public Iterator<Namespace> iterator() {
    return Collections.unmodifiableCollection(this.namespacesByUri.values()).iterator();
  }

  /**
   * @return the prefix mapping as a map.
   */
  public Map<String, String> toMap() {
    Map<String,String> map = new HashMap<>(this.namespacesByUri.values().size());
    this.namespacesByUri.values().forEach(namespace -> map.put(namespace.getUri(), namespace.getPrefix()));
    return map;
  }

  /**
   * Returns the prefix corresponding to the given namespace URI.
   *
   * @param uri The namespace URI.
   *
   * @return The corresponding prefix or <code>null</code> if not mapped.
   */
  public String getPrefix(String uri) {
    Namespace namespace = this.namespacesByUri.get(uri);
    return namespace != null ? namespace.getPrefix() : null;
  }

  /**
   * Returns the prefix corresponding to the given namespace URI.
   *
   * @param prefix The namespace prefix.
   *
   * @return The corresponding URI or <code>null</code> if not mapped.
   */
  public String getUri(String prefix) {
    Namespace namespace = this.namespacesByPrefix.get(prefix);
    return namespace != null ? namespace.getUri() : null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PrefixMapping mapping = (PrefixMapping) o;
    return this.namespacesByUri.equals(mapping.namespacesByUri);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.namespacesByUri.values());
  }

  @Override
  public String toString() {
    return "PrefixMapping{" + this.namespacesByUri.values() + '}';
  }

  /**
   * Merge two prefix mapping and return a new prefix mapping
   *
   * <p>The first prefix mapping takes precedence over the second one, so if a namespace URI is mapped different
   * prefixes, the prefix from first mapping is used.</p>
   *
   * @return a new prefix mapping including namespaces from both mappings
   */
  public static PrefixMapping merge(PrefixMapping a, PrefixMapping b) {
    PrefixMapping mapping = new PrefixMapping();
    mapping.add(a);
    mapping.add(b);
    return mapping;
  }

  /**
   * Merge two prefix mapping and return a new prefix mapping
   *
   * <p>The first prefix mapping takes precedence over the second one, so if a namespace URI is mapped different
   * prefixes, the prefix from first mapping is used.</p>
   *
   * @return a new prefix mapping including namespaces from both mappings
   */
  public static PrefixMapping noNamespace() {
    return new PrefixMapping(Namespace.NO_NAMESPACE);
  }

}
