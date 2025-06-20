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

import org.pageseeder.diffx.api.DiffHandler;
import org.pageseeder.diffx.api.Operator;
import org.pageseeder.diffx.token.EndElementToken;
import org.pageseeder.diffx.token.StartElementToken;
import org.pageseeder.diffx.token.XMLToken;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * The slicer takes two sequences and removes the common elements from the beginning
 * and the end of the chain so that only the smallest sequences are passed to the
 * DiffXAlgorithmBase.
 *
 * <p>The slice does NOT modify the original sequences.
 *
 * <p>Note: Using this class may lead to problems in the execution of the Diff-X
 * algorithm and incorrect results, because it could potentially take off some parts
 * that helps the Diff-X algorithm ensuring that the XML is well-formed.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 * @since 0.7.0
 */
public final class TokenListSlicer {

  /**
   * The first sequence of tokens to test.
   */
  final List<? extends XMLToken> sequence1;

  /**
   * The second sequence of tokens to test.
   */
  final List<? extends XMLToken> sequence2;

  /**
   * The common start between the two sequences.
   */
  int startCount = -1;

  /**
   * The common end between the two sequences.
   */
  int endCount = -1;

  /**
   * Creates a new sequence slicer.
   *
   * @param seq0 The first sequence to slice.
   * @param seq1 The second sequence to slice.
   */
  public TokenListSlicer(List<? extends XMLToken> seq0, List<? extends XMLToken> seq1) {
    this.sequence1 = seq0;
    this.sequence2 = seq1;
  }

  /**
   * Analyse the sequences to know whether they can be sliced.
   *
   * @return the number of common tokens
   */
  public int analyze() throws IllegalStateException {
    this.startCount = computeStart();
    this.endCount = sliceEnd(this.startCount);
    return this.startCount + this.endCount;
  }

  /**
   * Slices the start of both sequences.
   *
   * <p>The common start sequence will be stored in the class until the next
   * {@link #handleStart(DiffHandler)} is called.
   *
   * @return The number of common elements at the start of the sequences.
   * @throws IllegalStateException If the start buffer is not empty.
   */
  public int computeStart() throws IllegalStateException {
    int toBeRemoved = 0; // the number of tokens to be removed
    int depth = 0;       // the depth of the XML or number of open elements
    Iterator<? extends XMLToken> i = this.sequence1.iterator();
    Iterator<? extends XMLToken> j = this.sequence2.iterator();
    int counter = 0;
    // calculate the max possible index for slicing.
    while (i.hasNext() && j.hasNext()) {
      XMLToken token = i.next();
      if (j.next().equals(token)) {
        counter++;
        // increase the depth
        if (token instanceof StartElementToken) {
          depth++;
          // decrease the depth
        } else if (token instanceof EndElementToken) {
          depth--;
        }
        // if depth = 1, it is a direct child of the document element,
        // so we can cut off the whole branch
        if (depth == 1 || depth == 0) {
          toBeRemoved = counter;
        }
      } else {
        break;
      }
    }
    return toBeRemoved;
  }

  /**
   * Slices the end of both sequences.
   *
   * <p>The common end sequence will be stored in the class until the next
   * {@link #handleEnd(DiffHandler)} is called.
   *
   * @param start The index from which we can start slicing
   *
   * @return The number of common elements at the end of the sequences.
   * @throws IllegalStateException If the end buffer is not empty.
   */
  public int sliceEnd(int start) throws IllegalStateException {
    int depth = 0;       // the depth of the XML or number of open elements
    int toBeRemoved = 0; // number of tokens to be removed from the end
    int counter = 0;     // number of tokens evaluated
    int pos1 = this.sequence1.size() - 1;  // current position of the first sequence
    int pos2 = this.sequence2.size() - 1;  // current position of the second sequence
    while (pos1 >= start && pos2 >= start) {
      XMLToken token = this.sequence1.get(pos1);
      if (token.equals(this.sequence2.get(pos2))) {
        counter++;
        // increase the depth for close, decrease for open
        if (token instanceof EndElementToken) {
          depth++;
        } else if (token instanceof StartElementToken) {
          depth--;
        }
        // if depth = 1, it is a direct child of the document element,
        // so we can cut off the whole branch
        if (depth == 1 || depth == 0) {
          toBeRemoved = counter;
        }
        pos1--;
        pos2--;
      } else {
        break;
      }
    }
    return toBeRemoved;
  }

  /**
   * Formats the start subsequence that has been buffered by this class.
   *
   * <p>This method will clear the buffer, but will do nothing if the start buffer is
   * <code>null</code>.
   *
   * @param handler The handler for the output.
   *
   * @throws NullPointerException If the specified formatter is <code>null</code>.
   */
  public void handleStart(DiffHandler<XMLToken> handler) {
    for (int i = 0; i < this.startCount; i++) {
      handler.handle(Operator.MATCH, this.sequence1.get(i));
    }
  }

  /**
   * Formats the end subsequence that has been buffered by this class.
   *
   * <p>This method will clear the buffer, but will do nothing if the end buffer is
   * <code>null</code>.
   *
   * @param handler The handler for the output.
   *
   * @throws NullPointerException If the specified formatter is <code>null</code>.
   */
  public void handleEnd(DiffHandler<XMLToken> handler) {
    int from = this.sequence1.size() - this.endCount;
    int to = this.sequence1.size();
    for (int i = from; i < to; i++) {
      handler.handle(Operator.MATCH, this.sequence1.get(i));
    }
  }

  /**
   * @return The number of common tokens at the start of the sequence.
   */
  public int getStartCount() {
    return this.startCount;
  }

  /**
   * @return The number of common tokens at the end of the sequence.
   */
  public int getEndCount() {
    return this.endCount;
  }

  /**
   * @return The common sublist at the start of the sequence.
   */
  public List<? extends XMLToken> getStart() {
    if (this.startCount <= 0) return Collections.emptyList();
    return this.sequence1.subList(0, this.startCount);
  }

  /**
   * @return The common sublist at the end of the sequence.
   */
  public List<? extends XMLToken> getEnd() {
    if (this.endCount <= 0) return Collections.emptyList();
    int size = this.sequence1.size();
    return this.sequence1.subList(size - this.endCount, size);
  }

  public List<? extends XMLToken> getSubSequence1() {
    if (this.startCount <= 0 && this.endCount <= 0) return this.sequence1;
    return this.sequence1.subList(this.startCount, this.sequence1.size() - this.endCount);
  }

  public List<? extends XMLToken> getSubSequence2() {
    if (this.startCount <= 0 && this.endCount <= 0) return this.sequence2;
    return this.sequence2.subList(this.startCount, this.sequence2.size() - this.endCount);
  }

}
