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

import org.pageseeder.diffx.token.StartElementToken;
import org.pageseeder.diffx.token.Token;
import org.pageseeder.xmlwriter.XMLWriter;

import javax.xml.XMLConstants;
import java.io.IOException;

/**
 * A basic implementation of the close element token.
 *
 * <p>It corresponds to the <code>startElement</code> SAX event.
 *
 * <p>This implementation is not namespace aware.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class StartElementTokenImpl extends TokenBase implements StartElementToken {

  /**
   * The local name of the element.
   */
  private final String name;

  /**
   * Hashcode value for this token.
   */
  private final int hashCode;

  /**
   * Creates a new open element token.
   *
   * @param name The local name of the element
   *
   * @throws NullPointerException if the name is <code>null</code>.
   */
  public StartElementTokenImpl(String name) throws NullPointerException {
    if (name == null)
      throw new NullPointerException("Element must have a name.");
    this.name = name;
    this.hashCode = toHashCode(name);
  }

  /**
   * @return Returns the name.
   */
  @Override
  public String getName() {
    return this.name;
  }

  /**
   * @return Returns the Namespace URI.
   */
  @Override
  public String getURI() {
    return XMLConstants.NULL_NS_URI;
  }

  @Override
  public int hashCode() {
    return this.hashCode;
  }

  /**
   * Returns <code>true</code> if the token is an open element token.
   *
   * @param token The token to compare with this token.
   *
   * @return <code>true</code> if this token is equal to the specified token;
   *         <code>false</code> otherwise.
   */
  @Override
  public boolean equals(Token token) {
    if (token.getClass() != this.getClass()) return false;
    StartElementTokenImpl oee = (StartElementTokenImpl) token;
    return oee.name.equals(this.name);
  }

  @Override
  public String toString() {
    return "openElement: "+this.name;
  }

  @Override
  public void toXML(XMLWriter xml) throws IOException {
    xml.openElement(this.name, false);
  }

  /**
   * Converts this token to an XML open tag.
   *
   * <p>Note that this method does not allow attributes to be put after this element.
   *
   * {@inheritDoc}
   */
  @Override
  public StringBuffer toXML(StringBuffer xml) {
    return xml.append('<').append(this.name).append('>');
  }

  /**
   * Calculates the hashcode for this token.
   *
   * @param s String from which the hashcode is calculated.
   * @return a number suitable as a hashcode.
   */
  private static int toHashCode(String s) {
    assert s != null;
    return 11 * 41 + s.hashCode();
  }
}
