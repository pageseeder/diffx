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

import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.event.EventType;
import org.pageseeder.xmlwriter.XMLWriter;

/**
 * A branch of XML data.
 *
 * <p>A branch of XML data must start and end with the same element.
 *
 * <p>Implementation note: this class wraps an array of DiffX events and does not give
 * access to this array, so it can be considered immutable.
 *
 * @author Christophe Lauret
 * @version 27 March 2010
 */
public final class XMLBranchEvent extends DiffXEventBase implements DiffXEvent {

  /**
   * The array of Diff-X events that make up the branch.
   */
  private final DiffXEvent[] branch;

  /**
   * Pre-calculated hashcode to speed up equal comparison.
   */
  private final int hashCode;

  /**
   * Creates a new XML branch.
   *
   * @param events The array of events that make up the branch.
   */
  public XMLBranchEvent(DiffXEvent[] events) {
    this.branch = events;
    this.hashCode = toHashCode(events);
  }

  @Override
  public int hashCode() {
    return this.hashCode;
  }

  /**
   * Returns <code>true</code> if the diffX events in the branch are all equal.
   *
   * {@inheritDoc}
   */
  @Override
  public boolean equals(DiffXEvent e) {
    if (e.getClass() != this.getClass()) return false;
    if (e.hashCode() != this.hashCode) return false;
    XMLBranchEvent be = (XMLBranchEvent)e;
    // branch must have the same length
    if (this.branch.length != be.branch.length) return false;
    // every single event must be equal
    for (int i = 0; i < this.branch.length; i++) {
      if (!be.branch[i].equals(this.branch[i]))
        return false;
    }
    // if we arrive here they are equal
    return true;
  }

  /**
   * Write the DiffX events in order.
   *
   * {@inheritDoc}
   */
  @Override
  public void toXML(XMLWriter xml) throws IOException {
    for (DiffXEvent element : this.branch) {
      element.toXML(xml);
    }
  }

  @Override
  public StringBuffer toXML(StringBuffer xml) throws NullPointerException {
    for (DiffXEvent element : this.branch) {
      element.toXML(xml);
    }
    return xml;
  }

  @Override
  public EventType getType() {
    return EventType.ELEMENT;
  }

  /**
   * Calculates the hashcode for this event.
   *
   * @param events Events to calculate the value from.
   * @return a number suitable as a hashcode.
   */
  private static int toHashCode(DiffXEvent[] events) {
    int hash = 17;
    for (DiffXEvent e : events) {
      hash = hash * 13 + (e != null? e.hashCode() : 0);
    }
    return hash;
  }
}
