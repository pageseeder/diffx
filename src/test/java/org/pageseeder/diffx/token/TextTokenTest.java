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
package org.pageseeder.diffx.token;

import org.junit.jupiter.api.Test;
import org.pageseeder.diffx.token.impl.WordToken;

public class TextTokenTest {


  @Test
  public void testWordToken() {
    WordToken a = new WordToken("a");
    WordToken b = new WordToken("b");
    WordToken c = new WordToken("c");
    WordToken ab = new WordToken("ab");
    WordToken bc = new WordToken("bc");
    WordToken abc = new WordToken("abc");
//    System.err.println(a.hashCode() + "/" + a.getCharacters().hashCode());
//    System.err.println(b.hashCode() + "/" + b.getCharacters().hashCode());
//    System.err.println(c.hashCode() + "/" + c.getCharacters().hashCode());
//    System.err.println(ab.hashCode() + "/" + ab.getCharacters().hashCode());
//    System.err.println(bc.hashCode() + "/" + bc.getCharacters().hashCode());
//    System.err.println(abc.hashCode() + "/" + abc.getCharacters().hashCode());
  }

}
