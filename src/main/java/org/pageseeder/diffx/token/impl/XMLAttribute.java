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
import org.pageseeder.diffx.token.AttributeToken;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.xmlwriter.XMLWriter;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.util.Objects;

/**
 * An implementation of the attribute token for XML.
 *
 * @author Christophe Lauret
 * @author Jean-Baptiste Reure
 *
 * @since 0.5.0
 * @version 0.9.0
 */
public final class XMLAttribute extends TokenBase implements AttributeToken {

  /**
   * The namespace URI this attribute belongs to.
   */
  private final @NotNull String uri;

  /**
   * The name of the attribute.
   */
  private final @NotNull String name;

  /**
   * The value of the attribute.
   */
  private final @NotNull String value;

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
  public XMLAttribute(@NotNull String name, @NotNull String value) {
    this(XMLConstants.NULL_NS_URI, name, value);
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
  public XMLAttribute(@NotNull String uri, @NotNull String name, @NotNull String value) {
    this.name = Objects.requireNonNull(name, "Attribute must have a name.");
    this.value = Objects.requireNonNull(value, "The attribute value cannot be null, use \"\".");
    this.uri = Objects.requireNonNull(uri, "The uri value cannot be null, use \"\".");
    this.hashCode = toHashCode(uri, name, value);
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
  public @NotNull String getValue() {
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
  public void toXML(@NotNull XMLWriter xml) throws IOException {
    xml.attribute(this.uri, this.name, this.value);
  }

  @Override
  public void toXML(@NotNull XMLStreamWriter xml) throws XMLStreamException {
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
