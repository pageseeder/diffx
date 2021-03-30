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
package org.pageseeder.diffx.util;

/**
 * The set of constants used in this API.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class Constants {

  /**
   * Prevent creation of instances
   */
  private Constants() {
  }

  /**
   * The namespace URI used for elements that may have been modified.
   */
  public static final String BASE_NS_URI = "http://www.topologi.com/2005/Diff-X";

  /**
   * The namespace URI used for elements that may have been deleted.
   */
  public static final String DELETE_NS_URI = BASE_NS_URI + "/Delete";

  /**
   * The namespace URI used for elements that may have been inserted.
   */
  public static final String INSERT_NS_URI = BASE_NS_URI + "/Insert";

}
