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
package org.pageseeder.diffx.algorithm;

import org.pageseeder.diffx.DiffXException;

/**
 * Class of exceptions thrown when a factory method failed to produce
 * the desired object.
 *
 * @author Christophe Lauret
 * @version 14 May 2005
 */
@Deprecated
public final class FactoryException extends DiffXException {

  /**
   * Version number for the serialised class.
   */
  private static final long serialVersionUID = -4029990831933233646L;

  /**
   * Creates a new factory exception wrapping an occurring exception.
   *
   * @param ex The exception to be wrapped.
   */
  public FactoryException(Exception ex) {
    super(ex);
  }

}
