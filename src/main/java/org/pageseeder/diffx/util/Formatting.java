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
package org.pageseeder.diffx.util;

import org.pageseeder.diffx.sequence.PrefixMapping;
import org.pageseeder.xmlwriter.XMLWriterNSImpl;

import java.util.Enumeration;

/**
 * The set of constants used in this API.
 *
 * @author Christophe Lauret
 * @version 17 May 2005
 */
public final class Formatting {

  /**
   * Prevent creation of instances
   */
  private Formatting() {
  }

  /**
   * Write the namespace mapping to the XML output
   */
  public static void declareNamespaces(XMLWriterNSImpl xml, PrefixMapping mapping) {
    xml.setPrefixMapping(Constants.BASE_NS_URI, "dfx");
    xml.setPrefixMapping(Constants.DELETE_NS_URI, "del");
    xml.setPrefixMapping(Constants.INSERT_NS_URI, "ins");
    if (mapping != null) {
      for (Enumeration<String> uris = mapping.getURIs(); uris.hasMoreElements();) {
        String uri = uris.nextElement();
        xml.setPrefixMapping(uri, mapping.getPrefix(uri));
      }
    }
  }
}
