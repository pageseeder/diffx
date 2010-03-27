/*
 * This file is part of the DiffX library.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at 
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package com.topologi.diffx.util;

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

  /**
   * The default URI (empty).
   */
  public static final String DEFAULT_URI = "";

  /**
   * The namespace used for elements that may have been modified.
   */
  public static final String BASE_NS_URI = "http://www.topologi.com/2005/Diff-X";

  /**
   * The namespace used for elements that may have been deleted.
   */
  public static final String DELETE_NS_URI = BASE_NS_URI + "/Delete";

  /**
   * The namespace used for elements that may have been inserted.
   */
  public static final String INSERT_NS_URI = BASE_NS_URI + "/Insert";

  /**
   * The official XML Namespace prefix.
   *
   * Defined by the XML specification to be "xml".
   */
  public static final String XML_NS_PREFIX = "xml";
  
  /**
   * The official XML Namespace name URI.
   *
   * Defined by the XML specification to be "http://www.w3.org/XML/1998/namespace".
   */
  public static final String XML_NS_URI = "http://www.w3.org/XML/1998/namespace";

  /**
   * The official XML attribute used for specifying XML Namespace declarations, XMLConstants.XMLNS_ATTRIBUTE, Namespace name URI.
   * 
   * Defined by the XML specification to be "http://www.w3.org/2000/xmlns/".
   */
  public static final String XMLNS_ATTRIBUTE_NS_URI = "http://www.w3.org/2000/xmlns/";

  /**
   * The official XML attribute used for specifying XML Namespace declarations.
   *
   * It is NOT valid to use as a prefix. Defined by the XML specification to be "xmlns".
   */
  public static final String XMLNS_ATTRIBUTE = "xmlns";

}
