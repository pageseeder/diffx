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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.pageseeder.diffx.config.DiffXConfig;
import org.pageseeder.diffx.event.AttributeEvent;
import org.pageseeder.diffx.event.CloseElementEvent;
import org.pageseeder.diffx.event.OpenElementEvent;
import org.pageseeder.diffx.event.TextEvent;
import org.pageseeder.diffx.event.impl.EventFactory;
import org.pageseeder.diffx.event.impl.ProcessingInstructionEvent;
import org.pageseeder.diffx.load.text.TextTokenizer;
import org.pageseeder.diffx.load.text.TokenizerFactory;
import org.pageseeder.diffx.sequence.EventSequence;
import org.pageseeder.diffx.sequence.PrefixMapping;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

/**
 * Loads a DOM documents as a sequence of events.
 *
 * <p>This class implements the methods {@link Recorder#process(File)} and
 * {@link Recorder#process(String)} for convenience, but is it much more efficient
 * to feed this recorder directly with a DOM.
 *
 * <p>This class is not synchronised.
 *
 * @author Christophe Lauret
 *
 * @version 0.9.0
 * @since 0.7
 */
public final class DOMRecorder implements XMLRecorder {

  // class attributes -------------------------------------------------------------------------------

  /**
   * The DiffX configuration to use
   */
  private DiffXConfig config = new DiffXConfig();

  // state variables --------------------------------------------------------------------------------

  /**
   * The factory that will produce events according to the configuration.
   */
  private transient EventFactory efactory;

  /**
   * The text tokenizer used by this recorder.
   */
  private transient TextTokenizer tokenizer;

  /**
   * The sequence of event for this recorder.
   */
  private transient EventSequence sequence;

  /**
   * The sequence of event for this recorder.
   */
  private transient PrefixMapping mapping;

  /**
   * The weight of the current element.
   */
  private transient int currentWeight = -1;

  /**
   * The stack of events' weight, should only contain <code>Integer</code>.
   */
  private transient List<Integer> weights = new ArrayList<Integer>();

  /**
   * Indicates whether the given document is a fragment.
   *
   * <p>An fragment is a portion of XML that is not necessarily well-formed by itself, because the
   * namespace has been declared higher in the hierarchy, in which if the DOM tree was serialised
   * it would not produce well-formed XML.
   *
   * <p>This option indicates that the recorder should try to generate the prefix mapping without
   * the declaration.
   */
  private transient boolean isFragment = true;

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
  public EventSequence process(File file) throws LoadingException, IOException {
    InputStream in = new BufferedInputStream(new FileInputStream(file));
    return process(new InputSource(in));
  }

  @Override
  public EventSequence process(String xml) throws LoadingException {
    return process(new InputSource(new StringReader(xml)));
  }

