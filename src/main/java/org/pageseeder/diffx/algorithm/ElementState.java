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
package org.pageseeder.diffx.algorithm;

import org.jetbrains.annotations.NotNull;
import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.handler.DiffHandler;
import org.pageseeder.diffx.token.AttributeToken;
import org.pageseeder.diffx.token.EndElementToken;
import org.pageseeder.diffx.token.StartElementToken;
import org.pageseeder.diffx.token.Token;

import java.io.UncheckedIOException;

/**
 * Maintains the state of open and closed elements during the processing the Diff-X
 * algorithm.
 *
 * <p>This class has two purposes, firstly to provide an object that is more specialised
 * than the generic lists and stack for use by the DiffX algorithms. Second, to delegate
 * some of the complexity of algorithm.
 *
 * <p>This class has several methods that are similar to <code>List</code> interface
 * but does not implement it.
 *
 * <p>This class is not synchronised and is not meant to be serializable.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 * @since 0.7.0
 */
public final class ElementState implements DiffHandler<Token> {

  /**
   * The stack of open elements.
   */
  private transient StartElementToken[] openElements;

  /**
   * The operator corresponding to the stack
   */
  private transient Operator[] openChanges;

  /**
   * The size of both lists (the number of elements they contains).
   */
  private transient int size;

  /**
   * Constructs an stack of elements with the specified initial capacity.
   *
   * @param initialCapacity The initial capacity of the list.
   *
   * @throws IllegalArgumentException if the specified initial capacity is negative.
   */
  public ElementState(int initialCapacity) throws IllegalArgumentException {
    if (initialCapacity < 0)
      throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
    this.openElements = new StartElementToken[initialCapacity];
    this.openChanges = new Operator[initialCapacity];
  }

  /**
   * Constructs an empty stack with an initial capacity of 12.
   */
  public ElementState() {
    this(12);
  }

  /**
   * Increases the capacity of this class instance, if necessary, to ensure
   * that it can hold at least the number of elements specified by the
   * minimum capacity argument.
   *
   * @param minCapacity The desired minimum capacity.
   */
  public void ensureCapacity(int minCapacity) {
    int oldCapacity = this.openElements.length;
    if (minCapacity > oldCapacity) {
      // make a copy of the old arrays.
      StartElementToken[] oldElements = this.openElements;
      Operator[] oldChanges = this.openChanges;
      int newCapacity = oldCapacity * 3 / 2 + 1;
      if (newCapacity < minCapacity) {
        newCapacity = minCapacity;
      }
      // create new arrays
      this.openElements = new StartElementToken[newCapacity];
      this.openChanges = new Operator[newCapacity];
      // copy the values of the old arrays into the new ones
      System.arraycopy(oldElements, 0, this.openElements, 0, this.size);
      System.arraycopy(oldChanges, 0, this.openChanges, 0, this.size);
    }
  }

  /**
   * Returns the number of elements in this stack.
   *
   * @return the number of elements in this stack.
   */
  public int size() {
    return this.size;
  }

  /**
   * Tests if this list has no elements.
   *
   * @return <code>true</code> if this list has no elements;
   * <code>false</code> otherwise.
   */
  public boolean isEmpty() {
    return this.size == 0;
  }

  /**
   * Returns <code>true</code> if this list contains the specified element.
   *
   * @param element Element whose presence is to be tested.
   *
   * @return <code>true</code> if the specified element is present;
   * <code>false</code> otherwise.
   */
  public boolean contains(StartElementToken element) {
    return indexOf(element) >= 0;
  }

  /**
   * Searches for the first occurrence of the given argument, testing
   * for equality using the <code>equals</code> method.
   *
   * @param element The open element to find.
   *
   * @return The index of the first occurrence of the argument in this list;
   * returns <code>-1</code if the object is not found.
   * @see org.pageseeder.diffx.token.Token#equals(Token)
   */
  public int indexOf(StartElementToken element) {
    if (element == null) {
      for (int i = 0; i < this.size; i++)
        if (this.openElements[i] == null)
          return i;
    } else {
      for (int i = 0; i < this.size; i++)
        if (element.equals(this.openElements[i]))
          return i;
    }
    return -1;
  }

  /**
   * Returns the index of the last occurrence of the specified object in
   * this list.
   *
   * @param element The desired element.
   *
   * @return The index of the last occurrence of the specified open element;
   * or -1 if not found.
   */
  public int lastIndexOf(StartElementToken element) {
    if (element == null) {
      for (int i = this.size - 1; i >= 0; i--)
        if (this.openElements[i] == null)
          return i;
    } else {
      for (int i = this.size - 1; i >= 0; i--)
        if (element.equals(this.openElements[i]))
          return i;
    }
    return -1;
  }

  // Maintenance methods ------------------------------------------------------------------------

  /**
   * Returns the current open element.
   *
   * @return The current open element; or <code>null</code> if none.
   */
  public StartElementToken current() {
    if (!isEmpty())
      return this.openElements[this.size - 1];
    else
      return null;
  }

  /**
   * Returns the change of the current open element.
   *
   * @return The change of the current open element; or ' ' if none.
   */
  public Operator currentChange() {
    if (!isEmpty())
      return this.openChanges[this.size - 1];
    else
      return null;
  }

