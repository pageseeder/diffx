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

import java.io.IOException;

import org.pageseeder.diffx.format.DiffXFormatter;
import org.pageseeder.diffx.sequence.EventSequence;

/**
 * Performs the diff comparison of sequences of events.
 *
 * @author Christophe Lauret
 * @version 15 December 2004
 */
public interface DiffXAlgorithm {

  /**
   * Returns the length of the longest common subsequence.
   *
   * @return the length of the longest common subsequence.
   */
  int length();

  /**
   * Performs the comparison and writes the results using the specified Diff-X formatter.
   *
   * @param formatter The formatter that will handle the output.
   *
   * @throws IOException If thrown by the formatter.
   */
  void process(DiffXFormatter formatter) throws IOException;

  /**
   * Returns the first sequence used for the diff-x comparison.
   *
   * @return the first sequence used for the diff-x comparison.
   */
  EventSequence getFirstSequence();

  /**
   * Returns the second sequence used for the diff-x comparison.
   *
   * @return the second sequence used for the diff-x comparison.
   */
  EventSequence getSecondSequence();

}