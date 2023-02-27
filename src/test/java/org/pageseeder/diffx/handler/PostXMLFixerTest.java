/*
 * Copyright (c) 2010-2023 Allette Systems (Australia)
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
import org.pageseeder.diffx.action.Operation;
import org.pageseeder.diffx.action.OperationsBuffer;
import org.pageseeder.diffx.test.TestOperations;
import org.pageseeder.diffx.token.XMLToken;

import java.util.List;

public class PostXMLFixerTest {

  @Test
  public void testNoChange() {
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "</a>");
    List<Operation<XMLToken>> result = fix(operations);
    System.out.println(result);
  }

  @Test
  public void testExample1() {
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "+<b>", "</a>", "+</b>");
    List<Operation<XMLToken>> result = fix(operations);
    System.out.println(result);
  }

  @Test
  public void testExample2() {

  }

  private List<Operation<XMLToken>> fix(List<Operation<XMLToken>> source) {
    OperationsBuffer<org.pageseeder.diffx.token.XMLToken> target = new OperationsBuffer<>();
    PostXMLFixer fixer = new PostXMLFixer(target);
    fixer.start();
    for (Operation<XMLToken> o : source) {
      fixer.handle(o.operator(), o.token());
    }
    fixer.end();
    return target.getOperations();
  }

}
