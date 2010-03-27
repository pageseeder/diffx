/*
 * This file is part of the DiffX library.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at 
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package com.topologi.diffx.xml;

import java.io.IOException;
import java.io.Writer;

import com.topologi.diffx.xml.esc.XMLEscapeWriter;
import com.topologi.diffx.xml.esc.XMLEscapeWriterUTF8;

/**
 * A base implementation for XML writers.
 *
 * <p>Provides methods to generate well-formed XML data easily. wrapping a writer.
 *
 * <p>This version only supports utf-8 encoding, if writing to a file make sure that the 
 * encoding of the file output stream is "utf-8".
 *
 * <p>The recommended implementation is to use a <code>BufferedWriter</code> to write.
 *
 * <pre>
 *  Writer writer =
 *     new BufferedWriter(new OutputStreamWriter(new FileOutputStream("foo.out"),"utf-8"));
 * </pre>
 *
 * @author  Christophe Lauret
 * @version 17 May 2005
 */
abstract class XMLWriterBase implements XMLWriter {

  /**
   * Where the XML data goes.
   */
  final Writer writer;

  /**
   * Encoding of the output xml
   */
  String encoding = "utf-8";

  /**
   * Encoding of the output xml
   */
  XMLEscapeWriter writerEscape;

  /**
   * Level of the depth of the xml document currently produced.
   *
   * <p>This attribute changes depending on the state of the instance.
   */
  int depth = 0;

  /**
   * Indicates whether the xml should be indented or not.
   *
   * <p>The default is <code>true</code> (indented).
   *
   * <p>The indentation is 2 white-spaces.
   */
  boolean indent;

  /**
   * The default indentation spaces used.
   */
  private String indentChars = null;

  /**
   * Flag to indicate that the element open tag is not finished yet. 
   */
  boolean isNude = false; 

// constructors -------------------------------------------------------------------------

  /**
   * <p>Creates a new XML writer.
   *
   * @param writer  Where this writer should write the XML data.
   * @param indent  Set the indentation flag.
   * 
   * @throws NullPointerException If the writer is <code>null</code>.
   */
  public XMLWriterBase(Writer writer, boolean indent) throws NullPointerException {
    if (writer == null) 
      throw new NullPointerException("XMLWriter cannot use a null writer.");
    this.writer = writer;
    this.writerEscape = new XMLEscapeWriterUTF8(writer);
    this.indent = indent;
	if (indent) this.indentChars = "  ";
  }

// setup methods ------------------------------------------------------------------------

  /**
   * @see XMLWriter#xmlDecl()
   */
  public final void xmlDecl() throws IOException {
    this.writer.write("<?xml version=\"1.0\" encoding=\""+this.encoding+"\"?>");
    if (this.indent) this.writer.write('\n');
  }

  /**
   * @see XMLWriter#setIndentChars(String)
   */
  public final void setIndentChars(String spaces) throws IllegalStateException, IllegalArgumentException {
    if (this.depth != 0)
      throw new IllegalStateException("To late to set the indentation characters!");
    // check that this is a valid indentation string
    if (spaces != null) {
      for (int i = 0; i < spaces.length(); i++)
        if (!Character.isSpaceChar(spaces.charAt(i)))
          throw new IllegalArgumentException("Not a valid indentation string.");
    }
    // update the flags
    this.indentChars = spaces;
    this.indent = (spaces != null);
  }

  /**
   * Sets the encoding to use.
   * 
   * <p>The encoding must match the encoding used if there is an underlying
   * <code>OutputStreamWriter</code>. 
   * 
   * @param encoding The encoding to use.
   * 
   * @throws IllegalArgumentException If the encoding is not valid.
   * @throws IllegalStateException    If the writer has already been used.
   */
  public final void setEncoding(String encoding) throws IllegalStateException, IllegalArgumentException {
    if (this.depth != 0)
      throw new IllegalStateException("To late to set the encoding!");
    this.encoding = encoding;
  }

// write text methods -------------------------------------------------------------------

  /**
   * @see XMLWriter#writeText(String)
   */
  public final void writeText(String text) throws IOException {
    if (text == null) return;
    deNude();
    writerEscape.writeText(text);
  }

