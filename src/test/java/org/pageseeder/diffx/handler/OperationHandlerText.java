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
package org.pageseeder.diffx.handler;

import org.junit.jupiter.api.Test;
import org.pageseeder.diffx.action.Operation;
import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.event.Token;
import org.pageseeder.diffx.event.impl.CharToken;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OperationHandlerText {

  @Test
  public void testEmpty() {
    OperationHandler handler = new OperationHandler();
    List<Operation> Operations = handler.getOperations();
    assertTrue(Operations.isEmpty());
  }

  @Test
  public void testSingle() {
    Token token = new CharToken('x');
    for (Operator operator : Operator.values()) {
      OperationHandler handler = new OperationHandler();
      handler.handle(operator, token);
      List<Operation> operations = handler.getOperations();
      assertEquals(1, operations.size());
      Operation operation = operations.get(0);
      assertEquals(operator, operation.operator());
      assertEquals(token, operation.token());
    }
  }

  @Test
  public void testMixed() {
    Token token1 = new CharToken('x');
    Token token2 = new CharToken('y');
    for (Operator operator1 : Operator.values()) {
      for (Operator operator2 : Operator.values()) {
        OperationHandler handler = new OperationHandler();
        handler.handle(operator1, token1);
        handler.handle(operator2, token2);
        List<Operation> operations = handler.getOperations();
        assertEquals(2, operations.size());
        Operation Operation1 = operations.get(0);
        Operation Operation2 = operations.get(1);
        assertEquals(new Operation(operator1, token1), Operation1);
        assertEquals(new Operation(operator2, token2), Operation2);
      }
    }
  }
}
