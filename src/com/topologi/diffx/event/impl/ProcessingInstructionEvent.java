/*
 * This file is part of the DiffX library.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at 
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package com.topologi.diffx.event.impl;


import java.io.IOException;

import com.topologi.diffx.event.DiffXEvent;
import com.topologi.diffx.xml.XMLWriter;

/**
 * A processing instruction event.
 * 
 * @author Christophe Lauret
 * @version 4 April 2005
 */
public final class ProcessingInstructionEvent extends DiffXEventBase
                                              implements DiffXEvent {

  /**
   * The target of the processing instruction.
   */
  private final String target;

  /**
   * The data of the processing instruction.
   */
  private final String data;

  /**
   * Creates a new processing instruction event.
   *
   * @param target The target of the processing instruction.
   * @param data   The data of the processing instruction.
   * 
   * @throws NullPointerException if any of the argument is <code>null</code>.
   */
  public ProcessingInstructionEvent(String target, String data) throws NullPointerException {
    this.target = target;
    this.data = data;
  }

  /**
   * Returns the target of the processing instruction.
   * 
   * @return The target of the processing instruction.
   */
  public String getTarget() {
    return this.target;
  }

  /**
   * Returns the data of the processing instruction.
   * 
   * @return The data of the processing instruction.
   */
  public String getData() {
    return this.data;
  }

  /**
   * {@inheritDoc}
   */
  public int hashCode() {
    return this.target.hashCode() + this.data.hashCode();
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
    ProcessingInstructionEvent pi = (ProcessingInstructionEvent)e;
    // TODO: handle nulls
    return (pi.target.equals(this.target)
         && pi.data.equals(this.data));
  }

  /**
   * {@inheritDoc}
   */
  public String toString() {
    return "pi: "+this.target+": "+this.data;
  }

  /**
   * {@inheritDoc}
   */
  public void toXML(XMLWriter xml) throws IOException {
    xml.writePI(this.target, this.data);
  }

  /**
   * {@inheritDoc}
   */
  public StringBuffer toXML(StringBuffer xml) throws NullPointerException {
    xml.append("<?");
    xml.append(this.target);
    xml.append(' ');
    xml.append(this.data);
    xml.append("?>");
    return xml;
  }

}
