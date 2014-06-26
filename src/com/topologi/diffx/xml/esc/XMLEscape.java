/*
 * This file is part of the DiffX library.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package com.topologi.diffx.xml.esc;

/**
 * An interface to escape XML character data.
 *
 * <p>This interface assumes that the values to be escapes do not orignate from
 * XML text, in order words, there should not be already any entity or markup
 * in the document. If it is the case the methods in this class should also
 * escapes them. Thus "&amp;amp;" would be represented as "&amp;amp;amp;".
 *
 * <p>Also the method will not try to escape characters that cannot be escaped.
 *
 * <p>This class is mostly concerned about producing well formed XML and
 * not does attempt to produce valid data.
 *
 * @see <a href="http://www.w3.org/TR/xml/">Extensible Markup Language (XML) 1.0</a>
 *
 * @author  Christophe Lauret
 * @version 0.7.7
 */
public interface XMLEscape {

  /**
   * Returns a well-formed attribute value.
   *
   * <p>This method must replace any character in the specified value by the
   * corresponding numeric character reference or the predefined XML general
   * entities, if the character is not allowed or not in the encoding range.
   *
   * <p>Attribute values must not contain any ampersand (#x26) or less than
   * (#x3C) characters. This method will replace them by the corresponding
   * named entity.
   *
   * <p>Quotes and apostrophes must also be escaped depending on what was used
   * in the attribute markup. Since this method is not aware of which type of
   * quotes was used, both are escaped. Double quotes (#x22) are escaped using
   * a named character entity. In case the end result is HTML 4, single quotes
   * (#x27) are escaped using a numeric character entity.
   *
   * <p>Characters in ranges (#x00-#x1F) and (#x80-#x9F) are silently ignored
   * except for line feed (#x0A), carriage return (#x0D) and tab (#x09).
   *
   * @see <a href="http://www.w3.org/TR/xml/#NT-AttValue">Extensible Markup
   * Language (XML) 1.0 - 2.3 Common Syntactic Constructs</a>
   *
   * @param ch  The value that needs to be attribute-escaped.
   * @param off The start (offset) of the characters.
   * @param len The length of characters to.
   *
   * @return A well-formed value for the attribute.
   */
  String toAttributeValue(char[] ch, int off, int len);

  /**
   * Returns a well-formed attribute value.
   *
   * <p>Method provided for convenience, using the same specifications as
   * {@link #toAttributeValue(char[], int, int)}.
   *
   * <p>This method should return <code>null</code> if the given
   * value is <code>null</code>.
   *
   * @param value The value that needs to be attribute-escaped.
   *
   * @return A well-formed value for the attribute.
   */
  String toAttributeValue(String value);

  /**
   * Writes a well-formed XML literal text value.
   *
   * <p>This method must replace any character in the specified text by the
   * corresponding numeric character reference or the predefined XML general
   * entities, if the character is not allowed or not in the encoding range.
   *
   * <p>Literal text values must not contain any 'ampersand' (#x26) or 'less
   * than' (#x3C) characters. This method will replace them by the
   * corresponding named entity.
   *
   * <p>Out of precaution this method may also encode the 'greater than'
   * (#xCE) character, in case it follows "]]".
   *
   * <p>Characters in ranges (#x00-#x1F) and (#x80-#x9F) are silently ignored
   * except for line feed (#x0A), carriage return (#x0D) and tab (#x09).
   *
   * @see <a href="http://www.w3.org/TR/xml/#syntax">Extensible Markup
   * Language (XML) 1.0 - 2.4 Character Data and Markup</a>
   *
   * @param ch  The value that needs to be attribute-escaped.
   * @param off The start (offset) of the characters.
   * @param len The length of characters to.
   *
   * @return A well-formed value for the text node.
   */
  String toElementText(char[] ch, int off, int len);

  /**
   * Returns a well-formed text value for the element.
   *
   * <p>Method provided for convenience, using the same specifications as
   * {@link #toElementText(char[], int, int)}.
   *
   * <p>This method should return <code>null</code> if the given
   * value is <code>null</code>.
   *
   * @param value The value that needs to be text-escaped.
   *
   * @return A well-formed value for the text node.
   */
  String toElementText(String value);

  /**
   * Returns the encoding used by the implementing class.
   *
   * @return The encoding used by the implementing class.
   */
  String getEncoding();

}