  /**
   * @see XMLWriter#writeText(char[], int, int)
   */
  public final void writeText(char[] text, int off, int len) throws IOException {
    deNude();
    writerEscape.writeText(text, off, len);
  }

  /**
   * @see XMLWriter#writeText(char)
   */
  public final void writeText(char c) throws IOException {
	deNude();
    writerEscape.writeText(c);
  }

  /**
   * Writes the string value of an object.
   *
   * <p>Does nothing if the object is <code>null</code>.
   *
   * @see Object#toString
   * @see #writeText(java.lang.String)
   *
   * @param o The object that should be written as text.
   * 
   * @throws IOException If thrown by the wrapped writer.
   */
  public final void writeText(Object o) throws IOException {
	// TODO: what about an XML serializable ???
	// TODO: Add to interface ???
    if (o != null)
      this.writeText(o.toString());
  }

// write xml methods --------------------------------------------------------------------

  /**
   * @see XMLWriter#writeXML(java.lang.String)
   */
  public final void writeXML(String text) throws IOException {
    if (text == null) return;
	deNude();
    this.writer.write(text);
  }

  /**
   * @see XMLWriter#writeXML(char[], int, int)
   */
  public final void writeXML(char[] text, int off, int len) throws IOException {
	deNude();
    this.writer.write(text, off, len);
  }

// PI and comments ----------------------------------------------------------------------

  /**
   * @see XMLWriter#writeComment(String) 
   */
  public final void writeComment(String comment) throws IOException, IllegalArgumentException {
    if (comment == null)
      return;
    if (comment.indexOf("--") >= 0)
      throw new IllegalArgumentException("A comment must not contain '--'.");
    deNude();
    this.writer.write("<!-- ");
    this.writer.write(comment);
    this.writer.write(" -->");
    if (indent)
      this.writer.write('\n');
  }

  /**
   * @see XMLWriter#writePI(String, String)
   */
  public final void writePI(String target, String data) throws IOException {
    deNude();
    this.writer.write("<?");
    this.writer.write(target);
    this.writer.write(' ');
    this.writer.write(data);
    this.writer.write("?>");
    if (indent)
      this.writer.write('\n');
  }

// attribute methods --------------------------------------------------------------------

  /**
   * @see XMLWriter#attribute(String, String)
   */
  public final void attribute(String name, String value)
      throws IOException, IllegalStateException {
    if (!this.isNude) throw new IllegalArgumentException("Cannot write attribute: too late!");
    this.writer.write(' ');
    this.writer.write(name);
    this.writer.write("=\"");
    writerEscape.writeAttValue(value);
    this.writer.write('"');
  }

  /**
   * @see XMLWriter#attribute(String, int)
   */
  public final void attribute(String name, int value)
      throws IOException, IllegalStateException {
    if (!this.isNude) throw new IllegalArgumentException("Cannot write attribute: too late!");
    this.writer.write(' ');
    this.writer.write(name);
    this.writer.write("=\"");
    this.writer.write(Integer.toString(value));
    this.writer.write('"');
  }

// open/close specific elements ---------------------------------------------------------

  /**
   * @see XMLWriter#element(String, String)
   */
  public void element(String name, String text) throws IOException {
    this.openElement(name);
    this.writeText(text);
    this.closeElement();
  }

// direct access to the writer ----------------------------------------------------------

  /**
   * @see XMLWriter#flush()
   */
  public final void flush() throws IOException {
    this.writer.flush();
  }

// base class and convenience methods ---------------------------------------------------

  /**
   * Writes the end of the open element tag.
   * 
   * <p>After this method is invoked it is not possible to write attributes
   * for an element.
   * 
   * @throws IOException If thrown by the wrapped writer.
   */
  abstract void deNude() throws IOException;

  /**
   * Insert the correct amount of space characterss depending on the depth and if 
   * the <code>indent</code> flag is set to <code>true</code>.
   *
   * @throws IOException If thrown by the wrapped writer.
   */
  void indent() throws IOException {
    if (indent) for (int i = 0; i < depth; i++) this.writer.write(this.indentChars);
  }

  /**
   * Does nothing.
   * 
   * <p>This method exists so that we can explicitly say that we should do nothing
   * in certain conditions. 
   */
  static final void doNothing() {
    return;
  }

}