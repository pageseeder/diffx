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

import org.pageseeder.diffx.token.EndElementToken;
import org.pageseeder.diffx.token.Token;
import org.pageseeder.diffx.token.ElementToken;
import org.pageseeder.diffx.token.StartElementToken;
import org.pageseeder.diffx.token.impl.ElementTokenImpl;

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
    for (Token event : input.tokens()) {
      processor.add(event);
    }
    return processor.sequence();
  }

  /**
   * Collapses the specified sequence using the current configuration.
   *
   * @param tokens The input sequence to be collapse
   *
   * @return The collapsed sequence.
   */
  public List<? extends Token> fold(List<? extends Token> tokens) {
    if (this.elements.isEmpty()) return tokens;
    FoldingProcessor processor = new FoldingProcessor();
    for (Token event : tokens) {
      processor.add(event);
    }
    return processor.tokens();
  }

  private boolean isFoldable(Token event) {
    if (!(event instanceof StartElementToken)) return false;
    if (this.elements == ALL) return true;
    return this.elements.contains(((StartElementToken) event).getName());
  }

  private boolean isMatching(Token event, StartElementToken open) {
    return event instanceof EndElementToken && ((EndElementToken) event).match(open);
  }

  private class FoldingProcessor {

    private final List<Token> tokens = new ArrayList<>();

    private final List<Folder> stack = new ArrayList<>();

    private Folder current() {
      return this.stack.get(this.stack.size()-1);
    }

    private boolean hasCurrent() {
      return this.stack.size() > 0;
    }

    void add(Token event) {
      if (isFoldable(event)) {
        Folder subfolder = new Folder((StartElementToken)event);
        this.stack.add(subfolder);
      } else if (this.stack.isEmpty()) {
        this.tokens.add(event);
      } else {
        Folder current = this.current();
        if (isMatching(event, current.open)) {
          ElementToken element = current.seal((EndElementToken) event);
          this.stack.remove(this.stack.size()-1); // pop
          if (this.stack.isEmpty()) {
            this.tokens.add(element);
          } else {
            this.current().add(element);
          }
        } else {
          current.add(event);
        }
      }
    }

    List<? extends Token> tokens() {
      return this.tokens;
    }

    EventSequence sequence() {
      return new EventSequence(this.tokens);
    }
  }

  private static class Folder {

    final StartElementToken open;

    private final List<Token> children = new ArrayList<>();

    Folder(StartElementToken open) {
      this.open = open;
    }

    void add(Token event) {
      this.children.add(event);
    }

    ElementToken seal(EndElementToken close) {
      return new ElementTokenImpl(this.open, close, this.children);
    }
  }
}
