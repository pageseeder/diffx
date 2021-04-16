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
package org.pageseeder.diffx.test;

import org.pageseeder.diffx.config.DiffXConfig;
import org.pageseeder.diffx.event.*;
import org.pageseeder.diffx.event.impl.*;
import org.pageseeder.diffx.format.DiffXFormatter;
import org.pageseeder.diffx.sequence.EventSequence;

import java.io.IOException;
import java.io.StringWriter;

/**
 * A Diff-X formatter implementation used solely for testing.
 *
 * <p>This formatter which write exactly what receives using the abstract representation of
 * each token and adding a plus / minus sign for insertions / deletion. This class is useful
 * to test the output of an algorithm.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 * @see Events
 */
public final class TestFormatter implements DiffXFormatter {

  /**
   * Set to <code>true</code> to show debug info.
   */
  private static final boolean DEBUG = System.getProperty("DEBUG") != null;

  /**
   * Where the output goes.
   */
  private final StringWriter out;

  /**
   * Creates a new test formatter
   */
  public TestFormatter() {
    this.out = new StringWriter();
  }

  /**
   * Writes the abstract representation.
   *
   * @see org.pageseeder.diffx.format.DiffXFormatter#format(org.pageseeder.diffx.event.Token)
   */
  public void format(Token e) {
    out.write(toAbstractString(e));
    out.flush();
    if (DEBUG) System.err.println(toAbstractString(e));
  }

  /**
   * Writes a plus sign '+' followed by the abstract representation.
   *
   * @see org.pageseeder.diffx.format.DiffXFormatter#insert(org.pageseeder.diffx.event.Token)
   */
  public void insert(Token e) {
    out.write("+" + toAbstractString(e));
    out.flush();
    if (DEBUG) System.err.println("+" + toAbstractString(e));
  }

  /**
   * Writes a minus sign '-' followed by the abstract representation.
   *
   * @see org.pageseeder.diffx.format.DiffXFormatter#delete(org.pageseeder.diffx.event.Token)
   */
  public void delete(Token e) {
    out.write("-" + toAbstractString(e));
    out.flush();
    if (DEBUG) System.err.println("-" + toAbstractString(e));
  }

  /**
   * Ignored as the config does not change the format output in this case.
   */
  public void setConfig(DiffXConfig config) {
  }

  /**
   * Formats the entire sequence by formatting each event.
   *
   * @param seq The token sequence to format
   * @throws IOException Should an I/O exception be thrown by the <code>format</code> method.
   */
  public void format(EventSequence seq) throws IOException {
    for (int i = 0; i < seq.size(); i++) {
      format(seq.getToken(i));
    }
    out.flush();
  }

  /**
   * Returns the output of the formatter.
   *
   * @return The output of the formatter.
   */
  public String getOutput() {
    return this.out.toString();
  }


  /**
   * Returns a simple representation for each token in order to recognise them depending on
   * their class.
   *
   * <p>This method will return <code>null</code> if it does not know how to format it.
   *
   * @param e The token to format
   * @return Its 'abstract' representation or <code>null</code>.
   */
  public static String toAbstractString(Token e) {
    // TODO: handle unknown token implementations nicely.
    // an element to open
    if (e instanceof StartElementToken) return '<' + ((StartElementToken) e).getName() + '>';
    // an element to close
    if (e instanceof EndElementToken) return "</" + ((EndElementToken) e).getName() + '>';
    // an element
    if (e instanceof ElementToken) return '<' + ((ElementToken) e).getName() + "/>";
    // an attribute
    if (e instanceof AttributeToken)
      return "@{" + ((AttributeToken) e).getName() + '=' + ((AttributeToken) e).getValue() + '}';
    // a word
    if (e instanceof WordToken) return "$w{" + ((CharactersTokenBase) e).getCharacters() + '}';
    // a white space token
    if (e instanceof SpaceToken) return "$s{" + ((CharactersTokenBase) e).getCharacters() + '}';
    // a single character
    if (e instanceof CharToken) return "$c{" + ((CharToken) e).getCharacters() + '}';
    // an ignorable space token
    if (e instanceof IgnorableSpaceToken) return "$i{" + ((IgnorableSpaceToken) e).getCharacters() + '}';
    // a single line
    if (e instanceof LineToken) return "$L" + ((LineToken) e).getLineNumber();
    return e.getClass().toString();
  }

}
