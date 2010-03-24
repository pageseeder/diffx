package com.topologi.diffx.sequence;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Provides a mapping of namespace URIs to prefixes.
 * 
 * <p>This class can be used to reconstruct the qualified element or attribute names.
 * 
 * <p>Note that for each namespace URI there can only be one prefix.
 * 
 * @author Christophe Lauret
 * @version 6 December 2008
 * 
 * @since 0.7
 */
public final class PrefixMapping {

  /**
   * Maps namespace URIs to prefixes.
   */
  private final Hashtable<String,String> mappings = new Hashtable<String,String>();

  /**
   * Add the specified mapping if the namespace URI has not beem mapped before.
   * 
   * <p>This method will garantee that the mapping is actually unique, that is that
   * the namespace URI correspond to one and only one prefix and that the prefix only
   * corresponds to one and only one namespace URI.
   * 
   * @param uri    The namespace URI to map. 
   * @param prefix The prefix to use.
   * 
   * @throws NullPointerException if the URI or prefix is <code>null</code>
   */
  public void add(String uri, String prefix) throws NullPointerException {
    if (!this.mappings.containsKey(uri)) {
      int count = 0;
      String actualPrefix = prefix;
      while (this.mappings.contains(actualPrefix))
        actualPrefix = prefix + (count++);  
      this.mappings.put(uri, actualPrefix);
    }
  }

  /**
   * Returns an enumeration of the namespace URIs used in this mapping.
   * 
   * @return An enumeration of the namespace URIs used in this mapping.
   */
  public Enumeration getURIs() {
    return this.mappings.keys();
  }

  /**
   * Returns the prefix corresponding to the given namespace URI.
   * 
   * @param uri The namespace URI to map.
   * 
   * @return The corresponding prefix. 
   */
  public String getPrefix(String uri) {
    return uri == null? "" : (String)this.mappings.get(uri);
  }

}
