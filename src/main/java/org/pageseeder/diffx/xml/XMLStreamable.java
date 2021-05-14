/*
 * Copyright (c) 2010-2021 Allette Systems (Australia)
 *    http://www.allette.com.au
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pageseeder.diffx.xml;

import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Objects implementing this class have an XML representation that can be written with
 * an <code>XMLStreamWriter</code>.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public interface XMLStreamable {

  /**
   * Write an XML representation of this object using the specified writer.
   *
   * @param xml The writer
   *
   * @throws XMLStreamException If thrown by XMLStreamWriter
   */
  void toXML(@NotNull XMLStreamWriter xml) throws XMLStreamException;

}
