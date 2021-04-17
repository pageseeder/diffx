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

import javax.xml.XMLConstants;
import java.util.Objects;

/**
 * A namespace
 */
public final class Namespace {

  /**
   * Namespace instance to use to represent that there is no Namespace.
   * <p>
   * Defined by the Namespace specification to be "".
   */
  public static final Namespace NO_NAMESPACE = new Namespace(XMLConstants.NULL_NS_URI, XMLConstants.DEFAULT_NS_PREFIX);

  /**
   * Namespace instance for the built-in XML namespace.
   */
  public static final Namespace XML_NAMESPACE = new Namespace(XMLConstants.XML_NS_URI, XMLConstants.XML_NS_PREFIX);

  /**
   * Pseudo-namespace instance for XML namespace declarations.
   */
  public static final Namespace XMLNS_ATTRIBUTE = new Namespace(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, XMLConstants.XMLNS_ATTRIBUTE);

  /**
   * The namespace URI.
   */
  private final String uri;

  /**
   * The namespace prefix
   */
  private final String prefix;

  public Namespace(String uri, String prefix) {
    this.uri = Objects.requireNonNull(uri);
    this.prefix = Objects.requireNonNull(prefix);
  }

  public String getUri() {
    return this.uri;
  }

  public String getPrefix() {
    return this.prefix;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Namespace namespace = (Namespace) o;
    return getUri().equals(namespace.getUri()) && getPrefix().equals(namespace.getPrefix());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getUri(), getPrefix());
  }

  @Override
  public String toString() {
    return "{" + this.uri + "=" + this.prefix + "}";
  }

  /**
   * @param namespace namespace to check.
   *
   * @return true if the namespace should be declared; false if it matches no namespace of built-in XML namespaces.
   */
  public static boolean isDeclarable(Namespace namespace) {
    return !(XML_NAMESPACE.equals(namespace) || XMLNS_ATTRIBUTE.equals(namespace) || NO_NAMESPACE.equals(namespace)
    );
  }
}
