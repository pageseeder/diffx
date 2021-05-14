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
package org.pageseeder.diffx.core;

import org.junit.jupiter.api.Nested;
import org.pageseeder.diffx.algorithm.BasicGeneralDiffTest;
import org.pageseeder.diffx.algorithm.BasicLinesDiffTest;
import org.pageseeder.diffx.algorithm.BasicXMLDiffTest;
import org.pageseeder.diffx.algorithm.DiffAlgorithm;
import org.pageseeder.diffx.token.XMLToken;

/**
 * Test case for the default XML processor
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class DefaultXMLProcessorTest {

  private DefaultXMLProcessor newProcessor() {
    return new DefaultXMLProcessor();
  }

  @Nested
  public class GeneralDiff extends BasicGeneralDiffTest {
    @Override
    public DiffAlgorithm<XMLToken> getDiffAlgorithm() {
      return newProcessor();
    }
  }

  @Nested
  public class LinesDiff extends BasicLinesDiffTest {
    @Override
    public DiffAlgorithm<XMLToken> getDiffAlgorithm() {
      return newProcessor();
    }
  }

  @Nested
  public class BasicXMLDiff extends BasicXMLDiffTest {
    @Override
    public DiffAlgorithm<XMLToken> getDiffAlgorithm() {
      return newProcessor();
    }
  }

  @Nested
  public class AdvancedXMLDiff extends AdvancedXMLDiffTest {
    @Override
    public DiffAlgorithm<XMLToken> getDiffAlgorithm() {
      return newProcessor();
    }
  }

  @Nested
  public class CoalesceXMLDiff extends CoalesceXMLDiffTest {
    @Override
    public DiffProcessor<XMLToken> getProcessor() {
      DefaultXMLProcessor processor = newProcessor();
      processor.setCoalesce(true);
      return processor;
    }
  }

  @Nested
  public class RandomXMLDiff extends RandomXMLDiffTest {
    @Override
    public DiffAlgorithm<XMLToken> getDiffAlgorithm() {
      DefaultXMLProcessor processor = newProcessor();
      processor.setThreshold(Integer.MAX_VALUE);
      return processor;
    }
  }

}
