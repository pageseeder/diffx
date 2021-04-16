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
package org.pageseeder.diffx.event.impl;

import java.io.IOException;

import org.pageseeder.diffx.event.EndElementToken;
import org.pageseeder.diffx.event.Token;
import org.pageseeder.diffx.event.StartElementToken;
import org.pageseeder.xmlwriter.XMLWriter;

/**
 * The token corresponding to the <code>startElement</code> SAX event.
 *
 * @author Christophe Lauret
 * @version 28 March 2010
 */
public final class EndElementTokenNSImpl extends TokenBase implements EndElementToken {

  /**
   * The corresponding open element token.
   */
  private final StartElementToken open;

  /**
   * Creates a new close element token on the default namespace URI.
   *
   * @param name The local name of the element
   *
   * @throws NullPointerException If the name is <code>null</code>.
   */
  public EndElementTokenNSImpl(String name) throws NullPointerException {
    if (name == null)
      throw new NullPointerException("Element must have a name.");
    this.open = new StartElementTokenNSImpl(name);
  }

  /**
   * Creates a new close element token.
   *
   * @param uri  The namespace URI of the element
   * @param name The local name of the element
   *
   * @throws NullPointerException if any of the argument is <code>null</code>.
   */
  public EndElementTokenNSImpl(String uri, String name) throws NullPointerException {
    if (uri == null)
      throw new NullPointerException("The URI cannot be null, use \"\".");
    if (name == null)
      throw new NullPointerException("Element must have a name.");
    this.open = new StartElementTokenNSImpl(uri, name);
  }

  /**
   * Creates a new close element token from the corresponding open element token.
   *
   * @param token The corresponding open element token.
   *
   * @throws NullPointerException If the name is <code>null</code>.
   */
  public EndElementTokenNSImpl(StartElementToken token) throws NullPointerException {
    if (token == null)
      throw new NullPointerException("Element must have a name.");
    this.open = token;
  }

  /**
   * @return Returns the name.
   */
  @Override
  public String getName() {
    return this.open.getName();
  }

  /**
   * @return Returns the namespace URI.
   */
  @Override
  public String getURI() {
    return this.open.getURI();
  }

  @Override
  public StartElementToken getOpenElement() {
    return this.open;
  }

  @Override
  public boolean match(StartElementToken token) {
    if (token == null) return false;
    if (token == this.open) return true;
    return token.getURI().equals(getURI())
        &&  token.getName().equals(getName());
  }

  @Override
  public int hashCode() {
    return 89 + this.open.hashCode();
  }

  /**
   * Returns <code>true</code> if the token is a close element token.
   *
   * @param e The token to compare with this token.
   *
   * @return <code>true</code> if this token is equal to the specified token;
   *         <code>false</code> otherwise.
   */
  @Override
  public boolean equals(Token e) {
    if (e.getClass() != this.getClass()) return false;
    EndElementTokenNSImpl ce = (EndElementTokenNSImpl)e;
    return ce.getURI().equals(getURI())
        &&  ce.getName().equals(getName());
  }

  @Override
  public String toString() {
    return "closeElement: "+getName()+" ["+getURI()+"]";
  }

  @Override
  public void toXML(XMLWriter xml) throws IOException {
    xml.closeElement();
  }

  @Override
  public StringBuffer toXML(StringBuffer xml) throws NullPointerException {
    // TODO: handle namespaces.
    xml.append("</").append(getName()).append('>');
    return xml;
  }
}
