package org.pageseeder.diffx.similarity;

import org.junit.jupiter.api.Test;

class EditSimilarityTest {

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
    assertScore((float) 1 / 2, "A B", "A C");
    assertScore((float) 2 / 3, "A B C", "A D C");
  }

  @Test
  void testScore_Swap() {
    assertScore((float) 1 / 2, "A B", "B A");
    assertScore((float) 1 / 3, "A B C", "C B A");
  }

  @Test
  void testScore_Insert() {
    assertScore((float) 2 / 3, "A", "A B");
    assertScore((float) 1 / 2, "A", "A B C");
    assertScore((float) 4 / 5, "A C", "A B C");
    assertScore((float) 1 / 2, "B", "A B C");
  }

  @Test
  void testScore_Delete() {
    assertScore((float) 2 / 3, "A B", "A");
    assertScore((float) 1 / 2, "A B C", "A");
    assertScore((float) 4 / 5, "A B C", "A C");
    assertScore((float) 1 / 2, "A B C", "B");
  }

  private void assertScore(float expected, String a, String b) {
    StreamSimilarityTest.assertScore(expected, a, b, new EditSimilarity<>());
  }
}
