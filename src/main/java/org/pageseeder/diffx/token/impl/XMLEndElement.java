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
package org.pageseeder.diffx.token.impl;

import org.jetbrains.annotations.NotNull;
import org.pageseeder.diffx.token.EndElementToken;
import org.pageseeder.diffx.token.StartElementToken;
import org.pageseeder.diffx.token.XMLToken;

/**
 * The token corresponding to the <code>startElement</code> SAX event.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class XMLEndElement extends TokenBase implements EndElementToken {

  /**
   * The corresponding open element token.
   */
  private final StartElementToken open;

  /**
   * Creates a new close element token on the default namespace URI.
   *
   * @param name The name of the element
   *
   * @throws NullPointerException If the name is <code>null</code>.
   */
  public XMLEndElement(String name) throws NullPointerException {
    if (name == null)
      throw new NullPointerException("Element must have a name.");
    this.open = new XMLStartElement(name);
  }

  /**
   * Creates a new close element token.
   *
   * @param uri       The namespace URI of the element
   * @param localName The local name of the element (excluding prefix)
   *
   * @throws NullPointerException if any of the argument is <code>null</code>.
   */
  public XMLEndElement(String uri, String localName) throws NullPointerException {
    if (uri == null)
      throw new NullPointerException("The URI cannot be null, use \"\".");
    if (localName == null)
      throw new NullPointerException("Element must have a name.");
    this.open = new XMLStartElement(uri, localName);
  }

  /**
   * Creates a new close element token from the corresponding open element token.
   *
   * @param token The corresponding open element token.
   *
   * @throws NullPointerException If the name is <code>null</code>.
   */
  public XMLEndElement(StartElementToken token) throws NullPointerException {
    if (token == null)
      throw new NullPointerException("Element must have a name.");
    this.open = token;
  }

  /**
   * @return Returns the name.
   */
  @Override
  public @NotNull String getName() {
    return this.open.getName();
  }

  /**
   * @return Returns the namespace URI.
   */
  @Override
  public @NotNull String getNamespaceURI() {
    return this.open.getNamespaceURI();
  }

  @Override
  public StartElementToken getOpenElement() {
    return this.open;
  }

  @Override
  public String getValue() {
    return null;
  }

  @Override
  public boolean match(StartElementToken token) {
    if (token == null) return false;
    if (token == this.open) return true;
    return token.getNamespaceURI().equals(getNamespaceURI())
        && token.getName().equals(getName());
  }

  @Override
  public int hashCode() {
    return 89 + this.open.hashCode();
  }

  /**
   * Returns <code>true</code> if the token is a close element token.
   *
   * @param token The token to compare with this token.
   *
   * @return <code>true</code> if this token is equal to the specified token;
   * <code>false</code> otherwise.
   */
  @Override
  public boolean equals(XMLToken token) {
    if (token == this) return true;
    if (!(token instanceof EndElementToken)) return false;
    if (this.hashCode() != token.hashCode()) return false;
    EndElementToken other = (EndElementToken) token;
    return other.getName().equals(getName()) && other.getNamespaceURI().equals(getNamespaceURI());
  }

  @Override
  public String toString() {
    if (open.getNamespaceURI().isEmpty()) {
      return "</" + getName() + '>';
    } else {
      return "</{" + getNamespaceURI() + "}:" + getName() + '>';
    }
  }

}
