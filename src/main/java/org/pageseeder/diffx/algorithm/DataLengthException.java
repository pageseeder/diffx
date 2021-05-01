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
 * Exception thrown when the size of the input data exceeds the maxmimum supported by the
 * algorithm or processor.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class DataLengthException extends IllegalArgumentException {

  private final int size;

  private final int threshold;

  public DataLengthException(int size, int threshold) {
    super(toMessage(size, threshold));
    this.size = size;
    this.threshold = threshold;
  }

  /**
   * @return The offending length of the data.
   */
  public int getSize() {
    return this.size;
  }

  /**
   * @return maximum size allowed (inclusive)
   */
  public int getThreshold() {
    return this.threshold;
  }

  private static String toMessage(int size, int threshold) {
    return String.format("Too many points of comparison: %d is greater than maximum allowed (%d).", size, threshold);
  }

}
