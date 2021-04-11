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

import org.junit.Test;
import org.pageseeder.diffx.action.Action;
import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.event.impl.CharEvent;
import org.pageseeder.diffx.handler.ActionHandler;
import org.pageseeder.diffx.handler.DiffHandler;
import org.pageseeder.diffx.handler.MuxHandler;
import org.pageseeder.diffx.test.DiffAssertions;
import org.pageseeder.diffx.test.Events;
import org.pageseeder.diffx.test.RandomStringFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Test case for algorithm implementations.
 *
 * <p>To pass this test an algorithm must only be able to find the correct differences in a piece or text.
 * XML awareness isn't required.
 *
 * <p>All algorithms must pass this test to show they produce correct results.</p>
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public abstract class BaseProcessorLevel0Test extends BaseProcessorTest {

  @Test
  public final void testLevel0_Empty() {
    String a = "";
    String b = "";
    String[] exp = new String[]{""};
    assertDiffOKLevel0(a, b, exp);
  }

  // Identical ----------------------------------------------------------------

  @Test
  public final void testLevel0_Identical1() {
    String a = "x";
    String b = "x";
    String[] exp = new String[]{"x"};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Identical2() {
    String a = "xx";
    String b = "xx";
    String[] exp = new String[]{"xx"};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Identical3() {
    String a = "xyz";
    String b = "xyz";
    String[] exp = new String[]{"xyz"};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Identical10() {
    String a = "abcdefghij";
    String b = "abcdefghij";
    String[] exp = new String[]{"abcdefghij"};
    assertDiffOKLevel0(a, b, exp);
  }

  // Inserts and deletes ------------------------------------------------------

  @Test
  public final void testLevel0_Insert1() {
    String a = "x";
    String b = "";
    String[] exp = new String[]{"+x"};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Delete1() {
    String a = "";
    String b = "y";
    String[] exp = new String[]{"-y"};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Insert2() {
    String a = "xx";
    String b = "";
    String[] exp = new String[]{"+x+x"};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Delete2() {
    String a = "";
    String b = "yy";
    String[] exp = new String[]{"-y-y"};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Insert3() {
    String a = "xx";
    String b = "x";
    String[] exp = new String[]{"x+x", "+xx"};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Delete3() {
    String a = "y";
    String b = "yy";
    String[] exp = new String[]{"y-y", "-yy"};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Insert4() {
    String a = "xxx";
    String b = "xx";
    String[] exp = new String[]{"+xxx", "x+xx", "xx+x"};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Delete4() {
    String a = "yy";
    String b = "yyy";
    String[] exp = new String[]{"-yyy", "y-yy", "yy-y"};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Insert5() {
    String a = "   x   ";
    String b = "      ";
    String[] exp = new String[]{"   +x   "};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Delete5() {
    String a = "      ";
    String b = "   y   ";
    String[] exp = new String[]{"   -y   "};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Insert6() {
    String a = "testing";
    String b = "test";
    String[] exp = new String[]{"test+i+n+g"};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Delete6() {
    String a = "test";
    String b = "testing";
    String[] exp = new String[]{"test-i-n-g"};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Insert7() {
    String a = "foretaste";
    String b = "taste";
    String[] exp = new String[]{"+f+o+r+etaste"};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Delete7() {
    String a = "taste";
    String b = "foretaste";
    String[] exp = new String[]{"-f-o-r-etaste"};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Insert8() {
    String a = "baobab";
    String b = "bobb";
    String[] exp = new String[]{"b+aob+ab"};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Delete8() {
    String a = "bobb";
    String b = "baobab";
    String[] exp = new String[]{"b-aob-ab"};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Insert9() {
    String a = "alibaba";
    String b = "libb";
    String[] exp = new String[]{"+alib+ab+a"};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Delete9() {
    String a = "libb";
    String b = "alibaba";
    String[] exp = new String[]{"-alib-ab-a"};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Insert10() {
    String a = "links";
    String b = "ink";
    String[] exp = new String[]{"+link+s"};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Delete10() {
    String a = "ink";
    String b = "links";
    String[] exp = new String[]{"-link-s"};
    assertDiffOKLevel0(a, b, exp);
  }


  @Test
  public final void testLevel0_Insert11() {
    String a = "inks";
    String b = "ink";
    String[] exp = new String[]{"ink+s"};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Delete11() {
    String a = "ink";
    String b = "inks";
    String[] exp = new String[]{"ink-s"};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Insert12() {
    String a = "link";
    String b = "ink";
    String[] exp = new String[]{"+link"};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Delete12() {
    String a = "ink";
    String b = "link";
    String[] exp = new String[]{"-link"};
    assertDiffOKLevel0(a, b, exp);
  }
  // Replacements -------------------------------------------------------------

  @Test
  public final void testLevel0_Replace1() {
    String a = "x";
    String b = "y";
    String[] exp = new String[]{"+x-y", "-y+x"};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Replace2() {
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
  public final void testLevel0_Replace3() {
    String a = "xax";
    String b = "xbx";
    String[] exp = new String[]{"x+a-bx", "x-b+ax"};
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Replace4() {
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

  @Test
  public final void testLevel0_Replace5() {
    String a = "axax";
    String b = "bxbx";
    String[] exp = new String[]{
        "+a-bx+a-bx",
        "+a-bx-b+ax",
        "-b+ax+a-bx",
        "-b+ax-b+ax",
    };
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Replace6() {
    String a = "xaxa";
    String b = "xbxb";
    String[] exp = new String[]{
        "x+a-bx+a-b",
        "x+a-bx-b+a",
        "x-b+ax-b+a",
        "x-b+ax+a-b"
    };
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Replace7() {
    String a = "axbx";
    String b = "bxax";
    String[] exp = new String[]{
        "+a+xbx-a-x",
        "+a+xb-x-ax",
        "-b-xax+b+x"
    };
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Replace8() {
    String a = "axax";
    String b = "bxbx";
    String[] exp = new String[]{
        "+a-bx+a-bx",
        "+a-bx-b+ax",
        "-b+ax+a-bx",
        "-b+ax-b+ax"
    };
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Replace9() {
    String a = "axaxa";
    String b = "bxbxb";
    String[] exp = new String[]{
        "-b+ax-b+ax-b+a",
        "+a-bx+a-bx-b+a",
        "+a-bx+a-bx+a-b"
    };
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Replace10() {
    String a = "axbxa";
    String b = "bxaxb";
    String[] exp = new String[]{
        "+a+xbxa-x-b",
        "-b-xaxb+x+a"
    };
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Replace11() {
    String a = "xaxax";
    String b = "xbxbx";
    String[] exp = new String[]{
        "x+a-bx+a-bx",
        "x+a-bx-b+ax",
        "x-b+ax+a-bx",
        "x-b+ax-b+ax"
    };
    assertDiffOKLevel0(a, b, exp);
  }

  // More complex cases -------------------------------------------------------

  @Test
  public final void testLevel0_Complex1() {
    String a = "aba";
    String b = "bab";
    String[] exp = new String[]{
        "+aba-b",
        "-bab+a"
    };
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Complex2() {
    String a = "abab";
    String b = "baba";
    String[] exp = new String[]{
        "+abab-a",
        "-baba+b"
    };
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Complex3() {
    String a = "ababa";
    String b = "babab";
    String[] exp = new String[]{
        "+ababa-b",
        "-babab+a",
    };
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Complex4() {
    String a = "one little";
    String b = "two little";
    String[] exp = new String[]{
      "-t-wo+n+e little"
    };
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Complex5() {
    String a = "one little";
    String b = "too little";
    String[] exp = new String[]{
        "-t-oo+n+e little",
        "-to+n+e-o little",
        "-to+n-o+e little"
    };
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Complex6() {
    String a = "balaclava";
    String b = "bilabial";
    String[] exp = new String[]{
        "b+a+l+a+c-ila+v-b-ia-l",
        "b-i+ala+c-b-i+la+v-l+a"
    };
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Complex7() {
    String a = "Saturday";
    String b = "Sunday";
    String[] exp = new String[]{
        "S+a+tu+r-nday",
        "S+a+tu-n+rday"
    };
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Complex9() {
    String a = "Monday Tuesday Sunday";
    String b = "Monday Sunday";
    String[] exp = new String[]{
        "Monday +T+u+e+s+d+a+y+ Sunday",
        "Monday+ +T+u+e+s+d+a+y Sunday",
        "Mon+d+a+y+ +T+u+e+sday Sunday"
    };
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Complex10() {
    String a = "Monday Sunday";
    String b = "Monday Tuesday Sunday";
    String[] exp = new String[]{
        "Monday -T-u-e-s-d-a-y- Sunday",
        "Monday- -T-u-e-s-d-a-y Sunday",
        "Mon-d-a-y- -T-u-e-sday Sunday"
    };
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Complex11() {
    String a = "A car";
    String b = "A train";
    String[] exp = new String[]{
        "A +c+a-tr-a-i-n",
        "A -t-r+ca-i-n+r"
    };
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Complex12() {
    String a = "The car";
    String b = "A train";
    String[] exp = new String[]{
        "+T+h-A+e -t-r+ca-i-n+r",
        "+T+h+e-A +c+a-tr-a-i-n"
    };
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Complex13() {
    String a = "The red car";
    String b = "A blue train";
    String[] exp = new String[]{
        "+T+h+e-A +r-b-l-ue+d +c+a-tr-a-i-n",
        "+T-A- -b-l-u+he -tr+e+d+ +ca-i-n+r"
    };
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Complex14() {
    String a = "The little car";
    String b = "A big train";
    String[] exp = new String[]{
        "+T+h+e-A +l-bi+t+t+l+e-g +c+a-tr-a-i-n",
        "+T+h-A+e -b+li-g- t+t+l+e+ -r+ca-i-n+r"
    };
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_Complex15() {
    String a = "The little red car";
    String b = "A big blue train";
    String[] exp = new String[]{
        "+T+h+e-A +l-bi+t+t-g- -bl-ue -tr+e+d+ +ca+r-i-n",
        "+T+h+e-A +l-bi+t+t-g- -bl-ue -tr+e+d+ +ca-i-n+r",
        "+T+h-A+e -b+li+t-g- -b+tl-ue -tr+e+d+ +ca-i-n+r"
    };
    assertDiffOKLevel0(a, b, exp);
  }

  @Test
  public final void testLevel0_complex16() {
    String a = "xabx";
    String b = "abamx";
    assertDiffOKLevel0(a, b);
  }

  @Test
  public final void testLevel0_complex17() {
    String a = "xabcdefghijkmnopqrx";
    String b = "abcdefghijkjmnopqrx";
    assertDiffOKLevel0(a, b);
  }

  @Test
  public final void testLevel0_complex18() {
    String a = "rhxrwpdunx";
    String b = "rhrwpdpunwx";
    assertDiffOKLevel0(a, b);
  }

  @Test
  public final void testLevel0_complex19() {
    String a = "tbdcllohjt";
    String b = "bddcdlohjt";
    assertDiffOKLevel0(a, b);
  }

  @Test
  public final void testLevel0_complex20() {
    String a = "tbdcl";
    String b = "bddcdl";
    assertDiffOKLevel0(a, b);
  }


  // Random variations -------------------------------------------------------

  @Test
  public final void testLevel0_RandomVariations1() {
    RandomStringFactory factory = new RandomStringFactory();
    for (int n=3; n < 20; n++) {
      for (int i=0; i < 100; i++) {
        String a = factory.getRandomString(10, false);
        String b = factory.vary(a, .1);
        assertDiffOKLevel0(a, b);
      }
    }
  }

  @Test
  public final void testLevel0_RandomVariations2() {
    RandomStringFactory factory = new RandomStringFactory();
    for (int n=3; n < 20; n++) {
      for (int i=0; i < 100; i++) {
        String a = factory.getRandomString(100, false);
        String b = factory.vary(a, .1);
        assertDiffOKLevel0(a, b);
      }
    }
  }

  @Test
  public final void testLevel0_RandomVariations3() {
    RandomStringFactory factory = new RandomStringFactory();
    for (int n=3; n < 20; n++) {
      for (int i=0; i < 100; i++) {
        String a = factory.getRandomString(100, false);
        String b = factory.vary(a, .2);
        assertDiffOKLevel0(a, b);
      }
    }
  }

  // helpers
  // --------------------------------------------------------------------------

  public final void assertDiffOKLevel0(String text1, String text2) {
    assertDiffOKLevel0(text1, text2, new String[]{});
  }

  public final void assertDiffOKLevel0(String text1, String text2, String[] exp) {
    List<CharEvent> seq1 = Events.toCharEvents(text1);
    List<CharEvent> seq2 = Events.toCharEvents(text2);
    DiffAlgorithm algorithm = getDiffAlgorithm();
    ActionHandler af = new ActionHandler();
    CharTestHandler cf = new CharTestHandler();

    // Run the diff
    algorithm.diff(seq1, seq2, new MuxHandler(cf, af));

    // Extract output and actions
    String got = cf.getOutput();
    List<Action> actions = af.getActions();

    // Check
    try {
      DiffAssertions.assertIsApplicable(seq1, seq2, actions);
      DiffAssertions.assertIsCorrect(seq1, seq2, actions);
      if (exp.length > 0) {
        DiffAssertions.assertMatchTestOutput(actions, exp);
      }
    } catch (AssertionError ex) {
      printCharErrorDetails(text1, text2, exp, got, actions);
      throw ex;
    }
  }

  /**
   * Print the error details.
   */
  private void printCharErrorDetails(String text1, String text2, String[] exp, String got, List<Action> actions) {
    System.err.println("+------------------------------------------------");
    System.err.println("| Input A: \"" + text1 + "\"");
    System.err.println("| Input B: \"" + text2 + "\"");
    System.err.println("| Output:  \"" + got + "\"");
    if (exp.length > 0) {
      System.err.print("| Expect:  ");
      for (String s : exp) System.err.print("\"" + s + "\" ");
      System.err.println();
    }
    System.err.print("| Actions: ");
    for (Action action : actions) {
      System.err.print(action.type() == Operator.DEL ? '-' : action.type() == Operator.INS ? '+' : '=');
      System.err.print(action.events().stream().map((event) -> ((CharEvent) event).getChar()).collect(Collectors.toList()));
    }
    System.err.println();
  }

  private static final class CharTestHandler implements DiffHandler {

    StringBuilder out = new StringBuilder();

    @Override
    public void handle(Operator operator, DiffXEvent event) throws IllegalStateException {
      if (operator == Operator.INS || operator == Operator.DEL) out.append(operator);
      out.append(((CharEvent) event).getChar());
    }

    String getOutput() {
      return this.out.toString();
    }
  }

}
