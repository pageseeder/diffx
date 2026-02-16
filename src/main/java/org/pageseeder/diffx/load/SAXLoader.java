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

import org.jspecify.annotations.Nullable;
import org.pageseeder.diffx.api.LoadingException;
import org.pageseeder.diffx.config.DiffConfig;
import org.pageseeder.diffx.load.text.TextTokenizer;
import org.pageseeder.diffx.load.text.TokenizerFactory;
import org.pageseeder.diffx.token.*;
import org.pageseeder.diffx.token.impl.XMLComment;
import org.pageseeder.diffx.token.impl.XMLProcessingInstruction;
import org.pageseeder.diffx.xml.Sequence;
import org.xml.sax.*;
import org.xml.sax.ext.EntityResolver2;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Loads the SAX events in an {@link Sequence}.
 *
 * <p>By default, this class will try to use the XML reader using the JAXP provider.
 * If a custom XML reader class is specified, it will be used instead.
 *
 * <p>The XML reader implementation must support the following features settings
 * <pre>{@code
 *   http://xml.org/sax/features/validation         => false
 *   http://xml.org/sax/features/namespaces         => true | false
 *   http://xml.org/sax/features/namespace-prefixes => true | false
 * }</pre>
 *
 * <p>Security note: after the XMLReader is instantiated, this class will also attempt to
 * harden it to avoid potential XXE attacks. This is done on a best-effort basis meaning
 * that the configuration will be skipped if the feature is not supported, potentially
 * exposing your application. If you work with untrusted input, consider using a
 * dedicated XML reader implementation that provides stronger security guarantees.
 *
 * @author Christophe Lauret
 * @author Jean-Baptiste Reure
 *
 * @version 1.3.2
 * @since 0.6.0
 */
public final class SAXLoader extends XMLLoaderBase implements XMLLoader {

  /**
   * Returning an empty InputSource blocks external entity resolution.
   * (This is intentionally conservative; it prevents XXE/network/file access.)
   */
  private static final EntityResolver2 DENY_EXTERNAL_ENTITY_RESOLVER = new EntityResolver2() {
    @Override
    public InputSource getExternalSubset(String name, String baseURI) {
      return new InputSource(new StringReader(""));
    }

    @Override
    public InputSource resolveEntity(String name, String publicId, String baseURI, String systemId) {
      return new InputSource(new StringReader(""));
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) {
      return new InputSource(new StringReader(""));
    }
  };

  /**
   * Default XML reader factory using JAXP to instantiate a new instance.
   */
  private static final Function<DiffConfig, XMLReader> DEFAULT_READER_FACTORY = (DiffConfig config) -> {
    try {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setNamespaceAware(config.isNamespaceAware());
      factory.setValidating(false);
      // Harden JAXP factory-level behavior (effective for the default JAXP path).
      factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
      SAXParser parser = factory.newSAXParser();
      return parser.getXMLReader();
    } catch (ParserConfigurationException | SAXException ex) {
      throw new IllegalStateException("Unable to create XMLReader via JAXP fallback", ex);
    }
  };

  /**
   * Factory for creating XMLReader instances.
   */
  private static Function<DiffConfig, XMLReader> readerFactory = DEFAULT_READER_FACTORY;

  /**
   * The XML reader class in use (set to the default XML reader).
   */
  private static String readerClassName = "";

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

    TextTokenizer tokenizer = this.textTokenizer != null ? this.textTokenizer : TokenizerFactory.get(this.config);

    Handler handler = new Handler(this.config, tokenizer);

    reader.setContentHandler(handler);
    reader.setErrorHandler(handler);

    // Defense-in-depth: prevent XXE even if some features are ignored/unsupported.
    reader.setEntityResolver(DENY_EXTERNAL_ENTITY_RESOLVER);

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
   * Returns the name XMLReader class to use or "" when not in use.
   *
   * @return the name of a custom XMLReader class used by the SAXLoader.
   */
  public static String getXMLReaderClass() {
    return readerClassName;
  }

  /**
   * Returns the factory used to create XMLReader instances.
   *
   * <p>Implementation note: when the {@link #setXMLReaderFactory(Function)} is called with
   * a {@code null} value, this resets the reader factory to the default JAXP factory.
   *
   * @return the factory used by the SAXLoader to generate new XMLReader instances.
   */
  public static Function<DiffConfig, XMLReader> getXMLReaderFactory() {
    return readerFactory;
  }

  /**
   * Sets a factory used to create XMLReader instances.
   *
   * <p>Pass {@code null} to clear and use the default JAXP-backed reader selection.
   *
   * <p>The factory will be called for each parse to produce a fresh reader instance.
   *
   * <p>Whenever this method is called, it resets the reader class name.
   *
   * @param factory The factory used to create XMLReader instances.
   */
  public static void setXMLReaderFactory(@Nullable Function<DiffConfig, ? extends XMLReader> factory) {
    readerFactory = factory == null ? DEFAULT_READER_FACTORY : factory::apply;
    readerClassName = "";
  }

  /**
   * Sets the name of the XML reader class to use if you need to use a custom implementation.
   *
   * <p>Use <code>null</code> or empty string to reset the XML reader class and use the default XML reader.
   *
   * @param className The name of the XML reader class to use;
   *                  or <code>null</code> to reset the XML reader.
   * @deprecated Prefer {@link #setXMLReaderFactory(Function)}.
   */
  @Deprecated(forRemoval = true, since = "1.2.0")
  public static void setXMLReaderClass(@Nullable String className) {
    readerClassName = Objects.toString(className, "");
    // Always set the factory
    readerFactory = readerClassName.isEmpty()
        ? DEFAULT_READER_FACTORY
        : new LegacyCustomXMLReaderFactory(readerClassName);
  }

