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

import org.pageseeder.diffx.event.CloseElementEvent;
import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.event.OpenElementEvent;
import org.pageseeder.xmlwriter.XMLWriter;

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
   * Creates a new close element event.
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
  public OpenElementEvent getOpenElement() {
    return this.open;
  }

  @Override
  public boolean match(OpenElementEvent event) {
    if (event == null) return false;
    if (event == this.open) return true;
    return event.getURI().equals(getURI())
        &&  event.getName().equals(getName());
  }

  @Override
  public int hashCode() {
    return 89 + this.open.hashCode();
  }

  /**
   * Returns <code>true</code> if the event is a close element event.
   *
   * @param e The event to compare with this event.
   *
   * @return <code>true</code> if this event is equal to the specified event;
   *         <code>false</code> otherwise.
   */
  @Override
  public boolean equals(DiffXEvent e) {
    if (e.getClass() != this.getClass()) return false;
    CloseElementEventNSImpl ce = (CloseElementEventNSImpl)e;
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
