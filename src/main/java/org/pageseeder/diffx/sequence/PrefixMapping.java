/*
 * Copyright 2010-2021 Allette Systems (Australia)
 *    http://www.allette.com.au
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

import org.pageseeder.diffx.xml.NamespaceSet;

@Deprecated
public class PrefixMapping {

  final NamespaceSet namespaces;

  public PrefixMapping() {
    this.namespaces = new NamespaceSet();
  }

  public PrefixMapping(NamespaceSet namespaces) {
    this.namespaces = namespaces;
  }

  public NamespaceSet getNamespaces() {
    return namespaces;
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
    return this.namespaces.add(uri, prefix);
  }

}
