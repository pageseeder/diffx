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

import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.config.DiffXConfig;
import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.format.DiffXFormatter;
import org.pageseeder.diffx.handler.DiffHandler;
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
 * @author Christophe Lauret
 * @version 0.9.0
 * @see Events
 */
public final class TestHandler implements DiffHandler {

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
  public TestHandler() {
    this.out = new StringWriter();
  }

  /**
   * Writes the abstract representation.
   */
  public void handle(Operator operator, DiffXEvent e) {
    if (operator != Operator.KEEP) out.write(operator.toString());
    out.write(Events.toAbstractString(e));
    out.flush();
    if (DEBUG) System.err.println(Events.toAbstractString(e));
  }

  /**
   * Formats the entire sequence by formatting each event.
   *
   * @param seq The event sequence to format
   * @throws IOException Should an I/O exception be thrown by the <code>format</code> method.
   */
  public void format(EventSequence seq) throws IOException {
    for (int i = 0; i < seq.size(); i++) {
      handle(Operator.KEEP, seq.getEvent(i));
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