  /**
   * Indicates whether the specified token is a close element that
   * matches the name and URI of the current open element.
   *
   * @param token The token to check.
   *
   * @return <code>true</code> if it matches the current element;
   * <code>false</code> otherwise.
   */
  public boolean matchCurrent(Token token) {
    // cannot match if empty
    if (isEmpty()) return false;
    // cannot match if not a close element token
    if (!(token instanceof EndElementToken)) return false;
    // check if they match
    return ((EndElementToken) token).match(current());
  }

  /**
   * Updates the state from the specified token.
   *
   * <p>If the token is a START_ELEMENT token, it is pushed into the stack along with the corresponding operation.</p>
   *
   * <p>If the token is an END_ELEMENT token, it is popped from the stack.</p>
   *
   * @param token    The deleted token.
   * @param operator The corresponding operator
   */
  @Override
  public void handle(@NotNull Operator operator, Token token) throws UncheckedIOException, IllegalStateException {
    if (token instanceof StartElementToken) {
      push((StartElementToken) token, operator);
    } else if (token instanceof EndElementToken) {
      pop();
    }
  }

  /**
   * Indicates whether the specified operation is allowed.
   *
   * <p>It is allowed if:</p>
   * <ul>
   *   <li>The token is not an END_ELEMENT token type</li>
   *   <li>OR the token is an END_ELEMENT token that matches the last START_ELEMENT token and operator</li>
   * </ul>
   *
   * @param token The token to check.
   *
   * @return <code>true</code> if it matches the current element;
   * <code>false</code> otherwise.
   */
  public boolean isAllowed(Operator operator, Token token) {
    // cannot match if not a close element token
    if (!(token instanceof EndElementToken)) return true;
    // cannot match if empty
    if (isEmpty()) return false;
    // check if they match
    return ((EndElementToken) token).match(current())
        && this.openChanges[this.size - 1] == operator;
  }

  /**
   * Indicates whether the first specified token has priority over the second element.
   * <p>
   * It only seem to be the case when the algorithm has the choice between an attribute and another
   * element.
   *
   * @param token1 The token assumed to have priority.
   * @param token2 The other token.
   *
   * @return <code>true</code> if first specified token has priority over the second element;
   * <code>false</code> otherwise.
   */
  public boolean hasPriorityOver(Token token1, Token token2) {
    return token1 instanceof AttributeToken
        && !(token2 instanceof AttributeToken)
        && !isEmpty();
  }

  // Stack methods ------------------------------------------------------------------------

  /**
   * Push the specified open element and flags it with the specified change.
   *
   * @param token    The open element to push.
   * @param operator The operator.
   */
  private void push(StartElementToken token, Operator operator) {
    ensureCapacity(this.size + 1);
    this.openElements[this.size] = token;
    this.openChanges[this.size] = operator;
    this.size++;
  }

  /**
   * Removes the last element from the top of the stack.
   *
   * @return The last element from the top of the stack.
   */
  public StartElementToken pop() {
    if (this.size > 0) {
      this.size--;
      return this.openElements[this.size];
    }
    return null;
  }

  // Positional Access Operations ---------------------------------------------------------

  /**
   * Returns the open element at the specified position in this list.
   *
   * @param index index of element to return.
   *
   * @return The element at the specified position in this list.
   * @throws IndexOutOfBoundsException if index is out of range
   *                                   <code>(index &lt; 0 || index &gt;= size())</code>.
   */
  public StartElementToken get(int index) throws IndexOutOfBoundsException {
    checkRange(index);
    return this.openElements[index];
  }

  /**
   * Removes the element at the specified position in this list.
   * Shifts any subsequent elements to the left (subtracts one from their
   * indices).
   *
   * @param index The index of the element to removed.
   *
   * @return The element that was removed from the list.
   * @throws IndexOutOfBoundsException if index is out of range
   *                                   <code>(index &lt; 0 || index &gt;= size())</code>.
   */
  public StartElementToken remove(int index) throws IndexOutOfBoundsException {
    checkRange(index);
    StartElementToken oldValue = this.openElements[index];
    int numMoved = this.size - index - 1;
    if (numMoved > 0) {
      System.arraycopy(this.openElements, index + 1, this.openElements, index, numMoved);
    }
    this.openElements[--this.size] = null; // Let gc do its work
    return oldValue;
  }

  /**
   * Removes all of the elements from this list.  The list will
   * be empty after this call returns.
   */
  public void clear() {
    // Let gc do its work
    for (int i = 0; i < this.size; i++) {
      this.openElements[i] = null;
    }
    this.size = 0;
  }

  /**
   * Checks if the given index is in range. If not, throw an appropriate
   * runtime exception.  This method does *not* check if the index is
   * negative: It is always used immediately prior to an array access,
   * which throws an ArrayIndexOutOfBoundsException if index is negative.
   *
   * @param index The index to check.
   *
   * @throws IndexOutOfBoundsException if index is out of range
   *                                   <code>(index &lt; 0 || index &gt;= size())</code>.
   */
  private void checkRange(int index) throws IndexOutOfBoundsException {
    if (index >= this.size)
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.size);
  }

}
