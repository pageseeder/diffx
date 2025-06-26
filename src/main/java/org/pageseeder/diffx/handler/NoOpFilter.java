package org.pageseeder.diffx.handler;

import org.jetbrains.annotations.NotNull;
import org.pageseeder.diffx.api.DiffHandler;
import org.pageseeder.diffx.api.Operator;

/**
 * A no-operation implementation of a {@link DiffFilter}, which transparently forwards all
 * operations to the target {@link DiffHandler} without applying changes or processing.
 *
 * @param <T> The type of token being handled.
 *
 * @since 1.2.1
 * @version 1.2.1
 */
public class NoOpFilter<T> extends DiffFilter<T> {

  /**
   * Creates a new instance of {@code NoOpFilter}.
   *
   * @param target The target {@link DiffHandler} to which all operations will be forwarded.
   *               Must not be null.
   */
  public NoOpFilter(@NotNull DiffHandler<T> target) {
    super(target);
  }

  /**
   * Forwards the specified operation and associated token to the target {@link DiffHandler}.
   *
   * @param operator The operator indicating the type of operation (e.g., insertion, deletion, match).
   *                 Must not be null.
   * @param token    The token to be handled in conjunction with the specified operator.
   *                 Must not be null.
   *
   * @throws IllegalStateException If the handler is in an invalid state to process this method.
   */
  @Override
  public void handle(@NotNull Operator operator, @NotNull T token) {
    this.target.handle(operator, token);
  }

}
