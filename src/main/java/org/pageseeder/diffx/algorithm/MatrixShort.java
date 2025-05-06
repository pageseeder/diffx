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
 * A matrix implementation which uses the {@code short} instead of {@code int} so that it takes less
 * memory space during processing.
 *
 * <p>It can only be used when the max value to store is 32767.
 *
 * @author Christophe Lauret (Allette Systems)
 * @version 0.9.0
 */
public final class MatrixShort extends MatrixShortBase {

  /**
   * @see Matrix#incrementPath(int, int)
   */
  @Override
  public void incrementPath(int i, int j) {
    this.matrix[i][j] = (short) (this.matrix[i - 1][j - 1] + 1);
  }

  /**
   * @see Matrix#incrementByMaxPath(int, int)
   */
  @Override
  public void incrementByMaxPath(int i, int j) {
    this.matrix[i][j] = max(this.matrix[i - 1][j], this.matrix[i][j - 1]);
  }

  public int getLCSLength() {
    return this.get(this.matrix.length - 1, this.matrix[0].length - 1);
  }
}
