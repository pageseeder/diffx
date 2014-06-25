/*
 * This file is part of the DiffX library.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package com.topologi.diffx.xml.esc;

/**
 * A singleton for escaping XML data when using the 'ASCII' encoding.
 *
 * <p>Any character that isn't part of the ASCI range is going to be replaced
 * by a character entity.
 *
 * @author  Christophe Lauret
 * @version 0.7.7
 */
public final class XMLEscapeASCII extends XMLEscapeBase implements XMLEscape {

  /**
   * A static instance of the UTF8 escape class.
   */
  public static final XMLEscape ASCII_ESCAPE = new XMLEscapeASCII();

  /**
   * The encoding used for this instance.
   */
  private static final String ENCODING = "ASCII";

  /**
   * Prevent creation of instances
   */
  private XMLEscapeASCII() {
    super(ENCODING);
  }

  @Override
  public String toAttributeValue(char[] ch, int off, int len) {
    // process the rest
    StringBuffer out = new StringBuffer(len + len / 10);
    for (int i = off; i < off+len; i++) {
      // 0x00 to 0x1F
      if (ch[i] < 0x20) {
        // tabs, new lines and line feeds: preserve
        if (ch[i] == 0x09 || ch[i] == 0x0A || ch[i] == 0x0D) {
          out.append(ch[i]);
        } else {
          doNothing();
          // 0x20 to 0x7F
        }
      } else if (ch[i] < 0x7F) {
        switch (ch[i]) {
          case '&' :
            out.append("&amp;");
            break;
          case '<' :
            out.append("&lt;");
            break;
          case '"' :
            out.append("&quot;");
            break;
          case '\'' :
            out.append("&#x27;");
            break;
            // output by default
          default:
            out.append(ch[i]);
        }
      }
      // control characters (C1): prune
      else if (ch[i] < 0xA0) {
        doNothing();
      }
      // handle surrogate pairs (for characters outside BMP)
      else if (ch[i] >= 0xD800 && ch[i] <= 0xDFFF) {
        int codePoint = Character.codePointAt(ch, i, len);
        i += Character.charCount(codePoint) - 1;
        out.append("&#x").append(Integer.toHexString(codePoint)).append(';');
      }
      // all other characters: use numerical character entity
      else {
        out.append("&#x").append(Integer.toHexString(ch[i])).append(';');
      }
    }
    return out.toString();
  }

  @Override
  public String toElementText(char[] ch, int off, int len) {
    // process the rest
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
      // handle surrogate pairs (for characters outside BMP)
      else if (c >= 0xD800 && c <= 0xDFFF) {
        int codePoint = Character.codePointAt(ch, i, len);
        i += Character.charCount(codePoint) - 1;
        out.append("&#x").append(Integer.toHexString(codePoint)).append(';');
      }
      // characters outside the ASCII range
      else if (c > 0x9F) {
        out.append("&#x").append(Integer.toHexString(ch[i])).append(';');
      }
      // ASCII
      else out.append(c);
    }
    return out.toString();
  }

  /**
   * Does nothing.
   */
  private void doNothing() {
  }

}
