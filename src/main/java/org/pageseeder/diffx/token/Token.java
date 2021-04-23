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
package org.pageseeder.diffx.token;

import org.pageseeder.diffx.xml.XMLStreamable;
import org.pageseeder.xmlwriter.XMLWritable;

/**
 * Defines a token that can be processed for diffing.
 *
 * <p>As many equality checks are performed, implementations must provide efficient
 * {@link #equals(Token)} and {@link #hashCode()} methods.</p>
 *
 * <p>Most equality checks are performed against other unequal tokens, so equality checks
 * should generally precompute or cache the hashcode and use it in equality check to speed
 * things up.</p>
 *
 * <p>For convenience, this interface extends the <code>XMLWritable</code> and
 * <code>XMLStreamable</code> in order to write token as XML consistently.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public interface Token extends XMLWritable, XMLStreamable {

  /**
   * @return The type of token.
   */
  TokenType getType();

  @Override
  int hashCode();

  /**
   * Indicates whether the specified token is equal to this token.
   *
   * @param token The token to compare it with this one.
   *
   * @return <code>true</code> if considered equals;
   * <code>false</code> otherwise.
   */
  boolean equals(Token token);

}
