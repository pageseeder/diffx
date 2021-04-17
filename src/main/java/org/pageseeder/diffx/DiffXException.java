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
package org.pageseeder.diffx;

/**
 * The mother of all Diff-X exceptions.
 *
 * <p>This class is provided for convenience to distinguish between the purely
 * DiffX exceptions and exception of a different origin.
 *
 * @author Christophe Lauret
 * @version 27 March 2010
 */
public class DiffXException extends Exception {

  /**
   * As per requirement by the Serializable interface.
   */
  private static final long serialVersionUID = 3572025323967229569L;

  /**
   * Creates a new Diff-X exception.
   */
  public DiffXException() {
    super();
  }

  /**
   * Creates a new Diff-X exception with a given message.
   *
   * @param message The message explaining the exception.
   */
  public DiffXException(String message) {
    super(message);
  }

  /**
   * Creates a new Diff-X exception wrapping an occurring exception.
   *
   * @param ex The exception to be wrapped.
   */
  public DiffXException(Exception ex) {
    super(ex);
  }

  /**
   * Creates a new Diff-X exception wrapping an occurring exception.
   *
   * @param message The message explaining the exception.
   * @param ex      The exception to be wrapped.
   */
  public DiffXException(String message, Exception ex) {
    super(message, ex);
  }

}
