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
import org.pageseeder.diffx.action.Action;
import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.handler.ActionsBuffer;
import org.pageseeder.diffx.handler.MuxHandler;
import org.pageseeder.diffx.test.DiffAssertions;
import org.pageseeder.diffx.test.GeneralToken;
import org.pageseeder.diffx.test.TestHandler;
import org.pageseeder.diffx.token.Token;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Test case for algorithm implementations.
 *
 * <p>To pass this test an algorithm must only be able to find the correct differences in a list of tokens.
 * XML awareness isn't required.
 *
 * <p>All algorithms must pass this test to show that they produce correct results.</p>
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public abstract class BasicGeneralDiffTest extends AlgorithmTest<Token> {

  @Test
  public final void testGeneral_Empty() {
    String a = "";
    String b = "";
    String[] exp = new String[]{""};
    assertGeneralDiffOK(a, b, exp);
  }

  // Identical ----------------------------------------------------------------

  @Test
  public final void testGeneral_Identical1() {
    String a = "x";
    String b = "x";
    String[] exp = new String[]{"x"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Identical2() {
    String a = "xx";
    String b = "xx";
    String[] exp = new String[]{"xx"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Identical3() {
    String a = "xyz";
    String b = "xyz";
    String[] exp = new String[]{"xyz"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Identical10() {
    String a = "abcdefghij";
    String b = "abcdefghij";
    String[] exp = new String[]{"abcdefghij"};
    assertGeneralDiffOK(a, b, exp);
  }

  // Inserts and deletes ------------------------------------------------------

  @Test
  public final void testGeneral_Insert1() {
    String a = "";
    String b = "x";
    String[] exp = new String[]{"+x"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Delete1() {
    String a = "y";
    String b = "";
    String[] exp = new String[]{"-y"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Insert2() {
    String a = "";
    String b = "xx";
    String[] exp = new String[]{"+x+x"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Delete2() {
    String a = "yy";
    String b = "";
    String[] exp = new String[]{"-y-y"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Insert3() {
    String a = "x";
    String b = "xx";
    String[] exp = new String[]{"x+x", "+xx"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Delete3() {
    String a = "yy";
    String b = "y";
    String[] exp = new String[]{"y-y", "-yy"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Insert4() {
    String a = "xx";
    String b = "xxx";
    String[] exp = new String[]{"+xxx", "x+xx", "xx+x"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Delete4() {
    String a = "yyy";
    String b = "yy";
    String[] exp = new String[]{"-yyy", "y-yy", "yy-y"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Insert5() {
    String a = "      ";
    String b = "   x   ";
    String[] exp = new String[]{"   +x   "};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Delete5() {
    String a = "   y   ";
    String b = "      ";
    String[] exp = new String[]{"   -y   "};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Insert6() {
    String a = "test";
    String b = "testing";
    String[] exp = new String[]{"test+i+n+g"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Delete6() {
    String a = "testing";
    String b = "test";
    String[] exp = new String[]{"test-i-n-g"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Insert7() {
    String a = "taste";
    String b = "foretaste";
    String[] exp = new String[]{"+f+o+r+etaste"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Delete7() {
    String a = "foretaste";
    String b = "taste";
    String[] exp = new String[]{"-f-o-r-etaste"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Insert8() {
    String a = "bobb";
    String b = "baobab";
    String[] exp = new String[]{"b+aob+ab"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Delete8() {
    String a = "baobab";
    String b = "bobb";
    String[] exp = new String[]{"b-aob-ab"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Insert9() {
    String a = "libb";
    String b = "alibaba";
    String[] exp = new String[]{"+alib+ab+a"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Delete9() {
    String a = "alibaba";
    String b = "libb";
    String[] exp = new String[]{"-alib-ab-a"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Insert10() {
    String a = "ink";
    String b = "links";
    String[] exp = new String[]{"+link+s"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Delete10() {
    String a = "links";
    String b = "ink";
    String[] exp = new String[]{"-link-s"};
    assertGeneralDiffOK(a, b, exp);
  }


  @Test
  public final void testGeneral_Insert11() {
    String a = "ink";
    String b = "inks";
    String[] exp = new String[]{"ink+s"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Delete11() {
    String a = "inks";
    String b = "ink";
    String[] exp = new String[]{"ink-s"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Insert12() {
    String a = "ink";
    String b = "link";
    String[] exp = new String[]{"+link"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Delete12() {
    String a = "link";
    String b = "ink";
    String[] exp = new String[]{"-link"};
    assertGeneralDiffOK(a, b, exp);
  }
  // Replacements -------------------------------------------------------------

  @Test
  public final void testGeneral_Replace1() {
    String a = "y";
    String b = "x";
    String[] exp = new String[]{"+x-y", "-y+x"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Replace2() {
    String a = "yy";
    String b = "xx";
    String[] exp = new String[]{
        "+x+x-y-y",
        "+x-y+x-y",
        "+x-y-y+x",
        "-y+x-y+x",
        "-y-y+x+x",
        "-y+x+x-y"
    };
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Replace3() {
    String a = "xbx";
    String b = "xax";
    String[] exp = new String[]{"x+a-bx", "x-b+ax"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Replace4() {
    String a = "bxb";
    String b = "axa";
    String[] exp = new String[]{
        "+a-bx+a-b",
        "+a-bx-b+a",
        "-b+ax+a-b",
        "-b+ax-b+a",
    };
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Replace5() {
    String a = "bxbx";
    String b = "axax";
    String[] exp = new String[]{
        "+a-bx+a-bx",
        "+a-bx-b+ax",
        "-b+ax+a-bx",
        "-b+ax-b+ax",
    };
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Replace6() {
    String a = "xbxb";
    String b = "xaxa";
    String[] exp = new String[]{
        "x+a-bx+a-b",
        "x+a-bx-b+a",
        "x-b+ax-b+a",
        "x-b+ax+a-b"
    };
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Replace7() {
    String a = "bxax";
    String b = "axbx";
    String[] exp = new String[]{
        "+a+xbx-a-x",
        "+a+xb-x-ax",
        "+a-bx+b-ax",
        "-b-xax+b+x",
        "-b+ax-a+bx",
        "-b-xa+x+bx",
    };
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Replace8() {
    String a = "bxbx";
    String b = "axax";
    String[] exp = new String[]{
        "+a-bx+a-bx",
        "+a-bx-b+ax",
        "-b+ax+a-bx",
        "-b+ax-b+ax"
    };
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Replace9() {
    String a = "bxbxb";
    String b = "axaxa";
    String[] exp = new String[]{
        "-b+ax-b+ax-b+a",
        "-b+ax-b+ax+a-b",
        "-b+ax+a-bx+a-b",
        "+a-bx+a-bx-b+a",
        "+a-bx+a-bx+a-b",
    };
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Replace10() {
    String a = "bxaxb";
    String b = "axbxa";
    String[] exp = new String[]{
        "+a+xbxa-x-b",
        "-b-xaxb+x+a"
    };
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Replace11() {
    String a = "xbxbx";
    String b = "xaxax";
    String[] exp = new String[]{
        "x+a-bx+a-bx",
        "x+a-bx-b+ax",
        "x-b+ax+a-bx",
        "x-b+ax-b+ax"
    };
    assertGeneralDiffOK(a, b, exp);
  }

  // More complex cases -------------------------------------------------------

  @Test
  public final void testGeneral_Complex1() {
    String a = "bab";
    String b = "aba";
    String[] exp = new String[]{
        "+aba-b",
        "-bab+a"
    };
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Complex2() {
    String a = "baba";
    String b = "abab";
    String[] exp = new String[]{
        "+abab-a",
        "-baba+b"
    };
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Complex3() {
    String a = "babab";
    String b = "ababa";
    String[] exp = new String[]{
        "+ababa-b",
        "-babab+a",
    };
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Complex4() {
    String a = "two little";
    String b = "one little";
    String[] exp = new String[]{
        "-t-wo+n+e little"
    };
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Complex5() {
    String a = "too little";
    String b = "one little";
    String[] exp = new String[]{
        "-t-oo+n+e little",
        "-to+n+e-o little",
        "-to+n-o+e little",
        "-to-o+n+e little"
    };
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Complex6() {
    String a = "bilabial";
    String b = "balaclava";
    String[] exp = new String[]{
        "b+a+l+a+c-ila+v-b-ia-l",
        "b-i+ala+c-b-i+la+v-l+a",
        "b+a-ila+c+l-b-ia+v+a-l",
        "b-i+ala-b-i-a+cl+a+v+a",
        "b-i-la-b-i+la+cl+a+v+a",
        "b+a-ila-b+c+l-ia+v+a-l",
        "b+a-ila+c+l+a+v-b-ia-l"
    };
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Complex7() {
    String a = "Sunday";
    String b = "Saturday";
    String[] exp = new String[]{
        "S+a+tu+r-nday",
        "S+a+tu-n+rday"
    };
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Complex9() {
    String a = "Monday Sunday";
    String b = "Monday Tuesday Sunday";
    String[] exp = new String[]{
        "Monday +T+u+e+s+d+a+y+ Sunday",
        "Monday+ +T+u+e+s+d+a+y Sunday",
        "Mon+d+a+y+ +T+u+e+sday Sunday"
    };
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Complex10() {
    String a = "Monday Tuesday Sunday";
    String b = "Monday Sunday";
    String[] exp = new String[]{
        "Monday -T-u-e-s-d-a-y- Sunday",
        "Monday- -T-u-e-s-d-a-y Sunday",
        "Mon-d-a-y- -T-u-e-sday Sunday"
    };
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Complex11() {
    String a = "A train";
    String b = "A car";
    String[] exp = new String[]{
        "A +c+a-tr-a-i-n",
        "A +c-t-ra+r-i-n",
        "A -t+c-ra-i+r-n",
        "A -t-r+ca-i-n+r",
        "A -t-r+ca-i+r-n"
    };
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Complex12() {
    String a = "A train";
    String b = "The car";
    String[] exp = new String[]{
        "+T+h-A+e -t-r+ca-i-n+r",
        "+T+h+e-A -t+c-ra-i+r-n",
        "+T+h+e-A +c+a-tr-a-i-n",
        "+T+h+e-A +c-t-ra+r-i-n",
        "-A+T+h+e -t-r+ca-i-n+r",
        "-A+T+h+e -t-r+ca-i+r-n"
    };
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Complex13() {
    String a = "A blue train";
    String b = "The red car";
    String[] exp = new String[]{
        "+T+h+e-A +r-b-l-ue+d +c+a-tr-a-i-n",
        "+T+h+e-A -b-l+r-ue+d -t+c-ra-i+r-n",
        "+T+h+e-A +r-b-l-ue+d +c-t-ra+r-i-n",
        "+T-A- -b-l-u+he -tr+e+d+ +ca-i-n+r",
        "+T+h-A- -b-l-ue -tr+e+d+ +ca+r-i-n",
        "+T+h-A+e -b-l-u+re+d +c-t+ar-a-i-n",
        "-A- -b-l-u+T+he -tr+e+d+ +ca-i-n+r",
        "-A- -b-l-u+T+he -tr+e+d+ +ca-i+r-n"
    };
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Complex14() {
    String a = "A big train";
    String b = "The little car";
    String[] exp = new String[]{
        "+T+h+e-A +l-bi+t+t+l+e-g +c+a-tr-a-i-n",
        "+T+h-A+e -b+li-g- t+t+l+e+ -r+ca-i-n+r",
        "+T+h+e-A +l-bi-g- t+t+l+e+ +c-ra+r-i-n",
        "-A+T+h+e -b+li-g- +tt-r+l+e+ +ca-i-n+r",
        "-A+T+h+e -b+li-g- t-r+t+l+e+ +ca-i-n+r",
        "-A+T+h+e -b+li-g- t-r+t+l+e+ +ca-i+r-n"
    };
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Complex15() {
    String a = "A big blue train";
    String b = "The little red car";
    String[] exp = new String[]{
        "+T+h+e-A +l-bi+t+t-g- -bl-ue -tr+e+d+ +ca+r-i-n",
        "+T+h+e-A +l-bi+t+t-g- -bl-ue -tr+e+d+ +ca-i-n+r",
        "+T+h-A+e -b+li+t-g- -b+tl-ue -tr+e+d+ +ca-i-n+r",
        "+T+h+e-A +l-bi-g- +t+t-bl-ue -tr+e+d+ +ca-i+r-n",
        "-A+T+h+e -b+li-g- -b+t+tl-ue -tr+e+d+ +ca-i-n+r",
        "-A+T+h+e -b+li-g- -b+t+tl-ue -tr+e+d+ +ca-i+r-n"
    };
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_complex16() {
    String a = "abamx";
    String b = "xabx";
    assertGeneralDiffOK(a, b);
  }

  @Test
  public final void testGeneral_complex17() {
    String a = "abcdefghijkjmnopqrx";
    String b = "xabcdefghijkmnopqrx";
    assertGeneralDiffOK(a, b);
  }

  @Test
  public final void testGeneral_complex18() {
    String a = "rhrwpdpunwx";
    String b = "rhxrwpdunx";
    assertGeneralDiffOK(a, b);
  }

  @Test
  public final void testGeneral_complex19() {
    String a = "bddcdlohjt";
    String b = "tbdcllohjt";
    assertGeneralDiffOK(a, b);
  }

  @Test
  public final void testGeneral_complex20() {
    String a = "bddcdl";
    String b = "tbdcl";
    assertGeneralDiffOK(a, b);
  }

  // helpers
  // --------------------------------------------------------------------------

  public final void assertGeneralDiffOK(String testA, String textB) {
    assertGeneralDiffOK(testA, textB, new String[]{});
  }

  public final void assertGeneralDiffOK(String textA, String textB, String[] exp) {
    DiffAlgorithm<Token> algorithm = getDiffAlgorithm();
    assertGeneralDiffOK(textA, textB, algorithm, exp);
  }

  public static void assertGeneralDiffOK(String textA, String textB, DiffAlgorithm<Token> algorithm, String[] exp) {
    List<GeneralToken> seqA = GeneralToken.toList(textA);
    List<GeneralToken> seqB = GeneralToken.toList(textB);
    ActionsBuffer<Token> af = new ActionsBuffer<>();
    TestHandler cf = new TestHandler();

    // Run the diff
    algorithm.diff(seqA, seqB, new MuxHandler<>(cf, af));

    // Extract output and actions
    String got = cf.getOutput();
    List<Action<Token>> actions = af.getActions();

    // Check
    try {
      DiffAssertions.assertIsApplicable(seqA, seqB, actions);
      DiffAssertions.assertIsCorrect(seqA, seqB, actions);
      if (exp.length > 0) {
        DiffAssertions.assertMatchTestOutput(actions, exp);
      }
    } catch (AssertionError ex) {
      printGeneralErrorDetails(textA, textB, exp, got, actions);
      throw ex;
    }
  }

  /**
   * Print the error details.
   */
  private static void printGeneralErrorDetails(String textA, String textB, String[] exp, String got, List<Action<Token>> actions) {
    System.err.println("+------------------------------------------------");
    System.err.println("| Input A: \"" + textA + "\"");
    System.err.println("| Input B: \"" + textB + "\"");
    System.err.println("| Output:  \"" + got + "\"");
    if (exp.length > 0) {
      System.err.print("| Expect:  ");
      for (String s : exp) System.err.print("\"" + s + "\" ");
      System.err.println();
    }
    System.err.print("| Actions: ");
    for (Action<Token> action : actions) {
      System.err.print(action.operator() == Operator.DEL ? '-' : action.operator() == Operator.INS ? '+' : '=');
      System.err.print(action.tokens().stream().map(Object::toString).collect(Collectors.toList()));
    }
    System.err.println();
  }

}
