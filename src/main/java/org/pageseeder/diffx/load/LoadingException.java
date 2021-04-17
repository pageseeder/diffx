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
package org.pageseeder.diffx.load;

import org.pageseeder.diffx.DiffXException;

/**
 * Class of exceptions occurring when trying to load data for Diff-X.
 *
 * @author Christophe Lauret
 * @version 14 May 2005
 */
public final class LoadingException extends DiffXException {

  /**
   * Version number for the serialised class.
   */
  private static final long serialVersionUID = -5026953481292613087L;

  /**
   * Creates a new Loading exception.
   */
  public LoadingException() {
    super();
  }

  /**
   * Creates a new loading exception with a given message.
   *
   * @param message The message explaining the exception.
   */
  public LoadingException(String message) {
    super(message);
  }

  /**
   * Creates a new loading exception wrapping an occurring exception.
   *
   * @param ex The exception to be wrapped.
   */
  public LoadingException(Exception ex) {
    super(ex);
  }

  /**
   * Creates a new loading exception wrapping an occurring exception.
   *
   * @param message The message explaining the exception.
   * @param ex      The exception to be wrapped.
   */
  public LoadingException(String message, Exception ex) {
    super(message, ex);
  }

}
