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
package org.pageseeder.diffx.format;

import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.config.DiffXConfig;
import org.pageseeder.diffx.sequence.PrefixMapping;
import org.pageseeder.diffx.token.XMLToken;

import java.io.OutputStream;
import java.io.Writer;

/**
 * A simple XML diff output that writes strictly what it is given.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
@Deprecated
public final class StrictXMLFormatter implements XMLDiffXFormatter {

  private final StrictXMLDiffOutput output;

  private boolean isDocumentStart = true;

  /**
   * Creates a new formatter on the standard output.
   */
  public StrictXMLFormatter() {
    this.output = new StrictXMLDiffOutput(System.out);
  }

  /**
   * Creates a new formatter using the specified writer.
   *
   * @param w The writer to use.
   */
  public StrictXMLFormatter(Writer w) {
    this.output = new StrictXMLDiffOutput(w);
  }

  /**
   * Creates a new formatter using the specified writer.
   *
   * @param out The output stream to use.
   */
  public StrictXMLFormatter(OutputStream out) {
    this.output = new StrictXMLDiffOutput(out);
  }

  @Override
  public void format(XMLToken token) {
    if (this.isDocumentStart) {
      this.output.start();
      this.isDocumentStart = false;
    }
    this.output.handle(Operator.MATCH, token);
  }

  @Override
  public void insert(XMLToken token) {
    if (this.isDocumentStart) {
      this.output.start();
      this.isDocumentStart = false;
    }
    this.output.handle(Operator.INS, token);
  }

  @Override
  public void delete(XMLToken token) throws IllegalStateException {
    if (this.isDocumentStart) {
      this.output.start();
      this.isDocumentStart = false;
    }
    this.output.handle(Operator.DEL, token);
  }

  @Override
  public void setConfig(DiffXConfig config) {
  }

  @Override
  public void setWriteXMLDeclaration(boolean show) {
    this.output.setWriteXMLDeclaration(show);
  }

  @Override
  public void declarePrefixMapping(PrefixMapping mapping) {
    this.output.setNamespaces(mapping.getNamespaces());
  }

}
