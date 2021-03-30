package org.pageseeder.diffx.algorithm;

import junit.framework.TestCase;
import org.junit.Test;
import org.pageseeder.diffx.event.impl.CharEvent;
import org.pageseeder.diffx.sequence.EventSequence;

public class MatrixBuilderTest extends TestCase {

  @Test
  public void testBothEmpty() {
    EventSequence s1 = new EventSequence();
    EventSequence s2 = new EventSequence();
    Matrix matrix = new MatrixBuilder().build(s1, s2);
    assertEquals(1, matrix.size());
    assertEquals(0, matrix.getLCSLength());
  }

  @Test
  public void testFirstEmpty() {
    EventSequence s1 = new EventSequence();
    EventSequence s2 = asSequenceOfCharEvents("y");
    Matrix matrix = new MatrixBuilder().build(s1, s2);
    assertEquals(2, matrix.size()); // 1x2
    assertEquals(0, matrix.getLCSLength());
  }

  @Test
  public void testSecondEmpty() {
    EventSequence s1 = asSequenceOfCharEvents("x");
    EventSequence s2 = new EventSequence();
    Matrix matrix = new MatrixBuilder().build(s1, s2);
    matrix.get(0,0);
    assertEquals(2, matrix.size()); // 2x1
    assertEquals(0, matrix.getLCSLength());
  }

  @Test
  public void testOneByOne() {
    EventSequence s1 = asSequenceOfCharEvents("x");
    EventSequence s2 = asSequenceOfCharEvents("x");
    Matrix matrix = new MatrixBuilder().build(s1, s2);
    matrix.get(0,0);
    assertEquals(4, matrix.size()); // 2x2
    assertEquals(1, matrix.getLCSLength()); // "xyz"
  }

  @Test
  public void testTwoByTwo() {
    EventSequence s1 = asSequenceOfCharEvents("xy");
    EventSequence s2 = asSequenceOfCharEvents("xy");
    Matrix matrix = new MatrixBuilder().build(s1, s2);
    matrix.get(0,0);
    assertEquals(9, matrix.size()); // 3x3
    assertEquals(2, matrix.getLCSLength()); // <x> </x>
  }

  @Test
  public void testIdentical() {
    EventSequence s1 = asSequenceOfCharEvents("xyz");
    EventSequence s2 = asSequenceOfCharEvents("xyz");
    Matrix matrix = new MatrixBuilder().build(s1, s2);
    matrix.get(0,0);
    assertEquals(16, matrix.size()); // 4x4
    assertEquals(3, matrix.getLCSLength()); // <x> "xyz" </x>
  }

  @Test
  public void testDifference1() {
    EventSequence s1 = asSequenceOfCharEvents("xyz");
    EventSequence s2 = asSequenceOfCharEvents("xuz");
    Matrix matrix = new MatrixBuilder().build(s1, s2);
    assertEquals(16, matrix.size()); // 4x4
    assertEquals(2, matrix.getLCSLength()); // 'x' 'z'
  }

  @Test
  public void testDifference2() {
    EventSequence s1 = asSequenceOfCharEvents("xyz");
    EventSequence s2 = asSequenceOfCharEvents("xuv");
    Matrix matrix = new MatrixBuilder().build(s1, s2);
    assertEquals(16, matrix.size()); // 4x4
    assertEquals(1, matrix.getLCSLength()); // 'x'
  }


  @Test
  public void testNoCommon() {
    EventSequence s1 = asSequenceOfCharEvents("abc");
    EventSequence s2 = asSequenceOfCharEvents("xyz");
    Matrix matrix = new MatrixBuilder().build(s1, s2);
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
    EventSequence s1 = asSequenceOfCharEvents("GCCCTAGCG");
    EventSequence s2 = asSequenceOfCharEvents("GCGCAATG");
    Matrix matrix = new MatrixBuilder().build(s1, s2);
    assertEquals(90, matrix.size()); // 10x9
    assertEquals(5, matrix.getLCSLength()); // "GCGAG"
  }


  private static EventSequence asSequenceOfCharEvents(String string) {
    EventSequence s = new EventSequence();
    for (char c : string.toCharArray()) {
      s.addEvent(new CharEvent(c));
    }
    return s;
  }

}
