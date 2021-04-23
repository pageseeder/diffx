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
import org.pageseeder.diffx.token.impl.CommentToken;
import org.pageseeder.diffx.token.impl.ProcessingInstructionToken;
import org.pageseeder.diffx.token.impl.XMLAttribute;
import org.pageseeder.diffx.xml.PrefixMapping;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads a DOM documents as a sequence of tokens.
 *
 * <p>This class implements the methods {@link Recorder#process(File)} and
 * {@link Recorder#process(String)} for convenience, but is it much more efficient
 * to feed this recorder directly with a DOM.
 *
 * <p>This class is not synchronised.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 * @since 0.7
 */
public final class DOMRecorder implements XMLRecorder {

  /**
   * The DiffX configuration to use
   */
  private DiffXConfig config = new DiffXConfig();

  // state variables --------------------------------------------------------------------------------

  /**
   * The factory that will produce tokens according to the configuration.
   */
  private TokenFactory tokenFactory;

  /**
   * The text tokenizer used by this recorder.
   */
  private TextTokenizer tokenizer;

  /**
   * The sequence of token for this recorder.
   */
  private Sequence sequence;

  /**
   * The sequence of token for this recorder.
   */
  private PrefixMapping mapping;

  /**
   * Indicates whether the given document is a fragment.
   *
   * <p>A fragment is a portion of XML that is not necessarily well-formed by itself, because the
   * namespace has been declared higher in the hierarchy, in which if the DOM tree was serialised
   * it would not produce well-formed XML.
   *
   * <p>This option indicates that the recorder should try to generate the prefix mapping without
   * the declaration.
   */
  private boolean isFragment = true;

  // methods ----------------------------------------------------------------------------------------

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

  @Override
  public Sequence process(File file) throws LoadingException, IOException {
    try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
      return process(new InputSource(in));
    }
  }

  @Override
  public Sequence process(String xml) throws LoadingException {
    return process(new InputSource(new StringReader(xml)));
  }

  /**
   * Runs the recorder on the specified input source.
   *
   * @param is The input source.
   *
   * @return The recorded sequence of tokens.
   * @throws LoadingException If thrown while parsing.
   */
  @Override
  public Sequence process(InputSource is) throws LoadingException {
    this.isFragment = false; // input source is not a fragment
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    dbFactory.setNamespaceAware(this.config.isNamespaceAware());
    dbFactory.setExpandEntityReferences(true);
    dbFactory.setValidating(false);
    try {
      DocumentBuilder builder = dbFactory.newDocumentBuilder();
      Document document = builder.parse(is);
      return this.process(document);
    } catch (Exception ex) {
      throw new LoadingException(ex);
    }
  }

  /**
   * Processes the given node and returns the corresponding token sequence.
   *
   * @param node The W3C DOM node to be processed.
   *
   * @return The recorded sequence of tokens.
   * @throws LoadingException If thrown while parsing.
   */
  public Sequence process(Node node) throws LoadingException {
    // initialise the state variables.
    this.tokenFactory = new TokenFactory(this.config.isNamespaceAware());
    this.tokenizer = TokenizerFactory.get(this.config);
    this.sequence = new Sequence();
    this.mapping = this.sequence.getPrefixMapping();
    // start processing the nodes
    loadNode(node);
    this.isFragment = true;
    return this.sequence;
  }

  /**
   * Processes the given node list and returns the corresponding token sequence.
   *
   * <p>This method only returns the token sequence from the first node in the node list, if the
   * node list is empty, this method returns an empty sequence.
   *
   * @param node The W3C DOM node to be processed.
   *
   * @return The recorded sequence of tokens.
   * @throws LoadingException If thrown while parsing.
   */
  public Sequence process(NodeList node) throws LoadingException {
    if (node.getLength() == 0)
      return new Sequence();
    return process(node.item(0));
  }

  // specific loaders ---------------------------------------------------------------------

  /**
   * Loads the given node in the current sequence.
   *
   * @param node The W3C DOM node to load.
   *
   * @throws LoadingException If thrown while parsing.
   */
  private void loadNode(Node node) throws LoadingException {
    // dispatch to the correct loader performance: order by frequency of occurrence
    if (node instanceof Element) {
      loadElement((Element) node);
    } else if (node instanceof Text) {
      loadText((Text) node);
    } else if (node instanceof Document) {
      loadDocument((Document) node);
    } else if (node instanceof ProcessingInstruction) {
      loadPI((ProcessingInstruction) node);
    } else if (node instanceof Comment) {
      loadComment((Comment) node);
    }
    // all other node types are ignored (attributes loaded as part of Element)
  }

  /**
   * Loads the given document in the current sequence.
   *
   * @param document The W3C DOM document node to load.
   *
   * @throws LoadingException If thrown while parsing.
   */
  private void loadDocument(Document document) throws LoadingException {
    loadElement(document.getDocumentElement());
  }

  /**
   * Loads the given element in the current sequence.
   *
   * @param element The W3C DOM element node to load.
   *
   * @throws LoadingException If thrown while parsing.
   */
  private void loadElement(Element element) throws LoadingException {
    StartElementToken start = toStartElement(element);
    this.sequence.addToken(start);
    loadAttributes(element);
    NodeList list = element.getChildNodes();
    for (int i = 0; i < list.getLength(); i++) {
      loadNode(list.item(i));
    }
    EndElementToken close = this.tokenFactory.newEndElement(start);
    this.sequence.addToken(close);
  }

  /**
   * Loads the given text in the current sequence depending on the configuration.
   *
   * @param text The W3C DOM text node to load.
   */
  private void loadText(Text text) {
    List<TextToken> tokens = this.tokenizer.tokenize(text.getData());
    for (TextToken token : tokens) {
      this.sequence.addToken(token);
    }
  }

  /**
   * Loads the given processing instruction in the current sequence.
   *
   * @param pi The W3C DOM PI node to load.
   */
  private void loadPI(ProcessingInstruction pi) {
    this.sequence.addToken(new ProcessingInstructionToken(pi.getTarget(), pi.getData()));
  }

  /**
   * Add the comment attribute in the current sequence.
   *
   * @param comment The W3C DOM comment node to load.
   */
  private void loadComment(Comment comment) {
    this.sequence.addToken(new CommentToken(comment.getTextContent()));
  }

  /**
   * Handles the prefix mapping.
   * <p>
   * If the current process is working on a fragment,
   *
   * @param uri    The namespace URI.
   * @param prefix The prefix used for the namespace.
   */
  private void handlePrefixMapping(String uri, String prefix) {
    if (this.isFragment) {
      if (this.mapping.getPrefix(uri) != null) return;
      if (prefix == null && !"".equals(uri)) {
        this.mapping.add(uri, "");
      } else if (prefix != null && !"xmlns".equals(prefix)) {
        this.mapping.add(uri, prefix);
      }
    }
  }

  private StartElementToken toStartElement(Element element) {
    if (this.config.isNamespaceAware()) {
      String uri = element.getNamespaceURI() != null ? uri = element.getNamespaceURI() : XMLConstants.NULL_NS_URI;
      handlePrefixMapping(uri, element.getPrefix());
      return this.tokenFactory.newStartElement(uri, element.getLocalName());
    }
    return this.tokenFactory.newStartElement(XMLConstants.NULL_NS_URI, element.getNodeName());
  }

  private void loadAttributes(Element element) {
    NamedNodeMap attributes = element.getAttributes();
    // only 1 attribute, just load it
    if (attributes.getLength() == 1) {
      AttributeToken token = toAttribute((Attr) attributes.item(0));
      if (token != null) {
        this.sequence.addToken(token);
      }
    } else if (attributes.getLength() > 1) {
      List<AttributeToken> tokens = new ArrayList<>();
      for (int i = 0; i < attributes.getLength(); i++) {
        AttributeToken token = toAttribute((Attr) attributes.item(i));
        if (token != null) {
          tokens.add(token);
        }
      }
      tokens.sort(new AttributeComparator());
      this.sequence.addTokens(tokens);
    }
  }

  /**
   * Loads the given attribute in the current sequence.
   *
   * @param attr The W3C DOM attribute node to load.
   */
  private AttributeToken toAttribute(Attr attr) {
    String uri = attr.getNamespaceURI();
    if (uri == null) uri = XMLConstants.NULL_NS_URI;
    handlePrefixMapping(uri, attr.getPrefix());
    // a namespace declaration, translate the token into a prefix mapping
    if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(uri)) {
      // FIXME Handle default namespace declaration on root element
      this.sequence.addNamespace(attr.getValue(), attr.getLocalName());
      return null;
    } else {
      if (this.config.isNamespaceAware()) {
        return this.tokenFactory.newAttribute(uri, attr.getLocalName(), attr.getValue());
      }
      return this.tokenFactory.newAttribute(attr.getNodeName(), attr.getValue());
    }
  }

}
