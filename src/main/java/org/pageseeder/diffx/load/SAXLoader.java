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
package org.pageseeder.diffx.load;

import org.pageseeder.diffx.api.LoadingException;
import org.pageseeder.diffx.config.DiffConfig;
import org.pageseeder.diffx.load.text.TextTokenizer;
import org.pageseeder.diffx.load.text.TokenizerFactory;
import org.pageseeder.diffx.token.*;
import org.pageseeder.diffx.token.impl.XMLComment;
import org.pageseeder.diffx.token.impl.XMLProcessingInstruction;
import org.pageseeder.diffx.xml.Sequence;
import org.xml.sax.*;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.XMLConstants;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Loads the SAX events in an {@link Sequence}.
 *
 * <p>It is possible to specify the name of the XML reader implementation class.
 * By default this class will try to use the Crimson parser
 * <code>org.apache.crimson.parser.XMLReaderImpl</code>.
 *
 * <p>The XML reader implementation must support the following features settings
 * <pre>
 *   http://xml.org/sax/features/validation         => false
 *   http://xml.org/sax/features/namespaces         => true | false
 *   http://xml.org/sax/features/namespace-prefixes => true | false
 * </pre>
 *
 * @author Christophe Lauret
 * @author Jean-Baptiste Reure
 * @version 0.9.0
 * @since 0.6.0
 */
public final class SAXLoader extends XMLLoaderBase implements XMLLoader {

  /**
   * The default XML reader in use.
   */
  private static final String DEFAULT_XML_READER;

  static {
    String className;
    try {
      className = XMLReaderFactory.createXMLReader().getClass().getName();
    } catch (SAXException ex) {
      System.err.println("org.pageseeder.diffx.SAXLoader cannot find a default XML loader!");
      className = "";
    }
    DEFAULT_XML_READER = className;
  }

  /**
   * The XML reader class in use (set to the default XML reader).
   */
  private static String readerClassName = DEFAULT_XML_READER;

  /**
   * Runs the loader on the specified input source.
   *
   * @param is The input source.
   *
   * @return The recorded sequence of tokens.
   * @throws LoadingException If thrown whilst parsing.
   * @throws IOException      Should I/O error occur.
   */
  @Override
  public Sequence load(InputSource is) throws LoadingException, IOException {
    XMLReader reader = newReader(this.config);
    Handler handler = new Handler(this.config);
    reader.setContentHandler(handler);
    reader.setErrorHandler(handler);

    try {
      reader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
    } catch (SAXNotRecognizedException | SAXNotSupportedException ex) {
      // Ignore
    }

    try {
      reader.parse(is);
    } catch (SAXException ex) {
      throw new LoadingException(ex);
    }
    return handler.sequence;
  }

  /**
   * Returns the name XMLReader class used by the SAXRecorders.
   *
   * @return the name XMLReader class used by the SAXRecorders.
   */
  public static String getXMLReaderClass() {
    return readerClassName;
  }

  /**
   * Sets the name of the XML reader class to use.
   *
   * <p>Use <code>null</code> to reset the XML reader class and use the default XML reader.
   *
   * <p>A new reader will be created only if the specified class is different from the current one.
   *
   * @param className The name of the XML reader class to use;
   *                  or <code>null</code> to reset the XML reader.
   */
  public static void setXMLReaderClass(String className) {
    // if the className is null reset to default
    if (className == null) {
      className = DEFAULT_XML_READER;
    }
    readerClassName = className;
  }

  /**
   * Initialises the XML reader using the defined class name.
   *
   * @throws LoadingException If one of the features could not be set.
   */
  private static XMLReader newReader(DiffConfig config) throws LoadingException {
    try {
      XMLReader reader = XMLReaderFactory.createXMLReader(readerClassName);
      reader.setFeature("http://xml.org/sax/features/validation", false);
      reader.setFeature("http://xml.org/sax/features/namespaces", config.isNamespaceAware());
      reader.setFeature("http://xml.org/sax/features/namespace-prefixes", !config.isNamespaceAware());
      return reader;
    } catch (SAXException ex) {
      throw new LoadingException(ex);
    }
  }

  /**
   * A SAX2 handler that records XML tokens.
   *
   * <p>This class is an inner class as there is no reason to expose its method to the
   * public API.
   *
   * @author Christophe Lauret
   * @author Jean-Baptiste Reure
   * @version 0.9.0
   * @since 0.6.0
   */
  private final static class Handler extends DefaultHandler implements LexicalHandler {

    /**
     * The sequence of token for this loader.
     */
    private Sequence sequence;

    /**
     * A buffer for character data.
     */
    private final StringBuilder ch = new StringBuilder();

