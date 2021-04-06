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
package org.pageseeder.diffx.core;

import org.pageseeder.diffx.algorithm.BaseAlgorithmLevel2Test;
import org.pageseeder.diffx.algorithm.DiffXAlgorithm;
import org.pageseeder.diffx.algorithm.GuanoAlgorithm;
import org.pageseeder.diffx.sequence.EventSequence;

/**
 * Test case for Guano Diff-X algorithm.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class DefaultXMLProcessorTest extends BaseProcessorLevel2Test {

  @Override
  public DiffProcessor getDiffProcessor() {
    return new DefaultXMLProcessor();
  }

}
