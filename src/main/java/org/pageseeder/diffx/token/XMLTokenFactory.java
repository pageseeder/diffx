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
package org.pageseeder.diffx.token;

import org.pageseeder.diffx.token.impl.XMLAttribute;
import org.pageseeder.diffx.token.impl.XMLEndElement;
import org.pageseeder.diffx.token.impl.XMLStartElement;

/**
 * Factory for tokens.
 *
 * <p>This class is designed to returned tokens that are optimised for the type of sequence
 * that it is being inserted in.
 *
 * <p>Non-namespace aware objects are lighter than namespace aware ones.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 * @since 0.5.0
 */
public final class XMLTokenFactory {

  /**
   * Indicates whether the factory should generate namespace tokens.
   */
  private final boolean isNamespaceAware;

  /**
   * Creates a new namespace-aware factory for tokens.
   */
  public XMLTokenFactory() {
    this.isNamespaceAware = true;
  }

  /**
   * Creates a factory for tokens.
   *
   * @param isNamespaceAware <code>true</code> to create new namespace aware factory;
   *                         <code>false</code> otherwise.
   */
  public XMLTokenFactory(boolean isNamespaceAware) {
    this.isNamespaceAware = isNamespaceAware;
  }

  public boolean isNamespaceAware() {
    return this.isNamespaceAware;
  }

  /**
   * Returns the open element token from the uri and name given.
   *
   * <p>If the factory is namespace aware, it returns an open element implementation
   * using the namespace URI and the name.
   *
   * <p>If the factory is NOT namespace aware, it returns an open element implementation
   * using the specified name.
   *
   * <p>Use this implementation if the name of the element is determined prior to the
   * call of this method.
   *
   * @param uri  The namespace URI of the element (ignored if not namespace aware)
   * @param name The name of the element.
   *
   * @return The open element token from the uri and name given.
   */
  public StartElementToken newStartElement(String uri, String name) {
    return this.isNamespaceAware ? new XMLStartElement(uri, name) : new XMLStartElement(name);
  }

  /**
   * Returns the open element token from the uri and names given.
   *
   * <p>If the factory is namespace aware, it returns an open element implementation
   * using the namespace URI and the local name.
   *
   * <p>If the factory is NOT namespace aware, it returns an open element implementation
   * using the qName (namespace-prefixed name).
   *
   * @param uri       The namespace URI of the element (ignored if not namespace aware)
   * @param localName The local name of the element.
   * @param qName     The qualified name of the element.
   *
   * @return The open element token from the uri and name given.
   */
  public StartElementToken newStartElement(String uri, String localName, String qName) {
    return this.isNamespaceAware ? new XMLStartElement(uri, localName) : new XMLStartElement(qName);
  }

  /**
   * Returns the close element token from the corresponding open element token.
   *
   * @param open The corresponding open element token.
   *
   * @return The close element token from the corresponding open element token.
   */
  public EndElementToken newEndElement(StartElementToken open) {
    return new XMLEndElement(open);
  }

  /**
   * Returns the attribute token from the name and value given.
   *
   * <p>If the factory is namespace aware, it returns an attribute on the NULL URI namespace.
   *
   * <p>If the factory is NOT namespace aware, it returns an attribute implementation
   * using the specified name.
   *
   * <p>Use this implementation if the name of the element is determined prior to the
   * call of this method.
   *
   * @param name  The name of the attribute.
   * @param value The value of the attribute.
   *
   * @return The open element token from the uri and name given.
   */
  public AttributeToken newAttribute(String name, String value) {
    return new XMLAttribute(name, value);
  }

  /**
   * Returns the attribute token from the name and value given.
   *
   * <p>If the factory is namespace-aware, it returns an attribute implementation
   * using the namespace URI and the name.
   *
   * <p>If the factory is NOT namespace-aware, it returns an attribute implementation
   * using the specified name.
   *
   * <p>Use this implementation if the name of the element is determined prior to the
   * call of this method.
   *
   * @param uri   The namespace URI of the attribute (ignored if not namespace aware)
   * @param name  The name of the attribute.
   * @param value The value of the attribute.
   *
   * @return The open element token from the uri and name given.
   */
  public AttributeToken newAttribute(String uri, String name, String value) {
    return this.isNamespaceAware ? new XMLAttribute(uri, name, value) : new XMLAttribute(name, value);
  }

  /**
   * Returns the attribute token from the name and value given.
   *
   * <p>If the factory is namespace-aware, it returns an attribute implementation
   * using the namespace URI and the local name.
   *
   * <p>If the factory is NOT namespace-aware, it returns an attribute implementation
   * using the qName (namespace-prefixed name).
   *
   * @param uri       The namespace URI of the attribute (ignored if not namespace aware)
   * @param localName The local name of the attribute.
   * @param qName     The qualified name of the attribute.
   * @param value     The value of the attribute.
   *
   * @return The open element token from the uri and name given.
   */
  public AttributeToken newAttribute(String uri, String localName, String qName, String value) {
    return this.isNamespaceAware ? new XMLAttribute(uri, localName, value) : new XMLAttribute(qName, value);
  }

}
