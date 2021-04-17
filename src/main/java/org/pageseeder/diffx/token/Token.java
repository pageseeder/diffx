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

import org.pageseeder.xmlwriter.XMLFormattable;
import org.pageseeder.xmlwriter.XMLWritable;

/**
 * Defines and token that can be processed by DiffX.
 *
 * <p>The main characteristics of a Diff-X token is that it can be compared for
 * equality with another Diff-X token.
 *
 * <p>Events can be associated with a weight that can be used or ignored by an algorithm.
 * The more weight the less likely an token will be considered to have been modified. The
 * weight can change depending on the algorithm or configuration used.
 *
 * <p>For convenience, this interface extends the <code>XMLWritable</code> and
 * <code>XMLFormattable</code> in order to turn an token into XML easily. <b>This
 * may change in the future, if the impact on performance is too heavy</b>.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public interface Token extends XMLWritable, XMLFormattable {

  /**
   * @return The type of token.
   */
  TokenType getType();

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
