/*
 * Copyright 2010-2021 Allette Systems (Australia)
 *    http://www.allette.com.au
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
package org.pageseeder.diffx.test;


import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Random;

public class RandomXMLFactory {

  private static final List<String> BLOCK_ELEMENT_NAMES = List.of("p", "div", "h1", "h2", "h3");

  private static final List<String> INLINE_ELEMENT_NAMES = List.of("b", "i", "u", "span");

  private static final List<String> ATTRIBUTE_NAMES = List.of("id", "name", "title", "dir", "hidden", "is");

  private final Random random = new Random();

  private final RandomStringFactory stringFactory = new RandomStringFactory();

  public static final void prettyPrint(Document xml) throws TransformerException {
    Transformer tf = TransformerFactory.newInstance().newTransformer();
    tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    tf.setOutputProperty(OutputKeys.INDENT, "yes");
    Writer out = new StringWriter();
    tf.transform(new DOMSource(xml), new StreamResult(out));
    System.out.println(out);
  }

  public static void main(String[] args) throws Exception {
    RandomXMLFactory f = new RandomXMLFactory();
    Document docA = f.getRandomXML(5, 5);
    prettyPrint(docA);
    Document docB = f.vary(docA, .5);
    prettyPrint(docB);
  }

  public Document getRandomXML(int maxDepth, int maxBreadth) {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.newDocument();
      Element element = doc.createElement("root");
      doc.appendChild(element);
      if (maxDepth > 0) {
        attachAttributes(doc, element, 4);
        attachChildren(doc, element, maxDepth - 1, maxBreadth);
      }
      return doc;
    } catch (ParserConfigurationException ex) {
      throw new IllegalStateException(ex);
    }
  }

  public Document vary(Document source, double changes) {
    Document variation = (Document) source.cloneNode(true);
    Element element = variation.getDocumentElement();
    vary(variation, element, changes);
    return variation;
  }

  public Document vary(Document doc, Element element, double changes) {
    NamedNodeMap attributes = element.getAttributes();
    for (int i = 0; i < attributes.getLength(); i++) {
      double next = this.random.nextDouble();
      if (next < changes) {
        Attr attr = (Attr) attributes.item(i);
        int op = this.random.nextInt(3);
        if (op == 0 || op == 1) {
          attributes.removeNamedItem(attr.getName());
        }
        if (op == 1 || op == 2) {
          Attr newAttr = nextAttribute(doc);
          attributes.setNamedItem(newAttr);
        }
      }
    }
    NodeList children = element.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      double next = this.random.nextDouble();
      if (next < changes) {
        Node node = children.item(i);
        int op = this.random.nextInt(3);
        if (op == 0) {
          element.removeChild(node);
        }
        if (op == 1) {
          if (node.getNodeType() == Node.ELEMENT_NODE) {
            vary(doc, (Element) node, changes);
          } else if (node.getNodeType() == Node.TEXT_NODE) {
            String newText = this.stringFactory.vary(node.getTextContent(), changes);
            Text newTextNode = doc.createTextNode(newText);
            element.insertBefore(newTextNode, node);
          }
        }
      }
    }
    return doc;
  }

  private void attachChildren(Document doc, Element element, int maxDepth, int maxBreadth) {
    if (this.random.nextBoolean()) {
      attachBlocks(doc, element, maxDepth - 1, maxBreadth);
    } else {
      attachInline(doc, element, maxDepth - 1, maxBreadth);
    }
  }

  private void attachBlocks(Document doc, Element element, int maxDepth, int maxBreadth) {
    int count = 0;
    if (maxDepth > 0 && count < maxBreadth) {
      while (this.random.nextInt(100) < 80) {
        String name = nextBlockElement();
        Element child = doc.createElement(name);
        attachAttributes(doc, child, 5);
        attachChildren(doc, element, maxDepth, maxBreadth);
        element.appendChild(child);
        count++;
      }
    } else if (this.random.nextBoolean()) {
      Text text = nextTextNode(doc, 100);
      element.appendChild(text);
    }
  }

  private void attachInline(Document doc, Element element, int maxDepth, int maxBreadth) {
    int count = 0;
    while (this.random.nextInt(100) < 80 && count < maxBreadth) {
      if (this.random.nextBoolean() && maxDepth > 0) {
        String name = nextInlineElement();
        Element inline = doc.createElement(name);
        attachAttributes(doc, inline, 1);
        attachInline(doc, inline, maxDepth - 1, maxBreadth);
        element.appendChild(inline);
      } else {
        Text text = nextTextNode(doc, 100);
        element.appendChild(text);
      }
      count++;
    }
  }

  private void attachAttributes(Document doc, Element element, int max) {
    int count = 0;
    while (this.random.nextBoolean() && count < max) {
      Attr attr = nextAttribute(doc);
      if (!element.hasAttribute(attr.getName())) {
        element.setAttributeNode(attr);
        count++;
      }
    }
  }

  private Attr nextAttribute(Document doc) {
    Attr attribute = doc.createAttribute(nextAttributeName());
    attribute.setValue(nextAttributeValue());
    return attribute;
  }

  private Text nextTextNode(Document doc, int maxLength) {
    String content = stringFactory.getRandomString(random.nextInt(maxLength), true);
    return doc.createTextNode(content);
  }

  private String nextAttributeName() {
    return ATTRIBUTE_NAMES.get(random.nextInt(ATTRIBUTE_NAMES.size()));
  }

  private String nextAttributeValue() {
    switch (random.nextInt(3)) {
      case 1:
        return Boolean.toString(this.random.nextBoolean());
      case 2:
        return Integer.toString(this.random.nextInt(Short.MAX_VALUE));
      default:
        return this.stringFactory.getRandomString(random.nextInt(10) + 1, false);
    }
  }

  private String nextBlockElement() {
    return BLOCK_ELEMENT_NAMES.get(random.nextInt(BLOCK_ELEMENT_NAMES.size()));
  }

  private String nextInlineElement() {
    return INLINE_ELEMENT_NAMES.get(random.nextInt(INLINE_ELEMENT_NAMES.size()));
  }

}
