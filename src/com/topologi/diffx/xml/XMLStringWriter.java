/*
 * This file is part of the DiffX library.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at 
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package com.topologi.diffx.xml;

import java.io.IOException;
import java.io.StringWriter;

/**
 * An XML which writes on to a string.
 *
 * <p>This XML writer is backed by a {@link StringWriter} and will defer the XML writer's method to 
 * either a {@link XMLWriterImpl} or {@link XMLWriterNSImpl} depending on whether namespace support is 
 * required.
 *
 * @author  Christophe Lauret
 * @version 11 December 2011
 */
public final class XMLStringWriter implements XMLWriter {

  /**
   * Wraps an XML Writer
   */
  private final StringWriter writer;

  /**
   * Wraps an XML Writer
   */
  private final XMLWriter xml;

  /**
   * <p>Creates a new XML string writer.
   *
   * @param namespaces Whether this XML writer should use namespaces.
   */
  public XMLStringWriter(boolean namespaces) {
    this(namespaces, false);
  }

  /**
   * <p>Create a new XML string writer.
   *
   * @param namespaces Whether this XML writer should use namespaces.
   * @param indent  Set the indentation flag.
   */
  public XMLStringWriter(boolean namespaces, boolean indent) {
    this.writer = new StringWriter();
    this.xml = namespaces? new XMLWriterNSImpl(this.writer, indent) : new XMLWriterImpl(this.writer, indent);
  }

  // XML Writer methods
  // ----------------------------------------------------------------------------------------------

  @Override
  public void xmlDecl() throws IOException, IllegalStateException {
    this.xml.xmlDecl();
  }

  @Override
  public void setIndentChars(String spaces) throws IllegalStateException, IllegalArgumentException {
    this.xml.setIndentChars(spaces);
  }

  @Override
  public void writeText(char c) throws IOException {
    this.xml.writeText(c);
  }

  @Override
  public void writeText(String text) throws IOException {
    this.xml.writeText(text);
  }

  @Override
  public void writeText(char[] text, int off, int len) throws IOException {
    this.xml.writeText(text, off, len);
  }

  @Override
  public void writeXML(String text) throws IOException {
    this.xml.writeXML(text);
  }

  @Override
  public void writeXML(char[] text, int off, int len) throws IOException {
    this.xml.writeXML(text, off, len);
  }

  @Override
  public void writeComment(String comment) throws IOException {
    this.xml.writeComment(comment);
  }

  @Override
  public void writePI(String target, String data) throws IOException {
    this.xml.writePI(target, data);
  }

  @Override
  public void openElement(String name) throws IOException {
    this.xml.openElement(name);
  }

  @Override
  public void openElement(String name, boolean hasChildren) throws IOException {
    this.xml.openElement(name, hasChildren);
  }

  @Override
  public void openElement(String uri, String name, boolean hasChildren) throws IOException,
      UnsupportedOperationException {
    this.xml.openElement(uri, name, hasChildren);
  }

  @Override
  public void closeElement() throws IOException {
    this.xml.closeElement();
  }

  @Override
  public void element(String name, String text) throws IOException {
    this.xml.element(name, text);
  }

  @Override
  public void emptyElement(String element) throws IOException, UnsupportedOperationException {
    this.xml.emptyElement(element);
  }

  @Override
  public void emptyElement(String uri, String element) throws IOException, UnsupportedOperationException {
    this.xml.emptyElement(element);
  }

  @Override
  public void attribute(String name, String value) throws IOException, IllegalStateException {
    this.xml.attribute(name, value);
  }

  @Override
  public void attribute(String name, int value) throws IOException, IllegalStateException {
    this.xml.attribute(name, value);
  }

  @Override
  public void attribute(String uri, String name, String value) throws IOException, UnsupportedOperationException {
    this.xml.attribute(uri, name, value);
  }

  @Override
  public void attribute(String uri, String name, int value) throws IOException, UnsupportedOperationException {
    this.xml.attribute(uri, name, value);
  }

  @Override
  public void setPrefixMapping(String uri, String prefix) throws UnsupportedOperationException {
    this.xml.setPrefixMapping(uri, prefix);
  }

  @Override
  public void flush() throws IOException {
    this.xml.flush();
  }

  @Override
  public void close() throws IOException, UnclosedElementException {
    this.xml.close();
  }

  /**
   * Returns the XML content as a {@link String}.
   * 
   * @return the XML content as a {@link String}.
   */
  @Override
  public String toString() {
    return this.writer.toString();
  }

}