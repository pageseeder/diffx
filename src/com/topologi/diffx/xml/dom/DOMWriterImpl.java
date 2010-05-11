/*
 * This file is part of the DiffX library.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at 
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package com.topologi.diffx.xml.dom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import com.topologi.diffx.xml.IllegalCloseElementException;
import com.topologi.diffx.xml.UnclosedElementException;

/**
 * A simple implementation of a DOM writer
 *
 * <p>Provides methods to generate well-formed XML data easily via DOM.
 *
 * @author  Christophe Lauret
 * @version 7 June 2005
 */
public final class DOMWriterImpl implements DOMWriter {

// class attributes ---------------------------------------------------------------------

  /**
   * The DOM document on which we write.
   */
  private final Document document;

  /**
   * The new line used.
   */
  private final Node newline;

  /**
   * Indicates whether the xml should be indented or not.
   *
   * <p>The default is <code>true</code> (indented).
   *
   * <p>The indentation is 2 white-spaces.
   */
  private boolean indent;

  /**
   * The default indentation spaces used.
   */
  private String indentChars;

// state variables ----------------------------------------------------------------------

  /**
   * Level of the depth of the xml document currently produced.
   *
   * <p>This attribute changes depending on the state of the instance.
   */
  private transient int depth;

  /**
   * Flag to indicate that the element open tag is not finished yet. 
   */
  private transient boolean isNude; 

  /**
   * The current node being written onto.
   * 
   * <p>This node should always be an element except before and after writing where it
   * is the document node itself.
   */
  private transient Node currentElement;

  /**
   * An array to indicate which elements have children.
   */
  private transient List<Boolean> childrenFlags = new ArrayList<Boolean>();

// constructors -------------------------------------------------------------------------

  /**
   * <p>Creates a new XML writer for DOM using the default implementation on
   * the system.
   * 
   * <p>Attempts to create the DOM document using:
   * <pre>
   *  DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance()
   *  DocumentBuilder builder = factory.newDocumentBuilder();
   *  Document document = builder.newDocument();
   * </pre>
   *
   * @throws ParserConfigurationException If thrown by the document builder factory.
   */
  public DOMWriterImpl() throws ParserConfigurationException {
    this(newDocument());
  }

  /**
   * <p>Creates a new XML writer for DOM.
   *
   * @param document The DOM provided. 
   * 
   * @throws NullPointerException If the handler is <code>null</code>.
   */
  public DOMWriterImpl(Document document) throws NullPointerException {
    if (document == null) 
      throw new NullPointerException("The XMLWriter requires a DOM Document to write on.");
    this.document = document;
    this.currentElement = document;
    this.newline = document.createTextNode("\n");
  }

// setup methods ------------------------------------------------------------------------

  /**
   * Does nothing.
   */
  public void xmlDecl() {
  }

  /**
   * {@inheritDoc}
   */
  public void setIndentChars(String spaces) throws IllegalStateException, IllegalArgumentException {
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

// write text methods -------------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  public void writeText(String text) {
    if (text == null) return;
    this.deNude();
    Text textNode = this.document.createTextNode(text);
    this.currentElement.appendChild(textNode);
  }

  /**
   * {@inheritDoc}
   */
  public void writeText(char[] text, int off, int len) {
    this.writeText(new String(text, off, len));
  }

  /**
   * This method is expensive as the character has to be converted to a String for DOM.
   * 
   * {@inheritDoc}
   */
  public void writeText(char c) {
    this.writeText(new String(new char[]{c}));
  }

  /**
   * Writes the string value of an object.
   *
   * <p>Does nothing if the object is <code>null</code>.
   *
   * @see Object#toString
   *
   * @param o The object that should be written as text.
   * 
   * @throws IOException If thrown by the wrapped writer.
   */
  public void writeText(Object o) throws IOException {
    // TODO: what about an XML serializable ???
    // TODO: Add to interface ???
    if (o != null)
      this.writeText(o.toString());
  }

// write xml methods are not supported --------------------------------------------------

