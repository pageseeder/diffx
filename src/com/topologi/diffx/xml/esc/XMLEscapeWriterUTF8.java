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
 * A utility class for escaping XML data using the UTF-8 encoding.
 *
 * <p>Only characters which must be escaped are escaped since the Unicode
 * Transformation Format should support all Unicode code points.
 *
 * <p>Escape methods in this class will escape non-BMP character for better
 * compatibility with storage mechanism which do not support them, for example
 * some databases.
 *
 * @author  Christophe Lauret
 * @version 0.7.8
 */
public final class XMLEscapeWriterUTF8 extends XMLEscapeWriterBase implements XMLEscapeWriter {

  /**
   * The encoding used for this instance.
   */
  private static final String UTF8 = "utf-8";

  // TODO Allow any UTF

  /**
   * Creates a new XML escape writer using the utf-8 encoding.
   *
   * @param writer The writer to wrap.
   *
   * @throws NullPointerException if the writer is <code>null</code>.
   */
  public XMLEscapeWriterUTF8(Writer writer) throws NullPointerException {
    super(writer, UTF8);
  }

  @Override
  public void writeAttValue(char[] ch, int off, int len) throws IOException {
    char c;
    for (int i = off; i < off+len; i++) {
      c = ch[i];
      // '<' always replace with '&lt;'
      if      (c == '<') super.w.write("&lt;");
      // '&' always replace with '&amp;'
      else if (c == '&') super.w.write("&amp;");
      // '&' always replace with '&quot;'
      else if (c == '"') super.w.write("&quot;");
      // '\'' always replace with '&#39;'
      else if (c == '\'') super.w.write("&#39;");
      // preserve white space in C0 control characters
      else if (c == '\n' || c == '\r' || c == '\t') super.w.write(c);
      // ignore C0 and C1 control characters
      else if (c < 0x20 || c >= 0x7F && c < 0xA0) doNothing();
      // handle surrogate pairs (for characters outside BMP)
      else if (c >= 0xD800 && c <= 0xDFFF) {
        int codePoint = Character.codePointAt(ch, i, len);
        i += Character.charCount(codePoint) - 1;
        super.w.write("&#x");
        super.w.write(Integer.toHexString(codePoint));
        super.w.write(";");
      }
      // copy the rest verbatim
      else super.w.write(c);
    }
  }

  @Override
  public void writeText(char[] ch, int off, int len) throws IOException {
    // process the rest
    char c = ' ';
    for (int i = off; i < off+len; i++) {
      c = ch[i];
      // '<' always replace with '&lt;'
      if (c == '<') super.w.write("&lt;");
      // '>' replace with '&gt;' if following ']'
      else if (c == '>' && i > 0 && ch[i-1] == ']') super.w.write("&gt;");
      // '&' always replace with '&amp;'
      else if (c == '&') super.w.write("&amp;");
      // preserve white space in C0 control characters
      else if (c == '\n' || c == '\r' || c == '\t') super.w.write(c);
      // ignore C0 and C1 control characters
      else if (c < 0x20 || c >= 0x7F && c < 0xA0) doNothing();
      // handle surrogate pairs (for characters outside BMP)
      else if (c >= 0xD800 && c <= 0xDFFF) {
        int codePoint = Character.codePointAt(ch, i, len);
        i += Character.charCount(codePoint) - 1;
        super.w.write("&#x");
        super.w.write(Integer.toHexString(codePoint));
        super.w.write(";");
      }
      // copy the rest verbatim
      else super.w.write(c);
    }
  }

  @Override
  public void writeText(char c) throws IOException {
    // '<' must always be escaped
    if (c == '<') super.w.write("&lt;");
    // '>' always escaped just in case it appears after ']]'
    else if (c == '>') super.w.write("&gt;");
    // '&' must always be escaped
    else if (c == '&') super.w.write("&amp;");
    // white space characters within C0 range
    else if (c == '\n' || c == '\r' || c == '\t') super.w.write(c);
    // ignore C0 and C1 control characters
    else if (c < 0x20 || c >= 0x7F && c < 0xA0) doNothing();
    // Everything else write verbatim
    else super.w.write(c);
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
