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
import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.format.DiffXFormatter;
import org.pageseeder.diffx.sequence.EventSequence;

import java.io.IOException;
import java.io.StringWriter;

/**
 * A Diff-X formatter implementation used solely for testing.
 *
 * <p>This formatter which write exactly what receives using the abstract representation of
 * each event and adding a plus / minus sign for insertions / deletion. This class is useful
 * to test the output of an algorithm.
 *
 * @see org.pageseeder.diffx.test.EventUtils
 *
 * @author Christophe Lauret
 * @version 16 December 2004
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
   * @see org.pageseeder.diffx.format.DiffXFormatter#format(org.pageseeder.diffx.event.DiffXEvent)
   */
  public void format(DiffXEvent e) throws IOException {
    out.write(EventUtils.toAbstractString(e));
    out.flush();
    if (DEBUG) System.err.println(EventUtils.toAbstractString(e));
  }

  /**
   * Writes a plus sign '+' followed by the abstract representation.
   *
   * @see org.pageseeder.diffx.format.DiffXFormatter#insert(org.pageseeder.diffx.event.DiffXEvent)
   */
  public void insert(DiffXEvent e) throws IOException {
    out.write("+"+ EventUtils.toAbstractString(e));
    out.flush();
    if (DEBUG) System.err.println("+"+ EventUtils.toAbstractString(e));
  }

  /**
   * Writes a minus sign '-' followed by the abstract representation.
   *
   * @see org.pageseeder.diffx.format.DiffXFormatter#delete(org.pageseeder.diffx.event.DiffXEvent)
   */
  public void delete(DiffXEvent e) throws IOException {
    out.write("-"+ EventUtils.toAbstractString(e));
    out.flush();
    if (DEBUG) System.err.println("-"+ EventUtils.toAbstractString(e));
  }

  /**
   * Ignored as the config does not change the format output in this case.
   */
  public void setConfig(DiffXConfig config) {
  }

  /**
   * Formats the entire sequence by formatting each event.
   *
   * @param seq The event sequence to format
   *
   * @throws IOException Should an I/O exception be thrown by the <code>format</code> method.
   */
  public void format(EventSequence seq) throws IOException {
    for (int i = 0; i < seq.size(); i++) {
      format(seq.getEvent(i));
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

}