  /**
   * Runs the recorder on the specified input source.
   *
   * @param is The input source.
   *
   * @return The recorded sequence of events.
   *
   * @throws LoadingException If thrown while parsing.
   */
  @Override
  public EventSequence process(InputSource is) throws LoadingException {
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
   * Processes the given node and returns the corresponding event sequence.
   *
   * @param node The W3C DOM node to be processed.
   *
   * @return The recorded sequence of events.
   *
   * @throws LoadingException If thrown while parsing.
   */
  public EventSequence process(Node node) throws LoadingException {
    // initialise the state variables.
    this.efactory = new EventFactory(this.config.isNamespaceAware());
    this.tokenizer = TokenizerFactory.get(this.config);
    this.sequence = new EventSequence();
    this.mapping = this.sequence.getPrefixMapping();
    // start processing the nodes
    loadNode(node);
    this.isFragment = true;
    return this.sequence;
  }

  /**
   * Processes the given node list and returns the corresponding event sequence.
   *
   * <p>This method only returns the event sequence from the first node in the node list, if the
   * node list is empty, this method returns an empty sequence.
   *
   * @param node The W3C DOM node to be processed.
   *
   * @return The recorded sequence of events.
   *
   * @throws LoadingException If thrown while parsing.
   */
  public EventSequence process(NodeList node) throws LoadingException {
    if (node.getLength() == 0)
      return new EventSequence();
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
    // dispatch to the correct loader performance: order by occurrence
    if (node instanceof Element) {
      load((Element)node);
    }
    if (node instanceof Text) {
      load((Text)node);
    } else if (node instanceof Attr) {
      load((Attr)node);
    } else if (node instanceof Document) {
      load((Document)node);
    } else if (node instanceof ProcessingInstruction) {
      load((ProcessingInstruction)node);
      // all other node types are ignored
    }
  }

  /**
   * Loads the given document in the current sequence.
   *
   * @param document The W3C DOM document node to load.
   *
   * @throws LoadingException If thrown while parsing.
   */
  private void load(Document document) throws LoadingException {
    load(document.getDocumentElement());
  }

  /**
   * Loads the given element in the current sequence.
   *
   * @param element The W3C DOM element node to load.
   *
   * @throws LoadingException If thrown while parsing.
   */
  private void load(Element element) throws LoadingException {
    if (this.currentWeight > 0) {
      this.weights.add(Integer.valueOf(this.currentWeight));
    }
    this.currentWeight = 1;
    // namespace handling
    OpenElementEvent open = null;
    // namespace aware configuration
    if (this.config.isNamespaceAware()) {
      String uri = element.getNamespaceURI() == null? "" : element.getNamespaceURI();
      String name = element.getLocalName();
      handlePrefixMapping(uri, element.getPrefix());
      open = this.efactory.makeOpenElement(uri, name);
      // not namespace aware
    } else {
      open = this.efactory.makeOpenElement(null, element.getNodeName());
    }

    this.sequence.addEvent(open);
    NamedNodeMap atts = element.getAttributes();
    // only 1 attribute, just load it
    if (atts.getLength() == 1) {
      load((Attr)atts.item(0));
      // several attributes sort them in alphabetical order
      // TODO: also use URI
    } else if (atts.getLength() > 1) {
      String[] names = new String[atts.getLength()];
      for (int i = 0; i < atts.getLength(); i++) {
        Attr attr = (Attr)atts.item(i);
        names[i] = attr.getName();
      }
      Arrays.sort(names);
      for (String name : names) {
        load((Attr)atts.getNamedItem(name));
      }
    }
    // load all the child nodes
    NodeList list = element.getChildNodes();
    for (int i = 0; i < list.getLength(); i++) {
      loadNode(list.item(i));
    }
    CloseElementEvent close = this.efactory.makeCloseElement(open);
    this.sequence.addEvent(close);
    // handle the weights
    close.setWeight(this.currentWeight);
    open.setWeight(this.currentWeight);
    this.currentWeight += popWeight();
  }

  /**
   * Loads the given text in the current sequence depending on the configuration.
   *
   * @param text The W3C DOM text node to load.
   *
   * @throws LoadingException If thrown while parsing.
   */
  private void load(Text text) throws LoadingException {
    List<TextEvent> events = this.tokenizer.tokenize(text.getData());
    for (TextEvent e : events) {
      this.sequence.addEvent(e);
    }
    this.currentWeight += events.size();
  }

  /**
   * Loads the given processing instruction in the current sequence.
   *
   * @param pi The W3C DOM PI node to load.
   *
   * @throws LoadingException If thrown while parsing.
   */
  private void load(ProcessingInstruction pi) throws LoadingException {
    this.sequence.addEvent(new ProcessingInstructionEvent(pi.getTarget(), pi.getData()));
    this.currentWeight++;
  }

  /**
   * Returns the last weight and remove it from the stack.
   *
   * @return The weight on top of the stack.
   */
  private int popWeight() {
    if (this.weights.size() > 0)
      return this.weights.remove(this.weights.size() - 1).intValue();
    else
      return 0;
  }

  /**
   * Loads the given attribute in the current sequence.
   *
   * @param attr The W3C DOM attribute node to load.
   */
  private void load(Attr attr) {
    String uri = attr.getNamespaceURI();
    if (uri == null) uri = XMLConstants.NULL_NS_URI;
    handlePrefixMapping(uri, attr.getPrefix());
    load(this.efactory.makeAttribute(uri,
        attr.getLocalName(),
        attr.getNodeName(),
        attr.getValue()));
  }

  /**
   * Loads the given attribute in the current sequence.
   *
   * @param e An attribute event.
   */
  private void load(AttributeEvent e) {
    // a namespace declaration, translate the event into a prefix mapping
    if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(e.getURI())) {
      this.sequence.mapPrefix(e.getValue(), e.getName());

      // a regular attribute
    } else {
      e.setWeight(2);
      this.currentWeight += 2;
      this.sequence.addEvent(e);
    }
  }

  /**
   * Handles the prefix mapping.
   *
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

}
