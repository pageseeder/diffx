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

import org.jetbrains.annotations.NotNull;
import org.pageseeder.diffx.token.XMLToken;

import java.io.PrintWriter;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A sequence of XML tokens.
 *
 * <p>This class wraps a list of <code>XMLToken</code>s alongside the namespaces.
 *
 * @implNote we use an <code>ArrayList</code> to store the tokens because some algorithms
 * need random access. Other list implementations may affect performance.
 *
 * @author Christophe Lauret
 *
 * @version 0.9.0
 * @since 0.7
 */
public final class Sequence extends AbstractList<XMLToken> implements List<XMLToken> {

  /**
   * The prefix mapping for the elements in this sequence.
   */
  private final NamespaceSet namespaces = new NamespaceSet();

  /**
   * The sequence of tokens.
   */
  private final List<XMLToken> tokens;

  /**
   * Creates a new token sequence.
   */
  public Sequence() {
    this.tokens = new ArrayList<>();
  }

  /**
   * Creates a new token sequence of the specified size.
   *
   * @param size The size of the sequence.
   */
  public Sequence(int size) {
    this.tokens = new ArrayList<>(size);
  }

  /**
   * Creates a new token sequence of the specified size.
   *
   * <p>Use a <code>List</code> implementation with that provide good random access performance.</p>
   *
   * @param tokens The size of the sequence.
   * @param namespaces The size of the sequence.
   */
  public Sequence(List<XMLToken> tokens, NamespaceSet namespaces) {
    this.tokens = tokens;
    this.namespaces.add(namespaces);
  }

  /**
   * Creates a new token sequence of the specified size.
   *
   * <p>Use a <code>List</code> implementation with that provide good random access performance.</p>
   *
   * @param namespaces The size of the sequence.
   */
  public Sequence(NamespaceSet namespaces) {
    this.tokens = new ArrayList<>();
    this.namespaces.add(namespaces);
  }

  /**
   * Creates a new token sequence of the specified size.
   *
   * <p>Use a <code>List</code> implementation with that provide good random access performance.</p>
   *
   * @param tokens The size of the sequence.
   */
  public Sequence(List<XMLToken> tokens) {
    this.tokens = tokens;
  }

  /**
   * Adds a sequence of tokens to this sequence and merge the namespaces if any.
   *
   * @param sequence The sequence of tokens to be added.
   */
  public void addSequence(@NotNull Sequence sequence) {
    this.tokens.addAll(sequence.tokens);
    this.namespaces.add(sequence.namespaces);
  }

  @Override
  public XMLToken get(int index) {
    return this.tokens.get(index);
  }

  @Override
  public void add(int index, XMLToken token) {
    this.tokens.add(index, token);
  }

  @Override
  public boolean add(XMLToken token) {
    return this.tokens.add(token);
  }

  /**
   * Adds a token to this sequence.
   *
   * @param token The token to be added.
   */
  public void addToken(XMLToken token) {
    this.tokens.add(token);
  }

  /**
   * Inserts a token to this sequence at the specified position.
   *
   * @param i     The position of the token.
   * @param token The token to be added.
   */
  public void addToken(int i, XMLToken token) {
    this.tokens.add(i, token);
  }

  /**
   * Adds a token to this sequence.
   *
   * @param tokens The tokens to be added.
   */
  public void addTokens(List<? extends XMLToken> tokens) {
    this.tokens.addAll(tokens);
  }

  /**
   * Returns the token at position i.
   *
   * @param i The position of the token.
   *
   * @return the token at position i.
   */
  public XMLToken getToken(int i) {
    return this.tokens.get(i);
  }

  /**
   * Replaces a token of this sequence at the specified position.
   *
   * @param index The 0-based index of the position.
   * @param token The token to be inserted.
   *
   * @return The token at the previous position.
   */
  public XMLToken setToken(int index, XMLToken token) {
    return this.tokens.set(index, token);
  }

  /**
   * Removes a token from this sequence at the specified position.
   *
   * @param index The 0-based index of the position.
   *
   * @return The removed token.
   */
  public XMLToken removeToken(int index) {
    return this.tokens.remove(index);
  }

  /**
   * @return The number of tokens in the sequence.
   */
  public int size() {
    return this.tokens.size();
  }

  /**
   * @return the sequence of tokens.
   */
  public List<XMLToken> tokens() {
    return this.tokens;
  }

  @Override
  public int hashCode() {
    return this.tokens.size();
  }

  /**
   * Returns <code>true</code> if the specified token sequence is the same as this one.
   *
   * @param seq The sequence of tokens to compare with this one.
   *
   * @return <code>true</code> if the specified token sequence is equal to this one;
   * <code>false</code> otherwise.
   */
  public boolean equals(Sequence seq) {
    if (seq == null) return false;
    return equals(this.tokens, seq.tokens);
  }

  /**
   * Returns <code>true</code> if the specified token sequence is the same as this one.
   *
   * <p>This method will redirect to the {@link #equals(Sequence)} method if the
   * specified object is an instance of {@link Sequence}.
   *
   * @param o The sequence of tokens to compare with this one.
   *
   * @return <code>true</code> if the specified token sequence is equal to this one;
   * <code>false</code> otherwise.
   */
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Sequence)) return false;
    return this.equals((Sequence) o);
  }

  @Override
  public String toString() {
    return "XMLSequence{namespaces=" + namespaces + ", tokens=" + tokens + '}';
  }

  /**
   * Export the sequence.
   *
   * @param w The print writer receiving the SAX events.
   */
  public void export(PrintWriter w) {
    for (XMLToken token : this.tokens) {
      w.println(token.toString());
    }
    w.flush();
  }

  /**
   * Maps a namespace URI to a prefix.
   *
   * <p>The replace element is usually used for the document element in order to override the
   * default namespace.</p>
   *
   * @param uri     The namespace URI to map.
   * @param prefix  The prefix to use.
   * @param replace Whether to replace the namespace
   *
   * @throws NullPointerException if the URI or prefix is <code>null</code>
   * @see NamespaceSet#add(String, String)
   * @see NamespaceSet#replace(String, String)
   */
  public void addNamespace(String uri, String prefix, boolean replace) throws NullPointerException {
    if (replace) {
      this.namespaces.replace(uri, prefix);
    } else {
      this.namespaces.add(uri, prefix);
    }
  }

  /**
   * Maps a namespace URI to a prefix.
   *
   * @param uri    The namespace URI to map.
   * @param prefix The prefix to use.
   *
   * @throws NullPointerException if the URI or prefix is <code>null</code>
   * @see NamespaceSet#add(String, String)
   */
  public void addNamespace(String uri, String prefix) throws NullPointerException {
    this.namespaces.add(uri, prefix);
  }

  /**
   * @return the prefix mapping for the namespace URIs in this sequence.
   */
  public NamespaceSet getNamespaces() {
    return this.namespaces;
  }

  @Override
  public @NotNull Iterator<XMLToken> iterator() {
    return this.tokens.iterator();
  }

  private static boolean equals(List<XMLToken> first, List<XMLToken> second) {
    if (first.size() != second.size()) return false;
    XMLToken x1;
    XMLToken x2;
    for (int i = 0; i < first.size(); i++) {
      x1 = first.get(i);
      x2 = second.get(i);
      if (!x1.equals(x2)) return false;
    }
    return true;
  }

}
