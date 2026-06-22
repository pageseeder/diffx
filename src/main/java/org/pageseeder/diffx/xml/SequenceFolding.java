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
import java.util.List;

/**
 * Folds (collapses) specified elements in an XML token sequence into single {@link ElementToken}
 * instances, reducing the granularity of the sequence before diffing.
 *
 * <p>When elements are folded, their start element, end element, and all children are replaced
 * by a single {@link ElementToken}. This is useful when the content of certain elements should
 * be compared as an atomic unit rather than token by token.</p>
 *
 * <p>Folding can be configured to target specific elements by name, or all elements.</p>
 *
 * @see Sequence
 * @see ElementToken
 */
public class SequenceFolding {

  /**
   * Sentinel list indicating that all elements should be folded.
   */
  public static final List<String> ALL = List.of("*");

  /**
   * The list of element names to fold, or {@link #ALL} to fold every element.
   */
  public final List<String> elements;

  /**
   * Creates a new instance that folds the specified elements.
   *
   * @param elements the list of element local names to fold, or {@link #ALL} to fold all elements.
   */
  public SequenceFolding(List<String> elements) {
    this.elements = elements;
  }

  /**
   * Creates a new instance that folds only the specified elements.
   *
   * <p>Null and empty element names are ignored, and duplicates are removed.</p>
   *
   * @param elements the local names of the elements to fold.
   * @return a new {@code SequenceFolding} for the given elements.
   */
  public static SequenceFolding forElements(String... elements) {
    List<String> list = new ArrayList<>();
    for (String element : elements) {
      //noinspection ConstantValue
      if (element != null && !element.isEmpty() && !list.contains(element)) {
        list.add(element);
      }
    }
    return new SequenceFolding(list);
  }

  /**
   * Creates a new instance that folds all elements.
   *
   * @return a new {@code SequenceFolding} that folds every element.
   */
  public static SequenceFolding forAllElements() {
    return new SequenceFolding(ALL);
  }

  /**
   * Folds the specified sequence using the current configuration.
   *
   * <p>If no elements are configured for folding, the input sequence is returned unchanged.</p>
   *
   * @param input the input sequence to fold.
   * @return the folded sequence.
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
   * Folds the specified list of tokens using the current configuration.
   *
   * <p>If no elements are configured for folding, the input list is returned unchanged.</p>
   *
   * @param tokens the input tokens to fold.
   * @param <T> the type of XML token.
   * @return the folded token list.
   */
  @SuppressWarnings("unchecked")
  public <T extends XMLToken> List<T> fold(List<T> tokens) {
    if (this.elements.isEmpty()) return tokens;
    FoldingProcessor processor = new FoldingProcessor();
    for (T token : tokens) {
      processor.add(token);
    }
    return (List<T>) processor.tokens();
  }

  /**
   * Processes a stream of XML tokens and folds matching elements into {@link ElementToken} instances.
   *
   * <p>This processor uses a stack to track nested foldable elements. When a foldable start
   * element is encountered, a new {@link Folder} is pushed onto the stack. Tokens are collected
   * until the matching end element is found, at which point the folder is sealed into an
   * {@link ElementToken} and either added to the parent folder or to the output list.</p>
   */
  private class FoldingProcessor {

    private final List<XMLToken> tokens = new ArrayList<>();

    private final List<Folder> stack = new ArrayList<>();

    private Folder current() {
      return this.stack.get(this.stack.size() - 1);
    }

    private boolean isFoldable(XMLToken token) {
      if (!(token instanceof StartElementToken)) return false;
      if (elements == ALL) return true;
      return elements.contains(token.getName());
    }

    private boolean isMatching(XMLToken token, StartElementToken open) {
      return token instanceof EndElementToken && ((EndElementToken) token).match(open);
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

    List<XMLToken> tokens() {
      return this.tokens;
    }

    Sequence sequence() {
      return new Sequence(this.tokens);
    }
  }

  /**
   * Accumulates child tokens for a single element being folded.
   */
  private static class Folder {

    /** The start element token that opened this folder. */
    final StartElementToken open;

    private final List<XMLToken> children = new ArrayList<>();

    Folder(StartElementToken open) {
      this.open = open;
    }

    /**
     * Adds a child token to this folder.
     *
     * @param token the child token to add.
     */
    void add(XMLToken token) {
      this.children.add(token);
    }

    /**
     * Seals this folder with the given end element, producing an {@link ElementToken}
     * that represents the entire element and its children.
     *
     * @param close the matching end element token.
     * @return the folded element token.
     */
    ElementToken seal(EndElementToken close) {
      return new XMLElement(this.open, close, this.children);
    }
  }
}
