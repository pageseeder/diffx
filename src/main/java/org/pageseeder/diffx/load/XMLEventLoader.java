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
import org.pageseeder.diffx.load.text.TextTokenizer;
import org.pageseeder.diffx.load.text.TokenizerFactory;
import org.pageseeder.diffx.token.*;
import org.pageseeder.diffx.token.impl.*;
import org.pageseeder.diffx.xml.Sequence;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.*;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static javax.xml.stream.XMLStreamConstants.COMMENT;

/**
 * Loads the XML tokens using an {@link XMLEventReader}.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 * @since 0.9.0
 */
public final class XMLEventLoader extends XMLLoaderBase implements XMLLoader {

  @Override
  public Sequence load(File file) throws LoadingException, IOException {
    XMLInputFactory factory = XMLStreamLoader.toFactory(this.config);
    try (InputStream in = new BufferedInputStream(Files.newInputStream(file.toPath()))) {
      XMLEventReader reader = factory.createXMLEventReader(in);
      return load(reader);
    } catch (XMLStreamException ex) {
      throw new LoadingException(ex);
    }
  }

  @Override
  public Sequence load(String xml) throws LoadingException {
    XMLInputFactory factory = XMLStreamLoader.toFactory(this.config);
    try (StringReader source = new StringReader(xml)) {
      XMLEventReader reader = factory.createXMLEventReader(source);
      return load(reader);
    } catch (XMLStreamException ex) {
      throw new LoadingException(ex);
    }
  }

  @Override
  public Sequence load(InputSource source) throws LoadingException, IOException {
    XMLInputFactory factory = XMLStreamLoader.toFactory(this.config);
    try {
      XMLEventReader reader = toXMLEventReader(factory, source);
      return load(reader);
    } catch (XMLStreamException ex) {
      throw new LoadingException(ex);
    }
  }


  /**
   * Loads an XML sequence from an {@link XMLEventReader}.
   *
   * @param reader The {@code XMLEventReader} providing the XML input to load.
   * @return A {@code Sequence} object representing the loaded XML structure.
   * @throws LoadingException If an error occurs while processing the XML events.
   */
  public Sequence load(XMLEventReader reader) throws LoadingException {
    XMLTokenFactory tokenFactory = new XMLTokenFactory(this.config.isNamespaceAware());
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
      Namespace namespace = (Namespace) ns.next();
      sequence.addNamespace(namespace.getNamespaceURI(), namespace.getPrefix());
    }
  }

  private static void processStartElement(StartElement event, Sequence sequence, XMLTokenFactory factory, List<StartElementToken> startElements) {
    QName name = event.getName();
    StartElementToken startElement = factory.newStartElement(name.getNamespaceURI(), name.getLocalPart());
    sequence.addToken(startElement);
    startElements.add(startElement);
  }

  private static void processAttributes(StartElement event, Sequence sequence, boolean namespaceAware, AttributeComparator comparator) {
    // `getAttributes` must return `Attribute` instances by contract
    List<AttributeToken> attributes = null;
    for (Iterator<?> it = event.getAttributes(); it.hasNext(); ) {
      Attribute attribute = (Attribute) it.next();
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

  private static void processEndElement(EndElement event, Sequence sequence, XMLTokenFactory factory, List<StartElementToken> startElements) {
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
      ProcessingInstruction instruction = (ProcessingInstruction) event;
      XMLToken token = new XMLProcessingInstruction(instruction.getTarget(), instruction.getData());
      sequence.addToken(token);
    } else if (event.getEventType() == COMMENT) {
      XMLComment token = new XMLComment(((Comment) event).getText());
      sequence.addToken(token);
    }
  }

  private static AttributeToken toAttribute(Attribute attribute, boolean namespaceAware) {
    QName name = attribute.getName();
    if (namespaceAware)
      return new XMLAttribute(name.getNamespaceURI(), name.getLocalPart(), attribute.getValue());
    if (name.getPrefix().isEmpty())
      return new XMLAttribute(name.getLocalPart(), attribute.getValue());
    return new XMLAttribute(name.getPrefix() + ":" + name.getLocalPart(), attribute.getValue());
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
