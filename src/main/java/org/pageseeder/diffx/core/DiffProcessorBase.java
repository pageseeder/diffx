/*
 * Copyright (c) 2010-2021 Allette Systems (Australia)
 *    http://www.allette.com.au
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pageseeder.diffx.core;

import org.pageseeder.diffx.DiffException;
import org.pageseeder.diffx.config.DiffConfig;
import org.pageseeder.diffx.format.XMLDiffOutput;
import org.pageseeder.diffx.api.DiffHandler;
import org.pageseeder.diffx.sequence.Sequence;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.xml.NamespaceSet;

abstract class DiffProcessorBase implements XMLDiffProcessor {

  protected boolean coalesce = false;

  @Override
  public void setCoalesce(boolean coalesce) {
    this.coalesce = coalesce;
  }

  @Override
  public boolean isCoalescing() {
    return this.coalesce;
  }

  @Override
  public void diff(Sequence from, Sequence to, DiffConfig config, DiffHandler<XMLToken> handler)
      throws DiffException {

    // Supply the namespaces to the output
    if (handler instanceof XMLDiffOutput) {
      NamespaceSet namespaces = NamespaceSet.merge(from.getNamespaces(), from.getNamespaces());
      ((XMLDiffOutput) handler).setNamespaces(namespaces);
    }

    this.diff(from.tokens(), to.tokens(), handler);
  }

}
