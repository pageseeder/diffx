package org.pageseeder.diffx.algorithm;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.pageseeder.diffx.action.ActionsBuffer;
import org.pageseeder.diffx.api.DiffAlgorithm;
import org.pageseeder.diffx.api.Equality;
import org.pageseeder.diffx.config.WhiteSpaceProcessing;
import org.pageseeder.diffx.load.text.TokenizerBySpaceWord;
import org.pageseeder.diffx.token.TextToken;
import org.pageseeder.diffx.token.XMLToken;

import java.util.List;
import java.util.function.Function;

abstract class BasicEqualityAlgorithmTest extends AlgorithmTest<XMLToken> {

  private final Function<Equality<XMLToken>, DiffAlgorithm<XMLToken>> algorithmFunction;

  private static final Equality<XMLToken> CASE_INSENSITIVE = (a, b) -> {
    if (a instanceof TextToken && b instanceof TextToken) {
      return a.getValue().equalsIgnoreCase(b.getValue());
    }
    return a.equals(b);
  };

  BasicEqualityAlgorithmTest(Function<Equality<XMLToken>, DiffAlgorithm<XMLToken>> algorithmFunction) {
    this.algorithmFunction = algorithmFunction;
  }

  @Test
  final void testEquality_0() {
    List<TextToken> a = TokenizerBySpaceWord.tokenize("A Logical step", WhiteSpaceProcessing.PRESERVE);
    List<TextToken> b = TokenizerBySpaceWord.tokenize("A Logical Step", WhiteSpaceProcessing.PRESERVE);

    DiffAlgorithm<XMLToken> algorithm = algorithmFunction.apply(CASE_INSENSITIVE);
    ActionsBuffer<XMLToken> result = new ActionsBuffer<>();
    algorithm.diff(a, b, result);
    System.out.println(result.getActions());
    Assertions.assertEquals(0, result.countEdits());
  }

  @Test
  final void testEquality_1() {
    List<TextToken> a = TokenizerBySpaceWord.tokenize("a logical step", WhiteSpaceProcessing.PRESERVE);
    List<TextToken> b = TokenizerBySpaceWord.tokenize("A Logical Step", WhiteSpaceProcessing.PRESERVE);

    DiffAlgorithm<XMLToken> algorithm = algorithmFunction.apply(CASE_INSENSITIVE);
    ActionsBuffer<XMLToken> result = new ActionsBuffer<>();
    algorithm.diff(a, b, result);
    System.out.println(result.getActions());
    Assertions.assertEquals(0, result.countEdits());
  }

  @Test
  final void testEquality_2() {
    List<TextToken> a = TokenizerBySpaceWord.tokenize("an illogical step", WhiteSpaceProcessing.PRESERVE);
    List<TextToken> b = TokenizerBySpaceWord.tokenize("A Logical Step", WhiteSpaceProcessing.PRESERVE);

    DiffAlgorithm<XMLToken> algorithm = algorithmFunction.apply(CASE_INSENSITIVE);
    ActionsBuffer<XMLToken> result = new ActionsBuffer<>();
    algorithm.diff(a, b, result);
    System.out.println(result.getActions());
    Assertions.assertEquals(4, result.countEdits());
  }

}
