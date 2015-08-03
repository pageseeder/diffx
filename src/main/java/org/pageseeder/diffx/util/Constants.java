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
package org.pageseeder.diffx.util;

import javax.xml.XMLConstants;

/**
 * The set of constants used in this API.
 *
 * @author Christophe Lauret
 * @version 17 May 2005
 */
public final class Constants {

  /**
   * Prevent creation of instances
   */
  private Constants() {
  }

  // Diff-x specific constants
  // ----------------------------------------------------------------------------------------------

  /**
   * The namespace URI used for elements that may have been modified.
   */
  public static final String BASE_NS_URI = "http://www.topologi.com/2005/Diff-X";

  /**
   * The namespace URI used for elements that may have been deleted.
   */
  public static final String DELETE_NS_URI = BASE_NS_URI + "/Delete";

  /**
   * The namespace URI used for elements that may have been inserted.
   */
  public static final String INSERT_NS_URI = BASE_NS_URI + "/Insert";

  // XML constants (deprecated use Java 5 XMLConstants instead)
  // ----------------------------------------------------------------------------------------------

  /**
   * The default URI (empty).
   *
   * Same as {@link XMLConstants#NULL_NS_URI}.
   *
   * @deprecated Use {@link XMLConstants#NULL_NS_URI} instead.
   */
  @Deprecated public static final String DEFAULT_URI = XMLConstants.NULL_NS_URI;

  /**
   * The official XML namespace prefix.
   *
   * Same as {@link XMLConstants#XML_NS_PREFIX}.
   *
   * @deprecated Use {@link XMLConstants#XML_NS_PREFIX} instead.
   */
  @Deprecated public static final String XML_NS_PREFIX = XMLConstants.XML_NS_PREFIX;

  /**
   * The official XML Namespace name URI.
   *
   * Same as {@link XMLConstants#XML_NS_URI}.
   *
   * @deprecated Use {@link XMLConstants#XML_NS_URI} instead.
   */
  @Deprecated public static final String XML_NS_URI = XMLConstants.XML_NS_URI;

  /**
   * The official XML attribute used for specifying XML namespace declarations, XMLConstants.XMLNS_ATTRIBUTE, namespace URI.
   *
   * Defined by the XML specification to be "http://www.w3.org/2000/xmlns/".
   *
   * @deprecated Use {@link XMLConstants#XMLNS_ATTRIBUTE_NS_URI} instead.
   */
  @Deprecated public static final String XMLNS_ATTRIBUTE_NS_URI = XMLConstants.XMLNS_ATTRIBUTE_NS_URI;

  /**
   * The official XML attribute used for specifying XML Namespace declarations.
   *
   * It is NOT valid to use as a prefix. Defined by the XML specification to be "xmlns".
   *
   * @deprecated Use {@link XMLConstants#XMLNS_ATTRIBUTE} instead.
   */
  @Deprecated public static final String XMLNS_ATTRIBUTE = XMLConstants.XMLNS_ATTRIBUTE;

}
