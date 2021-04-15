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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.pageseeder.diffx.action.Action;
import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.event.impl.CharEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ActionHandlerText {

  @Test
  public void testEmpty() {
    ActionHandler handler = new ActionHandler();
    List<Action> actions = handler.getActions();
    assertTrue(actions.isEmpty());
  }

  @Test
  public void testSingle() {
    DiffXEvent event = new CharEvent('x');
    for (Operator operator : Operator.values()) {
      ActionHandler handler = new ActionHandler();
      handler.handle(operator, event);
      List<Action> actions = handler.getActions();
      assertEquals(1, actions.size());
      Action action = actions.get(0);
      assertEquals(operator, action.operator());
      assertEquals(1, action.events().size());
      assertEquals(event, action.events().get(0));
    }
  }

  @Test
  public void testDouble() {
    DiffXEvent event1 = new CharEvent('x');
    DiffXEvent event2 = new CharEvent('y');
    for (Operator operator : Operator.values()) {
      ActionHandler handler = new ActionHandler();
      handler.handle(operator, event1);
      handler.handle(operator, event2);
      List<Action> actions = handler.getActions();
      assertEquals(1, actions.size());
      Action action = actions.get(0);
      assertEquals(operator, action.operator());
      assertEquals(2, action.events().size());
      assertEquals(event1, action.events().get(0));
      assertEquals(event2, action.events().get(1));
    }
  }

  @Test
  public void testMixed() {
    DiffXEvent event1 = new CharEvent('x');
    DiffXEvent event2 = new CharEvent('y');
    for (Operator operator1 : Operator.values()) {
      Iterable<Operator> others = Arrays.stream(Operator.values()).filter(o -> o != operator1).collect(Collectors.toSet());
      for (Operator operator2 : others) {
        ActionHandler handler = new ActionHandler();
        handler.handle(operator1, event1);
        handler.handle(operator2, event2);
        List<Action> actions = handler.getActions();
        assertEquals(2, actions.size());
        Action action1 = actions.get(0);
        Action action2 = actions.get(1);
        assertEquals(new Action(operator1, Collections.singletonList(event1)), action1);
        assertEquals(new Action(operator2, Collections.singletonList(event2)), action2);
      }
    }
  }
}
