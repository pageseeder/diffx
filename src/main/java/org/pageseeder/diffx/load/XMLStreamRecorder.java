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

import org.pageseeder.diffx.config.DiffXConfig;
import org.pageseeder.diffx.load.text.TextTokenizer;
import org.pageseeder.diffx.load.text.TokenizerFactory;
import org.pageseeder.diffx.sequence.Sequence;
import org.pageseeder.diffx.token.*;
import org.pageseeder.diffx.token.impl.*;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static javax.xml.stream.XMLStreamConstants.COMMENT;
import static javax.xml.stream.XMLStreamConstants.PROCESSING_INSTRUCTION;

/**
 * Records the XML events in an {@link Sequence}.
 *
 * @author Christophe Lauret
 * @author Jean-Baptiste Reure
 * @version 0.9.0
 * @since 0.6.0
 */
public final class XMLStreamRecorder implements XMLRecorder {

  /**
   * The DiffX configuration to use
   */
  private DiffXConfig config = new DiffXConfig();

  /**
   * Runs the recorder on the specified file.
   *
   * @param file The file to process.
   *
   * @return The recorded sequence of tokens.
   * @throws LoadingException If thrown while parsing.
   * @throws IOException      Should I/O error occur.
   */
  @Override
  public Sequence process(File file) throws LoadingException, IOException {
    XMLInputFactory factory = toFactory(this.config);
    try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
      XMLStreamReader reader = factory.createXMLStreamReader(in);
      return process(reader);
    } catch (XMLStreamException ex) {
      throw new LoadingException(ex);
    }
  }

  /**
   * Runs the recorder on the specified string.
   *
   * <p>This method is provided for convenience. It is best to only use this method for
   * short strings.
   *
   * @param xml The XML string to process.
   *
   * @return The recorded sequence of tokens.
   * @throws LoadingException If thrown while parsing.
   */
  @Override
  public Sequence process(String xml) throws LoadingException {
    XMLInputFactory factory = toFactory(this.config);
    try (StringReader source = new StringReader(xml)) {
      XMLStreamReader reader = factory.createXMLStreamReader(source);
      return process(reader);
    } catch (XMLStreamException ex) {
      throw new LoadingException(ex);
    }
  }

  /**
   * Runs the recorder on the specified input source.
   *
   * @param source The input source.
   *
   * @return The recorded sequence of tokens.
   * @throws LoadingException If thrown whilst parsing.
   * @throws IOException      Should I/O error occur.
   */
  @Override
  public Sequence process(InputSource source) throws LoadingException, IOException {
    XMLInputFactory factory = toFactory(this.config);
    try {
      XMLStreamReader reader = toXMLStreamReader(factory, source);
      return process(reader);
    } catch (XMLStreamException ex) {
      throw new LoadingException(ex);
    }
  }

  /**
   * Returns the configuration used by this recorder.
   *
   * @return the configuration used by this recorder.
   */
  public DiffXConfig getConfig() {
    return this.config;
  }

  /**
   * Sets the configuration used by this recorder.
   *
   * @param config The configuration used by this recorder.
   */
  public void setConfig(DiffXConfig config) {
    this.config = config;
  }

  public Sequence process(XMLStreamReader reader) throws LoadingException {
    TokenFactory tokenFactory = new TokenFactory(this.config.isNamespaceAware());
    TextTokenizer tokenizer = TokenizerFactory.get(this.config);
    List<StartElementToken> startElements = new ArrayList<>();
    Sequence sequence = new Sequence();
    sequence.addNamespace(XMLConstants.XML_NS_URI, XMLConstants.XML_NS_PREFIX);
    sequence.addNamespace(XMLConstants.NULL_NS_URI, XMLConstants.DEFAULT_NS_PREFIX);
    try {
      while (reader.hasNext()) {
        reader.next();
        if (reader.isStartElement()) {
          processNamespaces(reader, sequence);
          processStartElement(reader, sequence, tokenFactory, startElements);
          processAttributes(reader, sequence, this.config.isNamespaceAware());
        } else if (reader.isEndElement()) {
          processEndElement(reader, sequence, tokenFactory, startElements);
        } else if (reader.isCharacters()) {
          processText(reader, sequence, tokenizer);
        } else {
          processOther(reader, sequence);
        }
      }
    } catch (XMLStreamException ex) {
      throw new LoadingException(ex);
    }
    return sequence;
  }

  protected static XMLInputFactory toFactory(DiffXConfig config) {
    XMLInputFactory factory = XMLInputFactory.newInstance();
    factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
    factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.TRUE);
    factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, config.isNamespaceAware()? Boolean.TRUE : Boolean.FALSE);
    return factory;
  }

  private static void processStartElement(XMLStreamReader stream, Sequence sequence, TokenFactory factory, List<StartElementToken> startElements) {
    assert stream.isStartElement();
    QName name = stream.getName();
    StartElementToken startElement = factory.newStartElement(name.getNamespaceURI(), name.getLocalPart());
    sequence.addToken(startElement);
    startElements.add(startElement);
  }

  private static void processNamespaces(XMLStreamReader stream, Sequence sequence) {
    assert stream.isStartElement();
    int namespaceCount = stream.getNamespaceCount();
    if (namespaceCount > 0) {
      for (int i=0; i < namespaceCount; i++) {
        String namespaceURI = stream.getNamespaceURI(i);
        if (namespaceURI == null) namespaceURI = XMLConstants.NULL_NS_URI;
        String prefix = stream.getNamespacePrefix(i);
        if (prefix == null) prefix = XMLConstants.DEFAULT_NS_PREFIX;
        sequence.addNamespace(namespaceURI, prefix);
      }
    }
  }

  private static void processAttributes(XMLStreamReader stream, Sequence sequence, boolean namespaceAware) {
    assert stream.isStartElement();
    // Add attributes immediately after
    int attributeCount = stream.getAttributeCount();
    if (attributeCount > 0) {
      AttributeToken[] attributes = new AttributeToken[attributeCount];
      for (int i=0; i< attributeCount; i++) {
        attributes[i] = toAttribute(stream, i, namespaceAware);
      }
      Arrays.sort(attributes, new AttributeComparator());
      for (AttributeToken token : attributes) {
        sequence.addToken(token);
      }
    }
  }

  private static void processEndElement(XMLStreamReader stream, Sequence sequence, TokenFactory factory, List<StartElementToken> startElements) {
    assert stream.isEndElement();
    StartElementToken startElement = startElements.remove(startElements.size() - 1);
    EndElementToken endElement = factory.newEndElement(startElement);
    sequence.addToken(endElement);
  }

  private static void processText(XMLStreamReader stream, Sequence sequence, TextTokenizer tokenizer) {
    assert stream.isCharacters();
    if (stream.isWhiteSpace()) {
      sequence.addToken(new SpaceToken(stream.getText()));
    } else {
      sequence.addTokens(tokenizer.tokenize(stream.getText()));
    }
  }

  /**
   * Processing instructions and comments.
   */
  private static void processOther(XMLStreamReader stream, Sequence sequence) {
    if (stream.getEventType() == PROCESSING_INSTRUCTION) {
      Token token = new ProcessingInstructionToken(stream.getPITarget(), stream.getPIData());
      sequence.addToken(token);
    } else if (stream.getEventType() == COMMENT) {
      CommentToken token = new CommentToken(stream.getText());
      sequence.addToken(token);
    }
  }

  private static AttributeToken toAttribute(XMLStreamReader stream, int i, boolean namespaceAware) {
    String localName = stream.getAttributeLocalName(i);
    String value = stream.getAttributeValue(i);
    if (namespaceAware) {
      String namespaceURI = stream.getAttributeNamespace(i);
      if (namespaceURI == null) namespaceURI = XMLConstants.NULL_NS_URI;
      return new AttributeTokenNSImpl(namespaceURI, localName, value);
    }
    String prefix = stream.getAttributePrefix(i);
    if (prefix.isEmpty()) {
      return new AttributeTokenImpl(localName, value);
    }
    return new AttributeTokenImpl(prefix+":"+localName, value);
  }

  private static XMLStreamReader toXMLStreamReader(XMLInputFactory factory, InputSource source)
      throws XMLStreamException, LoadingException {
    if (source.getByteStream() != null) {
      String encoding = Objects.toString(source.getEncoding(), "utf-8");
      return factory.createXMLStreamReader(source.getByteStream(), encoding);
    }
    if (source.getCharacterStream() != null) {
      return factory.createXMLStreamReader(source.getSystemId(), source.getCharacterStream());
    }
    throw new LoadingException("Invalid InputSource");
  }
}
