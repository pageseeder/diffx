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
package org.pageseeder.diffx.format;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import org.pageseeder.diffx.config.DiffXConfig;
import org.pageseeder.diffx.event.*;
import org.pageseeder.diffx.event.impl.CharEvent;
import org.pageseeder.diffx.event.impl.CharactersEventBase;
import org.pageseeder.diffx.event.impl.IgnorableSpaceEvent;
import org.pageseeder.diffx.event.impl.LineEvent;
import org.pageseeder.diffx.event.impl.SpaceEvent;
import org.pageseeder.diffx.event.impl.WordEvent;

/**
 * A simple formatter to write the short string version of the events.
 *
 * @author Christophe Lauret
 * @version 18 March 2005
 */
public final class ShortStringFormatter implements DiffXFormatter {

  //  class attributes ---------------------------------------------------------------------------

  /**
   * The output goes here.
   */
  private final Writer out;

  //  constructors -------------------------------------------------------------------------------

  /**
   * Creates a new formatter on the standard output.
   *
   * @see System#out
   *
   * @throws IOException should an I/O exception occurs.
   */
  public ShortStringFormatter() throws IOException {
    this(new PrintWriter(System.out));
  }

  /**
   * Creates a new formatter using the specified writer.
   *
   * @param w The writer to use.
   *
   * @throws IOException should an I/O exception occurs.
   */
  public ShortStringFormatter(Writer w) throws IOException {
    this.out = w;
  }

  // methods ------------------------------------------------------------------------------

  /**
   * Writes the event as a short string.
   *
   * {@inheritDoc}
   */
  @Override
  public void format(DiffXEvent e) throws IOException, IllegalStateException {
    this.out.write(toShortString(e));
  }

  /**
   * Writes the event as a short string preceded by '+'.
   *
   * {@inheritDoc}
   */
  @Override
  public void insert(DiffXEvent e) throws IOException, IllegalStateException {
    this.out.write("+");
    this.out.write(toShortString(e));
  }

  /**
   * Writes the event as a short string preceded by '+'.
   *
   * {@inheritDoc}
   */
  @Override
  public void delete(DiffXEvent e) throws IOException, IllegalStateException {
    this.out.write("-");
    this.out.write(toShortString(e));
  }

  /**
   * Ignored.
   *
   * {@inheritDoc}
   */
  @Override
  public void setConfig(DiffXConfig config) {
  }

  // private helpers ----------------------------------------------------------------------

  /**
   * Returns the short string for the given event.
   *
   * @param e The event.
   *
   * @return The short string for the given event.
   */
  public static String toShortString(DiffXEvent e) {
    // an element to open
    if (e instanceof OpenElementEvent)
      return '<'+((OpenElementEvent)e).getName()+'>';
    // an element to close
    if (e instanceof CloseElementEvent)
      return "</"+((CloseElementEvent)e).getName()+'>';
    // an attribute
    if (e instanceof AttributeEvent)
      return "@"+((AttributeEvent)e).getName();
    // a word
    if (e instanceof WordEvent)
      return '"'+((CharactersEventBase)e).getCharacters()+'"';
    // a white space event
    if (e instanceof SpaceEvent)
      return "_s_";
    // a single character
    if (e instanceof CharEvent)
      return '\''+((CharactersEventBase)e).getCharacters()+'\'';
    // an ignorable space event
    if (e instanceof IgnorableSpaceEvent)
      return "_i_";
    if (e instanceof ElementEvent)
      return '<'+((ElementEvent)e).getName()+"/>";
    // a single line
    if (e instanceof LineEvent) return "L#"+((LineEvent)e).getLineNumber();
    if (e == null) return "-";
    return "???";
  }

}
