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
import org.pageseeder.diffx.token.StartElementToken;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.xmlwriter.XMLWriter;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;

/**
 * The token corresponding to the <code>startElement</code> SAX event.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class XMLStartElement extends TokenBase implements XMLToken, StartElementToken {

  /**
   * The namespace URI of the element.
   */
  private final String uri;

  /**
   * The local name of the element.
   */
  private final String name;

  /**
   * Hashcode value for this token.
   */
  private final int hashCode;

  /**
   * Creates a new start element token with no namespace.
   *
   * @param name The name of the element
   *
   * @throws NullPointerException if the name is <code>null</code>.
   * @see XMLConstants#NULL_NS_URI
   */
  public XMLStartElement(String name) throws NullPointerException {
    if (name == null)
      throw new NullPointerException("Element must have a name.");
    this.uri = XMLConstants.NULL_NS_URI;
    this.name = name;
    this.hashCode = toHashCode(XMLConstants.NULL_NS_URI, name);
  }

  /**
   * Creates a new open element token.
   *
   * @param uri       The namespace URI of the element
   * @param localName The local name of the element (excluding prefix)
   *
   * @throws NullPointerException if any of the argument is <code>null</code>.
   */
  public XMLStartElement(String uri, String localName) throws NullPointerException {
    if (uri == null)
      throw new NullPointerException("The URI cannot be null, use \"\".");
    if (localName == null)
      throw new NullPointerException("Element must have a name.");
    this.uri = uri;
    this.name = localName;
    this.hashCode = toHashCode(uri, localName);
  }

  @Override
  public @NotNull String getName() {
    return this.name;
  }

  @Override
  public @NotNull String getNamespaceURI() {
    return this.uri;
  }

  @Override
  public int hashCode() {
    return this.hashCode;
  }

  /**
   * Returns <code>true</code> if the token is a open element token.
   *
   * @param token The token to compare with this token.
   *
   * @return <code>true</code> if this token is equal to the specified token;
   * <code>false</code> otherwise.
   */
  @Override
  public boolean equals(XMLToken token) {
    if (token == this) return true;
    if (!(token instanceof StartElementToken)) return false;
    if (this.hashCode != token.hashCode()) return false;
    StartElementToken other = (StartElementToken) token;
    return this.name.equals(other.getName()) && this.uri.equals(other.getNamespaceURI());
  }

  @Override
  public String toString() {
    if (this.uri.isEmpty()) {
      return "<" + this.name + '>';
    } else {
      return "<{" + this.uri + "}:" + this.name + '>';
    }
  }

  @Override
  public void toXML(XMLWriter xml) throws IOException {
    xml.openElement(this.uri, this.name, false);
  }

  @Override
  public void toXML(@NotNull XMLStreamWriter xml) throws XMLStreamException {
    // We shouldn't specify a namespace URI if empty on an XMLStreamWriter
    if (this.uri.isEmpty()) {
      xml.writeStartElement(this.name);
    } else {
      xml.writeStartElement(this.uri, this.name);
    }
  }

  @Override
  public String getValue() {
    return null;
  }

  /**
   * Calculates the hashcode for this token.
   *
   * @param uri  The namespace URI.
   * @param name The element name.
   *
   * @return a number suitable as a hashcode.
   */
  private int toHashCode(String uri, String name) {
    assert uri != null;
    assert name != null;
    int hash = 107;
    hash = hash * 13 + uri.hashCode();
    hash = hash * 13 + name.hashCode();
    return hash;
  }

}
