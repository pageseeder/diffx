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
package org.pageseeder.diffx.action;

/**
 * The different basic types of difference operators used by DiffX.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public enum Operator {

  /**
   * An insertion.
   */
  INS {

    /**
     * @return DEL
     */
    public Operator flip() {
      return DEL;
    }
    public String toString() {
      return "+";
    }
  },

  /**
   * A deletion.
   */
  DEL {

    /**
     * @return INS
     */
    public Operator flip() {
      return INS;
    }

    public String toString() {
      return "-";
    }
  },

  /**
   * A match.
   */
  MATCH {

    /**
     * @return MATCH
     */
    public Operator flip() {
      return this;
    }

    public String toString() {
      return "=";
    }
  };

  /**
   * @return the operator performing the opposite operation.
   */
  public abstract Operator flip();
}
