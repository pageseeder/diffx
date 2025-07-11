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
import org.pageseeder.diffx.token.impl.WordToken;
import org.pageseeder.diffx.token.impl.XMLStartElement;
import org.pageseeder.diffx.xml.Namespace;
import org.pageseeder.diffx.xml.SequenceProcessor;

import java.util.*;

/**
 * This class is an implementation of the {@link SequenceProcessor} interface
 * that processes XML sequences by removing ignorable whitespace based on specified rules.
 *
 * <p>This class allows the configuration of elements to be ignored during whitespace stripping.
 * It provides mechanisms to define elements whose whitespace should always be ignored, maybe ignored,
 * or never ignored, and processes XML tokens accordingly.
 *
 * <p>It relies on the {@link StripWhitespace} context to manage whitespace stripping, evaluating
 * each token and applying the stripping rules based on the context and token relationships.
 *
 * @author Christophe Lauret
 *
 * @version 1.2.1
 * @since 1.1.0
 */
public class ExtendedWhitespaceStripper implements SequenceProcessor {

  /**
   * An enumeration that defines the rules for stripping whitespace in various contexts.
   */
  enum StripWhitespace {

    /**
     * Whitespace will always be stripped since this context only supports elements as
     * child nodes so whitespaces are only used for indentation.
     *
     * <p>For example, for the following HTML elements {@code <table>}, {@code <ul>},
     * {@code <ol>}
     */
    ALWAYS,

    /**
     * Leading whitespace can be removed as this context supports mixed
     * content but does not render leading or trailing spaces.
     *
     * <p>For example, for the following HTML elements {@code <p>}, {@code <li>},
     * {@code <td>}
     */
    LEADING,

    /**
     * Trailing whitespace can be removed as this context supports mixed
     * content but does not render leading or trailing spaces.
     *
     * <p>For example, for the following HTML elements {@code <p>}, {@code <li>},
     * {@code <td>}
     */
    TRAILING,

    /**
     * Whitespace will never be stripped either because the element naturally preserves
     * whitespaces as they are significant or because the element is used for inline
     * markup.
     *
     * <p>This is the default.
     *
     * <p>For example, for the following HTML elements {@code <pre>}, {@code <i>},
     * {@code <em>}
     */
    NEVER
  }


  private final Set<StartElementToken> alwaysIgnore = new HashSet<>();
  private final Set<StartElementToken> maybeIgnore = new HashSet<>();

  public void setAlwaysIgnore(String... names) {
    setAlwaysIgnore(Namespace.NO_NAMESPACE, names);
  }

  public void setMaybeIgnore(String... names) {
    setMaybeIgnore(Namespace.NO_NAMESPACE, names);
  }

  public void setAlwaysIgnore(Namespace ns, String... names) {
    this.alwaysIgnore.clear();
    this.alwaysIgnore.addAll(toSet(names, ns));
  }

  public void setMaybeIgnore(Namespace ns, String... names) {
    this.maybeIgnore.clear();
    this.maybeIgnore.addAll(toSet(names, ns));
  }

  StripWhitespace forElement(StartElementToken start) {
    if (this.alwaysIgnore.contains(start)) return StripWhitespace.ALWAYS;
    if (this.maybeIgnore.contains(start)) return StripWhitespace.LEADING;
    return StripWhitespace.NEVER;
  }

  /**
   * Filters a sequence of XML tokens to remove ignorable whitespace based on the context
   * and strip rules defined for certain XML elements.
   *
   * @param tokens The list of XML tokens to be filtered, including start elements, end elements,
   *               text tokens, and other token types.
   * @return A filtered list of XML tokens where ignorable whitespace has been excluded
   *         based on the context of each token.
   */
  @Override
  public @NotNull List<XMLToken> process(@NotNull List<XMLToken> tokens) {
    Deque<StripWhitespace> context = new ArrayDeque<>();
    List<XMLToken> out = new ArrayList<>(tokens.size());
    for (int i = 0; i < tokens.size(); i++) {
      XMLToken token = tokens.get(i);
      XMLTokenType type = token.getType();
      boolean include = true;
      if (type == XMLTokenType.START_ELEMENT) {
        StripWhitespace sc = forElement((StartElementToken)token);
        context.push(sc);
      } else if (type == XMLTokenType.END_ELEMENT) {
        context.pop();
      } else if (type == XMLTokenType.TEXT) {
        if (token.isWhitespace()) {
          include = includeWhitespace(context, tokens, i);
        } else if (context.peek() == StripWhitespace.LEADING) {
          if (token.getValue().startsWith(" ")) {
            out.add(new WordToken(token.getValue().substring(1)));
            include = false;
          }
          replaceHead(context, StripWhitespace.TRAILING);
        }
      }

      // Include
      if (include) {
        out.add(token);
      }
    }
    return out;
  }

  private boolean includeWhitespace(Deque<StripWhitespace> context, List<XMLToken> tokens, int i) {
    StripWhitespace stripContext = context.peek();
    if (stripContext == StripWhitespace.ALWAYS) {
      return false;
    } else if (stripContext == StripWhitespace.LEADING) {
      XMLToken next = tokens.get(i+1);
      if (next.getType() == XMLTokenType.TEXT) {
        return false;
      } else if (next.getType() == XMLTokenType.START_ELEMENT) {
        replaceHead(context, StripWhitespace.TRAILING);
        StripWhitespace sc = forElement((StartElementToken)next);
        if (sc == StripWhitespace.NEVER)
          replaceHead(context, StripWhitespace.TRAILING);
        return false;
      } else if (next.getType() == XMLTokenType.END_ELEMENT) {
        return false;
      }
    } else if (stripContext == StripWhitespace.TRAILING) {
      XMLToken next = tokens.get(i+1);
      if (next.getType() == XMLTokenType.END_ELEMENT) {
        return false;
      }
    }
    return true;
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

  private static void replaceHead(Deque<StripWhitespace> context, StripWhitespace value) {
    context.pop();
    context.push(value);
  }

}
