/*
 * This file is part of the DiffX library.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package com.topologi.diffx.xml.esc;

import java.io.IOException;
import java.io.Writer;

/**
 * An interface to escape XML character data onto a writer.
 *
 * <p>This interface mimics the {@link XMLEscape} interface but is designed to
 * be more efficient for writers by wrapping a {@link Writer} and writing
 * directly onto it.
 *
 * <p>This class is mostly concerned about producing well formed XML and
 * not does attempt to produce valid data.
 *
 * @see <a href="http://www.w3.org/TR/xml/">Extensible Markup Language (XML) 1.0</a>
 *
 * @author  Christophe Lauret
 * @version 0.7.7
 */
public interface XMLEscapeWriter {

  /**
   * Writes a well-formed attribute value.
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
   * @throws IOException If thrown by the underlying writer.
   */
  void writeAttValue(char[] ch, int off, int len) throws IOException;

  /**
   * Writes a well-formed attribute value.
   *
   * <p>Method provided for convenience, using the same specifications as
   * {@link #writeAttValue(char[], int, int)}.
   *
   * @param value The value that needs to be attribute-escaped.
   *
   * @throws IOException If thrown by the underlying writer.
   */
  void writeAttValue(String value) throws IOException;

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
   * @throws IOException If thrown by the underlying writer.
   */
  void writeText(char[] ch, int off, int len) throws IOException;

  /**
   * Writes the text string so that the text value for the element remains
   * well-formed.
   *
   * <p>Method provided for convenience, using the same specifications as
   * {@link #writeText(char[], int, int)}.
   *
   * <p>This method should do nothing if the given value is <code>null</code>.
   *
   * @param text The text that needs to be text-escaped.
   *
   * @throws IOException If thrown by the underlying writer.
   */
  void writeText(String text) throws IOException;

  /**
   * Writes the character so that the text value for the element remains
   * well-formed.
   *
   * <p>Some implementations may unable to deal with java characters outside
   * the Basic Multilingual Plane (BMP). As a result, java characters which
   * correspond to UTF-16 surrogate pairs (#xD800 - 0xDFFF) in  may be not be
   * handled appropriately.
   *
   * <p>Unicode Transformation Format (UTF) implementation should copy the
   * java character verbatim.
   *
   * @param c The character that needs to be text-escaped.
   *
   * @throws IOException If thrown by the underlying writer.
   */
  void writeText(char c) throws IOException;

  /**
   * Returns the encoding used by the implementing class.
   *
   * @return The encoding used by the implementing class.
   */
  String getEncoding();

}
