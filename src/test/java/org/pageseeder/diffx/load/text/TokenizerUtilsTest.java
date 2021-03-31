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

import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for the tokenizer utility class.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public class TokenizerUtilsTest {

  @Test
  public void testGetLeadingWhiteSpace() {
    Assert.assertEquals(0, TokenizerUtils.getLeadingWhiteSpace(""));
    Assert.assertEquals(0, TokenizerUtils.getLeadingWhiteSpace("x"));
    Assert.assertEquals(0, TokenizerUtils.getLeadingWhiteSpace("x "));
    Assert.assertEquals(0, TokenizerUtils.getLeadingWhiteSpace("xxx  "));
    Assert.assertEquals(1, TokenizerUtils.getLeadingWhiteSpace(" "));
    Assert.assertEquals(1, TokenizerUtils.getLeadingWhiteSpace(" x"));
    Assert.assertEquals(1, TokenizerUtils.getLeadingWhiteSpace(" x  "));
    Assert.assertEquals(2, TokenizerUtils.getLeadingWhiteSpace("\t\tx  x  "));
    Assert.assertEquals(2, TokenizerUtils.getLeadingWhiteSpace("\t\tx x "));
    Assert.assertEquals(4, TokenizerUtils.getLeadingWhiteSpace("\t  \n"));
    Assert.assertEquals(4, TokenizerUtils.getLeadingWhiteSpace("\t  \nx x "));
  }

  @Test
  public void testGetTrailingWhiteSpace() {
    Assert.assertEquals(0, TokenizerUtils.getTrailingWhiteSpace(""));
    Assert.assertEquals(0, TokenizerUtils.getTrailingWhiteSpace("x"));
    Assert.assertEquals(0, TokenizerUtils.getTrailingWhiteSpace(" x"));
    Assert.assertEquals(0, TokenizerUtils.getTrailingWhiteSpace("  xxx"));
    Assert.assertEquals(1, TokenizerUtils.getTrailingWhiteSpace(" "));
    Assert.assertEquals(1, TokenizerUtils.getTrailingWhiteSpace("x "));
    Assert.assertEquals(1, TokenizerUtils.getTrailingWhiteSpace("  x "));
    Assert.assertEquals(2, TokenizerUtils.getTrailingWhiteSpace(" x  x\t\t"));
    Assert.assertEquals(2, TokenizerUtils.getTrailingWhiteSpace(" x x\t\t"));
    Assert.assertEquals(4, TokenizerUtils.getTrailingWhiteSpace("\t  \n"));
    Assert.assertEquals(4, TokenizerUtils.getTrailingWhiteSpace("x x\t  \n"));
  }

}
