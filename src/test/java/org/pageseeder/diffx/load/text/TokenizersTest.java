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
package org.pageseeder.diffx.load.text;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test case for the tokenizer utility class.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
class TokenizersTest {

  @Test
  void testGetLeadingWhiteSpace() {
    assertEquals(0, Tokenizers.getLeadingWhiteSpace(""));
    assertEquals(0, Tokenizers.getLeadingWhiteSpace("x"));
    assertEquals(0, Tokenizers.getLeadingWhiteSpace("x "));
    assertEquals(0, Tokenizers.getLeadingWhiteSpace("xxx  "));
    assertEquals(1, Tokenizers.getLeadingWhiteSpace(" "));
    assertEquals(1, Tokenizers.getLeadingWhiteSpace(" x"));
    assertEquals(1, Tokenizers.getLeadingWhiteSpace(" x  "));
    assertEquals(2, Tokenizers.getLeadingWhiteSpace("\t\tx  x  "));
    assertEquals(2, Tokenizers.getLeadingWhiteSpace("\t\tx x "));
    assertEquals(4, Tokenizers.getLeadingWhiteSpace("\t  \n"));
    assertEquals(4, Tokenizers.getLeadingWhiteSpace("\t  \nx x "));
  }

  @Test
  void testGetTrailingWhiteSpace() {
    assertEquals(0, Tokenizers.getTrailingWhiteSpace(""));
    assertEquals(0, Tokenizers.getTrailingWhiteSpace("x"));
    assertEquals(0, Tokenizers.getTrailingWhiteSpace(" x"));
    assertEquals(0, Tokenizers.getTrailingWhiteSpace("  xxx"));
    assertEquals(1, Tokenizers.getTrailingWhiteSpace(" "));
    assertEquals(1, Tokenizers.getTrailingWhiteSpace("x "));
    assertEquals(1, Tokenizers.getTrailingWhiteSpace("  x "));
    assertEquals(2, Tokenizers.getTrailingWhiteSpace(" x  x\t\t"));
    assertEquals(2, Tokenizers.getTrailingWhiteSpace(" x x\t\t"));
    assertEquals(4, Tokenizers.getTrailingWhiteSpace("\t  \n"));
    assertEquals(4, Tokenizers.getTrailingWhiteSpace("x x\t  \n"));
  }

}
