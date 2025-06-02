package org.pageseeder.diffx.xml;

import org.jetbrains.annotations.NotNull;
import org.pageseeder.diffx.api.TokenProcessor;
import org.pageseeder.diffx.token.XMLToken;

public interface SequenceProcessor extends TokenProcessor<XMLToken> {

  /**
   * Processes the specified sequence.
   *
   * @param sequence The sequence to process.
   *
   * @return The sequence as a result of processing the specified sequence.
   */
  default Sequence process(@NotNull Sequence sequence) {
    return new Sequence(process(sequence.tokens()), sequence.getNamespaces());
  }

}
