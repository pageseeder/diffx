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

import org.pageseeder.diffx.config.DiffXConfig;
import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.event.TextEvent;
import org.pageseeder.diffx.event.impl.CharactersEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Formatter to be used as a formatting filter which intercepts diffx events and coalesces consecutive text
 * events for the same operation.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 * @since 0.9.0
 */
public final class CoalescingFilter implements DiffXFormatter {

  private enum Operation { INS, DEL, FORMAT };

  /**
   * Target format.
   */
  private final DiffXFormatter target;

  /**
   * Buffer of text event to coalesce.
   */
  private final List<TextEvent> buffer = new ArrayList<>();

  /**
   * State variable which changes as new events are reported.
   */
  private Operation current = Operation.FORMAT;

  public CoalescingFilter(DiffXFormatter target) {
    this.target = target;
  }

  @Override
  public void format(DiffXEvent event) throws IOException, IllegalStateException {
    if (event instanceof TextEvent) {
      handleText((TextEvent)event, Operation.FORMAT);
    } else {
      flushText();
      format(event);
    }
  }

  @Override
  public void insert(DiffXEvent event) throws IOException, IllegalStateException {
    if (event instanceof TextEvent) {
      handleText((TextEvent)event, Operation.INS);
    } else {
      flushText();
      insert(event);
    }
  }

  @Override
  public void delete(DiffXEvent event) throws IOException, IllegalStateException {
    if (event instanceof TextEvent) {
      handleText((TextEvent)event, Operation.DEL);
    } else {
      flushText();
      delete(event);
    }
  }

  @Override
  public void setConfig(DiffXConfig config) {
  }

  private void handleText(TextEvent event, Operation operation) throws IOException {
    if (this.current != operation) {
      this.flushText();
      this.current = operation;
    }
    this.buffer.add(event);
  }

  /**
   * Flush the text to the target formatter and clear the buffer if there are any text events.
   *
   * @throws IOException If thrown by the target filter.
   */
  public void flushText() throws IOException {
    if (this.buffer.size() > 0) {
      TextEvent text = coalesce(this.buffer);
      if (this.current == Operation.FORMAT) this.target.format(text);
      else if (this.current == Operation.INS) this.target.insert(text);
      else if (this.current == Operation.DEL) this.target.delete(text);
      this.buffer.clear();
    }
  }

  /**
   * Coalesce text events into a single one.
   *
   * @param events A list of text events
   *
   * @return A single text event
   */
  public static TextEvent coalesce(List<TextEvent> events) {
    // If there's only one event, no need to coalesce
    if (events.size() == 1) return events.get(0);
    // Concatenate text of all text nodes
    StringBuilder text = new StringBuilder();
    for (TextEvent event : events) {
      text.append(event.getCharacters());
    }
    return new CharactersEvent(text.toString());
  }

}
