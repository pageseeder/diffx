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

import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.xmlwriter.esc.XMLEscape;
import org.pageseeder.xmlwriter.esc.XMLEscapeUTF8;

/**
 * A base class for DiffX events.
 *
 * <p>
 * This class is purely provided for convenience and consistency, it is best, although not strictly
 * required, that most <code>DiffXEvent</code> implementations extend this class.
 *
 * @author Christophe Lauret
 * @version 3 February 2005
 */
abstract class DiffXEventBase implements DiffXEvent {

  /**
   * For use by the events to escape XML chars.
   */
  static final XMLEscape ESC = XMLEscapeUTF8.UTF8_ESCAPE;

  /**
   *
   */
  int weight = 1;

  @Override
  public abstract int hashCode();

  @Override
  public abstract boolean equals(DiffXEvent e);

  /**
   * Invokes the {@link DiffXEvent#equals(DiffXEvent)} method if the specified object if not
   * <code>null</code> and is an instance of {@link DiffXEvent}.
   *
   * @param o The object to compare.
   *
   * @return <code>true</code> if the specified object is equal;
   *         <code>false</code> otherwise.
   */
  @Override
  public final boolean equals(Object o) {
    if (o == null)
      return false;
    if (!(o instanceof DiffXEvent))
      return false;
    return equals((DiffXEvent) o);
  }

  @Override
  public String toXML() {
    return this.toXML(new StringBuffer()).toString();
  }

  @Override
  public int getWeight() {
    return this.weight;
  }

  @Override
  public void setWeight(int weight) {
    this.weight = weight;
  }

}
