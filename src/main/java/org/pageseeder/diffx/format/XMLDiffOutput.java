/*
 * Copyright 2010-2021 Allette Systems (Australia)
 *    http://www.allette.com.au
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
package org.pageseeder.diffx.format;

import org.pageseeder.diffx.api.DiffHandler;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.xml.NamespaceSet;

/**
 * An interface for formatting the output of the Diff-X algorithm as XML.
 *
 * <p>This interface defines some additional methods that are specific to XML.
 *
 * @author Christophe Lauret
 * @since 0.9.0
 */
public interface XMLDiffOutput extends DiffHandler<XMLToken> {

  /**
   * Set whether the formatter should include the XML declaration or not.
   *
   * @param show <code>true</code> to get the formatter to write the XML declaration;
   *             <code>false</code> otherwise.
   */
  void setWriteXMLDeclaration(boolean show);

  /**
   * Sets the specified prefix mapping so that namespaced can be declared when needed.
   *
   * <p>This method must be called before calls to the DiffHandler methods</p>
   *
   * @param namespaces The namespaces to use.
   */
  void setNamespaces(NamespaceSet namespaces);

}
