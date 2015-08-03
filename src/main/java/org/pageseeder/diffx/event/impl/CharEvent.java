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
import org.pageseeder.xmlwriter.XMLWriter;

/**
 * Event corresponding to a single character.
 *
 * @author Christophe Lauret
 * @version 28 March 2010
 */
public final class CharEvent extends DiffXEventBase {

  /**
   * The character associated with this event.
   */
  public final char c;

  /**
   * Creates a new character event.
   *
   * @param c The character to wrap.
   */
  public CharEvent(char c) {
    this.c = c;
  }

  @Override
  public int hashCode() {
    return 79 + this.c;
  }

  @Override
  public boolean equals(DiffXEvent e) {
    if (e.getClass() != this.getClass()) return false;
    return this.c == ((CharEvent)e).c;
  }

  @Override
  public String toString() {
    return "char: '"+this.c+'\'';
  }

  @Override
  public void toXML(XMLWriter xml) throws IOException {
    xml.writeText(this.c);
  }

  @Override
  public StringBuffer toXML(StringBuffer xml) throws NullPointerException {
    // TODO: ridiculously inefficient !
    return xml.append(ESC.toElementText(new char[]{this.c}, 0, 1));
  }

}
