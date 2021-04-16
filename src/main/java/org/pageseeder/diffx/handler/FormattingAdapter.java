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

import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.event.Token;
import org.pageseeder.diffx.format.DiffXFormatter;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * A handler wrapping a formatter so that the formatter can be used in the new API.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public class FormattingAdapter implements DiffHandler {

  /**
   * The target formatter protected to allow any subclasses to access it.
   */
  protected final DiffXFormatter formatter;

  public FormattingAdapter(DiffXFormatter formatter) {
    this.formatter = formatter;
  }

  /**
   * Invoke the formatter's method corresponding to the operator.
   *
   * @param operator The operator
   * @param token    The token to handle
   *
   * @throws UncheckedIOException Wraps any IO exception thrown by the formatter
   */
  @Override
  public void handle(Operator operator, Token token) throws UncheckedIOException {
    try {
      switch (operator) {
        case MATCH:
          this.formatter.format(token);
          break;
        case INS:
          this.formatter.insert(token);
          break;
        case DEL:
          this.formatter.delete(token);
          break;
        default:
          // Ignore and do nothing
      }
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  @Override
  public String toString() {
    return "FormattingAdapter("+this.formatter+")";
  }

}
