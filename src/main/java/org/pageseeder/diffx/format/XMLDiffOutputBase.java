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

import org.pageseeder.diffx.api.Operator;
import org.pageseeder.diffx.xml.Namespace;
import org.pageseeder.diffx.xml.NamespaceSet;

import java.util.EnumMap;
import java.util.Objects;

/**
 * A base class for XML diff output implementations.
 *
 * @author Christophe Lauret
 *
 * @version 1.3.0
 * @since 0.9.0
 */
abstract class XMLDiffOutputBase implements XMLDiffOutput {

  /**
   * The namespace URI reserved for the diff.
   */
  private static final String DIFF_NS_URI = "https://www.pageseeder.org/diffx";

  /**
   * The namespace URI used for elements that may have been modified.
   */
  private static final String LEGACY_DIFF_NS_URI = "http://www.topologi.com/2005/Diff-X";

  private static final EnumMap<Operator, Namespace> DEFAULT = new EnumMap<>(Operator.class);

  static {
    DEFAULT.put(Operator.MATCH, new Namespace(DIFF_NS_URI, "diff"));
    DEFAULT.put(Operator.INS, new Namespace(DIFF_NS_URI + "/insert", "ins"));
    DEFAULT.put(Operator.DEL, new Namespace(DIFF_NS_URI + "/delete", "del"));
  }

  private static final EnumMap<Operator, Namespace> LEGACY = new EnumMap<>(Operator.class);

  static {
    LEGACY.put(Operator.MATCH, new Namespace(LEGACY_DIFF_NS_URI, "dfx"));
    LEGACY.put(Operator.INS, new Namespace(LEGACY_DIFF_NS_URI + "/Insert", "ins"));
    LEGACY.put(Operator.DEL, new Namespace(LEGACY_DIFF_NS_URI + "/Delete", "del"));
  }

  protected NamespaceSet namespaces = NamespaceSet.noNamespace();

  /**
   * {@code true} (default) to include the XML namespace declaration when the {@link #start()} method is called.
   */
  protected boolean includeXMLDeclaration = false;

  protected boolean useLegacyNamespaces = false;

  @Override
  public final void setWriteXMLDeclaration(boolean show) {
    this.includeXMLDeclaration = show;
  }

  @Override
  public final void setNamespaces(NamespaceSet namespaces) {
    this.namespaces = Objects.requireNonNull(namespaces);
  }

  public Namespace getDiffNamespace() {
    return this.useLegacyNamespaces ? LEGACY.get(Operator.MATCH) : DEFAULT.get(Operator.MATCH);
  }

  public Namespace getDiffNamespace(Operator operator) {
    return this.useLegacyNamespaces ? LEGACY.get(operator) : DEFAULT.get(operator);
  }

}
