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
import java.util.Objects;

import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.xmlwriter.XMLWriter;

/**
 * A comment event.
 *
 * @author Christophe Lauret
 * @author Jason Harrop
 *
 * @version 27 March 2010
 */
public final class CommentEvent extends DiffXEventBase implements DiffXEvent {

  /**
   * The comment string.
   */
  private final String comment;

  /**
   * Hashcode value for this event.
   */
  private final int hashCode;

  /**
   * Creates a new comment event.
   *
   * @param comment the comment string.
   *
   * @throws NullPointerException if the comment is <code>null</code>.
   */
  public CommentEvent(String comment) throws NullPointerException {
    this.comment = Objects.requireNonNull(comment);
    this.hashCode = toHashcode(comment);
  }

  /**
   * @return the comment string.
   */
  public String getComment() {
    return this.comment;
  }

  @Override
  public int hashCode() {
    return this.hashCode;
  }

  /**
   * Returns <code>true</code> if the event is a comment event.
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
    CommentEvent other = (CommentEvent) e;
    return other.comment.equals(this.comment);
  }

  @Override
  public String toString() {
    return "comment: " + this.comment;
  }

  @Override
  public void toXML(XMLWriter xml) throws IOException {
    xml.writeComment(this.comment);
  }

  @Override
  public StringBuffer toXML(StringBuffer xml) {
    xml.append("<!--");
    xml.append(this.comment);
    xml.append("-->");
    return xml;
  }

  @Override
  public String getType() {
    return "comment";
  }

  /**
   * Calculates the hashcode for this event.
   *
   * @param comment The comment string.
   * @return a number suitable as a hashcode.
   */
  private static int toHashcode(String comment) {
    return comment != null? 19*37 + comment.hashCode() : 19*37;
  }

}
