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
package org.pageseeder.diffx.sequence;

import org.pageseeder.diffx.event.CloseElementEvent;
import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.event.ElementEvent;
import org.pageseeder.diffx.event.OpenElementEvent;
import org.pageseeder.diffx.event.impl.ElementEventImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Collapses the
 */
public class SequenceFolding {

  public static final List<String> ALL = Collections.singletonList("*");

  public final List<String> elements;

  public SequenceFolding(List<String> elements) {
    this.elements = elements;
  }

  public static SequenceFolding forElements(String[] elements) {
    // Only keep distinct non-null element names
    List<String> list = Arrays.stream(elements)
        .filter(i -> i != null && i.length() > 0)
        .distinct()
        .collect(Collectors.toList());
    return new SequenceFolding(list);
  }

  public static SequenceFolding forAllElements() {
    return new SequenceFolding(ALL);
  }

  /**
   * Collapses the specified sequence using the current configuration.
   *
   * @param input The input sequence to be collapse
   *
   * @return The collapsed sequence.
   */
  public EventSequence fold(EventSequence input) {
    if (this.elements.isEmpty()) return input;
    FoldingProcessor processor = new FoldingProcessor();
    for (DiffXEvent event : input.events()) {
      processor.add(event);
    }
    return processor.sequence();
  }

  /**
   * Collapses the specified sequence using the current configuration.
   *
   * @param events The input sequence to be collapse
   *
   * @return The collapsed sequence.
   */
  public List<? extends DiffXEvent> fold(List<? extends DiffXEvent> events) {
    if (this.elements.isEmpty()) return events;
    FoldingProcessor processor = new FoldingProcessor();
    for (DiffXEvent event : events) {
      processor.add(event);
    }
    return processor.events();
  }

  private boolean isFoldable(DiffXEvent event) {
    if (!(event instanceof OpenElementEvent)) return false;
    if (this.elements == ALL) return true;
    return this.elements.contains(((OpenElementEvent) event).getName());
  }

  private boolean isMatching(DiffXEvent event, OpenElementEvent open) {
    return event instanceof CloseElementEvent && ((CloseElementEvent) event).match(open);
  }

  private class FoldingProcessor {

    private final List<DiffXEvent> events = new ArrayList<>();

    private final List<Folder> stack = new ArrayList<>();

    private Folder current() {
      return this.stack.get(this.stack.size()-1);
    }

    private boolean hasCurrent() {
      return this.stack.size() > 0;
    }

    void add(DiffXEvent event) {
      if (isFoldable(event)) {
        Folder subfolder = new Folder((OpenElementEvent)event);
        this.stack.add(subfolder);
      } else if (this.stack.isEmpty()) {
        this.events.add(event);
      } else {
        Folder current = this.current();
        if (isMatching(event, current.open)) {
          ElementEvent element = current.seal((CloseElementEvent) event);
          this.stack.remove(this.stack.size()-1); // pop
          if (this.stack.isEmpty()) {
            this.events.add(element);
          } else {
            this.current().add(element);
          }
        } else {
          current.add(event);
        }
      }
    }

    List<? extends DiffXEvent> events() {
      return this.events;
    }

    EventSequence sequence() {
      return new EventSequence(this.events);
    }
  }

  private static class Folder {

    OpenElementEvent open;

    private final List<DiffXEvent> children = new ArrayList<>();

    Folder(OpenElementEvent open) {
      this.open = open;
    }

    void add(DiffXEvent event) {
      this.children.add(event);
    }

    ElementEvent seal(CloseElementEvent close) {
      return new ElementEventImpl(this.open, close, this.children);
    }
  }
}
