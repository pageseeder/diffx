package org.pageseeder.diffx.algorithm;

import org.junit.jupiter.api.Test;
import org.pageseeder.diffx.sequence.EventSequence;
import org.pageseeder.diffx.token.impl.CharToken;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MatrixProcessorTest {

  @Test
  public void testBothEmpty() {
    EventSequence s1 = new EventSequence();
    EventSequence s2 = new EventSequence();
    Matrix matrix = new MatrixProcessor().process(s1, s2);
    assertEquals(1, matrix.size());
    assertEquals(0, matrix.getLCSLength());
  }

  @Test
  public void testFirstEmpty() {
    EventSequence s1 = new EventSequence();
    EventSequence s2 = asSequenceOfCharTokens("y");
    Matrix matrix = new MatrixProcessor().process(s1, s2);
    assertEquals(2, matrix.size()); // 1x2
    assertEquals(0, matrix.getLCSLength());
  }

  @Test
  public void testSecondEmpty() {
    EventSequence s1 = asSequenceOfCharTokens("x");
    EventSequence s2 = new EventSequence();
    Matrix matrix = new MatrixProcessor().process(s1, s2);
    matrix.get(0, 0);
    assertEquals(2, matrix.size()); // 2x1
    assertEquals(0, matrix.getLCSLength());
  }

  @Test
  public void testOneByOne() {
    EventSequence s1 = asSequenceOfCharTokens("x");
    EventSequence s2 = asSequenceOfCharTokens("x");
    Matrix matrix = new MatrixProcessor().process(s1, s2);
    matrix.get(0, 0);
    assertEquals(4, matrix.size()); // 2x2
    assertEquals(1, matrix.getLCSLength()); // "xyz"
  }

  @Test
  public void testTwoByTwo() {
    EventSequence s1 = asSequenceOfCharTokens("xy");
    EventSequence s2 = asSequenceOfCharTokens("xy");
    Matrix matrix = new MatrixProcessor().process(s1, s2);
    matrix.get(0, 0);
    assertEquals(9, matrix.size()); // 3x3
    assertEquals(2, matrix.getLCSLength()); // <x> </x>
  }

  @Test
  public void testIdentical() {
    EventSequence s1 = asSequenceOfCharTokens("xyz");
    EventSequence s2 = asSequenceOfCharTokens("xyz");
    Matrix matrix = new MatrixProcessor().process(s1, s2);
    matrix.get(0, 0);
    assertEquals(16, matrix.size()); // 4x4
    assertEquals(3, matrix.getLCSLength()); // <x> "xyz" </x>
  }

  @Test
  public void testDifference1() {
    EventSequence s1 = asSequenceOfCharTokens("xyz");
    EventSequence s2 = asSequenceOfCharTokens("xuz");
    Matrix matrix = new MatrixProcessor().process(s1, s2);
    assertEquals(16, matrix.size()); // 4x4
    assertEquals(2, matrix.getLCSLength()); // 'x' 'z'
  }

  @Test
  public void testDifference2() {
    EventSequence s1 = asSequenceOfCharTokens("xyz");
    EventSequence s2 = asSequenceOfCharTokens("xuv");
    Matrix matrix = new MatrixProcessor().process(s1, s2);
    assertEquals(16, matrix.size()); // 4x4
    assertEquals(1, matrix.getLCSLength()); // 'x'
  }

  @Test
  public void testNoCommon() {
    EventSequence s1 = asSequenceOfCharTokens("abc");
    EventSequence s2 = asSequenceOfCharTokens("xyz");
    Matrix matrix = new MatrixProcessor().process(s1, s2);
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
    EventSequence s1 = asSequenceOfCharTokens("GCCCTAGCG");
    EventSequence s2 = asSequenceOfCharTokens("GCGCAATG");
    Matrix matrix = new MatrixProcessor().process(s1, s2);
    assertEquals(90, matrix.size()); // 10x9
    assertEquals(5, matrix.getLCSLength()); // "GCGAG"
  }

  @Test
  public void testExample2() {
    EventSequence s1 = asSequenceOfCharTokens("acbdeacbed");
    EventSequence s2 = asSequenceOfCharTokens("debabb");
    Matrix matrix = new MatrixProcessor().process(s1, s2);
    assertEquals(77, matrix.size()); // 11x7
    assertEquals(4, matrix.getLCSLength()); // "deab"
  }

  private static EventSequence asSequenceOfCharTokens(String string) {
    EventSequence s = new EventSequence();
    for (char c : string.toCharArray()) {
      s.addToken(new CharToken(c));
    }
    return s;
  }

}
