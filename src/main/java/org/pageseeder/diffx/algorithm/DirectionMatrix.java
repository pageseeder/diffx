/*
 * Copyright 2010-2021 Allette Systems (Australia)
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
 * A direction-only matrix used for reconstructing a Diff-X path.
 *
 * <p>This matrix stores only the comparison between moving on the X and Y direction
 * for each cell. It is enough for the Wagner-Fischer backtracking logic.
 *
 * @author Christophe Lauret
 *
 * @version 1.3.3
 * @since 1.3.3
 */
public final class DirectionMatrix {

  private static final byte SAME = 0;
  private static final byte X_GREATER = 1;
  private static final byte Y_GREATER = 2;

  private int lengthX;
  private int lengthY;
  private byte[] directions;

  void setup(int lengthX, int lengthY) {
    this.lengthX = lengthX;
    this.lengthY = lengthY;
    this.directions = new byte[lengthX * lengthY];
  }

  void setDirection(int i, int j, byte direction) {
    this.directions[index(i, j)] = direction;
  }

  boolean isGreaterX(int i, int j) {
    return this.directions[index(i, j)] == X_GREATER;
  }

  boolean isGreaterY(int i, int j) {
    return this.directions[index(i, j)] == Y_GREATER;
  }

  boolean isSameXY(int i, int j) {
    return this.directions[index(i, j)] == SAME;
  }

  int lengthX() {
    return this.lengthX;
  }

  int lengthY() {
    return this.lengthY;
  }

  private int index(int i, int j) {
    return i * this.lengthY + j;
  }

  static byte compare(int down, int right) {
    if (down > right) return X_GREATER;
    if (down < right) return Y_GREATER;
    return SAME;
  }
}
