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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

final class PostXMLFixerTest {

  @Test
  void testNoChange() {
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "</a>");
    List<Operation<XMLToken>> result = fix(operations);
    assertEquals(operations, result);
  }

  @Test
  void testExample1A() {
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "+<b>", "</a>", "+</b>");
    List<Operation<XMLToken>> expect = TestOperations.toXMLOperations("<a>", "+<b>", "+</b>", "</a>");
    List<Operation<XMLToken>> result = fix(operations);
    assertEquals(expect, result);
  }

  @Test
  void testExample1B() {
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("+<b>", "<a>", "+</b>", "</a>");
    List<Operation<XMLToken>> expect = TestOperations.toXMLOperations("+<b>", "<a>", "</a>", "+</b>");
    List<Operation<XMLToken>> result = fix(operations);
    assertEquals(expect, result);
  }

  @Test
  void testExample2A() {
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "+<b>", "-<i>", "</a>", "+</b>", "-</i>");
    List<Operation<XMLToken>> expect = TestOperations.toXMLOperations("<a>", "-<i>", "+<b>", "+</b>", "-</i>", "</a>");
    List<Operation<XMLToken>> result = fix(operations);
    assertEquals(expect, result);
  }

  @Test
  void testExample2B() {
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "-<i>", "+<b>", "</a>", "+</b>", "-</i>");
    List<Operation<XMLToken>> expect = TestOperations.toXMLOperations("<a>", "-<i>", "+<b>", "+</b>", "-</i>", "</a>");
    List<Operation<XMLToken>> result = fix(operations);
    assertEquals(expect, result);
  }

  @Test
  void testAttribute1() {
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "-Y", "+@m=x", "+X", "</a>");
    List<Operation<XMLToken>> expect = TestOperations.toXMLOperations("<a>", "+@m=x", "+X", "-Y", "</a>");
    List<Operation<XMLToken>> result = fix(operations);
    assertEquals(expect, result);
  }

  @Test
  void testAttribute2() {
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "-@m=y", "-Y", "+@m=x", "+X", "</a>");
    List<Operation<XMLToken>> expect = TestOperations.toXMLOperations("<a>", "-@m=y", "+@m=x", "+X", "-Y", "</a>");
    List<Operation<XMLToken>> result = fix(operations);
    assertEquals(expect, result);
  }

  @Test
  void testExample2C() {
    // (-) <f> <l><i> <b>x</b> <n/>        a</i></l></f>
    // (+) <f> <p>    <b>x</b> </p><l><i>  a</i></l></f>
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations(
      "<f>",
        "-<l>", "-<i>", "+<p>",
        "<b>", "x", "</b>",
        "-<n>", "-</n>", "+</p>", "+<l>", "+<i>",
        "a", "</i>", "</l>", "</f>"
    );
    List<Operation<XMLToken>> expect = TestOperations.toXMLOperations(
        "<f>",
              "-<l>", "-<i>", "+<p>",
              "<b>", "x", "</b>",
              "+</p>", "+<l>", "+<i>", "-<n>", "-</n>",
              "a", "+</i>", "+</l>", "-</i>", "-</l>",
              "</f>"
    );
    List<Operation<XMLToken>> result = fix(operations);
    assertEquals(expect, result);
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
