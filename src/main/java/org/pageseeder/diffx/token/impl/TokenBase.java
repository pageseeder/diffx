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
package org.pageseeder.diffx.token.impl;

import org.pageseeder.diffx.token.Token;

/**
 * A base class for DiffX tokens.
 *
 * <p>
 * This class is purely provided for convenience and consistency, it is best, although not strictly
 * required, that most <code>Token</code> implementations extend this class.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 * @since 0.6.0
 */
abstract class TokenBase implements Token {

  @Override
  public abstract int hashCode();

  @Override
  public abstract boolean equals(Token token);

  /**
   * Invokes the {@link Token#equals(Token)} method if the specified object if not
   * <code>null</code> and is an instance of {@link Token}.
   *
   * @param o The object to compare.
   *
   * @return <code>true</code> if the specified object is equal;
   * <code>false</code> otherwise.
   */
  @Override
  public final boolean equals(Object o) {
    if (o == null)
      return false;
    if (!(o instanceof Token))
      return false;
    return equals((Token) o);
  }

}
