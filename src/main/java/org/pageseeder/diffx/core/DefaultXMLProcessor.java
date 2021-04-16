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
package org.pageseeder.diffx.core;

import org.pageseeder.diffx.token.Token;
import org.pageseeder.diffx.handler.CoalescingFilter;
import org.pageseeder.diffx.handler.DiffHandler;

import java.util.List;

public class DefaultXMLProcessor implements DiffProcessor {

  private boolean coalesce = false;

  /**
   * Set whether to consecutive text operations should be coalesced into a single operation.
   *
   * @param coalesce <code>true</code> to coalesce; <code>false</code> to leave a separate operations.
   */
  public void setCoalesce(boolean coalesce) {
    this.coalesce = coalesce;
  }

  @Override
  public void diff(List<? extends Token> first, List<? extends Token> second, DiffHandler handler) {
    MatrixXMLAlgorithm algorithm = new MatrixXMLAlgorithm();
    DiffHandler actual = getFilter(handler);
    handler.start();
    algorithm.diff(first, second, actual);
    handler.end();
  }

  private DiffHandler getFilter(DiffHandler handler) {
    return this.coalesce ? new CoalescingFilter(handler) : handler;
  }

  @Override
  public String toString() {
    return "DefaultXMLProcessor{" +
        "coalesce=" + coalesce +
        '}';
  }
}
