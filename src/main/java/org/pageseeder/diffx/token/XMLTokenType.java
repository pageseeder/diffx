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
 * Assign a type of token that can affect processing of the diff.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 * @since 0.9.0
 */
public enum XMLTokenType {

  /**
   * Text only.
   */
  TEXT,

  /**
   * An XML attribute.
   */
  ATTRIBUTE,

  /**
   * An XML element.
   */
  ELEMENT,

  /**
   * The start of an XML element.
   */
  START_ELEMENT,

  /**
   * The end of an XML element.
   */
  END_ELEMENT,

  /**
   * An XML comment.
   */
  COMMENT,

  /**
   * Any other type.
   */
  PROCESSING_INSTRUCTION,

  /**
   * Any other type.
   */
  OTHER;

  @Override
  public String toString() {
    return this.name().toLowerCase().replace('_', '-');
  }
}
