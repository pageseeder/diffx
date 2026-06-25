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

final class XMLEventBalancerTest {

  // --- Pass-through ---

  @Test
  void testMatchedElement() {
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "</a>");
    List<Operation<XMLToken>> result = balance(operations);
    assertEquals(operations, result);
  }

  @Test
  void testMatchedNestedElements() {
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "<b>", "</b>", "</a>");
    List<Operation<XMLToken>> result = balance(operations);
    assertEquals(operations, result);
  }

  @Test
  void testMatchedElementWithText() {
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "X", "</a>");
    List<Operation<XMLToken>> result = balance(operations);
    assertEquals(operations, result);
  }

  @Test
  void testMatchedElementWithAttribute() {
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "@n=v", "</a>");
    List<Operation<XMLToken>> result = balance(operations);
    assertEquals(operations, result);
  }

  @Test
  void testNoChangeHasNoError() {
    assertFalse(balanceAndReturn(TestOperations.toXMLOperations("<a>", "</a>")).hasError());
  }

  // --- Insertions only ---

  @Test
  void testInsertedText() {
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "+X", "+Y", "</a>");
    List<Operation<XMLToken>> result = balance(operations);
    assertEquals(operations, result);
  }

  @Test
  void testInsertedElement() {
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "+<b>", "+</b>", "</a>");
    List<Operation<XMLToken>> result = balance(operations);
    assertEquals(operations, result);
  }

  // --- Deletions only ---

  @Test
  void testDeletedText() {
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "-X", "-Y", "</a>");
    List<Operation<XMLToken>> result = balance(operations);
    assertEquals(operations, result);
  }

  @Test
  void testDeletedElement() {
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "-<b>", "-</b>", "</a>");
    List<Operation<XMLToken>> result = balance(operations);
    assertEquals(operations, result);
  }

  // --- Attribute flushing ---

  @Test
  void testAttributeReplacement() {
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "-@m=y", "+@m=x", "</a>");
    List<Operation<XMLToken>> result = balance(operations);
    assertEquals(operations, result);
  }

  @Test
  void testAttributeAfterMatchedAttribute() {
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "@n=v", "-@m=y", "+@m=x", "X", "</a>");
    List<Operation<XMLToken>> result = balance(operations);
    assertEquals(operations, result);
  }

  // --- Element reordering ---

  @Test
  void testInsertedElementReordering() {
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "+<b>", "</a>", "+</b>");
    List<Operation<XMLToken>> expect = TestOperations.toXMLOperations("<a>", "+<b>", "+</b>", "</a>");
    List<Operation<XMLToken>> result = balance(operations);
    assertEquals(expect, result);
  }

  @Test
  void testDeletedElementReordering() {
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "-<b>", "</a>", "-</b>");
    List<Operation<XMLToken>> expect = TestOperations.toXMLOperations("<a>", "-<b>", "-</b>", "</a>");
    List<Operation<XMLToken>> result = balance(operations);
    assertEquals(expect, result);
  }

  // --- Mixed edits ---

  @Test
  void testReplaceText() {
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "-X", "+Y", "</a>");
    List<Operation<XMLToken>> expect = TestOperations.toXMLOperations("<a>", "-X", "+Y", "</a>");
    List<Operation<XMLToken>> result = balance(operations);
    assertEquals(expect, result);
  }

  @Test
  void testReplaceElement() {
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "-<b>", "-</b>", "+<c>", "+</c>", "</a>");
    List<Operation<XMLToken>> expect = TestOperations.toXMLOperations("<a>", "-<b>", "-</b>", "+<c>", "+</c>", "</a>");
    List<Operation<XMLToken>> result = balance(operations);
    assertEquals(expect, result);
  }

  @Test
  void testInsertedTextBeforeDeletedElement() {
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "+X", "-<b>", "-</b>", "</a>");
    List<Operation<XMLToken>> expect = TestOperations.toXMLOperations("<a>", "-<b>", "-</b>", "+X", "</a>");
    List<Operation<XMLToken>> result = balance(operations);
    assertEquals(expect, result);
  }

  @Test
  void testDeletedTextBeforeInsertedElement() {
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "-X", "+<b>", "+</b>", "</a>");
    List<Operation<XMLToken>> expect = TestOperations.toXMLOperations("<a>", "-X", "+<b>", "+</b>", "</a>");
    List<Operation<XMLToken>> result = balance(operations);
    assertEquals(expect, result);
  }

  // --- Operator preference ---

  @Test
  void testDefaultPrefersDeletions() {
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "M", "+X", "-Y", "</a>");
    List<Operation<XMLToken>> expect = TestOperations.toXMLOperations("<a>", "M", "-Y", "+X", "</a>");
    List<Operation<XMLToken>> result = balance(operations);
    assertEquals(expect, result);
  }

  @Test
  void testLastOperatorPrefersDel() {
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "-X", "-Y", "+Z", "</a>");
    List<Operation<XMLToken>> expect = TestOperations.toXMLOperations("<a>", "-X", "-Y", "+Z", "</a>");
    List<Operation<XMLToken>> result = balance(operations);
    assertEquals(expect, result);
  }

  @Test
  void testLastOperatorPrefersInsViaAttributes() {
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "@n=v", "-@m=y", "+@m=x", "+X", "-Y", "</a>");
    List<Operation<XMLToken>> expect = TestOperations.toXMLOperations("<a>", "@n=v", "-@m=y", "+@m=x", "+X", "-Y", "</a>");
    List<Operation<XMLToken>> result = balance(operations);
    assertEquals(expect, result);
  }

  // --- Context-based dispatching ---

  @Test
  void testInsContextPrefersInsertionOverDeletedEnd() {
    // INS <b> is flushed by the MATCH text, putting INS:<b> on the unclosed stack.
    // Then DEL </c> doesn't match INS:<b>; the INS context sends INS X first.
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "+<b>", "text", "-</c>", "+X", "</a>");
    List<Operation<XMLToken>> expect = TestOperations.toXMLOperations("<a>", "+<b>", "text", "+X", "+</b>", "</a>");
    List<Operation<XMLToken>> result = balance(operations);
    assertEquals(expect, result);
  }

  @Test
  void testDelContextPrefersDeletionOverInsertedEnd() {
    // DEL <b> is flushed by the MATCH text, putting DEL:<b> on the unclosed stack.
    // Then INS </c> doesn't match DEL:<b>; the DEL context sends DEL X first.
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "-<b>", "text", "+</c>", "-X", "</a>");
    List<Operation<XMLToken>> expect = TestOperations.toXMLOperations("<a>", "-<b>", "text", "-X", "-</b>", "</a>");
    List<Operation<XMLToken>> result = balance(operations);
    assertEquals(expect, result);
  }

  // --- MATCH end element mismatch ---

  @Test
  void testMatchEndElementMismatchSendsMatchingEnd() {
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "</c>");
    List<Operation<XMLToken>> expect = TestOperations.toXMLOperations("<a>", "</a>");
    List<Operation<XMLToken>> result = balance(operations);
    assertEquals(expect, result);
  }

  // --- hasError ---

  @Test
  void testHasErrorOnMismatchedDeletedEndElement() {
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "-<b>", "-</c>", "</a>");
    assertTrue(balanceAndReturn(operations).hasError());
  }

  @Test
  void testHasErrorOnMismatchedInsertedEndElement() {
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "+<b>", "+</c>", "</a>");
    assertTrue(balanceAndReturn(operations).hasError());
  }

  @Test
  void testNoErrorWhenEndElementsMatch() {
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "+<b>", "+</b>", "-<c>", "-</c>", "</a>");
    assertFalse(balanceAndReturn(operations).hasError());
  }

  @Test
  void testNoErrorWhenFollowedByMatchingStart() {
    // DEL </c> doesn't match DEL <b>, but is followed by DEL <c> — recoverable, no error
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "-<b>", "-</c>", "-<c>", "</a>");
    assertFalse(balanceAndReturn(operations).hasError());
  }

  // --- end() flushes pending edits ---

  @Test
  void testEndFlushesPendingInsertions() {
    OperationsBuffer<XMLToken> target = new OperationsBuffer<>();
    XMLEventBalancer balancer = new XMLEventBalancer(target);
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "+X");
    balancer.start();
    for (Operation<XMLToken> o : operations) balancer.handle(o.operator(), o.token());
    balancer.end();
    List<Operation<XMLToken>> expect = TestOperations.toXMLOperations("<a>", "+X", "</a>");
    assertEquals(expect, target.getOperations());
  }

  @Test
  void testEndFlushesPendingDeletions() {
    OperationsBuffer<XMLToken> target = new OperationsBuffer<>();
    XMLEventBalancer balancer = new XMLEventBalancer(target);
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "-X");
    balancer.start();
    for (Operation<XMLToken> o : operations) balancer.handle(o.operator(), o.token());
    balancer.end();
    List<Operation<XMLToken>> expect = TestOperations.toXMLOperations("<a>", "-X", "</a>");
    assertEquals(expect, target.getOperations());
  }

  // --- end() closes unclosed elements ---

  @Test
  void testEndClosesUnclosedMatchedElements() {
    OperationsBuffer<XMLToken> target = new OperationsBuffer<>();
    XMLEventBalancer balancer = new XMLEventBalancer(target);
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "<b>");
    balancer.start();
    for (Operation<XMLToken> o : operations) balancer.handle(o.operator(), o.token());
    balancer.end();
    List<Operation<XMLToken>> expect = TestOperations.toXMLOperations("<a>", "<b>", "</b>", "</a>");
    assertEquals(expect, target.getOperations());
  }

  @Test
  void testEndClosesUnclosedNestedElements() {
    OperationsBuffer<XMLToken> target = new OperationsBuffer<>();
    XMLEventBalancer balancer = new XMLEventBalancer(target);
    List<Operation<XMLToken>> operations = TestOperations.toXMLOperations("<a>", "<b>", "<c>");
    balancer.start();
    for (Operation<XMLToken> o : operations) balancer.handle(o.operator(), o.token());
    balancer.end();
    List<Operation<XMLToken>> expect = TestOperations.toXMLOperations("<a>", "<b>", "<c>", "</c>", "</b>", "</a>");
    assertEquals(expect, target.getOperations());
  }

  // --- Helpers ---

  private List<Operation<XMLToken>> balance(List<Operation<XMLToken>> source) {
    OperationsBuffer<XMLToken> target = new OperationsBuffer<>();
    XMLEventBalancer balancer = new XMLEventBalancer(target);
    balancer.start();
    for (Operation<XMLToken> o : source) balancer.handle(o.operator(), o.token());
    balancer.end();
    return target.getOperations();
  }

  private XMLEventBalancer balanceAndReturn(List<Operation<XMLToken>> source) {
    OperationsBuffer<XMLToken> target = new OperationsBuffer<>();
    XMLEventBalancer balancer = new XMLEventBalancer(target);
    balancer.start();
    for (Operation<XMLToken> o : source) balancer.handle(o.operator(), o.token());
    balancer.end();
    return balancer;
  }

}
