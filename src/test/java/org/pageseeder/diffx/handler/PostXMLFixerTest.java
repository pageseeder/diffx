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

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

final class PostXMLFixerTest {

  // --- Pass-through ---

  @Test
  void testNoChange() {
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "</a>");
    List<Operation<XMLToken>> result = fix(operations);
    assertEquals(operations, result);
  }

  @Test
  void testNoChangeHasNoError() {
    assertFalse(fixAndReturnFixer(TestOperations.toXMLOperations("<a>", "</a>")).hasError());
  }

  // --- Element reordering ---

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

  // --- Attribute flushing ---

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
  void testAttributeAfterAttribute() {
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "@n=v", "-@m=y", "+@m=x", "X", "</a>");
    List<Operation<XMLToken>> expect = TestOperations.toXMLOperations("<a>", "@n=v", "-@m=y", "+@m=x", "X", "</a>");
    List<Operation<XMLToken>> result = fix(operations);
    assertEquals(expect, result);
  }

  // --- Insertions only ---

  @Test
  void testInsertionsOnlyText() {
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "+X", "+Y", "</a>");
    List<Operation<XMLToken>> expect = TestOperations.toXMLOperations("<a>", "+X", "+Y", "</a>");
    List<Operation<XMLToken>> result = fix(operations);
    assertEquals(expect, result);
  }

  @Test
  void testInsertionsOnlyElement() {
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "+<b>", "+</b>", "</a>");
    List<Operation<XMLToken>> expect = TestOperations.toXMLOperations("<a>", "+<b>", "+</b>", "</a>");
    List<Operation<XMLToken>> result = fix(operations);
    assertEquals(expect, result);
  }

  // --- Deletions only ---

  @Test
  void testDeletionsOnlyText() {
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "-X", "-Y", "</a>");
    List<Operation<XMLToken>> expect = TestOperations.toXMLOperations("<a>", "-X", "-Y", "</a>");
    List<Operation<XMLToken>> result = fix(operations);
    assertEquals(expect, result);
  }

  @Test
  void testDeletionsOnlyElement() {
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "-<b>", "-</b>", "</a>");
    List<Operation<XMLToken>> expect = TestOperations.toXMLOperations("<a>", "-<b>", "-</b>", "</a>");
    List<Operation<XMLToken>> result = fix(operations);
    assertEquals(expect, result);
  }

  // --- Operator preference in else branch ---

  @Test
  void testLastOperatorPrefersInsViaAttributes() {
    // After flushing attributes (DEL then INS), lastOperator=INS, so INS text is preferred next
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "@n=v", "-@m=y", "+@m=x", "+X", "-Y", "</a>");
    List<Operation<XMLToken>> result = fix(operations);
    List<Operation<XMLToken>> expect = TestOperations.toXMLOperations("<a>", "@n=v", "-@m=y", "+@m=x", "+X", "-Y", "</a>");
    assertEquals(expect, result);
  }

  @Test
  void testLastOperatorPrefersDel() {
    // Default preference sends DEL first; with multiple DELs, they stay grouped
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "-X", "-Y", "+Z", "</a>");
    List<Operation<XMLToken>> result = fix(operations);
    List<Operation<XMLToken>> expect = TestOperations.toXMLOperations("<a>", "-X", "-Y", "+Z", "</a>");
    assertEquals(expect, result);
  }

  @Test
  void testDefaultPrefersDeletions() {
    // When lastOperator is MATCH and both queues have non-end-element tokens, DEL is preferred
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "M", "+X", "-Y", "</a>");
    List<Operation<XMLToken>> result = fix(operations);
    List<Operation<XMLToken>> expect = TestOperations.toXMLOperations("<a>", "M", "-Y", "+X", "</a>");
    assertEquals(expect, result);
  }

  // --- hasError flag ---

  @Test
  void testHasErrorOnMismatchedDeletedEndElement() {
    // Deleted end element that doesn't match the last unclosed deleted start
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "-<b>", "-</c>", "</a>");
    PostXMLFixer fixer = fixAndReturnFixer(operations);
    assertTrue(fixer.hasError());
  }

  @Test
  void testHasErrorOnMismatchedInsertedEndElement() {
    // Inserted end element that doesn't match the last unclosed inserted start
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "+<b>", "+</c>", "</a>");
    PostXMLFixer fixer = fixAndReturnFixer(operations);
    assertTrue(fixer.hasError());
  }

  @Test
  void testNoErrorWhenEndElementsMatch() {
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "+<b>", "+</b>", "-<c>", "-</c>", "</a>");
    PostXMLFixer fixer = fixAndReturnFixer(operations);
    assertFalse(fixer.hasError());
  }

  // --- MATCH end element mismatch ---

  @Test
  void testMatchEndElementMismatchSendsMatchingEnd() {
    // MATCH </c> doesn't match the last unclosed MATCH <a>, so fixer sends </a> instead
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "</c>");
    List<Operation<XMLToken>> result = fix(operations);
    List<Operation<XMLToken>> expect = TestOperations.toXMLOperations("<a>", "</a>");
    assertEquals(expect, result);
  }

  // --- end() flushes pending edits ---

  @Test
  void testEndFlushesPendingInsertions() {
    // No trailing MATCH to trigger flush — end() must flush
    OperationsBuffer<XMLToken> target = new OperationsBuffer<>();
    PostXMLFixer fixer = new PostXMLFixer(target);
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "+X");
    fixer.start();
    for (Operation<XMLToken> o : operations) {
      fixer.handle(o.operator(), o.token());
    }
    fixer.end();
    List<Operation<XMLToken>> expect = TestOperations.toXMLOperations("<a>", "+X", "</a>");
    assertEquals(expect, target.getOperations());
  }

  @Test
  void testEndFlushesPendingDeletions() {
    OperationsBuffer<XMLToken> target = new OperationsBuffer<>();
    PostXMLFixer fixer = new PostXMLFixer(target);
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "-X");
    fixer.start();
    for (Operation<XMLToken> o : operations) {
      fixer.handle(o.operator(), o.token());
    }
    fixer.end();
    List<Operation<XMLToken>> expect = TestOperations.toXMLOperations("<a>", "-X", "</a>");
    assertEquals(expect, target.getOperations());
  }

  // --- end() closes unclosed elements ---

  @Test
  void testEndClosesUnclosedMatchedElements() {
    OperationsBuffer<XMLToken> target = new OperationsBuffer<>();
    PostXMLFixer fixer = new PostXMLFixer(target);
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "<b>");
    fixer.start();
    for (Operation<XMLToken> o : operations) {
      fixer.handle(o.operator(), o.token());
    }
    fixer.end();
    List<Operation<XMLToken>> expect = TestOperations.toXMLOperations("<a>", "<b>", "</b>", "</a>");
    assertEquals(expect, target.getOperations());
  }

  @Test
  void testEndClosesUnclosedNestedElements() {
    OperationsBuffer<XMLToken> target = new OperationsBuffer<>();
    PostXMLFixer fixer = new PostXMLFixer(target);
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "<b>", "<c>");
    fixer.start();
    for (Operation<XMLToken> o : operations) {
      fixer.handle(o.operator(), o.token());
    }
    fixer.end();
    List<Operation<XMLToken>> expect = TestOperations.toXMLOperations("<a>", "<b>", "<c>", "</c>", "</b>", "</a>");
    assertEquals(expect, target.getOperations());
  }

  // --- Mixed text and element edits ---

  @Test
  void testInsertedTextBeforeDeletedElement() {
    // DEL preferred from MATCH default; DEL <b> sent first, then its matching </b>, then INS X
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "+X", "-<b>", "-</b>", "</a>");
    List<Operation<XMLToken>> result = fix(operations);
    List<Operation<XMLToken>> expect = TestOperations.toXMLOperations("<a>", "-<b>", "-</b>", "+X", "</a>");
    assertEquals(expect, result);
  }

  @Test
  void testDeletedTextBeforeInsertedElement() {
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "-X", "+<b>", "+</b>", "</a>");
    List<Operation<XMLToken>> result = fix(operations);
    List<Operation<XMLToken>> expect = TestOperations.toXMLOperations("<a>", "-X", "+<b>", "+</b>", "</a>");
    assertEquals(expect, result);
  }

  // --- Helpers ---

  private List<Operation<XMLToken>> fix(List<Operation<XMLToken>> source) {
    OperationsBuffer<XMLToken> target = new OperationsBuffer<>();
    PostXMLFixer fixer = new PostXMLFixer(target);
    fixer.start();
    for (Operation<XMLToken> o : source) {
      fixer.handle(o.operator(), o.token());
    }
    fixer.end();
    return target.getOperations();
  }

  private PostXMLFixer fixAndReturnFixer(List<Operation<XMLToken>> source) {
    OperationsBuffer<XMLToken> target = new OperationsBuffer<>();
    PostXMLFixer fixer = new PostXMLFixer(target);
    fixer.start();
    for (Operation<XMLToken> o : source) {
      fixer.handle(o.operator(), o.token());
    }
    fixer.end();
    return fixer;
  }

}
