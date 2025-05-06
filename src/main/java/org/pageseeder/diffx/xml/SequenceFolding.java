/*
 * Copyright (c) 2010-2021 Allette Systems (Australia)
 *    http://www.allette.com.au
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pageseeder.diffx.xml;

import org.pageseeder.diffx.token.ElementToken;
import org.pageseeder.diffx.token.EndElementToken;
import org.pageseeder.diffx.token.StartElementToken;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.token.impl.XMLElement;

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

  public static SequenceFolding forElements(String... elements) {
    // Only keep distinct non-null element names
    List<String> list = Arrays.stream(elements)
        .filter(i -> i != null && !i.isEmpty())
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
  public Sequence fold(Sequence input) {
    if (this.elements.isEmpty()) return input;
    FoldingProcessor processor = new FoldingProcessor();
    for (XMLToken token : input.tokens()) {
      processor.add(token);
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
  public List<? extends XMLToken> fold(List<? extends XMLToken> tokens) {
    if (this.elements.isEmpty()) return tokens;
    FoldingProcessor processor = new FoldingProcessor();
    for (XMLToken token : tokens) {
      processor.add(token);
    }
    return processor.tokens();
  }

  private boolean isFoldable(XMLToken token) {
    if (!(token instanceof StartElementToken)) return false;
    if (this.elements == ALL) return true;
    return this.elements.contains(token.getName());
  }

  private boolean isMatching(XMLToken token, StartElementToken open) {
    return token instanceof EndElementToken && ((EndElementToken) token).match(open);
  }

  private class FoldingProcessor {

    private final List<XMLToken> tokens = new ArrayList<>();

    private final List<Folder> stack = new ArrayList<>();

    private Folder current() {
      return this.stack.get(this.stack.size() - 1);
    }

    private boolean hasCurrent() {
      return !this.stack.isEmpty();
    }

    void add(XMLToken token) {
      if (isFoldable(token)) {
        Folder subfolder = new Folder((StartElementToken) token);
        this.stack.add(subfolder);
      } else if (this.stack.isEmpty()) {
        this.tokens.add(token);
      } else {
        Folder current = this.current();
        if (isMatching(token, current.open)) {
          ElementToken element = current.seal((EndElementToken) token);
          this.stack.remove(this.stack.size() - 1); // pop
          if (this.stack.isEmpty()) {
            this.tokens.add(element);
          } else {
            this.current().add(element);
          }
        } else {
          current.add(token);
        }
      }
    }

    List<? extends XMLToken> tokens() {
      return this.tokens;
    }

    Sequence sequence() {
      return new Sequence(this.tokens);
    }
  }

  private static class Folder {

    final StartElementToken open;

    private final List<XMLToken> children = new ArrayList<>();

    Folder(StartElementToken open) {
      this.open = open;
    }

    void add(XMLToken token) {
      this.children.add(token);
    }

    ElementToken seal(EndElementToken close) {
      return new XMLElement(this.open, close, this.children);
    }
  }
}
