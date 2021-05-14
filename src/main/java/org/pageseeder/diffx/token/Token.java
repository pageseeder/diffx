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
package org.pageseeder.diffx.token;

/**
 * Defines a token that can be processed for diffing.
 *
 * <p>As many equality checks are performed, implementations must provide efficient
 * {@link #equals(Object)} and {@link #hashCode()} methods.</p>
 *
 * <p>Most equality checks are performed against other unequal tokens, so equality checks
 * should generally precompute or cache the hashcode and use it in equality check to speed
 * things up.</p>
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public interface Token {

  @Override
  int hashCode();

}
