/*
 * This file is part of the DiffX library.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at 
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package com.topologi.diffx.event.impl;

import java.io.IOException;

import com.topologi.diffx.event.AttributeEvent;
import com.topologi.diffx.event.DiffXEvent;
import com.topologi.diffx.xml.XMLWriter;

/**
 * A basic implementation of the attribute event.
 * 
 * <p>This implementation is not namespace aware.
 * 
 * @author Christophe Lauret, Jean-Baptiste Reure
 * @version 3 April 2005
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
   * Creates a new attribute event.
   *
   * @param name  The local name of the attribute.
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
  }

  /**
   * {@inheritDoc}
   */
  public String getName() {
    return this.name;
  }

  /**
   * Always return <code>null</code>.
   * 
   * {@inheritDoc}
   */
  public String getURI() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  public String getValue() {
    return this.value;
  }

  /**
   * {@inheritDoc}
   */
  public int hashCode() {
    return this.name.hashCode() + this.value.hashCode();
  }

  /**
   * Returns <code>true</code> if the event is a  
   * 
   * @param e The event to compare with this event.
   * 
   * @return <code>true</code> if this event is equal to the specified event;
   *         <code>false</code> otherwise.
   */
  public boolean equals(DiffXEvent e) {
    if (e.getClass() != this.getClass()) return false;
    AttributeEventImpl bae = (AttributeEventImpl)e;
    return (bae.name.equals(this.name)
         && bae.value.equals(this.value));
  }

  /**
   * {@inheritDoc}
   */
  public String toString() {
    return "attribute: "+this.name+"="+this.value;
  }

  /**
   * {@inheritDoc}
   */
  public void toXML(XMLWriter xml) throws IOException {
    xml.attribute(this.name, this.value);
  }

  /**
   * {@inheritDoc}
   */
  public StringBuffer toXML(StringBuffer xml) throws NullPointerException {
    xml.append(' ');
    xml.append(this.name);
    xml.append("=\"");
    xml.append(ESC.toAttributeValue(this.value));
    xml.append('"');
    return xml;
  }

}
