/*
 * Copyright 2010-2015 Allette Systems (Australia)
 * http://www.allette.com.au
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pageseeder.diffx.handler;

import org.jetbrains.annotations.NotNull;
import org.pageseeder.diffx.api.DiffHandler;
import org.pageseeder.diffx.api.Operator;

import java.util.Arrays;

/**
 * A {@code MuxHandler} acts as a multiplexer for multiple {@link DiffHandler} instances, forwarding
 * diff operations to all the handlers it manages. This facilitates applying multiple handlers to
 * the same diff processing.
 *
 * @param <T> The type of token this handler processes.
 *
 * @author Christophe Lauret
 *
 * @since 1.0
 * @version 1.0
 */
public class MuxHandler<T> implements DiffHandler<T> {

  private final DiffHandler<T>[] handlers;

  @SafeVarargs
  public MuxHandler(DiffHandler<T>... handlers) {
    this.handlers = handlers;
  }

  @Override
  public void start() {
    for (DiffHandler<T> handler : handlers) handler.start();
  }

  @Override
  public void handle(@NotNull Operator operator, @NotNull T token) throws IllegalStateException {
    for (DiffHandler<T> handler : handlers) handler.handle(operator, token);
  }

  @Override
  public void end() {
    for (DiffHandler<T> handler : handlers) handler.end();
  }

  @Override
  public String toString() {
    return "MuxHandler -> " + Arrays.toString(this.handlers);
  }
}
