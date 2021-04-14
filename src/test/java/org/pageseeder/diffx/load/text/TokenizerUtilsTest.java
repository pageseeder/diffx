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
package org.pageseeder.diffx.load.text;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Test case for the tokenizer utility class.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public class TokenizerUtilsTest {

  @Test
  public void testGetLeadingWhiteSpace() {
    assertEquals(0, TokenizerUtils.getLeadingWhiteSpace(""));
    assertEquals(0, TokenizerUtils.getLeadingWhiteSpace("x"));
    assertEquals(0, TokenizerUtils.getLeadingWhiteSpace("x "));
    assertEquals(0, TokenizerUtils.getLeadingWhiteSpace("xxx  "));
    assertEquals(1, TokenizerUtils.getLeadingWhiteSpace(" "));
    assertEquals(1, TokenizerUtils.getLeadingWhiteSpace(" x"));
    assertEquals(1, TokenizerUtils.getLeadingWhiteSpace(" x  "));
    assertEquals(2, TokenizerUtils.getLeadingWhiteSpace("\t\tx  x  "));
    assertEquals(2, TokenizerUtils.getLeadingWhiteSpace("\t\tx x "));
    assertEquals(4, TokenizerUtils.getLeadingWhiteSpace("\t  \n"));
    assertEquals(4, TokenizerUtils.getLeadingWhiteSpace("\t  \nx x "));
  }

  @Test
  public void testGetTrailingWhiteSpace() {
    assertEquals(0, TokenizerUtils.getTrailingWhiteSpace(""));
    assertEquals(0, TokenizerUtils.getTrailingWhiteSpace("x"));
    assertEquals(0, TokenizerUtils.getTrailingWhiteSpace(" x"));
    assertEquals(0, TokenizerUtils.getTrailingWhiteSpace("  xxx"));
    assertEquals(1, TokenizerUtils.getTrailingWhiteSpace(" "));
    assertEquals(1, TokenizerUtils.getTrailingWhiteSpace("x "));
    assertEquals(1, TokenizerUtils.getTrailingWhiteSpace("  x "));
    assertEquals(2, TokenizerUtils.getTrailingWhiteSpace(" x  x\t\t"));
    assertEquals(2, TokenizerUtils.getTrailingWhiteSpace(" x x\t\t"));
    assertEquals(4, TokenizerUtils.getTrailingWhiteSpace("\t  \n"));
    assertEquals(4, TokenizerUtils.getTrailingWhiteSpace("x x\t  \n"));
  }

}
