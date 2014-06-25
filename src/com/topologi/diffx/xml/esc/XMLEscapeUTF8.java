/*
 * This file is part of the DiffX library.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package com.topologi.diffx.xml.esc;

/**
 * A utility class for escaping XML data when using the UTF-8 encoding.
 *
 * <p>Only characters which must be escaped are escaped since the Unicode
 * Transformation Format should support all Unicode code points.
 *
 * @author  Christophe Lauret
 * @version 0.7.7
 */
public final class XMLEscapeUTF8 extends XMLEscapeBase implements XMLEscape {

  /**
   * A static instance of the UTF8 escape class.
   */
  public static final XMLEscape UTF8_ESCAPE = new XMLEscapeUTF8();

  /**
   * The encoding used for this instance.
   */
  private static final String ENCODING = "utf-8";

  /**
   * Prevent creation of instances
   */
  private XMLEscapeUTF8() {
    super(ENCODING);
  }

  @Override
  public String toAttributeValue(char[] ch, int off, int len) {
    StringBuilder out = new StringBuilder();
    char c;
    for (int i = off; i < off+len; i++) {
      c = ch[i];
      // '<' always replace with '&lt;'
      if      (c == '<') out.append("&lt;");
      // '&' always replace with '&amp;'
      else if (c == '&') out.append("&amp;");
      // '&' always replace with '&quot;'
      else if (c == '"') out.append("&quot;");
      // '\'' always replace with '&#39;'
      else if (c == '\'') out.append("&#39;");
      // preserve white space in C0 control characters
      else if (c == '\n' || c == '\r' || c == '\t') out.append(c);
      // ignore C0 and C1 control characters
      else if (c < 0x20 || c >= 0x7F && c < 0xA0) doNothing();
      // copy the rest verbatim
      else out.append(c);
    }
    return out.toString();
  }

  @Override
  public String toElementText(char[] ch, int off, int len) {
    StringBuffer out = new StringBuffer(len + len / 10);
    char c;
    for (int i = off; i < off+len; i++) {
      c = ch[i];
      // '<' always replace with '&lt;'
      if      (c == '<') out.append("&lt;");
      // '&' always replace with '&amp;'
      else if (c == '&') out.append("&amp;");
      // '\'' always replace with '&#39;'
      else if (c == '>' && i > 0 && ch[i-1] == ']' ) out.append("&gt;");
      // preserve white space in C0 control characters
      else if (c == '\n' || c == '\r' || c == '\t') out.append(c);
      // ignore C0 and C1 control characters
      else if (c < 0x20 || c >= 0x7F && c < 0xA0) doNothing();
      // copy the rest verbatim
      else out.append(c);
    }
    return out.toString();
  }

  /**
   * Does nothing.
   *
   * <p>This method exists so that we can explicitly say that we should do nothing
   * in certain conditions.
   */
  private static void doNothing() {
  }

}