    /**
     * The comparator in order to sort attribute correctly.
     */
    private final AttributeComparator comparator = new AttributeComparator();

    /**
     * The last open element token, should only contain <code>StartElementToken</code>s.
     */
    private final List<StartElementToken> openElements = new ArrayList<>();

    /**
     * The factory that will produce tokens according to the configuration.
     */
    private final XMLTokenFactory tokenFactory;

    /**
     * The text tokenizer according to the configuration.
     */
    private final TextTokenizer tokenizer;

    Handler(DiffConfig config) {
      this.tokenFactory = new XMLTokenFactory(config.isNamespaceAware());
      this.tokenizer = TokenizerFactory.get(config);
    }

    public Sequence getSequence() {
      return this.sequence;
    }

    @Override
    public void startDocument() {
      this.sequence = new Sequence();
      this.sequence.addNamespace(XMLConstants.XML_NS_URI, XMLConstants.XML_NS_PREFIX);
      this.sequence.addNamespace(XMLConstants.NULL_NS_URI, XMLConstants.DEFAULT_NS_PREFIX);
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) {
      // For the root element only, we may replace the mapping to the default prefix
      // (this method is called BEFORE the start element)
      this.sequence.addNamespace(uri, prefix, this.openElements.isEmpty());
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
      recordCharacters();
      StartElementToken open = this.tokenFactory.newStartElement(uri, localName, qName);
      this.openElements.add(open);
      this.sequence.addToken(open);
      handleAttributes(attributes);
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
      recordCharacters();
      StartElementToken open = popLastOpenElement();
      EndElementToken close = this.tokenFactory.newEndElement(open);
      this.sequence.addToken(close);
    }

    @Override
    public void characters(char[] buf, int pos, int len) {
      this.ch.append(buf, pos, len);
    }

    @Override
    public void ignorableWhitespace(char[] buf1, int pos, int len) {
      // this method is only useful if the XML provides a Schema or DTD
      // to define in which cases whitespaces can be considered ignorable.
      // By default, all white spaces are significant and therefore reported
      // by the characters method.
    }

    @Override
    public void processingInstruction(String target, String data) {
      this.sequence.addToken(new XMLProcessingInstruction(target, data));
    }

    @Override
    public void endDocument() {
    }

    /**
     * Records the characters which are in the buffer.
     */
    private void recordCharacters() {
      if (this.ch.length() > 0) {
        List<TextToken> tokens = this.tokenizer.tokenize(this.ch);
        for (TextToken token : tokens) {
          this.sequence.addToken(token);
        }
        this.ch.setLength(0);
      }
    }

    /**
     * Returns the last open element and remove it from the stack.
     *
     * @return The last open element.
     */
    private StartElementToken popLastOpenElement() {
      return this.openElements.remove(this.openElements.size() - 1);
    }

    /**
     * Handles the attributes, will add them to the sequence in order if any.
     *
     * @param attributes The attributes to handle.
     */
    private void handleAttributes(Attributes attributes) {
      // only one attribute
      if (attributes.getLength() == 1) {
        this.sequence.addToken(this.tokenFactory.newAttribute(attributes.getURI(0),
            attributes.getLocalName(0),
            attributes.getQName(0),
            attributes.getValue(0)));
        // several attributes
      } else if (attributes.getLength() > 1) {
        // store all the attributes
        AttributeToken[] attEvents = new AttributeToken[attributes.getLength()];
        for (int i = 0; i < attributes.getLength(); i++) {
          attEvents[i] = this.tokenFactory.newAttribute(attributes.getURI(i),
              attributes.getLocalName(i),
              attributes.getQName(i),
              attributes.getValue(i));
        }
        // sort them
        Arrays.sort(attEvents, this.comparator);
        // add them to the sequence
        for (AttributeToken attEvent : attEvents) {
          this.sequence.addToken(attEvent);
        }
      }
    }

    @Override
    public void comment(char[] ch, int start, int length) throws SAXException {
      this.sequence.addToken(new XMLComment(new String(ch, start, length)));
    }

    @Override
    public void startDTD(String name, String publicId, String systemId) {
    }

    @Override
    public void endDTD() {
    }

    @Override
    public void startEntity(String name) {
    }

    @Override
    public void endEntity(String name) {
    }

    @Override
    public void startCDATA() {
    }

    @Override
    public void endCDATA() {
    }

    @Override
    public void error(SAXParseException ex) throws SAXException {
      throw ex;
    }

    @Override
    public void fatalError(SAXParseException ex) throws SAXException {
      throw ex;
    }

    @Override
    public void warning(SAXParseException ex) throws SAXException {
      throw ex;
    }
  }

}
