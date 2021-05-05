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
import org.pageseeder.diffx.test.RandomStringFactory;
import org.pageseeder.diffx.test.TestHandler;
import org.pageseeder.diffx.token.Token;
import org.pageseeder.diffx.token.TokenType;
import org.pageseeder.xmlwriter.XMLWriter;

import javax.xml.stream.XMLStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
public abstract class BasicGeneralDiffTest extends AlgorithmTest {

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
    String a = "x";
    String b = "";
    String[] exp = new String[]{"+x"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Delete1() {
    String a = "";
    String b = "y";
    String[] exp = new String[]{"-y"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Insert2() {
    String a = "xx";
    String b = "";
    String[] exp = new String[]{"+x+x"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Delete2() {
    String a = "";
    String b = "yy";
    String[] exp = new String[]{"-y-y"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Insert3() {
    String a = "xx";
    String b = "x";
    String[] exp = new String[]{"x+x", "+xx"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Delete3() {
    String a = "y";
    String b = "yy";
    String[] exp = new String[]{"y-y", "-yy"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Insert4() {
    String a = "xxx";
    String b = "xx";
    String[] exp = new String[]{"+xxx", "x+xx", "xx+x"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Delete4() {
    String a = "yy";
    String b = "yyy";
    String[] exp = new String[]{"-yyy", "y-yy", "yy-y"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Insert5() {
    String a = "   x   ";
    String b = "      ";
    String[] exp = new String[]{"   +x   "};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Delete5() {
    String a = "      ";
    String b = "   y   ";
    String[] exp = new String[]{"   -y   "};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Insert6() {
    String a = "testing";
    String b = "test";
    String[] exp = new String[]{"test+i+n+g"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Delete6() {
    String a = "test";
    String b = "testing";
    String[] exp = new String[]{"test-i-n-g"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Insert7() {
    String a = "foretaste";
    String b = "taste";
    String[] exp = new String[]{"+f+o+r+etaste"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Delete7() {
    String a = "taste";
    String b = "foretaste";
    String[] exp = new String[]{"-f-o-r-etaste"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Insert8() {
    String a = "baobab";
    String b = "bobb";
    String[] exp = new String[]{"b+aob+ab"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Delete8() {
    String a = "bobb";
    String b = "baobab";
    String[] exp = new String[]{"b-aob-ab"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Insert9() {
    String a = "alibaba";
    String b = "libb";
    String[] exp = new String[]{"+alib+ab+a"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Delete9() {
    String a = "libb";
    String b = "alibaba";
    String[] exp = new String[]{"-alib-ab-a"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Insert10() {
    String a = "links";
    String b = "ink";
    String[] exp = new String[]{"+link+s"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Delete10() {
    String a = "ink";
    String b = "links";
    String[] exp = new String[]{"-link-s"};
    assertGeneralDiffOK(a, b, exp);
  }


  @Test
  public final void testGeneral_Insert11() {
    String a = "inks";
    String b = "ink";
    String[] exp = new String[]{"ink+s"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Delete11() {
    String a = "ink";
    String b = "inks";
    String[] exp = new String[]{"ink-s"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Insert12() {
    String a = "link";
    String b = "ink";
    String[] exp = new String[]{"+link"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Delete12() {
    String a = "ink";
    String b = "link";
    String[] exp = new String[]{"-link"};
    assertGeneralDiffOK(a, b, exp);
  }
  // Replacements -------------------------------------------------------------

  @Test
  public final void testGeneral_Replace1() {
    String a = "x";
    String b = "y";
    String[] exp = new String[]{"+x-y", "-y+x"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Replace2() {
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
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Replace3() {
    String a = "xax";
    String b = "xbx";
    String[] exp = new String[]{"x+a-bx", "x-b+ax"};
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Replace4() {
    String a = "axa";
    String b = "bxb";
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
    String a = "axax";
    String b = "bxbx";
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
    String a = "xaxa";
    String b = "xbxb";
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
    String a = "axbx";
    String b = "bxax";
    String[] exp = new String[]{
        "+a+xbx-a-x",
        "+a+xb-x-ax",
        "-b-xax+b+x",
        "-b+ax-a+bx"
    };
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Replace8() {
    String a = "axax";
    String b = "bxbx";
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
    String a = "axaxa";
    String b = "bxbxb";
    String[] exp = new String[]{
        "-b+ax-b+ax-b+a",
        "+a-bx+a-bx-b+a",
        "+a-bx+a-bx+a-b"
    };
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Replace10() {
    String a = "axbxa";
    String b = "bxaxb";
    String[] exp = new String[]{
        "+a+xbxa-x-b",
        "-b-xaxb+x+a"
    };
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Replace11() {
    String a = "xaxax";
    String b = "xbxbx";
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
    String a = "aba";
    String b = "bab";
    String[] exp = new String[]{
        "+aba-b",
        "-bab+a"
    };
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Complex2() {
    String a = "abab";
    String b = "baba";
    String[] exp = new String[]{
        "+abab-a",
        "-baba+b"
    };
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Complex3() {
    String a = "ababa";
    String b = "babab";
    String[] exp = new String[]{
        "+ababa-b",
        "-babab+a",
    };
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Complex4() {
    String a = "one little";
    String b = "two little";
    String[] exp = new String[]{
        "-t-wo+n+e little"
    };
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Complex5() {
    String a = "one little";
    String b = "too little";
    String[] exp = new String[]{
        "-t-oo+n+e little",
        "-to+n+e-o little",
        "-to+n-o+e little"
    };
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Complex6() {
    String a = "balaclava";
    String b = "bilabial";
    String[] exp = new String[]{
        "b+a+l+a+c-ila+v-b-ia-l",
        "b-i+ala+c-b-i+la+v-l+a",
        "b+a-ila+c+l-b-ia+v+a-l"
    };
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Complex7() {
    String a = "Saturday";
    String b = "Sunday";
    String[] exp = new String[]{
        "S+a+tu+r-nday",
        "S+a+tu-n+rday"
    };
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Complex9() {
    String a = "Monday Tuesday Sunday";
    String b = "Monday Sunday";
    String[] exp = new String[]{
        "Monday +T+u+e+s+d+a+y+ Sunday",
        "Monday+ +T+u+e+s+d+a+y Sunday",
        "Mon+d+a+y+ +T+u+e+sday Sunday"
    };
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Complex10() {
    String a = "Monday Sunday";
    String b = "Monday Tuesday Sunday";
    String[] exp = new String[]{
        "Monday -T-u-e-s-d-a-y- Sunday",
        "Monday- -T-u-e-s-d-a-y Sunday",
        "Mon-d-a-y- -T-u-e-sday Sunday"
    };
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Complex11() {
    String a = "A car";
    String b = "A train";
    String[] exp = new String[]{
        "A +c+a-tr-a-i-n",
        "A -t-r+ca-i-n+r",
        "A +c-t-ra+r-i-n"
    };
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Complex12() {
    String a = "The car";
    String b = "A train";
    String[] exp = new String[]{
        "+T+h-A+e -t-r+ca-i-n+r",
        "+T+h+e-A +c+a-tr-a-i-n",
        "+T+h+e-A +c-t-ra+r-i-n",
    };
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Complex13() {
    String a = "The red car";
    String b = "A blue train";
    String[] exp = new String[]{
        "+T+h+e-A +r-b-l-ue+d +c+a-tr-a-i-n",
        "+T-A- -b-l-u+he -tr+e+d+ +ca-i-n+r",
        "+T+h-A- -b-l-ue -tr+e+d+ +ca+r-i-n"
    };
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Complex14() {
    String a = "The little car";
    String b = "A big train";
    String[] exp = new String[]{
        "+T+h+e-A +l-bi+t+t+l+e-g +c+a-tr-a-i-n",
        "+T+h-A+e -b+li-g- t+t+l+e+ -r+ca-i-n+r",
        "+T+h+e-A +l-bi-g- t+t+l+e+ +c-ra+r-i-n"
    };
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_Complex15() {
    String a = "The little red car";
    String b = "A big blue train";
    String[] exp = new String[]{
        "+T+h+e-A +l-bi+t+t-g- -bl-ue -tr+e+d+ +ca+r-i-n",
        "+T+h+e-A +l-bi+t+t-g- -bl-ue -tr+e+d+ +ca-i-n+r",
        "+T+h-A+e -b+li+t-g- -b+tl-ue -tr+e+d+ +ca-i-n+r"
    };
    assertGeneralDiffOK(a, b, exp);
  }

  @Test
  public final void testGeneral_complex16() {
    String a = "xabx";
    String b = "abamx";
    assertGeneralDiffOK(a, b);
  }

  @Test
  public final void testGeneral_complex17() {
    String a = "xabcdefghijkmnopqrx";
    String b = "abcdefghijkjmnopqrx";
    assertGeneralDiffOK(a, b);
  }

  @Test
  public final void testGeneral_complex18() {
    String a = "rhxrwpdunx";
    String b = "rhrwpdpunwx";
    assertGeneralDiffOK(a, b);
  }

  @Test
  public final void testGeneral_complex19() {
    String a = "tbdcllohjt";
    String b = "bddcdlohjt";
    assertGeneralDiffOK(a, b);
  }

  @Test
  public final void testGeneral_complex20() {
    String a = "tbdcl";
    String b = "bddcdl";
    assertGeneralDiffOK(a, b);
  }


  // Random variations -------------------------------------------------------

  @Test
  public final void testGeneral_RandomVariations1() {
    RandomStringFactory factory = new RandomStringFactory();
    for (int n = 3; n < 20; n++) {
      for (int i = 0; i < 100; i++) {
        String a = factory.getRandomString(10, false);
        String b = factory.vary(a, .1);
        assertGeneralDiffOK(a, b);
      }
    }
  }

  @Test
  public final void testGeneral_RandomVariations2() {
    RandomStringFactory factory = new RandomStringFactory();
    for (int n = 3; n < 20; n++) {
      for (int i = 0; i < 100; i++) {
        String a = factory.getRandomString(100, false);
        String b = factory.vary(a, .1);
        assertGeneralDiffOK(a, b);
      }
    }
  }

  @Test
  public final void testGeneral_RandomVariations3() {
    RandomStringFactory factory = new RandomStringFactory();
    for (int n = 3; n < 20; n++) {
      for (int i = 0; i < 100; i++) {
        String a = factory.getRandomString(100, false);
        String b = factory.vary(a, .2);
        assertGeneralDiffOK(a, b);
      }
    }
  }

  // helpers
  // --------------------------------------------------------------------------

  public final void assertGeneralDiffOK(String text1, String text2) {
    assertGeneralDiffOK(text1, text2, new String[]{});
  }

  public final void assertGeneralDiffOK(String text1, String text2, String[] exp) {
    List<GeneralToken> seq1 = GeneralToken.toList(text1);
    List<GeneralToken> seq2 = GeneralToken.toList(text2);
    DiffAlgorithm algorithm = getDiffAlgorithm();
    ActionsBuffer af = new ActionsBuffer();
    TestHandler cf = new TestHandler();

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
      System.err.print(action.operator() == Operator.DEL ? '-' : action.operator() == Operator.INS ? '+' : '=');
      System.err.print(action.tokens().stream().map(Object::toString).collect(Collectors.toList()));
    }
    System.err.println();
  }

}
