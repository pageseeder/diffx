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
package org.pageseeder.diffx.action;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.pageseeder.diffx.action.Operator.*;

public class OperatorTest {

  @Test
  public void testFlip() {
    assertEquals(DEL, INS.flip());
    assertEquals(INS, DEL.flip());
    assertEquals(MATCH, MATCH.flip());
  }

  @Test
  public void testIsEdit() {
    assertTrue(INS.isEdit());
    assertTrue(DEL.isEdit());
    assertFalse(MATCH.isEdit());
  }

}
