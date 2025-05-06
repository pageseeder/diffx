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
package org.pageseeder.diffx.config;

/**
 * An immutable configuration object for the diff.
 *
 * <p>This class acts as a container for a set of properties that can be applied to the
 * main components:
 * <ul>
 *   <li>{@link org.pageseeder.diffx.load.XMLLoader} implementations,</li>
 *   <li>{@link org.pageseeder.diffx.core.DiffProcessor} implementations,<li>
 *   <li>and the {@link org.pageseeder.diffx.format.XMLDiffOutput} implementations.<li>
 * </ul>
 *
 * <p>In order to produce the correct results, the configuration must be applied
 * throughout the three steps of processing.
 *
 * @author Christophe Lauret
 * @version 1.0.1
 * @since 0.9.0
 */
public final class DiffConfig {

  private final boolean isNamespaceAware;

  private final WhiteSpaceProcessing whitespace;

  private final TextGranularity granularity;

  private final boolean allowDoctypeDeclaration;

  public DiffConfig(WhiteSpaceProcessing whitespace, TextGranularity granularity) {
    this(true, whitespace, granularity, false);
  }

  public DiffConfig(boolean isNamespaceAware, WhiteSpaceProcessing whitespace, TextGranularity granularity) {
    this(isNamespaceAware, whitespace, granularity, false);
  }

  private DiffConfig(boolean isNamespaceAware, WhiteSpaceProcessing whitespace, TextGranularity granularity, boolean allowDoctypeDeclaration) {
    this.isNamespaceAware = isNamespaceAware;
    this.whitespace = whitespace;
    this.granularity = granularity;
    this.allowDoctypeDeclaration = allowDoctypeDeclaration;
  }

  /**
   * Indicates whether the namespaces should be handled or ignored when processing XML.
   */
  public boolean isNamespaceAware() {
    return this.isNamespaceAware;
  }

  /**
   * Indicates whether Diffx allows doctype declarations.
   *
   * <p>Note: Allowing doctype declaration potentially exposes to XML External Entity (XXE) attacks.
   */
  public boolean allowDoctypeDeclaration() {
    return this.allowDoctypeDeclaration;
  }

  /**
   * @return The text granularity for this configuration.
   */
  public TextGranularity granularity() {
    return this.granularity;
  }

  /**
   * @return The whitespace processing for this configuration.
   */
  public WhiteSpaceProcessing whitespace() {
    return whitespace;
  }

  /**
   * Create a default config that is namespace aware, preserves whitespaces and
   * report differences within text at word level (including spaces, but excluding punctuation)
   */
  public static DiffConfig getDefault() {
    return new DiffConfig(true, WhiteSpaceProcessing.COMPARE, TextGranularity.SPACE_WORD);
  }

  /**
   * Create a default config that is namespace aware, preserves whitespaces and
   * report differences within text at word level (including spaces, but excluding punctuation)
   */
  public static DiffConfig legacyDefault() {
    return new DiffConfig(true, WhiteSpaceProcessing.COMPARE, TextGranularity.WORD);
  }

  /**
   * Create a new config with the specified granularity.
   *
   * @return a new instance
   */
  public DiffConfig granularity(TextGranularity granularity) {
    return new DiffConfig(this.isNamespaceAware, this.whitespace, granularity, this.allowDoctypeDeclaration);
  }

  /**
   * Create a new config with the specified whitespace processing.
   *
   * @return a new instance
   */
  public DiffConfig whitespace(WhiteSpaceProcessing whitespace) {
    return new DiffConfig(this.isNamespaceAware, whitespace, this.granularity, this.allowDoctypeDeclaration);
  }

  /**
   * Create a new config that is not namespace aware.
   *
   * @return a new instance
   */
  public DiffConfig noNamespaces() {
    return new DiffConfig(false, whitespace, this.granularity, this.allowDoctypeDeclaration);
  }

  /**
   * Create a new config to allow or disallow doctype declarations
   *
   * @return a new instance
   */
  public DiffConfig allowDoctypeDeclaration(boolean allow) {
    return new DiffConfig(this.isNamespaceAware, this.whitespace, this.granularity, allow);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DiffConfig that = (DiffConfig) o;
    if (this.isNamespaceAware != that.isNamespaceAware) return false;
    if (this.whitespace != that.whitespace) return false;
    return this.granularity == that.granularity;
  }

  @Override
  public int hashCode() {
    int result = (this.isNamespaceAware ? 1 : 0);
    result = 31 * result + this.whitespace.hashCode();
    result = 31 * result + this.granularity.hashCode();
    return result;
  }
}
