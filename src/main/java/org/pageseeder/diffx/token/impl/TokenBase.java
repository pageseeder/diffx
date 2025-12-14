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

import org.jspecify.annotations.Nullable;
import org.pageseeder.diffx.token.XMLToken;

/**
 * An abstract base implementation of the {@link XMLToken} interface,
 * providing foundational implementations for token comparison and equality.
 *
 * <p>This class serves as a base type for different kinds of XML tokens,
 * ensuring consistent behavior for equality and hash code operations.
 *
 * @author Christophe Lauret
 *
 * @since 0.6.0
 * @version 1.2.2
 */
abstract class TokenBase implements XMLToken {

  @Override
  public abstract int hashCode();

  /**
   * Invokes the {@link XMLToken#equals(XMLToken)} method if the specified object if not
   * <code>null</code> and is an instance of {@link XMLToken}.
   *
   * @param o The object to compare.
   *
   * @return <code>true</code> if the specified object is equal;
   * <code>false</code> otherwise.
   */
  @Override
  public final boolean equals(@Nullable Object o) {
    if (!(o instanceof XMLToken)) return false;
    return equals((XMLToken) o);
  }

}
