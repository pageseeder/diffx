package org.pageseeder.diffx.similarity;

import org.junit.jupiter.api.Test;

class JaccardSimilarityTest {

  @Test
  void testScore_Empty() {
    assertScore(1, "", "");
  }

  @Test
  void testScore_Different() {
    assertScore(0, "A", "X");
    assertScore(0, "A B", "X Y");
    assertScore(0, "A", "X Y");
    assertScore(0, "A B", "X");
  }

  @Test
  void testScore_Identical() {
    assertScore(1, "A", "A");
    assertScore(1, "A B", "A B");
    assertScore(1, "A B C", "A B C");
    assertScore(1, "A B C D", "A B C D");
  }

  @Test
  void testScore_Substitution() {
    assertScore(.33333333f, "A B", "A C");
    assertScore(.5f, "A B C", "A D C");
  }

  @Test
  void testScore_Swap() {
    assertScore(1, "A B", "B A");
    assertScore(1, "A B C", "C B A");
  }

  @Test
  void testScore_Insert() {
    assertScore(.5f, "A", "A B");
    assertScore(.33333333f, "A", "A B C");
  }

  @Test
  void testScore_Delete() {
    assertScore(.5f, "A B", "A");
    assertScore(.33333333f, "A B C", "A");
  }

  private void assertScore(float expected, String a, String b) {
    StreamSimilarityTest.assertScore(expected, a, b, new JaccardSimilarity<>());
  }
}
