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

import org.jspecify.annotations.Nullable;
import org.pageseeder.diffx.token.EndElementToken;
import org.pageseeder.diffx.token.StartElementToken;
import org.pageseeder.diffx.token.XMLToken;

import java.util.Objects;

/**
 * The token corresponding to the <code>startElement</code> SAX event.
 *
 * @author Christophe Lauret
 *
 * @version 1.1.2
 * @since 0.7.0
 */
public final class XMLEndElement extends TokenBase implements EndElementToken {

  /**
   * The corresponding open element token.
   */
  private final StartElementToken start;

  /**
   * Creates a new close element token on the default namespace URI.
   *
   * @param name The name of the element
   *
   * @throws NullPointerException If the name is <code>null</code>.
   */
  public XMLEndElement(String name) {
    this.start = new XMLStartElement(name);
  }

  /**
   * Creates a new close element token.
   *
   * @param uri       The namespace URI of the element
   * @param localName The local name of the element (excluding prefix)
   *
   * @throws NullPointerException if any of the argument is <code>null</code>.
   */
  public XMLEndElement(String uri, String localName) {
    this.start = new XMLStartElement(uri, localName);
  }

  /**
   * Creates a new close element token from the corresponding open element token.
   *
   * @param token The corresponding open element token.
   *
   * @throws NullPointerException If the name is <code>null</code>.
   */
  public XMLEndElement(StartElementToken token) {
    this.start = Objects.requireNonNull(token, "Element must have a name.");
  }

  /**
   * @return Returns the name.
   */
  @Override
  public String getName() {
    return this.start.getName();
  }

  /**
   * @return Returns the namespace URI.
   */
  @Override
  public String getNamespaceURI() {
    return this.start.getNamespaceURI();
  }

  @Override
  public StartElementToken getStartElement() {
    return this.start;
  }

  @Override
  public @Nullable String getValue() {
    return null;
  }

  @Override
  public boolean match(@Nullable StartElementToken token) {
    if (token == null) return false;
    if (token == this.start) return true;
    return token.getNamespaceURI().equals(getNamespaceURI())
        && token.getName().equals(getName());
  }

  @Override
  public int hashCode() {
    return 89 + this.start.hashCode();
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
  public boolean equals(@Nullable XMLToken token) {
    if (token == this) return true;
    if (!(token instanceof EndElementToken)) return false;
    if (this.hashCode() != token.hashCode()) return false;
    EndElementToken other = (EndElementToken) token;
    return other.getName().equals(getName()) && other.getNamespaceURI().equals(getNamespaceURI());
  }

  @Override
  public String toString() {
    if (this.start.getNamespaceURI().isEmpty()) {
      return "</" + getName() + '>';
    } else {
      return "</{" + getNamespaceURI() + "}:" + getName() + '>';
    }
  }

}
