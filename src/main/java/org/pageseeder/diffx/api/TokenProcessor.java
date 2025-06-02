package org.pageseeder.diffx.api;

import org.jetbrains.annotations.NotNull;
import org.pageseeder.diffx.token.Token;

import java.util.List;

/**
 * Defines a contract for filtering a list of tokens based on specific criteria.
 *
 * @param <T> The type of tokens to be filtered, extending the {@link Token} class.
 * @author Christophe Lauret
 */
@FunctionalInterface
public interface TokenProcessor<T extends Token> {

  /**
   * Filters the given list of tokens based on specific criteria.
   *
   * @param tokens The list of tokens to be filtered. Must not be null.
   * @return A filtered list of tokens that meet the specified criteria.
   * If no tokens meet the criteria, returns an empty list.
   */
  @NotNull List<T> process(@NotNull List<T> tokens);

  /**
   * Returns a composite {@code TokenProcessor} that sequentially processes
   * tokens with this processor and then applies the given {@code after} processor.
   *
   * <p>The resulting processor first applies the {@code process()} method of
   * this instance to the provided tokens, and then passes the result to the
   * {@code process()} method of the {@code after} processor.
   *
   * @param after The {@code TokenProcessor} to apply after this processor. Must not be null.
   * @return A composite {@code TokenProcessor} that applies this processor followed by the given {@code after} processor.
   * @throws NullPointerException If the {@code after} processor is null.
   */
  default TokenProcessor<T> andThen(@NotNull TokenProcessor<T> after) {
    return tokens -> after.process(this.process(tokens));
  }

}
