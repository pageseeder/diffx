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
import org.pageseeder.diffx.sequence.Sequence;
import org.pageseeder.diffx.token.Token;
import org.pageseeder.diffx.token.impl.CharToken;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MatrixProcessorTest {

  private static Sequence asSequenceOfCharTokens(String string) {
    Sequence s = new Sequence();
    for (char c : string.toCharArray()) {
      s.addToken(new CharToken(c));
    }
    return s;
  }

  @Test
  public void testBothEmpty() {
    Sequence s1 = new Sequence();
    Sequence s2 = new Sequence();
    Matrix matrix = new MatrixProcessor<Token>().process(s1, s2);
    assertEquals(1, matrix.size());
    assertEquals(0, matrix.getLCSLength());
  }

  @Test
  public void testFirstEmpty() {
    Sequence s1 = new Sequence();
    Sequence s2 = asSequenceOfCharTokens("y");
    Matrix matrix = new MatrixProcessor<Token>().process(s1, s2);
    assertEquals(2, matrix.size()); // 1x2
    assertEquals(0, matrix.getLCSLength());
  }

  @Test
  public void testSecondEmpty() {
    Sequence s1 = asSequenceOfCharTokens("x");
    Sequence s2 = new Sequence();
    Matrix matrix = new MatrixProcessor<Token>().process(s1, s2);
    matrix.get(0, 0);
    assertEquals(2, matrix.size()); // 2x1
    assertEquals(0, matrix.getLCSLength());
  }

  @Test
  public void testOneByOne() {
    Sequence s1 = asSequenceOfCharTokens("x");
    Sequence s2 = asSequenceOfCharTokens("x");
    Matrix matrix = new MatrixProcessor<Token>().process(s1, s2);
    matrix.get(0, 0);
    assertEquals(4, matrix.size()); // 2x2
    assertEquals(1, matrix.getLCSLength()); // "xyz"
  }

  @Test
  public void testTwoByTwo() {
    Sequence s1 = asSequenceOfCharTokens("xy");
    Sequence s2 = asSequenceOfCharTokens("xy");
    Matrix matrix = new MatrixProcessor<Token>().process(s1, s2);
    matrix.get(0, 0);
    assertEquals(9, matrix.size()); // 3x3
    assertEquals(2, matrix.getLCSLength()); // <x> </x>
  }

  @Test
  public void testIdentical() {
    Sequence s1 = asSequenceOfCharTokens("xyz");
    Sequence s2 = asSequenceOfCharTokens("xyz");
    Matrix matrix = new MatrixProcessor<Token>().process(s1, s2);
    matrix.get(0, 0);
    assertEquals(16, matrix.size()); // 4x4
    assertEquals(3, matrix.getLCSLength()); // <x> "xyz" </x>
  }

  @Test
  public void testDifference1() {
    Sequence s1 = asSequenceOfCharTokens("xyz");
    Sequence s2 = asSequenceOfCharTokens("xuz");
    Matrix matrix = new MatrixProcessor<Token>().process(s1, s2);
    assertEquals(16, matrix.size()); // 4x4
    assertEquals(2, matrix.getLCSLength()); // 'x' 'z'
  }

  @Test
  public void testDifference2() {
    Sequence s1 = asSequenceOfCharTokens("xyz");
    Sequence s2 = asSequenceOfCharTokens("xuv");
    Matrix matrix = new MatrixProcessor<Token>().process(s1, s2);
    assertEquals(16, matrix.size()); // 4x4
    assertEquals(1, matrix.getLCSLength()); // 'x'
  }

  @Test
  public void testNoCommon() {
    Sequence s1 = asSequenceOfCharTokens("abc");
    Sequence s2 = asSequenceOfCharTokens("xyz");
    Matrix matrix = new MatrixProcessor<Token>().process(s1, s2);
    // Only zeros!
    for (int i = 0; i < matrix.lengthX(); i++) {
      for (int j = 0; j < matrix.lengthY(); j++) {
        assertEquals(0, matrix.get(i, j));
      }
    }
    assertEquals(16, matrix.size()); // 4x4
    assertEquals(0, matrix.getLCSLength());
  }

  @Test
  public void testExample1() {
    Sequence s1 = asSequenceOfCharTokens("GCCCTAGCG");
    Sequence s2 = asSequenceOfCharTokens("GCGCAATG");
    Matrix matrix = new MatrixProcessor<Token>().process(s1, s2);
    assertEquals(90, matrix.size()); // 10x9
    assertEquals(5, matrix.getLCSLength()); // "GCGAG"
  }

  @Test
  public void testExample2() {
    Sequence s1 = asSequenceOfCharTokens("acbdeacbed");
    Sequence s2 = asSequenceOfCharTokens("debabb");
    Matrix matrix = new MatrixProcessor<Token>().process(s1, s2);
    assertEquals(77, matrix.size()); // 11x7
    assertEquals(4, matrix.getLCSLength()); // "deab"
  }

}
