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
import org.pageseeder.diffx.token.Token;
import org.pageseeder.diffx.xml.PrefixMapping;

import java.io.IOException;
import java.io.Writer;

/**
 * An XML formatter that tries to rectify the errors affecting the well-formedness of the XML.
 *
 * <p>This class will always close the elements correctly by maintaining a stack of parent
 * elements.
 *
 * <p>Implementation note: this classes uses the namespace prefixes 'dfx' and 'del', in the
 * future it should be possible to configure which prefixes to use for each namespace, but
 * in this version the namespace prefix mapping is hardcoded.
 *
 * <p>A limitation of this output is that it cannot report inserted/deleted attributes
 * with a namespace prefix.</p>
 *
 * @author Christophe Lauret
 * @version 0.9.0
 * @since 0.5.0
 */
@Deprecated
public final class SmartXMLFormatter implements XMLDiffXFormatter {

  private final SmartXMLDiffOutput output;

  private boolean isDocumentStart = true;

  /**
   * Creates a new formatter on the standard output.
   *
   * <p>This constructor is equivalent to:
   * <pre>new SmartXMLFormatter(new PrintWriter(System.out));</pre>.
   *
   * @throws IOException should an I/O exception occurs.
   * @see System#out
   */
  public SmartXMLFormatter() throws IOException {
    this.output = new SmartXMLDiffOutput();
  }

  /**
   * Creates a new formatter using the specified writer.
   *
   * @param w The writer to use.
   */
  public SmartXMLFormatter(Writer w) {
    this.output = new SmartXMLDiffOutput(w);
  }

  @Override
  public void format(Token token) throws IOException {
    this.startIfFirst();
    this.output.handleMatch(token);
    this.output.xml.flush();
  }

  @Override
  public void insert(Token token) throws IOException {
    this.startIfFirst();
    this.output.handleEdit(Operator.INS, token);
    this.output.xml.flush();
  }

  @Override
  public void delete(Token token) throws IOException {
    this.startIfFirst();
    this.output.handleEdit(Operator.DEL, token);
    this.output.xml.flush();
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
    this.output.setPrefixMapping(mapping);
  }

  private void startIfFirst() {
    if (this.isDocumentStart) {
      this.output.start();
      this.isDocumentStart = false;
    }
  }
}
