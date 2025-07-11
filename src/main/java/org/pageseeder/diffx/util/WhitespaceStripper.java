/*
 * Copyright 2010-2025 Allette Systems (Australia)
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
package org.pageseeder.diffx.util;

import org.jetbrains.annotations.NotNull;
import org.pageseeder.diffx.token.StartElementToken;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.token.XMLTokenType;
import org.pageseeder.diffx.token.impl.IgnorableSpaceToken;
import org.pageseeder.diffx.token.impl.SpaceToken;
import org.pageseeder.diffx.token.impl.XMLStartElement;
import org.pageseeder.diffx.xml.Namespace;
import org.pageseeder.diffx.xml.Sequence;
import org.pageseeder.diffx.xml.SequenceProcessor;

import java.util.*;

/**
 * A utility class for stripping unnecessary whitespace tokens from an XML sequence.
 *
 * <p>This class processes an XML {@link Sequence} and removes ignorable whitespace tokens,
 * based on a set of user-specified elements to ignore. Users can specify elements whose
 * content should have whitespace removed, and the utility ensures that only non-whitespace
 * tokens, or whitespace tokens outside ignored elements, are retained.
 *
 * <p>The {@code WhitespaceStripper} maintains an internal set of elements to ignore,
 * which can be configured through various methods. By default, the set of elements to
 * ignore is empty, and no whitespace is stripped until explicitly specified.
 *
 * <p>Thread-safety is not guaranteed for this class. For concurrent usage, external synchronization
 * is required.
 *
 * @author Christophe Lauret
 *
 * @version 1.2.0
 * @since 1.1.0
 */
public class WhitespaceStripper implements SequenceProcessor {

  private final Set<StartElementToken> elementsToIgnore;

  public WhitespaceStripper() {
    this.elementsToIgnore = new HashSet<>();
  }

  public WhitespaceStripper(String... names) {
    this.elementsToIgnore = toSet(names, Namespace.NO_NAMESPACE);
  }

  public void setElementsToIgnore(String... names) {
    setElementsToIgnore(Namespace.NO_NAMESPACE, names);
  }

  public void setElementsToIgnore(Namespace ns, String... names) {
    this.elementsToIgnore.clear();
    this.elementsToIgnore.addAll(toSet(names, ns));
  }

  /**
   * Removes ignorable whitespace from the given XML sequence based on the defined set of elements to ignore.
   *
   * <p>The method traverses through the sequence of XML tokens, checks the context of each token,
   * and filters out whitespace tokens as needed. Start and end elements are used to manage the context
   * stack, determining whether the current context is ignorable. Only the tokens that are not ignorable
   * in the given context are included in the new stripped sequence.
   *
   * @param sequence The XML sequence to process and strip of ignorable whitespace.
   * @return A new {@link Sequence} instance containing the filtered tokens without ignorable whitespace.
   */
  @Override
  public @NotNull Sequence process(@NotNull Sequence sequence) {
    return new Sequence(strip(sequence.tokens()), sequence.getNamespaces());
  }

  /**
   * Removes ignorable whitespace tokens from the given list of XML tokens.
   *
   * <p>The method processes the list of XML tokens and identifies ignorable whitespace
   * based on the specified context of start and end elements. It traverses the tokens
   * while maintaining a context stack, filtering out whitespace if it is deemed
   * ignorable in the current context. Only tokens that are not ignorable are included
   * in the resulting list.
   *
   * @param tokens The list of {@link XMLToken} objects representing the XML sequence
   *               to process and strip of ignorable whitespace.
   * @return A new list of {@link XMLToken} objects that excludes ignorable whitespace based on context.
   */
  @Override
  public @NotNull List<XMLToken> process(@NotNull List<XMLToken> tokens) {
    return strip(tokens);
  }

  /**
   * Removes ignorable whitespace from the given XML sequence based on the defined set of elements to ignore.
   *
   * <p>The method traverses through the sequence of XML tokens, checks the context of each token,
   * and filters out whitespace tokens as needed. Start and end elements are used to manage the context
   * stack, determining whether the current context is ignorable. Only the tokens that are not ignorable
   * in the given context are included in the new stripped sequence.
   *
   * @param sequence The XML sequence to process and strip of ignorable whitespace.
   * @return A new {@link Sequence} instance containing the filtered tokens without ignorable whitespace.
   */
  public @NotNull Sequence strip(@NotNull Sequence sequence) {
    return new Sequence(strip(sequence.tokens()), sequence.getNamespaces());
  }

  /**
   * Removes ignorable whitespace tokens from the given list of XML tokens.
   *
   * <p>The method processes the list of XML tokens and identifies ignorable whitespace
   * based on the specified context of start and end elements. It traverses the tokens
   * while maintaining a context stack, filtering out whitespace if it is deemed
   * ignorable in the current context. Only tokens that are not ignorable are included
   * in the resulting list.
   *
   * @param tokens The list of {@link XMLToken} objects representing the XML sequence
   *               to process and strip of ignorable whitespace.
   * @return A new list of {@link XMLToken} objects that excludes ignorable whitespace based on context.
   */
  public List<XMLToken> strip(List<XMLToken> tokens) {
    Deque<StartElementToken> context = new ArrayDeque<>();
    boolean ignorableContext = false;
    List<XMLToken> stripped = new ArrayList<>(tokens.size());
    for (XMLToken token : tokens) {
      XMLTokenType type = token.getType();
      boolean include = true;
      if (type == XMLTokenType.START_ELEMENT) {
        context.push((StartElementToken)token);
        ignorableContext = this.elementsToIgnore.contains(context.peek());
      } else if (type == XMLTokenType.END_ELEMENT) {
        context.pop();
        ignorableContext = this.elementsToIgnore.contains(context.peek());
      } else if (type == XMLTokenType.TEXT && ignorableContext && token.isWhitespace()) {
        include = false;
      }
      // Include
      if (include) {
        stripped.add(token);
      }
    }
    return stripped;
  }

  /**
   * Converts the specified array of element names into a set of {@link StartElementToken} instances.
   */
  private Set<StartElementToken> toSet(String[] names, Namespace ns) {
    HashSet<StartElementToken> elements = new HashSet<>(names.length);
    for (String name : names) {
      elements.add(new XMLStartElement(ns.getUri(), name));
    }
    return elements;
  }

}