  /**
   * {@inheritDoc}
   */
  public void writeXML(String text) throws UnsupportedOperationException {
    throw new UnsupportedOperationException("Cannot use unparsed XML as DOM node.");
  }

  /**
   * {@inheritDoc}
   */
  public void writeXML(char[] text, int off, int len) 
    throws UnsupportedOperationException {
    throw new UnsupportedOperationException("Cannot use unparsed XML as DOM node.");
  }

// PI and comments ----------------------------------------------------------------------

  /**
   * {@inheritDoc} 
   */
  public void writeComment(String comment) throws IOException {
    if (comment.indexOf("--") >= 0)
      throw new IllegalArgumentException("A comment must not contain '--'.");
    deNude();
    Node node = this.document.createComment(comment);
    this.currentElement.appendChild(node);
    if (this.indent) newLine();
  }

  /**
   * {@inheritDoc}
   */
  public void writePI(String target, String data) throws IOException {
    deNude();
    Node node = this.document.createProcessingInstruction(target, data);
    this.currentElement.appendChild(node);
    if (this.indent) newLine();
  }

// attribute methods --------------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  public void attribute(String name, String value) 
    throws IOException, IllegalStateException {
    if (!this.isNude)
      throw new IllegalArgumentException("Cannot write attribute: too late!");
    Attr att = this.document.createAttribute(name);
    att.setValue(value);
    this.currentElement.appendChild(att);
  }

  /**
   * {@inheritDoc}
   */
  public void attribute(String name, int value)
      throws IOException, IllegalStateException {
    attribute(name, Integer.toString(value));
  }

// open/close specific elements ---------------------------------------------------------

  /**
   * Writes a start element tag correctly indented.
   *
   * <p>It is the same as <code>openElement("", name, false)</code>
   *
   * @see #openElement(java.lang.String, java.lang.String, boolean)
   *
   * @param name the name of the element
   * 
   * @throws IOException If thrown by the wrapped writer.
   */
  public void openElement(String name) throws IOException {
    openElement(name, false);
  }

  /**
   * Writes a start element tag correctly indented.
   *
   * <p>Use the <code>hasChildren</code> parameter to specify whether this element is terminal
   * node or not, note: this affects the indenting. To produce correctly indented XML, you 
   * should use the same value for this flag when closing the element.
   *
   * <p>The name can contain attributes and should be a valid xml name.
   *
   * @param name        The name of the element.
   * @param hasChildren <code>true</code> if this element has children.
   * 
   * @throws IOException If thrown by the wrapped writer.
   */
  public void openElement(String name, boolean hasChildren) throws IOException {
    deNude();
    this.indent();
    this.childrenFlags.add(Boolean.valueOf(hasChildren));
    Element element = this.document.createElement(name);
    this.currentElement.appendChild(element);
    this.currentElement = element;
    this.isNude = true;
    this.depth++;
  }

  /**
   * {@inheritDoc}
   */
  public void element(String name, String text) throws IOException {
    this.openElement(name);
    this.writeText(text);
    this.closeElement();
  }

  /**
   * {@inheritDoc} 
   */
  public void closeElement() {
    if (this.currentElement.getNodeType() == Node.DOCUMENT_NODE)
      throw new IllegalCloseElementException();
    this.depth--;
    this.isNude = false;
    Boolean hasChildren = this.childrenFlags.remove(this.childrenFlags.size() - 1);
    if (hasChildren.booleanValue())
      this.indent();
    this.currentElement.normalize();
    this.currentElement = (Element)this.currentElement.getParentNode();
    // new line if parent has children
    if (this.indent) {
      Boolean b = this.childrenFlags.get(this.childrenFlags.size() - 1);
      if (b.booleanValue()) // and parent != root
        newLine();
    }
  }

  /**
   * {@inheritDoc}
   */
  public void emptyElement(String name) {
    Element element = this.document.createElement(name);
    this.currentElement.appendChild(element);
  }

// direct access to the writer ----------------------------------------------------------

  public void close() throws IOException, UnclosedElementException {
    // TODO Auto-generated method stub
    
  }

