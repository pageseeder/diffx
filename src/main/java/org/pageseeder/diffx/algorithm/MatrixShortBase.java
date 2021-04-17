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
 * A matrix implementation backed by a matrix of integers.
 *
 * @author Christophe Lauret (Allette Systems)
 * @version 0.9.0
 */
public abstract class MatrixShortBase implements Matrix {

  /**
   * The actual matrix storing the values.
   */
  protected short[][] matrix;

  /**
   * Returns the maximum of the two values.
   *
   * @param a The first value to compare.
   * @param b The second value to compare.
   *
   * @return The maximum of the two values.
   */
  protected static short max(short a, short b) {
    return a >= b ? a : b;
  }

  /**
   * @see Matrix#setup(int, int)
   */
  @Override
  public void setup(int width, int height) {
    this.matrix = new short[width][height];
  }

  /**
   * @see Matrix#set(int, int, int)
   */
  @Override
  public void set(int i, int j, int x) {
    this.matrix[i][j] = (short) x;
  }

  /**
   * @see Matrix#get(int, int)
   */
  @Override
  public int get(int i, int j) {
    return this.matrix[i][j];
  }

  @Override
  public int lengthX() {
    return this.matrix.length;
  }

  @Override
  public int lengthY() {
    return this.matrix[0].length;
  }

  /**
   * @see Matrix#isGreaterX(int, int)
   */
  @Override
  public boolean isGreaterX(int i, int j) {
    return this.matrix[i + 1][j] > this.matrix[i][j + 1];
  }

  /**
   * @see Matrix#isGreaterY(int, int)
   */
  @Override
  public boolean isGreaterY(int i, int j) {
    return this.matrix[i + 1][j] < this.matrix[i][j + 1];
  }

  /**
   * @see Matrix#isSameXY(int, int)
   */
  @Override
  public boolean isSameXY(int i, int j) {
    return this.matrix[i + 1][j] == this.matrix[i][j + 1];
  }

  /**
   * @see Object#toString()
   */
  @Override
  public String toString() {
    StringBuilder out = new StringBuilder();
    for (int j = 0; j < this.matrix[0].length; j++) {
      for (short[] element : this.matrix) {
        out.append(element[j]).append("\t");
      }
      out.append('\n');
    }
    return out.toString();
  }

  /**
   * Gets rid of the underlying matrix so that garbage collector can do its work.
   *
   * @see Matrix#release()
   */
  @Override
  public void release() {
    this.matrix = null;
  }
}
