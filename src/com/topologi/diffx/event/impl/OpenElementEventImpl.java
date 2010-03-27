/*
 * This file is part of the DiffX library.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at 
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package com.topologi.diffx.event.impl;

import java.io.IOException;

import com.topologi.diffx.util.Constants;
import com.topologi.diffx.event.DiffXEvent;
import com.topologi.diffx.event.OpenElementEvent;
import com.topologi.diffx.xml.XMLWriter;

/**
 * A basic implementation of the close element event.
 * 
 * <p>It corresponds to the <code>startElement</code> SAX event.
 * 
 * <p>This implementation is not namespace aware.
 * 
 * @author Christophe Lauret
 * @version 17 May 2005
 */
public final class OpenElementEventImpl extends DiffXEventBase implements OpenElementEvent {

  /**
   * The local name of the element.
   */
  private final String name;

  /**
   * Creates a new open element event.
   * 
   * @param name The local name of the element
   * 
   * @throws NullPointerException if the name is <code>null</code>.
   */
  public OpenElementEventImpl(String name) throws NullPointerException {
    if (name == null)
      throw new NullPointerException("Element must have a name.");
    this.name = name;
  }

  /**
   * @return Returns the name.
    */
  public String getName() {
    return this.name;
  }

  /**
   * @return Returns the uri.
   */
  public String getURI() {
    return Constants.DEFAULT_URI;
  }

  /**
   * {@inheritDoc}
   */
  public int hashCode() {
    return this.name.hashCode();
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
    OpenElementEventImpl oee = (OpenElementEventImpl)e;
    return (oee.name.equals(this.name));
  }

  /**
   * {@inheritDoc}
   */
  public String toString() {
    return "openElement: "+this.name;
  }

  /**
   * {@inheritDoc}
   */
  public void toXML(XMLWriter xml) throws IOException {
    xml.openElement(this.name, false);
  }

  /**
   * Converts this event to an XML open tag.
   * 
   * <p>Note that this method does not allow attributes to be put after this element.
   * 
   * {@inheritDoc}
   */
  public StringBuffer toXML(StringBuffer xml) {
    return xml.append('<').append(this.name).append('>');
  }

}
