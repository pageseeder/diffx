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

/**
 * A matrix for the computation of the Diff-X path.
 *
 * <p>This interface is intended to provide methods for initialising and accessing
 * the values of the matrix regardless of the storage method used.
 *
 * <p>Implementations could use binary matrices, I/O objects, etc...
 *
 * @author Christophe Lauret (Allette Systems)
 * @version 0.9.0
 */
public interface Matrix {

  /**
   * Create a matrix of the given width and height.
   *
   * @param width  The number of columns.
   * @param height The number of rows.
   */
  void setup(int width, int height);

  /**
   * Sets the value of the matrix at the given position.
   *
   * @param i The column index.
   * @param j The row index.
   * @param x The value to set.
   */
  void set(int i, int j, int x);

  /**
   * Returns the value at the given position.
   *
   * @param i The column index.
   * @param j The row index.
   *
   * @return The value at the given position.
   */
  int get(int i, int j);

  /**
   * Increment the path.
   *
   * <p>value(i, j) := value(i+1, j+1) + n
   *
   * @param i The column index.
   * @param j The row index.
   */
  void incrementPath(int i, int j);

  /**
   * Increment by the maximum path.
   *
   * <p>value(i, j) := max( value(i+1, j) , value(i, j+1) )
   *
   * @param i The column index.
   * @param j The row index.
   */
  void incrementByMaxPath(int i, int j);

  /**
   * Returns <code>true</code> we should move on the X direction.
   *
   * <p>if {@code value(i+1, j) > value(i, j+1)}
   *
   * @param i The column index.
   * @param j The row index.
   *
   * @return <code>true</code> to move to i+1;
   * <code>false</code> otherwise.
   */
  boolean isGreaterX(int i, int j);

  /**
   * Returns <code>true</code> we should move on the X direction.
   *
   * <p>if value(i+1, j) &lt; value(i, j+1)
   *
   * @param i The column index.
   * @param j The row index.
   *
   * @return <code>true</code> to move to j+1;
   * <code>false</code> otherwise.
   */
  boolean isGreaterY(int i, int j);

  /**
   * Returns <code>true</code> when moving on the X direction is
   * equivalent to moving on the Y direction.
   *
   * <p>if value(i+1, j) == value(i, j+1)
   *
   * @param i The column index.
   * @param j The row index.
   *
   * @return <code>true</code> if it is the same;
   * <code>false</code> otherwise.
   */
  boolean isSameXY(int i, int j);

  /**
   * Releases all the resources used only by this matrix object.
   *
   * <p>This class is not usable until after invoking this method, unless
   * it is set up again.
   */
  void release();

  /**
   * @return size of matrix on X axis
   */
  int lengthX();

  /**
   * @return size of matrix on Y axis
   */
  int lengthY();

  /**
   * @return Size of the matrix
   */
  default int size() {
    return this.lengthX() * this.lengthY();
  }

  int getLCSLength();

}
