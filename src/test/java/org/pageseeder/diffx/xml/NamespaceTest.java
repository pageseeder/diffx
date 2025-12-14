/*
 * Copyright 2010-2021 Allette Systems (Australia)
 *    http://www.allette.com.au
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

package org.pageseeder.diffx.xml;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

final class NamespaceTest {

  /**
   * Not an exhaustive test, just a sanity check.
   */
  @Test
  void testEquals() {
    Namespace example = new Namespace("http://example.org", "example");
    Namespace copy = new Namespace("http://example.org", "example");
    Namespace other1 = new Namespace("http://example.com", "example");
    Namespace other2 = new Namespace("http://example.org", "ex");
    Namespace other3 = new Namespace("http://example.com", "ex");
    Assertions.assertEquals(example, example);
    Assertions.assertEquals(example, copy);
    Assertions.assertEquals(example.hashCode(), copy.hashCode());
    Assertions.assertNotEquals(example, null);
    Assertions.assertNotEquals(example, other1);
    Assertions.assertNotEquals(example.hashCode(), other1.hashCode());
    Assertions.assertNotEquals(example, other2);
    Assertions.assertNotEquals(example.hashCode(), other2.hashCode());
    Assertions.assertNotEquals(example, other3);
    Assertions.assertNotEquals(example.hashCode(), other3.hashCode());
  }

  @Test
  void testGetCommonNamespace() {
    Namespace svg = new Namespace("http://www.w3.org/2000/svg", "svg");
    Namespace xlink = new Namespace("http://www.w3.org/1999/xlink", "xlink");
    Assertions.assertNull(Namespace.getCommon("https://example.org/1999/does_not_exists"));
    Assertions.assertEquals(svg, Namespace.getCommon("http://www.w3.org/2000/svg"));
    Assertions.assertEquals(xlink, Namespace.getCommon("http://www.w3.org/1999/xlink"));
  }

}
