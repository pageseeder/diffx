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

import org.pageseeder.diffx.format.DiffXFormatter;
import org.pageseeder.diffx.token.XMLToken;

import java.io.IOException;
import java.util.Iterator;

/**
 * The slicer takes two sequences and removes the common elements from the beginning
 * and the end of the chain so that only the smallest sequences are passed to the
 * DiffXAlgorithmBase.
 *
 * <p>The slice does modify the original sequences.
 *
 * <p>Note: Using this class may lead to problems in the execution of the Diff-X
 * algorithm and incorrect results, because it could potentially take off some parts
 * that helps the Diff-X algorithm ensuring that the XML is well-formed.
 *
 * @author Christophe Lauret
 * @version 6 December 2008
 */
public final class NaiveSequenceSlicer {
  // FIXME: symmetrical slicing.

  // class attributes ---------------------------------------------------------------------------

  /**
   * The first sequence of tokens to test.
   */
  final Sequence sequence1;

  /**
   * The second sequence of tokens to test.
   */
  final Sequence sequence2;

  /**
   * The common start between the two sequences.
   */
  Sequence start;

  /**
   * The common end between the two sequences.
   */
  Sequence end;

  // constructor --------------------------------------------------------------------------------

  /**
   * Creates a new sequence slicer.
   *
   * @param seq0 The first sequence to slice.
   * @param seq1 The second sequence to slice.
   */
  public NaiveSequenceSlicer(Sequence seq0, Sequence seq1) {
    this.sequence1 = seq0;
    this.sequence2 = seq1;
  }

  // methods ------------------------------------------------------------------------------------

  /**
   * Slices the start of both sequences.
   *
   * <p>The common start sequence will be stored in the class until the next
   * {@link #formatStart(DiffXFormatter)} is called.
   *
   * @return The number of common elements at the start of the sequences.
   * @throws IllegalStateException If the start buffer is not empty.
   */
  public int sliceStart() throws IllegalStateException {
    if (this.start != null)
      throw new IllegalStateException("The start buffer already contains a subsequence.");
    this.start = new Sequence();
    int count = 0;
    Iterator<XMLToken> i = this.sequence1.iterator();
    Iterator<XMLToken> j = this.sequence2.iterator();
    while (i.hasNext() && j.hasNext()) {
      XMLToken token = i.next();
      if (j.next().equals(token)) {
        count++;
        i.remove();
        j.remove();
        this.start.addToken(token);
      } else return count;
    }
    return count;
  }

  /**
   * Slices the end of both sequences.
   *
   * <p>The common end sequence will be stored in the class until the next
   * {@link #formatEnd(DiffXFormatter)} is called.
   *
   * @return The number of common elements at the end of the sequences.
   * @throws IllegalStateException If the end buffer is not empty.
   */
  public int sliceEnd() throws IllegalStateException {
    if (this.end != null)
      throw new IllegalStateException("The end buffer already contains a subsequence.");
    this.end = new Sequence();
    int count = 0;
    int pos1 = this.sequence1.size() - 1;
    int pos2 = this.sequence2.size() - 1;
    while (pos1 >= 0 && pos2 >= 0) {
      XMLToken token1 = this.sequence1.getToken(pos1);
      if (token1.equals(this.sequence2.getToken(pos2))) {
        count++;
        this.sequence1.removeToken(pos1--);
        this.sequence2.removeToken(pos2--);
        this.end.addToken(0, token1);
      } else {
        break;
      }
    }
    return count;
  }

  /**
   * Slices the start of both sequences and formats the start subsequence with the specified
   * formatter.
   *
   * <p>Implementation note: although this is functionally equivalent to call successively the
   * methods {@link #sliceStart()} and {@link #formatStart(DiffXFormatter)}, this method is
   * optimised and passes the token directly to the formatter without using a buffer.
   *
   * @param formatter The formatter that will handle the output.
   *
   * @return The number of common elements at the start of the sequences.
   * @throws IllegalStateException If the start buffer is not empty.
   * @throws NullPointerException  If the specified formatter is <code>null</code>.
   * @throws IOException           If an error occurs whilst writing with the formatter.
   */
  public int sliceStart(DiffXFormatter formatter)
      throws IllegalStateException, NullPointerException, IOException {
    if (this.start != null)
      throw new IllegalStateException("The start buffer already contains a subsequence.");
    int count = 0;
    Iterator<XMLToken> i = this.sequence1.iterator();
    Iterator<XMLToken> j = this.sequence2.iterator();
    while (i.hasNext() && j.hasNext()) {
      XMLToken token = i.next();
      if (j.next().equals(token)) {
        count++;
        i.remove();
        j.remove();
        formatter.format(token);
      } else {
        break;
      }
    }
    return count;
  }

  /**
   * Slices the end of both sequences and formats the start subsequence with the specified
   * formatter.
   *
   * <p>Implementation note: although this is exactly equivalent to successive calls to the
   * methods {@link #sliceEnd()} and {@link #formatEnd(DiffXFormatter)}.
   *
   * @param formatter The formatter that will handle the output.
   *
   * @return The number of common elements at the end of the sequences.
   * @throws IllegalStateException If the end buffer is not empty.
   * @throws NullPointerException  If the specified formatter is <code>null</code>.
   * @throws IOException           If an error occurs whilst writing with the formatter.
   */
  public int sliceEnd(DiffXFormatter formatter)
      throws IllegalStateException, NullPointerException, IOException {
    int count = sliceEnd();
    formatEnd(formatter);
    return count;
  }

  /**
   * Formats the start subsequence that has been buffered by this class.
   *
   * <p>This method will clear the buffer, but will do nothing if the start buffer is
   * <code>null</code>.
   *
   * @param formatter The formatter that will handle the output.
   *
   * @throws NullPointerException If the specified formatter is <code>null</code>.
   * @throws IOException          If an error occurs whilst writing with the formatter.
   */
  public void formatStart(DiffXFormatter formatter) throws NullPointerException, IOException {
    if (this.start == null) return;
    for (int i = 0; i < this.start.size(); i++) {
      formatter.format(this.start.getToken(i));
    }
    this.start = null;
  }

  /**
   * Formats the end subsequence that has been buffered by this class.
   *
   * <p>This method will clear the buffer, but will do nothing if the end buffer is
   * <code>null</code>.
   *
   * @param formatter The formatter that will handle the output.
   *
   * @throws NullPointerException If the specified formatter is <code>null</code>.
   * @throws IOException          If an error occurs whilst writing with the formatter.
   */
  public void formatEnd(DiffXFormatter formatter) throws NullPointerException, IOException {
    if (this.end == null) return;
    for (int i = 0; i < this.end.size(); i++) {
      formatter.format(this.end.getToken(i));
    }
    this.end = null;
  }

  /**
   * Returns the current start sequence buffer.
   *
   * @return The current start sequence buffer or <code>null</code> if none.
   */
  public Sequence getStart() {
    return this.start;
  }

  /**
   * Returns the current end sequence buffer.
   *
   * @return The current end sequence buffer or <code>null</code> if none.
   */
  public Sequence getEnd() {
    return this.end;
  }

}
