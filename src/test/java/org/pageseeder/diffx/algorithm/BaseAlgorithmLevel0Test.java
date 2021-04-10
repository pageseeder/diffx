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

import org.junit.Test;
import org.pageseeder.diffx.action.Action;
import org.pageseeder.diffx.action.ActionFormatter;
import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.config.DiffXConfig;
import org.pageseeder.diffx.core.BaseProcessorTest;
import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.event.impl.CharEvent;
import org.pageseeder.diffx.format.DiffXFormatter;
import org.pageseeder.diffx.format.MultiplexFormatter;
import org.pageseeder.diffx.sequence.EventSequence;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Test case for Diff-X algorithm implementations.
 *
 * <p>To pass this test an algorithm must only be able to find the correct differences in a piece or text.
 * XML awareness isn't required.
 *
 * <p>All algorithms must pass this test to show they produce correct results.</p>
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public abstract class BaseAlgorithmLevel0Test extends BaseAlgorithmTest {

  @Test
  public final void testLevel0_Empty() throws IOException {
    String a = "";
    String b = "";
    String[] exp = new String[]{""};
    assertDiffOKLevel0(a, b, exp);
  }

  // Identical ----------------------------------------------------------------

  @Test
  public final void testLevel0_Identical1() throws IOException {
    String a = "x";
    String b = "x";
    String[] exp = new String[]{"x"};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Identical2() throws IOException {
    String a = "xx";
    String b = "xx";
    String[] exp = new String[]{"xx"};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Identical3() throws IOException {
    String a = "xyz";
    String b = "xyz";
    String[] exp = new String[]{"xyz"};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Identical10() throws IOException {
    String a = "abcdefghij";
    String b = "abcdefghij";
    String[] exp = new String[]{"abcdefghij"};
    assertDiffOKLevel0(a, b, exp);
  }

  // Inserts and deletes ------------------------------------------------------

  @Test
  public final void testLevel0_Insert1() throws IOException {
    String a = "x";
    String b = "";
    String[] exp = new String[]{"+x"};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Delete1() throws IOException {
    String a = "";
    String b = "y";
    String[] exp = new String[]{"-y"};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Insert2() throws IOException {
    String a = "xx";
    String b = "";
    String[] exp = new String[]{"+x+x"};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Delete2() throws IOException {
    String a = "";
    String b = "yy";
    String[] exp = new String[]{"-y-y"};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Insert3() throws IOException {
    String a = "xx";
    String b = "x";
    String[] exp = new String[]{"x+x", "+xx"};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Delete3() throws IOException {
    String a = "y";
    String b = "yy";
    String[] exp = new String[]{"y-y", "-yy"};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Insert4() throws IOException {
    String a = "xxx";
    String b = "xx";
    String[] exp = new String[]{"+xxx", "x+xx", "xx+x"};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Delete4() throws IOException {
    String a = "yy";
    String b = "yyy";
    String[] exp = new String[]{"-yyy", "y-yy", "yy-y"};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Insert5() throws IOException {
    String a = "   x   ";
    String b = "      ";
    String[] exp = new String[]{"   +x   "};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Delete5() throws IOException {
    String a = "      ";
    String b = "   y   ";
    String[] exp = new String[]{"   -y   "};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Insert6() throws IOException {
    String a = "testing";
    String b = "test";
    String[] exp = new String[]{"test+i+n+g"};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Delete6() throws IOException {
    String a = "test";
    String b = "testing";
    String[] exp = new String[]{"test-i-n-g"};
    assertDiffOKLevel0(a, b, exp);
  }

  // Replacements -------------------------------------------------------------

  @Test
  public final void testLevel0_Replace1() throws IOException {
    String a = "x";
    String b = "y";
    String[] exp = new String[]{"+x-y", "-y+x"};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Replace2() throws IOException {
    String a = "xx";
    String b = "yy";
    String[] exp = new String[]{
        "+x+x-y-y",
        "+x-y+x-y",
        "+x-y-y+x",
        "-y+x-y+x",
        "-y-y+x+x",
        "-y+x+x-y"
    };
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Replace3() throws IOException {
    String a = "xax";
    String b = "xbx";
    String[] exp = new String[]{"x+a-bx", "x-b+ax"};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Replace4() throws IOException {
    String a = "axa";
    String b = "bxb";
    String[] exp = new String[]{
        "+a-bx+a-b",
        "+a-bx-b+a",
        "-b+ax+a-b",
        "-b+ax-b+a",
    };
    assertDiffOKLevel0(a, b, exp);
  }

  // More complex cases -------------------------------------------------------

  @Test
  public final void testLevel0_Complex1() throws IOException {
    String a = "aba";
    String b = "bab";
    String[] exp = new String[]{
        "+aba-b",
        "-bab+a"
    };
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Complex2() throws IOException {
    String a = "abab";
    String b = "baba";
    String[] exp = new String[]{
        "+abab-a",
        "-baba+b"
    };
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Complex3() throws IOException {
    String a = "ababa";
    String b = "babab";
    String[] exp = new String[]{
        "+ababa-b",
        "-babab+a",
    };
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Complex4() throws IOException {
    String a = "one little";
    String b = "two little";
    String[] exp = new String[]{
        "-t-wo+n+e little"
    };
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Complex5() throws IOException {
    String a = "one little";
    String b = "too little";
    String[] exp = new String[]{
        "-t-oo+n+e little"
    };
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Complex6() throws IOException {
    String a = "balaclava";
    String b = "bilabial";
    String[] exp = new String[]{
        "-t-oo+n+e little"
    };
    assertDiffOKLevel0(a, b, exp);
  }

  // helpers
  // --------------------------------------------------------------------------

  public final void assertDiffOKLevel0(String text1, String text2, String[] exp) throws IOException {
    EventSequence seq1 = asSequenceOfCharEvents(text1);
    EventSequence seq2 = asSequenceOfCharEvents(text2);

    // Setup and process
    DiffXAlgorithm diffx = makeDiffX(seq1, seq2);
    ActionFormatter af = new ActionFormatter();
    CharTestFormatter cf = new CharTestFormatter();
    diffx.process(new MultiplexFormatter(cf, af));
    String got = cf.getOutput();
    List<Action> actions = af.getActions();

    try {
      assertDiffIsCorrect(seq1, seq2, actions);
    } catch (AssertionError ex) {
      printCharErrorDetails(text1, text2, exp, got, actions);
      throw ex;
    }
  }

  private static EventSequence asSequenceOfCharEvents(String string) {
    EventSequence s = new EventSequence();
    for (char c : string.toCharArray()) {
      s.addEvent(new CharEvent(c));
    }
    return s;
  }

  /**
   * Print the error details.
   */
  private void printCharErrorDetails(String text1, String text2, String[] exp, String got, List<Action> actions) {
    System.err.println("+------------------------------------------------");
    System.err.println("| Input A: \"" + text1 + "\"");
    System.err.println("| Input B: \"" + text2 + "\"");
    System.err.println("| Output:  \"" + got + "\"");
    System.err.print("| Expect:  ");
    for (String s : exp) System.err.print("\"" + s + "\" ");
    System.err.println();
    System.err.print("| Actions: ");
    for (Action action : actions) {
      System.err.print(action.type() == Operator.DEL ? '-' : action.type() == Operator.INS ? '+' : '=');
      System.err.print(action.events().stream().map((event) -> ((CharEvent) event).getChar()).collect(Collectors.toList()));
    }
    System.err.println();
  }

  private static final class CharTestFormatter implements DiffXFormatter {

    StringBuilder out = new StringBuilder();

    @Override
    public void format(DiffXEvent event) throws IOException, IllegalStateException {
      out.append(((CharEvent) event).getChar());
    }

    @Override
    public void insert(DiffXEvent event) throws IOException, IllegalStateException {
      out.append('+').append(((CharEvent) event).getChar());
    }

    @Override
    public void delete(DiffXEvent event) throws IOException, IllegalStateException {
      out.append('-').append(((CharEvent) event).getChar());
    }

    @Override
    public void setConfig(DiffXConfig config) {
    }

    String getOutput() {
      return this.out.toString();
    }
  }

}
