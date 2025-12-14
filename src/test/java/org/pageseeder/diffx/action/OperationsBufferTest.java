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
import org.pageseeder.diffx.action.Operation;
import org.pageseeder.diffx.action.OperationsBuffer;
import org.pageseeder.diffx.api.Operator;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.token.impl.CharToken;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OperationsBufferTest {

  @Test
  void testEmpty() {
    OperationsBuffer<Object> handler = new OperationsBuffer<>();
    List<Operation<Object>> Operations = handler.getOperations();
    assertTrue(Operations.isEmpty());
  }

  @Test
  void testSingle() {
    XMLToken token = new CharToken('x');
    for (Operator operator : Operator.values()) {
      OperationsBuffer<XMLToken> handler = new OperationsBuffer<>();
      handler.handle(operator, token);
      List<Operation<XMLToken>> operations = handler.getOperations();
      assertEquals(1, operations.size());
      Operation<XMLToken> operation = operations.get(0);
      assertEquals(operator, operation.operator());
      assertEquals(token, operation.token());
    }
  }

  @Test
  void testMixed() {
    XMLToken token1 = new CharToken('x');
    XMLToken token2 = new CharToken('y');
    for (Operator operator1 : Operator.values()) {
      for (Operator operator2 : Operator.values()) {
        OperationsBuffer<XMLToken> handler = new OperationsBuffer<>();
        handler.handle(operator1, token1);
        handler.handle(operator2, token2);
        List<Operation<XMLToken>> operations = handler.getOperations();
        assertEquals(2, operations.size());
        Operation<XMLToken> Operation1 = operations.get(0);
        Operation<XMLToken> Operation2 = operations.get(1);
        assertEquals(new Operation<>(operator1, token1), Operation1);
        assertEquals(new Operation<>(operator2, token2), Operation2);
      }
    }
  }
}