  /**
   * Creates and configures a new XML reader instance.
   *
   * <p>This method works on a user-defined XML reader class name.
   *
   * <p>After the XML reader is instantiated, the following configuration is applied on a best-effort basis:
   * <ul>
   *   <li>Disabling external entity resolution</li>
   *   <li>Disabling external DTD loading</li>
   *   <li>Disabling namespace validation</li>
   *   <li>Disabling DTD validation</li>
   *   <li>Disabling DOCTYPE declaration if configured</li>
   * </ul>
   *
   * <p>Security note: If the SAX features aren't supported by your XMLReader implementation, the
   * configuration will be skipped which could possibly expose your application to XXE attacks
   * if the XML input is malicious.
   *
   * @throws LoadingException If one of the features could not be set.
   */
  private static XMLReader newReader(DiffConfig config) throws LoadingException {
    XMLReader reader;
    try {
      reader = readerFactory.apply(config);
      //noinspection ConstantValue (Defensive code to avoid an NPE)
      if (reader == null) throw new LoadingException("XMLReader factory returned null");
    } catch (Exception ex) {
      throw new LoadingException("XMLReader factory threw an exception: " + ex.getMessage(), ex);
    }
    try {
      // Features that should be supported
      reader.setFeature("http://xml.org/sax/features/validation", false);
      reader.setFeature("http://xml.org/sax/features/namespaces", config.isNamespaceAware());
      reader.setFeature("http://xml.org/sax/features/namespace-prefixes", !config.isNamespaceAware());

      // XXE hardening (best-effort, portable-first)
      trySetFeature(reader, "http://xml.org/sax/features/external-general-entities", false);
      trySetFeature(reader, "http://xml.org/sax/features/external-parameter-entities", false);
      trySetFeature(reader, "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

      // Optional extra hardening (Xerces-specific): disallow DOCTYPE if configured.
      if (!config.allowDoctypeDeclaration()) {
        trySetFeature(reader, "http://apache.org/xml/features/disallow-doctype-decl", true);
      }

      return reader;
    } catch (SAXException ex) {
      throw new LoadingException(ex);
    }
  }

  private static void trySetFeature(XMLReader reader, String uri, boolean value) {
    try {
      reader.setFeature(uri, value);
    } catch (SAXNotRecognizedException | SAXNotSupportedException ignored) {
      // Parser doesn't support this feature
    }
  }

  /**
   * A SAX2 handler that generates a list of XML tokens.
   *
   * <p>This class is an inner class as there is no reason to expose its method to the
   * public API.
   */
  private static final class Handler extends DefaultHandler implements LexicalHandler {

    /**
     * The sequence of token for this loader.
     */
    private final Sequence sequence = new Sequence();

    /**
     * A buffer for character data.
     */
    private final StringBuilder ch = new StringBuilder();

    /**
     * The comparator to sort attribute correctly.
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

    Handler(DiffConfig config, TextTokenizer tokenizer) {
      this.tokenFactory = new XMLTokenFactory(config.isNamespaceAware());
      this.tokenizer = Objects.requireNonNull(tokenizer, "tokenizer");
    }

    public Sequence getSequence() {
      return this.sequence;
    }

    @Override
    public void startDocument() {
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
      // by the 'characters' method.
    }

    @Override
    public void processingInstruction(String target, String data) {
      this.sequence.addToken(new XMLProcessingInstruction(target, data));
    }

    @Override
    public void endDocument() {
      // Nothing to do
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
      // We don't load external DTDs
    }

    @Override
    public void endDTD() {
      // We don't load external DTDs
    }

    @Override
    public void startEntity(String name) {
      // We don't report entities as a token, but as a text token
    }

    @Override
    public void endEntity(String name) {
      // We don't report entities as a token, but as a text token
    }

    @Override
    public void startCDATA() {
      // We don't report CDATA as a token, but as a text token
    }

    @Override
    public void endCDATA() {
      // We don't report CDATA as a token, but as a text token
    }

    @Override
    public void error(SAXParseException ex) throws SAXException {
      throw ex;
    }

    @Override
    public void warning(SAXParseException ex) throws SAXException {
      throw ex;
    }
  }

  /**
   * A factory for creating XMLReader instances using a legacy custom class.
   */
  private static final class LegacyCustomXMLReaderFactory implements Function<DiffConfig, XMLReader> {

    private final String readerClassName;

    LegacyCustomXMLReaderFactory(String readerClassName) {
      this.readerClassName = readerClassName;
    }

    @Override
    public XMLReader apply(DiffConfig config) {
      try {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Class<?> raw = Class.forName(readerClassName, true, cl);
        if (!XMLReader.class.isAssignableFrom(raw)) {
          throw new IllegalStateException("Class " + readerClassName + " does not implement " + XMLReader.class.getName());
        }
        @SuppressWarnings("unchecked")
        Class<? extends XMLReader> type = (Class<? extends XMLReader>) raw;
        return type.getDeclaredConstructor().newInstance();
      } catch (Exception ex) {
        throw new IllegalStateException(ex);
      }
    }
  }

}
