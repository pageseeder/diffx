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
package org.pageseeder.diffx.event;

/**
 * An interface for any data that comes from a text node.
 *
 * @author Christophe Lauret
 * @version 21 December 2004
 */
public interface TextEvent extends DiffXEvent {

  /**
   * Returns the characters that this event represents.
   *
   * <p>Note: this method will return the characters as used by Java (ie. Unicode), they
   * may not be suitable for writing to an XML string.
   *
   * @return The characters that this event represents.
   */
  String getCharacters();

  @Override
  default String getType() { return "text"; }

}