  /**
   * Normalises the current element.
   */
  public void flush() {
    this.currentElement.normalize();
  }

// DOM Writer methods -------------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  public Document getDocument() {
    return this.document;
  }

  // unsupported operations -------------------------------------------------------------------

  /**
   * Not supported.
   *
   * @param uri  This parameter is ignored.
   * @param name This parameter is ignored.
   * 
   * @throws UnsupportedOperationException This class does not handle namespaces.
   */
  public void openElement(String uri, String name) throws UnsupportedOperationException {
    throw new UnsupportedOperationException("This class does not handle namespaces.");
  }

  /**
   * Not supported.
   *
   * @param uri         This parameter is ignored.
   * @param name        This parameter is ignored.
   * @param hasChildren This parameter is ignored.
   * 
   * @throws UnsupportedOperationException This class does not handle namespaces.
   */
  public void openElement(String uri, String name, boolean hasChildren)
    throws UnsupportedOperationException {
    throw new UnsupportedOperationException("This class does not handle namespaces.");
  }

  /**
   * Not supported.
   *
   * @param uri     This parameter is ignored.
   * @param element This parameter is ignored.
   * 
   * @throws UnsupportedOperationException This class does not handle namespaces.
   */
  public void emptyElement(String uri, String element) 
      throws UnsupportedOperationException {
    throw new UnsupportedOperationException("This class does not handle namespaces");
  }

  /**
   * Not supported.
   * 
   * @param uri    This parameter is ignored.
   * @param prefix This parameter is ignored.
   * 
   * @throws UnsupportedOperationException This class does not handle namespaces.
   */
  public void setPrefixMapping(String uri, String prefix)
      throws UnsupportedOperationException {
    throw new UnsupportedOperationException("This class does not handle namespaces");
  }

  /**
   * Not supported.
   * 
   * @param uri    This parameter is ignored.
   * @param name  The name of the attribute.
   * @param value The value of the attribute.
   * 
   * @throws UnsupportedOperationException This class does not handle namespaces.
   */
  public void attribute(String uri, String name, String value) 
      throws UnsupportedOperationException {
    throw new UnsupportedOperationException("This class does not handle namespaces");
  }

  /**
   * Not supported.
   * 
   * @param uri    This parameter is ignored.
   * @param name  The name of the attribute.
   * @param value The value of the attribute.
   * 
   * @throws UnsupportedOperationException This class does not handle namespaces.
   */
  public void attribute(String uri, String name, int value) 
     throws UnsupportedOperationException {
    throw new UnsupportedOperationException("This class does not handle namespaces");
  }

// private helpers ----------------------------------------------------------------------

  /**
   * Insert the correct amount of space characterss depending on the depth and if 
   * the <code>indent</code> flag is set to <code>true</code>.
   */
  void indent() {
    if (this.indent) {
      StringBuffer out = new StringBuffer(this.depth * this.indentChars.length()); 
      for (int i = 0; i < this.depth; i++) out.append(this.indentChars);
      Node node = this.document.createTextNode(out.toString());
      this.currentElement.appendChild(node);
    }
  }

  /**
   * Writes the angle bracket if the element open tag is not finished.
   */
  private void deNude() {
    if (this.isNude) {
      if (this.indent) { //TODO: hasChildren
        newLine();
      }
      this.isNude = false;
    }
  }

  /**
   * Adds a new line to the DOM.
   */
  private void newLine() {
    this.currentElement.appendChild(this.newline.cloneNode(false));
  }

  /**
   * Returns a new DOM document.
   * 
   * <p>Attempts to create the DOM document using:
   * <pre>
   *  DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance()
   *  DocumentBuilder builder = factory.newDocumentBuilder();
   *  Document document = builder.newDocument();
   * </pre>
   * 
   * @return A new DOM document.
   * 
   * @throws ParserConfigurationException If thrown by the document builder factory. 
   */
  private static Document newDocument() throws ParserConfigurationException {
    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    return builder.newDocument();
  }

}