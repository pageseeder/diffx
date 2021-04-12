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

import org.pageseeder.diffx.event.AttributeEvent;
import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.xmlwriter.XMLWriter;

import javax.xml.XMLConstants;

/**
 * A basic implementation of the attribute event.
 *
 * <p>
 * This implementation is not namespace aware.
 *
 * @author Christophe Lauret
 * @author Jean-Baptiste Reure
 * @version 0.9.0
 */
public final class AttributeEventImpl extends DiffXEventBase implements AttributeEvent {

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
   * Creates a new attribute event.
   *
   * @param name The local name of the attribute.
   * @param value The value of the attribute.
   *
   * @throws NullPointerException if any of the argument is <code>null</code>.
   */
  public AttributeEventImpl(String name, String value) throws NullPointerException {
    if (name == null)
      throw new NullPointerException("Attribute must have a name.");
    if (value == null)
      throw new NullPointerException("The attribute value cannot be null, use \"\".");
    this.name = name;
    this.value = value;
    this.hashCode = toHashCode(name, value);
  }

  @Override
  public String getName() {
    return this.name;
  }

  /**
   * Always return <code>XMLConstants.NULL_NS_URI</code>.
   */
  @Override
  public String getURI() {
    return XMLConstants.NULL_NS_URI;
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
   * Returns <code>true</code> if the event is an attribute event.
   *
   * @param e The event to compare with this event.
   *
   * @return <code>true</code> if this event is equal to the specified event;
   *         <code>false</code> otherwise.
   */
  @Override
  public boolean equals(DiffXEvent e) {
    if (e.getClass() != this.getClass())
      return false;
    AttributeEventImpl bae = (AttributeEventImpl) e;
    return bae.name.equals(this.name) && bae.value.equals(this.value);
  }

  @Override
  public String toString() {
    return "attribute: " + this.name + "=" + this.value;
  }

  @Override
  public void toXML(XMLWriter xml) throws IOException {
    xml.attribute(this.name, this.value);
  }

  @Override
  public StringBuffer toXML(StringBuffer xml) throws NullPointerException {
    xml.append(' ');
    xml.append(this.name);
    xml.append("=\"");
    xml.append(ESC.toAttributeValue(this.value));
    xml.append('"');
    return xml;
  }

  /**
   * Calculates the hashcode for this event.
   *
   * @param name The attribute name.
   * @param value The attribute value.
   * @return a number suitable as a hashcode.
   */
  private static int toHashCode(String name, String value) {
    assert name != null;
    assert value != null;
    int hash = 23;
    hash = hash * 37 + name.hashCode();
    hash = hash * 37 + value.hashCode();
    return hash;
  }

}
