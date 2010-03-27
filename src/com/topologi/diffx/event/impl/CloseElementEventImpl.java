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
import com.topologi.diffx.event.CloseElementEvent;
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
public final class CloseElementEventImpl extends DiffXEventBase implements CloseElementEvent {

  /**
   * The corresponding open element event.
   */
  private final OpenElementEvent open;

  /**
   * Creates a new close element event.
   * 
   * @param name The local name of the element
   * 
   * @throws NullPointerException If the name is <code>null</code>.
   */
  public CloseElementEventImpl(String name) throws NullPointerException {
    if (name == null)
      throw new NullPointerException("Element must have a name.");
    this.open = new OpenElementEventImpl(name);
  }

  /**
   * Creates a new close element event that corresponds to the given open element.
   * 
   * @param event The corresponding open element.
   * 
   * @throws NullPointerException If the name is <code>null</code>.
   */
  public CloseElementEventImpl(OpenElementEvent event) throws NullPointerException {
    if (event == null)
      throw new NullPointerException("A close element must correspond to an open element.");
    this.open = event;
  }

  /**
   * @return Returns the name.
    */
  public String getName() {
    return this.open.getName();
  }

  /**
   * Always return the empty URI.  
   * 
   * @see Constants#DEFAULT_URI
   * 
   * @return Returns the uri.
   */
  public String getURI() {
    return Constants.DEFAULT_URI;
  }

  /**
   * {@inheritDoc}
   */
  public OpenElementEvent getOpenElement() {
    return this.open;
  }

  /**
   * Returns <code>true</code> if the open element has the same name.
   * 
   * {@inheritDoc}
   */
  public boolean match(OpenElementEvent event) {
    if (event == null) return false;
    if (event == this.open) return true;
    return (event.getName().equals(this.getName()));
  }

  /**
   * {@inheritDoc}
   */
  public int hashCode() {
    return 1 + this.open.hashCode();
  }

  /**
   * Returns <code>true</code> if the event is a close element
   * and has the same name.  
   * 
   * @param e The event to compare with this event.
   * 
   * @return <code>true</code> if this event is equal to the specified event;
   *         <code>false</code> otherwise.
   */
  public boolean equals(DiffXEvent e) {
    if (e.getClass() != this.getClass()) return false;
    CloseElementEventImpl ce = (CloseElementEventImpl)e;
    return (ce.getName().equals(this.getName()));
  }

  /**
   * {@inheritDoc}
   */
  public String toString() {
    return "closeElement: "+this.getName();
  }

  /**
   * {@inheritDoc}
   */
  public void toXML(XMLWriter xml) throws IOException {
    xml.closeElement();
  }

  /**
   * {@inheritDoc}
   */
  public StringBuffer toXML(StringBuffer xml) throws NullPointerException {
    xml.append("</").append(this.getName()).append('>');
    return xml;
  }

}
