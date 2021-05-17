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
package org.pageseeder.diffx.api;

/**
 * The basic types of difference operators to generate an edit script.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 * @since 0.9.0
 */
public enum Operator {

  /**
   * An insertion.
   */
  INS(true) {
    /**
     * @return Always DEL
     */
    public Operator flip() {
      return DEL;
    }

    /**
     * @return Always "+"
     */
    public String toString() {
      return "+";
    }

  },

  /**
   * A deletion.
   */
  DEL(true) {
    /**
     * @return Always INS
     */
    public Operator flip() {
      return INS;
    }

    /**
     * @return Always "-"
     */
    public String toString() {
      return "-";
    }

  },

  /**
   * A match.
   */
  MATCH(false) {
    /**
     * @return Always MATCH
     */
    public Operator flip() {
      return this;
    }

    /**
     * @return Always "="
     */
    public String toString() {
      return "=";
    }
  };

  private final boolean isEdit;

  Operator(boolean isEdit) {
    this.isEdit = isEdit;
  }

  /**
   * @return the operator performing the opposite operation.
   */
  public abstract Operator flip();

  /**
   * @return true if the operator is an edit (insertion or deletion).
   */
  public boolean isEdit() {
    return isEdit;
  }

}
