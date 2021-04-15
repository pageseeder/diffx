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

import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.handler.CoalescingFilter;
import org.pageseeder.diffx.handler.DiffHandler;

import java.util.List;

/**
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class ProgressiveXMLProcessor implements DiffProcessor {

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
  public void diff(List<? extends DiffXEvent> first, List<? extends DiffXEvent> second, DiffHandler handler) {
    GuanoAlgorithm algorithm = new GuanoAlgorithm();
    DiffHandler actual = handler;
    if (coalesce) actual = new CoalescingFilter(actual);
  //  actual = new CompareReplaceFilter(actual);
    handler.start();
    algorithm.diff(first, second, actual);
    handler.end();
  }

  @Override
  public String toString() {
    return "ProgressiveXMLProcessor{" +
        "coalesce=" + this.coalesce +
        '}';
  }
}
