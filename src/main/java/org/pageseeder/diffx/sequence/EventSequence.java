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
package org.pageseeder.diffx.sequence;

import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.xml.Sequence;

/**
 * @deprecated Provided for backward compatibility, use {@link Sequence} instead.
 */
@Deprecated
public class EventSequence {

  final Sequence sequence;

  public EventSequence(Sequence sequence) {
    this.sequence = sequence;
  }

  public XMLToken getToken(int i) {
    return this.sequence.getToken(i);
  }

  public int size() {
    return this.sequence.size();
  }

  public Sequence getSequence() {
    return sequence;
  }

  public void mapPrefix(String uri, String prefix) {
    this.sequence.addNamespace(uri, prefix);
  }

  public PrefixMapping getPrefixMapping() {
    return new PrefixMapping(this.sequence.getNamespaces());
  }

}
