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

import org.junit.jupiter.api.Test;
import org.pageseeder.diffx.api.DiffAlgorithm;
import org.pageseeder.diffx.api.DiffHandler;
import org.pageseeder.diffx.api.Operator;
import org.pageseeder.diffx.load.LineLoader;
import org.pageseeder.diffx.test.DiffAssertions;
import org.pageseeder.diffx.token.impl.LineToken;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests to test differences between lines in a text.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public abstract class BasicLinesDiffTest extends AlgorithmTest<LineToken> {

  @Test
  public final void testLines_NoChange() {
    String textA = "line #1\n"
        + "line #2\n"
        + "line #3\n";
    String textB = "line #1\n"
        + "line #2\n"
        + "line #3\n";
    String exp = "L1L2L3";
    String diff = processDiffLines(textA, textB);
    assertEquals(exp, diff);
  }

  @Test
  public final void testLines_Swap() {
    String textA = "line #1\n"
        + "line #X\n"
        + "line #3\n";
    String textB = "line #1\n"
        + "line #2\n"
        + "line #3\n";
    String[] exp = new String[]{
        "L1+L2-L2L3",
        "L1-L2+L2L3"
    };
    assertDiffLinesOK(textA, textB, exp);
  }

  @Test
  public final void testLines_Insert() {
    String textA = "line #1\n"
        + "line #3\n";
    String textB = "line #1\n"
        + "line #2\n"
        + "line #3\n";
    String[] exp = new String[]{
        "L1+L2L2",
        "L1+L2L3"
    };
    assertDiffLinesOK(textA, textB, exp);
  }

  @Test
  public final void testLines_Remove() {
    String textA = "line #1\n"
        + "line #2\n"
        + "line #3\n";
    String textB = "line #1\n"
        + "line #3\n";
    String[] exp = new String[]{
        "L1-L2L2",
        "L1-L2L3"
    };
    assertDiffLinesOK(textA, textB, exp);
  }

  private void assertDiffLinesOK(String textA, String textB, String[] exp) {
    String actual = processDiffLines(textA, textB);
    DiffAssertions.assertEqualsAny(exp, actual);
  }

  private String processDiffLines(String textA, String textB) {
    List<LineToken> seqA = loadLineEvents(textA);
    List<LineToken> seqB = loadLineEvents(textB);
    DiffAlgorithm<LineToken> processor = getDiffAlgorithm();
    TestLineHandler handler = new TestLineHandler();
    processor.diff(seqA, seqB, handler);
    return handler.getOutput();
  }

  private static List<LineToken> loadLineEvents(String text) {
    if (text.isEmpty()) return Collections.emptyList();
    return new LineLoader().load(text);
  }

  private static class TestLineHandler implements DiffHandler<LineToken> {

    StringBuilder out = new StringBuilder();

    @Override
    public void handle(Operator operator, LineToken token) {
      if (operator != Operator.MATCH) this.out.append(operator.toString());
      this.out.append("L").append(token.getLineNumber());
    }

    String getOutput() {
      return this.out.toString();
    }
  }
}
