/*
 * This file is part of the DiffX library.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at 
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package com.topologi.diffx.event.impl;

import java.io.IOException;

import com.topologi.diffx.event.CloseElementEvent;
import com.topologi.diffx.event.DiffXEvent;
import com.topologi.diffx.event.OpenElementEvent;
import com.topologi.diffx.xml.XMLWriter;

/**
 * The event corresponding to the <code>startElement</code> SAX event.
 * 
 * @author Christophe Lauret
 * @version 28 March 2010
 */
public final class CloseElementEventNSImpl extends DiffXEventBase implements CloseElementEvent {

  /**
   * The corresponding open element event.
   */
  private final OpenElementEvent open;

  /**
   * Creates a new close element event on the default namespace URI.
   * 
   * @param name The local name of the element
   * 
   * @throws NullPointerException If the name is <code>null</code>.
   */
  public CloseElementEventNSImpl(String name) throws NullPointerException {
    if (name == null)
      throw new NullPointerException("Element must have a name.");
    this.open = new OpenElementEventNSImpl(name);
  }

  /**
   * Creates a new close element event
   * 
   * @param uri  The namespace URI of the element    
   * @param name The local name of the element
   * 
   * @throws NullPointerException if any of the argument is <code>null</code>.
   */
  public CloseElementEventNSImpl(String uri, String name) throws NullPointerException {
    if (uri == null)
      throw new NullPointerException("The URI cannot be null, use \"\".");
    if (name == null)
      throw new NullPointerException("Element must have a name.");
    this.open = new OpenElementEventNSImpl(uri, name);
  }

  /**
   * Creates a new close element event from the corresponding open element event.
   * 
   * @param event The corresponding open element event.
   * 
   * @throws NullPointerException If the name is <code>null</code>.
   */
  public CloseElementEventNSImpl(OpenElementEvent event) throws NullPointerException {
    if (event == null)
      throw new NullPointerException("Element must have a name.");
    this.open = event;
  }

  /**
   * @return Returns the name.
    */
  public String getName() {
    return this.open.getName();
  }

  /**
   * @return Returns the namespace URI.
   */
  public String getURI() {
    return this.open.getURI();
  }

  /**
   * {@inheritDoc}
   */
  public OpenElementEvent getOpenElement() {
    return this.open;
  }

  /**
   * {@inheritDoc}
   */
  public boolean match(OpenElementEvent event) {
    if (event == null) return false;
    if (event == this.open) return true;
    return (event.getURI().equals(this.getURI())
        &&  event.getName().equals(this.getName()));
  }

  /**
   * {@inheritDoc}
   */
  public int hashCode() {
    return 89 + this.open.hashCode();
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
    CloseElementEventNSImpl ce = (CloseElementEventNSImpl)e;
    return (ce.getURI().equals(this.getURI())
        &&  ce.getName().equals(this.getName()));
  }

  /**
   * {@inheritDoc}
   */
  public String toString() {
    return "closeElement: "+this.getName()+" ["+this.getURI()+"]";
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
    // TODO: handle namespaces.
    xml.append("</").append(this.getName()).append('>');
    return xml;
  }
}
