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
import java.util.List;
import java.util.Random;

public class RandomHTMLFactory {

  private final Random random;

  private final DocumentBuilder builder;

  private static final List<String> BLOCK_ELEMENT_NAMES = List.of("p", "div", "h1", "h2", "h3", "table", "ol", "ul");

  private static final List<String> INLINE_ELEMENT_NAMES = List.of("b", "i", "u", "span");

  private static final List<String> ATTRIBUTE_NAMES = List.of("id", "name", "title", "dir", "hidden", "class");

  private final RandomStringFactory stringFactory = new RandomStringFactory();

  private Document doc;

  public RandomHTMLFactory() {
    this.random = new Random();
    this.builder = newBuilder();
  }

  private static DocumentBuilder newBuilder() {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setCoalescing(true);
      factory.setNamespaceAware(true);
      return factory.newDocumentBuilder();
    } catch (ParserConfigurationException ex) {
      throw new IllegalStateException(ex);
    }
  }

  public static void main(String[] args) throws Exception {
    RandomHTMLFactory f = new RandomHTMLFactory();
    Document htmlA = f.nextDocument();
    System.out.println(DOMUtils.toString(htmlA, true));
    Document htmlB = f.vary(htmlA, .1);
    System.out.println(DOMUtils.toString(htmlB, true));

  }

  public Document nextDocument() {
    this.doc = builder.newDocument();
    Element html = this.doc.createElement("html");
    this.doc.appendChild(html);
    if (this.random.nextBoolean()) {
      Element head = nextHead();
      html.appendChild(head);
    }
    Element body = nextBody();
    html.appendChild(body);
    return this.doc;
  }

  public Attr nextAttribute(String element) {
    String name = nextAttributeName();
    Attr attribute = doc.createAttribute(name);
    attribute.setValue(nextAttributeValue(name));
    return attribute;
  }

  public Element nextElement(String parentName, int depth) {
    String name = nextElementName(parentName);
    switch (name) {
      case "table":
        return nextTable(random.nextInt(10), random.nextInt(10));
      case "ul":
      case "ol":
        return nextList(name);
      default:
        Element element = this.doc.createElement(name);
        attachAttributes(element, 3);
        attachChildren(element, depth - 1, 10);
        return element;
    }
  }

  public Text nextText(String elementName) {
    String content = nextTextContent(elementName);
    return content != null ? doc.createTextNode(content) : null;
  }

  public String nextTextContent(String elementName) {
    switch (elementName) {
      case "div":
        return null;
      case "td":
        if (random.nextBoolean())
          return stringFactory.getRandomString(random.nextInt(16) + 2, true);
        else
          return Integer.toString(random.nextInt(512));
      case "p":
        return stringFactory.getRandomString(random.nextInt(256), true);
      case "i":
      case "u":
      case "b":
      case "span":
        return stringFactory.getRandomString(random.nextInt(16), true);
      default:
        return stringFactory.getRandomString(random.nextInt(100), true);
    }
  }

  public Element nextTable(int cols, int rows) {
    Element table = this.doc.createElement("table");
    // Thead
    if (random.nextBoolean()) {
      Element thead = this.doc.createElement("thead");
      Element tr = this.doc.createElement("tr");
      for (int i = 0; i < cols; i++) {
        Element th = this.doc.createElement("th");
        Text text = nextText("th");
        th.appendChild(text);
        tr.appendChild(th);
      }
      thead.appendChild(tr);
      table.appendChild(thead);
    }
    // Thead
    if (random.nextBoolean()) {
      Element thead = this.doc.createElement("tfoot");
      Element tr = this.doc.createElement("tr");
      for (int i = 0; i < cols; i++) {
        Element th = this.doc.createElement("td");
        Text text = nextText("td");
        th.appendChild(text);
        tr.appendChild(th);
      }
      thead.appendChild(tr);
      table.appendChild(thead);
    }
    Element tbody = this.doc.createElement("tbody");
    for (int j = 0; j < rows; j++) {
      Element tr = this.doc.createElement("tr");
      for (int i = 0; i < cols; i++) {
        Element td = this.doc.createElement("td");
        Text text = nextText("td");
        td.appendChild(text);
        tr.appendChild(td);
      }
      tbody.appendChild(tr);
    }
    table.appendChild(tbody);
    return table;
  }

  public Element nextHead() {
    Element head = this.doc.createElement("head");
    if (this.random.nextBoolean()) {
      Element title = nextTitle();
      head.appendChild(title);
    }
    while (this.random.nextInt(3) < 2) {
      Element meta = nextMeta();
      head.appendChild(meta);
    }
    return head;
  }

  public Element nextTitle() {
    Element title = this.doc.createElement("title");
    Text text = nextText("title");
    title.appendChild(text);
    return title;
  }

  public Element nextMeta() {
    Element meta = this.doc.createElement("meta");
    meta.setAttribute("name", stringFactory.getRandomString(random.nextInt(16) + 1, true));
    meta.setAttribute("content", stringFactory.getRandomString(random.nextInt(32) + 1, true));
    return meta;
  }

  public Element nextList(String name) {
    Element list = this.doc.createElement(name);
    int itemCount = random.nextInt(10);
    for (int i = 0; i < itemCount; i++) {
      Element li = this.doc.createElement("li");
      Text text = nextText("li");
      li.appendChild(text);
      list.appendChild(li);
    }
    return list;
  }

  public Element nextBody() {
    Element body = this.doc.createElement("body");
    int itemCount = random.nextInt(20);
    for (int i = 0; i < itemCount; i++) {
      Element element = nextElement("body", 4);
      body.appendChild(element);
    }
    return body;
  }

  private String nextElementName(String parentName) {
    switch (parentName) {
      case "body":
      case "div":
        return this.nextBlockElement();
      default:
        return this.nextInlineElement();
    }
  }

  private String nextAttributeName() {
    return ATTRIBUTE_NAMES.get(random.nextInt(ATTRIBUTE_NAMES.size()));
  }

  private String nextAttributeValue(String name) {
    switch (name) {
      case "hidden":
        return Boolean.toString(this.random.nextBoolean());
      case "id":
        return Integer.toString(this.random.nextInt(Short.MAX_VALUE));
      case "title":
        return this.stringFactory.getRandomString(random.nextInt(25) + 1, true);
      case "dir":
        return random.nextBoolean() ? "rtl" : "ltr";
      case "class":
        return this.stringFactory.getRandomString(random.nextInt(32) + 1, true);
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

  final void attachAttributes(Element element, int max) {
    int count = 0;
    while (this.random.nextBoolean() && count < max) {
      Attr attr = nextAttribute(element.getTagName());
      if (!element.hasAttribute(attr.getName())) {
        element.setAttributeNode(attr);
        count++;
      }
    }
  }

  final void attachChildren(Element element, int depth, int maxChildren) {
    int count = 0;
    do {
      if (this.random.nextBoolean() && depth > 0) {
        Element child = nextElement(element.getTagName(), depth);
        if (child != null) {
          attachAttributes(child, 3);
          attachChildren(child, depth - 1, maxChildren);
          element.appendChild(child);
          count++;
        }
      } else {
        Text text = nextText(element.getTagName());
        if (text != null) {
          element.appendChild(text);
          count++;
        }
      }
    } while (this.random.nextInt(100) < 80 && count < maxChildren);
  }

  public Document vary(Document source, double changes) {
    this.doc = (Document) source.cloneNode(true);
    Element element = this.doc.getDocumentElement();
    vary(element, changes);
    return this.doc;
  }

  private void vary(Element element, double changes) {
    varyAttributes(element, changes);
    varyChildren(element, changes);
  }

  private void varyAttributes(Element element, double changes) {
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
          Attr newAttr = nextAttribute(element.getTagName());
          attributes.setNamedItem(newAttr);
        }
      }
    }
  }

  private void varyChildren(Element element, double changes) {
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
            vary((Element) node, changes);
          } else if (node.getNodeType() == Node.TEXT_NODE) {
            String newText = this.stringFactory.vary(node.getTextContent(), changes);
            Text newTextNode = doc.createTextNode(newText);
            element.insertBefore(newTextNode, node);
          }
        }
      }
    }
  }

}