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

import org.w3c.dom.Document;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.io.Writer;

public final class DOMUtils {

  private DOMUtils() {
  }

  public static String toString(Document xml, boolean indent) {
    try {
      Transformer tf = TransformerFactory.newInstance().newTransformer();
      tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      tf.setOutputProperty(OutputKeys.INDENT, indent ? "yes" : "no");
      tf.setOutputProperty(OutputKeys.METHOD, "xml");
      Writer out = new StringWriter();
      tf.transform(new DOMSource(xml), new StreamResult(out));
      return out.toString();
    } catch (TransformerException ex) {
      throw new RuntimeException(ex);
    }
  }
}
