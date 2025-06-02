package org.pageseeder.diffx.similarity;

import org.junit.jupiter.api.Assertions;

import java.util.Arrays;
import java.util.stream.Stream;

public class StreamSimilarityTest {

  public static void assertScore(float expected, String a, String b, StreamSimilarity<String> similarity) {
    Stream<String> sa = Arrays.stream(a.split("\\W"));
    Stream<String> sb = Arrays.stream(b.split("\\W"));
    float score = similarity.score(sa, sb);
    Assertions.assertEquals(expected, score, .0001);
  }

  public static void assertScoreChars(float expected, String a, String b, StreamSimilarity<Character> similarity) {
    Stream<Character> sa = a.chars().mapToObj(c -> (char) c);
    Stream<Character> sb = b.chars().mapToObj(c -> (char) c);
    float score = similarity.score(sa, sb);
    Assertions.assertEquals(expected, score, .0001);
  }

}
