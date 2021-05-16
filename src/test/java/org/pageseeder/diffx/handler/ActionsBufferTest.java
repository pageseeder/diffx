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
package org.pageseeder.diffx.handler;

import org.junit.jupiter.api.Test;
import org.pageseeder.diffx.action.Action;
import org.pageseeder.diffx.api.Operator;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.token.impl.CharToken;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ActionsBufferTest {

  @Test
  public void testEmpty() {
    ActionsBuffer handler = new ActionsBuffer();
    List<Action> actions = handler.getActions();
    assertTrue(actions.isEmpty());
  }

  @Test
  public void testSingle() {
    XMLToken token = new CharToken('x');
    for (Operator operator : Operator.values()) {
      ActionsBuffer handler = new ActionsBuffer();
      handler.handle(operator, token);
      List<Action> actions = handler.getActions();
      assertEquals(1, actions.size());
      Action action = actions.get(0);
      assertEquals(operator, action.operator());
      assertEquals(1, action.tokens().size());
      assertEquals(token, action.tokens().get(0));
    }
  }

  @Test
  public void testDouble() {
    XMLToken token1 = new CharToken('x');
    XMLToken token2 = new CharToken('y');
    for (Operator operator : Operator.values()) {
      ActionsBuffer handler = new ActionsBuffer();
      handler.handle(operator, token1);
      handler.handle(operator, token2);
      List<Action> actions = handler.getActions();
      assertEquals(1, actions.size());
      Action action = actions.get(0);
      assertEquals(operator, action.operator());
      assertEquals(2, action.tokens().size());
      assertEquals(token1, action.tokens().get(0));
      assertEquals(token2, action.tokens().get(1));
    }
  }

  @Test
  public void testMixed() {
    XMLToken token1 = new CharToken('x');
    XMLToken token2 = new CharToken('y');
    for (Operator operator1 : Operator.values()) {
      Iterable<Operator> others = Arrays.stream(Operator.values()).filter(o -> o != operator1).collect(Collectors.toSet());
      for (Operator operator2 : others) {
        ActionsBuffer handler = new ActionsBuffer();
        handler.handle(operator1, token1);
        handler.handle(operator2, token2);
        List<Action> actions = handler.getActions();
        assertEquals(2, actions.size());
        Action action1 = actions.get(0);
        Action action2 = actions.get(1);
        assertEquals(new Action(operator1, Collections.singletonList(token1)), action1);
        assertEquals(new Action(operator2, Collections.singletonList(token2)), action2);
      }
    }
  }
}
