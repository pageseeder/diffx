package org.pageseeder.diffx.bench;

import org.pageseeder.diffx.profile.Profilers;
import org.pageseeder.diffx.test.TestTokens;
import org.pageseeder.diffx.token.impl.CharToken;

import java.util.List;

public class Datasets {

  public static DiffAlgorithmBench.ListPairSpec<CharToken> getRandomStringPair(int length, double variation) {
    String from = Profilers.getRandomString(length, false);
    String to = Profilers.vary(from, variation);
    List<CharToken> a = TestTokens.toCharTokens(from);
    List<CharToken> b = TestTokens.toCharTokens(to);
    return new DiffAlgorithmBench.ListPairSpec(a.toArray(), b.toArray());
  }

  public static <T> DiffAlgorithmBench.ListPairSpec<T> getPatternStringPair(int length, double variation) {
    String pattern = Profilers.getRandomString(20, false);
    StringBuilder from = new StringBuilder();
    while (from.length() < length) {
      from.append(Profilers.vary(pattern, .05));
    }
    String to = Profilers.vary(from.toString(), variation);
    List<CharToken> a = TestTokens.toCharTokens(from.toString());
    List<CharToken> b = TestTokens.toCharTokens(to);
    return new DiffAlgorithmBench.ListPairSpec(a.toArray(), b.toArray());
  }

}
