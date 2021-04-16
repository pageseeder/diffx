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

import org.junit.jupiter.api.Test;
import org.pageseeder.diffx.test.DiffAssertions;
import org.pageseeder.diffx.test.Events;
import org.pageseeder.diffx.test.TestHandler;
import org.pageseeder.diffx.token.Token;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests to test differences between lines in a text.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public abstract class BasicLinesDiffTest extends AlgorithmTest {

  @Test
  public final void testLines_NoChange() {
    String text1 = "line #1\n"
        + "line #2\n"
        + "line #3\n";
    String text2 = "line #1\n"
        + "line #2\n"
        + "line #3\n";
    String exp = "L1L2L3";
    String diff = processDiffLines(text1, text2);
    assertEquals(exp, diff);
  }

  @Test
  public final void testLines_Swap() {
    String text1 = "line #1\n"
        + "line #2\n"
        + "line #3\n";
    String text2 = "line #1\n"
        + "line #X\n"
        + "line #3\n";
    String[] exp = new String[]{
        "L1+L2-L2L3",
        "L1-L2+L2L3"
    };
    assertDiffLinesOK(text1, text2, exp);
  }

  @Test
  public final void testLines_Insert() {
    String text1 = "line #1\n"
        + "line #2\n"
        + "line #3\n";
    String text2 = "line #1\n"
        + "line #3\n";
    String[] exp = new String[]{
        "L1+L2L2",
        "L1+L2L3"
    };
    assertDiffLinesOK(text1, text2, exp);
  }

  @Test
  public final void testLines_Remove() {
    String text1 = "line #1\n"
        + "line #3\n";
    String text2 = "line #1\n"
        + "line #2\n"
        + "line #3\n";
    String[] exp = new String[]{
        "L1-L2L2",
        "L1-L2L3"
    };
    assertDiffLinesOK(text1, text2, exp);
  }

  private void assertDiffLinesOK(String text1, String text2, String[] exp) {
    String actual = processDiffLines(text1, text2);
    DiffAssertions.assertEqualsAny(exp, actual);
  }

  private String processDiffLines(String text1, String text2) {
    List<? extends Token> seq1 = Events.recordLineEvents(text1);
    List<? extends Token> seq2 = Events.recordLineEvents(text2);
    DiffAlgorithm processor = getDiffAlgorithm();
    TestHandler handler = new TestHandler();
    processor.diff(seq1, seq2, handler);
    return handler.getOutput();
  }

}
