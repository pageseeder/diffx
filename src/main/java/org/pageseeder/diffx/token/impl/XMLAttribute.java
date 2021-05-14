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

import org.pageseeder.diffx.token.AttributeToken;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.xmlwriter.XMLWriter;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;

/**
 * A namespace aware implementation of the attribute token for XML.
 *
 * @author Christophe Lauret
 * @author Jean-Baptiste Reure
 * @version 0.9.0
 * @since 0.5.0
 */
public final class XMLAttribute extends TokenBase implements AttributeToken {

  /**
   * The namespace URI this attribute belongs to.
   */
  private final String uri;

  /**
   * The name of the attribute.
   */
  private final String name;

  /**
   * The value of the attribute.
   */
  private final String value;

  /**
   * A suitable hashcode value.
   */
  private final int hashCode;

  /**
   * Creates a new attribute token with no namespace.
   *
   * @param name  The local name of the attribute.
   * @param value The value of the attribute.
   *
   * @throws NullPointerException if any of the argument is <code>null</code>.
   */
  public XMLAttribute(String name, String value) throws NullPointerException {
    if (name == null)
      throw new NullPointerException("Attribute must have a name.");
    if (value == null)
      throw new NullPointerException("The attribute value cannot be null, use \"\".");
    this.name = name;
    this.value = value;
    this.uri = XMLConstants.NULL_NS_URI;
    this.hashCode = toHashCode(this.uri, name, value);
  }

  /**
   * Creates a new attribute token.
   *
   * @param uri   The uri of the attribute.
   * @param name  The local name of the attribute.
   * @param value The value of the attribute.
   *
   * @throws NullPointerException if any of the argument is <code>null</code>.
   */
  public XMLAttribute(String uri, String name, String value) throws NullPointerException {
    if (name == null)
      throw new NullPointerException("Attribute must have a name.");
    if (value == null)
      throw new NullPointerException("The attribute value cannot be null, use \"\".");
    if (uri == null)
      throw new NullPointerException("The uri value cannot be null, use \"\".");
    this.name = name;
    this.value = value;
    this.uri = uri;
    this.hashCode = toHashCode(uri, name, value);
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public String getNamespaceURI() {
    return this.uri;
  }

  @Override
  public String getValue() {
    return this.value;
  }

  @Override
  public int hashCode() {
    return this.hashCode;
  }

  /**
   * Returns <code>true</code> if the token is an attribute token.
   *
   * @param token The token to compare with this token.
   *
   * @return <code>true</code> if this token is equal to the specified token;
   * <code>false</code> otherwise.
   */
  @Override
  public boolean equals(XMLToken token) {
    if (token == this) return true;
    if (!(token instanceof AttributeToken)) return false;
    if (this.hashCode != token.hashCode()) return false;
    AttributeToken other = (AttributeToken) token;
    return this.name.equals(other.getName())
        && this.value.equals(other.getValue())
        && this.uri.equals(other.getNamespaceURI());
  }

  @Override
  public String toString() {
    if (this.uri.isEmpty()) {
      return "@" + this.name + "=" + this.value;
    } else {
      return "@{" + this.uri + "}" + this.name + "=" + this.value;
    }
  }

  @Override
  public void toXML(XMLWriter xml) throws IOException {
    xml.attribute(this.uri, this.name, this.value);
  }

  @Override
  public void toXML(XMLStreamWriter xml) throws XMLStreamException {
    if (this.uri.isEmpty())
      xml.writeAttribute(this.name, this.value);
    else
      xml.writeAttribute(this.uri, this.name, this.value);
  }

  /**
   * Calculates the hashcode for this token.
   *
   * @param uri   The URI.
   * @param name  The attribute name.
   * @param value The attribute value.
   *
   * @return a number suitable as a hashcode.
   */
  private static int toHashCode(String uri, String name, String value) {
    assert uri != null;
    assert name != null;
    assert value != null;
    // Code below follows from Objects#hash method
    int hash = 17;
    hash = hash * 31 + uri.hashCode();
    hash = hash * 31 + name.hashCode();
    hash = hash * 31 + value.hashCode();
    return hash;
  }

}
