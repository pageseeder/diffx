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
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.*;
import java.io.*;
import java.util.*;

import static javax.xml.stream.XMLStreamConstants.*;

/**
 * Records the XML events in an {@link Sequence}.
 *
 * @author Christophe Lauret
 * @author Jean-Baptiste Reure
 * @version 0.9.0
 * @since 0.6.0
 */
public final class XMLEventRecorder implements XMLRecorder {

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
    XMLInputFactory factory = XMLStreamRecorder.toFactory(this.config);
    try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
      XMLEventReader reader = factory.createXMLEventReader(in);
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
    XMLInputFactory factory = XMLStreamRecorder.toFactory(this.config);
    try (StringReader source = new StringReader(xml)) {
      XMLEventReader reader = factory.createXMLEventReader(source);
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
    XMLInputFactory factory = XMLStreamRecorder.toFactory(this.config);
    try {
      XMLEventReader reader = toXMLEventReader(factory, source);
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

  public Sequence process(XMLEventReader reader) throws LoadingException {
    TokenFactory tokenFactory = new TokenFactory(this.config.isNamespaceAware());
    AttributeComparator comparator = new AttributeComparator();
    TextTokenizer tokenizer = TokenizerFactory.get(this.config);
    List<StartElementToken> startElements = new ArrayList<>();
    Sequence sequence = new Sequence();
    sequence.addNamespace(XMLConstants.XML_NS_URI, XMLConstants.XML_NS_PREFIX);
    sequence.addNamespace(XMLConstants.NULL_NS_URI, XMLConstants.DEFAULT_NS_PREFIX);
    try {
      while (reader.hasNext()) {
        XMLEvent event = reader.nextEvent();
        if (event.isStartElement()) {
          processNamespaces(event.asStartElement(), sequence);
          processStartElement(event.asStartElement(), sequence, tokenFactory, startElements);
          processAttributes(event.asStartElement(), sequence, this.config.isNamespaceAware(), comparator);
        } else if (event.isEndElement()) {
          processEndElement(event.asEndElement(), sequence, tokenFactory, startElements);
        } else if (event.isCharacters()) {
          processText(event.asCharacters(), sequence, tokenizer);
        } else {
          processOther(event, sequence);
        }
      }
    } catch (XMLStreamException ex) {
      throw new LoadingException(ex);
    }
    return sequence;
  }

  private static void processNamespaces(StartElement event, Sequence sequence) {
    // `getNamespaces` must return `Namespaces` instances by contract
    for (Iterator<?> ns = event.getNamespaces(); ns.hasNext(); ) {
      Namespace namespace = (Namespace)ns.next();
      sequence.addNamespace(namespace.getNamespaceURI(), namespace.getPrefix());
    }
  }

  private static void processStartElement(StartElement event, Sequence sequence, TokenFactory factory, List<StartElementToken> startElements) {
    QName name = event.getName();
    StartElementToken startElement = factory.newStartElement(name.getNamespaceURI(), name.getLocalPart());
    sequence.addToken(startElement);
    startElements.add(startElement);
  }

  private static void processAttributes(StartElement event, Sequence sequence, boolean namespaceAware, AttributeComparator comparator) {
    // `getAttributes` must return `Attribute` instances by contract
    List<AttributeToken> attributes = null;
    for (Iterator<?> it = event.getAttributes(); it.hasNext(); ) {
      Attribute attribute = (Attribute)it.next();
      if (attributes == null) attributes = new ArrayList<>();
      attributes.add(toAttribute(attribute, namespaceAware));
    }
    if (attributes != null) {
      if (attributes.size() > 1) {
        attributes.sort(comparator);
      }
      for (AttributeToken token : attributes) {
        sequence.addToken(token);
      }
    }
  }

  private static void processEndElement(EndElement event, Sequence sequence, TokenFactory factory, List<StartElementToken> startElements) {
    StartElementToken startElement = startElements.remove(startElements.size() - 1);
    EndElementToken endElement = factory.newEndElement(startElement);
    sequence.addToken(endElement);
  }

  private static void processText(Characters event, Sequence sequence, TextTokenizer tokenizer) {
    if (event.isIgnorableWhiteSpace()) {
      sequence.addToken(new IgnorableSpaceToken(event.getData()));
    } else if (event.isWhiteSpace()) {
      sequence.addToken(new SpaceToken(event.getData()));
    } else {
      sequence.addTokens(tokenizer.tokenize(event.getData()));
    }
  }

  /**
   * Processing instructions and comments.
   */
  private static void processOther(XMLEvent event, Sequence sequence) {
    if (event.isProcessingInstruction()) {
      ProcessingInstruction instruction = (ProcessingInstruction)event;
      Token token = new ProcessingInstructionToken(instruction.getTarget(), instruction.getData());
      sequence.addToken(token);
    } else if (event.getEventType() == COMMENT) {
      CommentToken token = new CommentToken(((Comment)event).getText());
      sequence.addToken(token);
    }
  }

  private static AttributeToken toAttribute(Attribute attribute, boolean namespaceAware) {
    QName name = attribute.getName();
    if (namespaceAware)
      return new XMLAttribute(name.getNamespaceURI(), name.getLocalPart(), attribute.getValue());
    if (name.getPrefix().isEmpty())
      return new XMLAttribute(name.getLocalPart(), attribute.getValue());
    return new XMLAttribute(name.getPrefix()+":"+name.getLocalPart(), attribute.getValue());
  }

  private static XMLEventReader toXMLEventReader(XMLInputFactory factory, InputSource source)
      throws XMLStreamException, LoadingException {
    if (source.getByteStream() != null) {
      String encoding = Objects.toString(source.getEncoding(), "utf-8");
      return factory.createXMLEventReader(source.getByteStream(), encoding);
    }
    if (source.getCharacterStream() != null) {
      return factory.createXMLEventReader(source.getSystemId(), source.getCharacterStream());
    }
    throw new LoadingException("Invalid InputSource");
  }
}
