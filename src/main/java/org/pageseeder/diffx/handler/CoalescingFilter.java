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
package org.pageseeder.diffx.handler;

import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.event.TextEvent;
import org.pageseeder.diffx.event.impl.CharactersEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Coalesces consecutive text events for the same operation.
 *
 * <p>This handler is </p>
 *
 * @author Christophe Lauret
 * @version 0.9.0
 * @since 0.9.0
 */
public final class CoalescingFilter extends DiffFilter implements DiffHandler {

  /**
   * Buffer of text event to coalesce.
   */
  private final List<TextEvent> buffer = new ArrayList<>();

  /**
   * Buffer of text event to coalesce using opposite operation of current.
   */
  private final List<TextEvent> altBuffer = new ArrayList<>();

  /**
   * The operator used for the last event in the buffer.
   */
  private Operator current = Operator.MATCH;

  public CoalescingFilter(DiffHandler target) {
    super(target);
  }

  @Override
  public void handle(Operator operator, DiffXEvent event) throws IllegalStateException {
    if (event instanceof TextEvent) {
      handleText((TextEvent)event, operator);
    } else {
      flushText();
      this.target.handle(operator, event);
    }
  }

  @Override
  public void end() {
    flushText();
    super.end();
  }

  private void handleText(TextEvent event, Operator operator) {
    if (this.current == operator) {
      // Same operator simply add the event
      this.buffer.add(event);
    } else {
      if (this.current == Operator.MATCH || operator == Operator.MATCH) {
        // Operator is match, flush and update
        this.flushText();
        this.current = operator;
        this.buffer.add(event);
      } else {
        // Current is INS or DEL
        this.altBuffer.add(event);
      }
    }
  }

  /**
   * Flush the text to the target handler and clear the buffer if there are any text events.
   */
  public void flushText() {
    if (this.buffer.size() > 0) {
      TextEvent text = coalesceText(this.buffer);
      this.target.handle(this.current, text);
      this.buffer.clear();
      if (this.current != Operator.MATCH && !this.altBuffer.isEmpty()) {
        TextEvent other = coalesceText(this.altBuffer);
        this.target.handle(this.current.flip(), other);
        this.altBuffer.clear();
      }
    }
  }

  /**
   * Coalesce text events into a single one.
   *
   * @param events A list of text events
   *
   * @return A single text event
   */
  public static TextEvent coalesceText(List<TextEvent> events) {
    // If there's only one event, no need to coalesce
    if (events.size() == 1) return events.get(0);
    // Concatenate text of all text nodes
    StringBuilder text = new StringBuilder();
    for (TextEvent event : events) {
      text.append(event.getCharacters());
    }
    return new CharactersEvent(text.toString());
  }

  /**
   * Coalesce events that all text nodes
   *
   * @param events A list of events
   *
   * @return A list of events with text events coalesced.
   */
  public static List<DiffXEvent> coalesce(List<DiffXEvent> events) {
    // If there's only one event, no need to coalesce
    if (events.size() <= 1) return events;
    List<DiffXEvent> coalesced = new ArrayList<>();
    CoalescingFilter filter = new CoalescingFilter((operator, event) -> coalesced.add(event));
    for (DiffXEvent event : events) filter.handle(Operator.MATCH, event);
    return coalesced;
  }

}
