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
package org.pageseeder.diffx.config;

import org.pageseeder.diffx.load.XMLLoader;

/**
 * The configuration to use with a DiffX operation.
 *
 * <p>This class acts as a container for a set of properties that can be applied to the
 * main components of Diff-X such as the:
 * <ul>
 *   <li>The {@link XMLLoader} implementations,</li>
 *   <li>The {@link org.pageseeder.diffx.algorithm.DiffXAlgorithm} implementations,<li>
 *   <li>and the {@link org.pageseeder.diffx.format.DiffXFormatter} implementations.<li>
 * </ul>
 *
 * <p>In order to produce the correct results, the configuration must be applied
 * throughout the three steps of processing.
 *
 * <p>There is an illegal state in this configuration, if the the diffx is not namespace
 * aware it cannot not report the differences in the prefixes. Therefore it is impossible
 * to set both flags to <code>false</code>.
 *
 * <p>The <code>set</code> methods for those flags will ensure that this situation does
 * not occur. The general rule is that the flag being set takes precedence.
 *
 * <p>Note that it simply mimics SAX2 which cannot have the features
 * <code>http://xml.org/sax/features/namespaces</code> and
 * <code>http://xml.org/sax/features/namespace-prefixes</code> both set to
 * <code>false</code>.
 *
 * @author Christophe Lauret
 * @version 10 May 2010
 * @see XMLLoader
 * @see org.pageseeder.diffx.algorithm.DiffXAlgorithm
 * @see org.pageseeder.diffx.format.DiffXFormatter
 */
public final class DiffXConfig {

  /**
   * Indicates whether the namespaces should be handled or ignored.
   */
  private boolean isNamespaceAware = true;

  /**
   * Indicates whether difference in prefixes should be reported.
   */
  private boolean reportPrefixDifferences = false;

  /**
   * Defines the whitespace for this configuration.
   */
  private WhiteSpaceProcessing whitespace = WhiteSpaceProcessing.COMPARE;

  /**
   * Defines the text granularity for this configuration.
   */
  private TextGranularity granularity = TextGranularity.WORD;

  /**
   * Creates a new configuration for Diff-X.
   */
  public DiffXConfig() {
  }

  /**
   * Creates a new configuration for Diff-X.
   *
   * @param granularity The granularity of text diffing.
   */
  public DiffXConfig(TextGranularity granularity) {
    if (granularity == null)
      throw new NullPointerException("The granularity cannot be configured to be not be null.");
    this.granularity = granularity;
  }

  /**
   * Creates a new configuration for Diff-X.
   *
   * @param whitespace  How whitespace should be processed.
   * @param granularity The granularity of text diffing.
   */
  public DiffXConfig(WhiteSpaceProcessing whitespace, TextGranularity granularity) {
    if (granularity == null)
      throw new NullPointerException("The granularity cannot be configured to be not be null.");
    if (whitespace == null)
      throw new NullPointerException("The whitespace processing cannot be configured to be not be null.");
    this.granularity = granularity;
    this.whitespace = whitespace;
  }

  // methods ----------------------------------------------------------------------------------------

  /**
   * Sets the granularity of text diffing for this configuration.
   *
   * @param granularity the text granularity of text diffing for this configuration.
   */
  public void setGranularity(TextGranularity granularity) {
    if (granularity == null)
      throw new NullPointerException("The granularity cannot be configured to be not be null.");
    this.granularity = granularity;
  }

  /**
   * Sets the white space processing for this configuration.
   *
   * @param whitespace the white space processing for this configuration.
   */
  public void setWhiteSpaceProcessing(WhiteSpaceProcessing whitespace) {
    if (whitespace == null)
      throw new NullPointerException("The whitespace cannot be configured to be not be null.");
    this.whitespace = whitespace;
  }

  /**
   * Sets whether Diff-X should take namespaces into account.
   *
   * <p>It is more efficient to disable namespace processing when the XML to
   * compare are not expected to use any namespace.
   *
   * <p>In order to avoid an illegal state, if this flag is set to <code>false</code>
   * and the differences in prefixes will be automatically reported.
   *
   * @param aware <code>true</code> to preserve the white spaces;
   *              <code>false</code> otherwise.
   */
  public void setNamespaceAware(boolean aware) {
    this.isNamespaceAware = aware;
    if (!aware) {
      this.reportPrefixDifferences = true;
    }
  }

  /**
   * Sets whether the Diff-X should report differences in prefixes.
   *
   * <p>In order to avoid an illegal state, if this flag is set to <code>false</code>
   * and then the processor becomes namespace aware.
   *
   * @param report <code>true</code> to report differences in prefixes;
   *               <code>false</code> to ignore them.
   */
  public void setReportPrefixDifferences(boolean report) {
    this.reportPrefixDifferences = report;
    if (!report) {
      this.isNamespaceAware = true;
    }
  }

  /**
   * Indicates whether the Diff-X takes namespaces into account.
   *
   * @return <code>true</code> to preserve the white spaces;
   * <code>false</code> otherwise.
   */
  public boolean isNamespaceAware() {
    return this.isNamespaceAware;
  }

  /**
   * Returns whether the differences in prefixes are reported.
   *
   * @return <code>true</code> to report differences in prefixes;
   * <code>false</code> to ignore them.
   */
  public boolean isReportPrefixDifferences() {
    return this.reportPrefixDifferences;
  }

  /**
   * Returns the granularity of text diffing for this configuration.
   *
   * @return the text granularity of text diffing for this configuration.
   */
  public TextGranularity getGranularity() {
    return this.granularity;
  }

  /**
   * Returns the granularity of text diffing for this configuration.
   *
   * @return the text granularity of text diffing for this configuration.
   */
  public WhiteSpaceProcessing getWhiteSpaceProcessing() {
    return this.whitespace;
  }

  /**
   * Indicates whether the differences in white spaces should be ignored or not.
   *
   * @return <code>true</code> except if white space processing is set to COMPARE
   * <code>false</code> otherwise.
   */
  public boolean isIgnoreWhiteSpace() {
    return this.whitespace != WhiteSpaceProcessing.COMPARE;
  }

  /**
   * Indicates whether the white spaces are preserved or not.
   *
   * @return <code>true</code> except if white space processing is set to IGNORE
   * <code>false</code> otherwise.
   */
  public boolean isPreserveWhiteSpace() {
    return this.whitespace != WhiteSpaceProcessing.IGNORE;
  }

}
