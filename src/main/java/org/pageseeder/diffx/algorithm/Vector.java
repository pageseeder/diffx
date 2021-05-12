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
package org.pageseeder.diffx.algorithm;

/**
 * Hold x-positions of end-points on a k-line.
 *
 * <q>
 * An array, V, contains the endpoints of the furthest reaching D-paths in elements V[- D], V[-D+ 2], . . . , V[D-2],
 * V[D]. By Lemma 1 this set of elements is disjoint from those where the endpoints of the (D+1)-paths will be stored
 * in the next iteration of the outer loop. Thus the array V can simultaneously hold the endpoints of the D-paths while
 * the (D+1)-path endpoints are being computed from them. Furthermore, to record an endpoint (x,y) in diagonal k it
 * suffices to retain just x because y is known to be x - k. Consequently, V is an array of integers where V[k] contains
 * the row index of the endpoint of a furthest reaching path in diagonal k.
 * </q>
 *
 * @author christophe Lauret
 * @version 0.9.0
 */
final class Vector {

  /**
   * Stores the actual x-position
   */
  private final int[] array;

  /**
   * Comparison direction flag
   */
  private final boolean isForward;

  /**
   * The maximum number of end points to store
   */
  private final int max;

  /**
   * Difference between length of A and B so that the k lines of the forward and reverse algorithms can be
   * computed correctly.
   */
  private int delta;

  /**
   * Private constructor
   */
  private Vector(int[] array, boolean forward, int max, int delta) {
    this.array = array;
    this.isForward = forward;
    this.max = max;
    this.delta = delta;
  }

  /**
   * Stores the x-position of an end point for a given k-line.
   *
   * @param k The k-line to store the position for
   * @param x The x-position of the end point
   */
  public void setX(int k, int x) {
    this.array[k - this.delta + this.max] = x;
  }

  /**
   * Returns the x-position for an end point for a given k-line
   *
   * @param k The k-line to recall the x-position for
   *
   * @return The x-position of an end point
   */
  public int getX(int k) {
    return this.array[k - this.delta + this.max];
  }

  /**
   * Calculates the y-position of an end point based on the x-position and the k-line.
   *
   * @param k The k-line the end point is on
   *
   * @return The y-position of the end point
   */
  public int getY(int k) {
    return this.getX(k) - k;
  }

  /**
   * Create a new V array for the linear algorithm.
   *
   * @param m       The length of the first sequence
   * @param n       The length of the second sequence
   * @param forward The comparison direction; True if forward, false otherwise
   */
  public static Vector createLinear(int m, int n, boolean forward) {
    return create(m, n, forward, (m + n) / 2 + 1);
  }

  /**
   * Create a new V array for the greedy algorithm.
   *
   * @param m The length of the first sequence
   * @param n The length of the second sequence
   */
  public static Vector createGreedy(int m, int n) {
    return create(m, n, true, m + n);
  }

  /**
   * Initializes a new instance of this helper class.
   *
   * @param m       The length of the first sequence
   * @param n       The length of the second sequence
   * @param forward true if forward comparison; false otherwise
   * @param max     Maximum number of end points
   */
  private static Vector create(int m, int n, boolean forward, int max) {
    if (max <= 0) {
      max = 1;
    }

    // as each point on a k-line can either come from a down or right move
    // there can only be two successor points for each end-point
    int[] array = new int[2 * max + 1];

    Vector vector = new Vector(array, forward, max, 0);
    vector.init(m, n);
    return vector;
  }

  /**
   * Initializes the k-line based on the comparison direction.
   *
   * @param n The length of the first object to compare
   * @param m The length of the second object to compare
   */
  public void init(int n, int m) {
    if (this.isForward) {
      this.setX(1, 0);
    } else {
      this.delta = n - m;
      this.setX(n - m - 1, n);
    }
  }

  /**
   * Creates a new deep copy of this object.
   *
   * @param d         Number of differences for the same trace
   * @param isForward The comparison direction; True if forward, false otherwise
   * @param delta     Keeps track of the differences between the first and the second object to compare as they may differ in
   *                  length
   *
   * @return A copy of this object
   * @throws IllegalArgumentException If d > the maximum number of end points to store
   */
  public Vector createCopy(int d, boolean isForward, int delta) {
    assert !(isForward && delta != 0);
    if (d == 0) {
      d++;
    }
    if (d > this.max)
      throw new IllegalArgumentException("D cannot exceed the maximum number of end points");
    int[] array = new int[2 * d + 1];
    System.arraycopy(this.array, (this.max - this.delta) - (d - delta), array, 0, array.length);
    return new Vector(array, isForward, d, isForward ? 0 : delta);
  }

  @Override
  public String toString() {
    return "Vector " + this.array.length + " [ " + (this.delta - this.max) + " .. " + this.delta + " .. " +
        (this.delta + this.max) + " ]";
  }
}