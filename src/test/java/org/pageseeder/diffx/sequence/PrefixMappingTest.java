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
package org.pageseeder.diffx.sequence;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class PrefixMappingTest {

  @Test
  public void testEmpty() {
    PrefixMapping mapping = new PrefixMapping();
    assertTrue(mapping.isEmpty());
    assertEquals(0, mapping.size());
    assertFalse(mapping.iterator().hasNext());
    assertNull(mapping.getUri("ns"));
    assertNull(mapping.getUri(""));
    assertNull(mapping.getPrefix("https://ns.example"));
    assertNull(mapping.getPrefix(""));
  }

  @Test
  public void testAddSingle() {
    PrefixMapping mapping = new PrefixMapping();
    mapping.add("https://ns.example", "ns");
    assertFalse(mapping.isEmpty());
    assertEquals(1, mapping.size());
    assertTrue(mapping.iterator().hasNext());
    assertEquals(new Namespace("https://ns.example", "ns"), mapping.iterator().next());
    // Match
    assertEquals("https://ns.example", mapping.getUri("ns"));
    assertEquals("ns", mapping.getPrefix("https://ns.example"));
    // No Match
    assertNull(mapping.getUri("test"));
    assertNull(mapping.getUri(""));
    assertNull(mapping.getPrefix("https://test.example"));
    assertNull(mapping.getPrefix(""));
  }

  @Test
  public void testAddSingle2() {
    PrefixMapping mapping = new PrefixMapping();
    Namespace namespace = new Namespace("https://ns.example", "ns");
    boolean added = mapping.add(namespace);
    assertTrue(added);
    assertFalse(mapping.isEmpty());
    assertEquals(1, mapping.size());
    assertTrue(mapping.contains(namespace));
    assertTrue(mapping.iterator().hasNext());
    assertEquals(namespace, mapping.iterator().next());
    // Match
    assertEquals("https://ns.example", mapping.getUri("ns"));
    assertEquals("ns", mapping.getPrefix("https://ns.example"));
    // No match
    assertNull(mapping.getUri("test"));
    assertNull(mapping.getUri(""));
    assertNull(mapping.getPrefix("https://test.example"));
    assertNull(mapping.getPrefix(""));
  }

  @Test
  public void testAddMultiple() {
    PrefixMapping mapping = new PrefixMapping();
    mapping.add("https://ns1.example", "ns1");
    mapping.add("https://ns2.example", "ns2");
    mapping.add("https://ns3.example", "ns3");
    assertFalse(mapping.isEmpty());
    assertEquals(3, mapping.size());
    assertTrue(mapping.iterator().hasNext());
    assertEquals("https://ns1.example", mapping.getUri("ns1"));
    assertEquals("https://ns2.example", mapping.getUri("ns2"));
    assertEquals("https://ns3.example", mapping.getUri("ns3"));
    assertEquals("ns1", mapping.getPrefix("https://ns1.example"));
    assertEquals("ns2", mapping.getPrefix("https://ns2.example"));
    assertEquals("ns3", mapping.getPrefix("https://ns3.example"));
    // No match
    assertNull(mapping.getUri("test"));
    assertNull(mapping.getUri(""));
    assertNull(mapping.getPrefix("https://test.example"));
    assertNull(mapping.getPrefix(""));
  }

  @Test
  public void testOverrideUri() {
    PrefixMapping mapping = new PrefixMapping();
    Namespace namespace = new Namespace("https://ns.example", "ns");
    Namespace override = new Namespace("https://ns.example", "os");
    boolean added = mapping.add(namespace);
    boolean overridden = mapping.add(override);
    assertTrue(added);
    assertFalse(overridden);
    assertFalse(mapping.isEmpty());
    assertEquals(1, mapping.size());
    assertTrue(mapping.contains(namespace));
    assertTrue(mapping.iterator().hasNext());
    // Match
    assertEquals("https://ns.example", mapping.getUri("ns"));
    assertEquals("ns", mapping.getPrefix("https://ns.example"));
    // No match
    assertNull(mapping.getUri("os"));
    assertNull(mapping.getUri(""));
    assertNull(mapping.getPrefix("https://test.example"));
    assertNull(mapping.getPrefix(""));
  }

  @Test
  public void testOverridePrefix() {
    PrefixMapping mapping = new PrefixMapping();
    Namespace namespace = new Namespace("https://ns.example", "ns");
    Namespace override = new Namespace("https://os.example", "ns");
    boolean added = mapping.add(namespace);
    boolean overridden = mapping.add(override);
    assertTrue(added);
    assertTrue(overridden);
    assertFalse(mapping.isEmpty());
    assertEquals(2, mapping.size());
    assertTrue(mapping.contains(namespace));
    assertFalse(mapping.contains(override));
    assertTrue(mapping.iterator().hasNext());
    // Match
    assertEquals("https://ns.example", mapping.getUri("ns"));
    assertEquals("ns", mapping.getPrefix("https://ns.example"));
    assertNotNull(mapping.getPrefix("https://os.example"));
    String newPrefix = mapping.getPrefix("https://os.example");
    assertEquals("https://os.example", mapping.getUri(newPrefix));
    // No match
    assertNull(mapping.getUri("test"));
    assertNull(mapping.getUri(""));
    assertNull(mapping.getPrefix("https://test.example"));
    assertNull(mapping.getPrefix(""));
  }

  @Test
  public void testOverrideDefaultPrefix() {
    PrefixMapping mapping = new PrefixMapping();
    Namespace namespace = new Namespace("https://ns.example", "");
    Namespace override = new Namespace("https://os.example", "");
    mapping.add(namespace);
    mapping.add(override);
    assertFalse(mapping.isEmpty());
    assertEquals(2, mapping.size());
    assertTrue(mapping.contains(namespace));
    assertFalse(mapping.contains(override));
    assertTrue(mapping.iterator().hasNext());
    // Match
    assertEquals("https://ns.example", mapping.getUri(""));
    assertEquals("", mapping.getPrefix("https://ns.example"));
    assertNotNull(mapping.getPrefix("https://os.example"));
    String newPrefix = mapping.getPrefix("https://os.example");
    assertEquals("https://os.example", mapping.getUri(newPrefix));
    // No match
    assertNull(mapping.getUri("test"));
    assertNull(mapping.getPrefix("https://test.example"));
    assertNull(mapping.getPrefix(""));
  }

  @Test
  public void testReplaceEmpty() {
    Namespace replacement = new Namespace("https://ns.example", "ns-bis");
    PrefixMapping mapping = new PrefixMapping();
    mapping.replace(replacement);
    assertEquals(1, mapping.size());
    assertTrue(mapping.contains(replacement));
    assertEquals("https://ns.example", mapping.getUri("ns-bis"));
    assertEquals("ns-bis", mapping.getPrefix("https://ns.example"));
  }

  @Test
  public void testReplaceUri() {
    Namespace namespace = new Namespace("https://ns.example", "ns");
    Namespace replacement = new Namespace("https://ns.example", "ns-bis");
    PrefixMapping mapping = new PrefixMapping();
    mapping.add(namespace);
    mapping.replace(replacement);
    assertEquals(1, mapping.size());
    assertFalse(mapping.contains(namespace));
    assertTrue(mapping.contains(replacement));
    assertEquals("https://ns.example", mapping.getUri("ns-bis"));
    assertEquals("ns-bis", mapping.getPrefix("https://ns.example"));
    assertNull(mapping.getUri("ns"));
  }

  @Test
  public void testReplacePrefix() {
    Namespace namespace = new Namespace("https://os.example", "ns");
    Namespace replacement = new Namespace("https://ns.example", "ns");
    PrefixMapping mapping = new PrefixMapping();
    mapping.add(namespace);
    mapping.replace(replacement);
    assertEquals(2, mapping.size());
    assertFalse(mapping.contains(namespace));
    assertTrue(mapping.contains(replacement));
    assertEquals("https://ns.example", mapping.getUri("ns"));
    assertEquals("ns", mapping.getPrefix("https://ns.example"));
    assertNotNull(mapping.getPrefix("https://os.example"));
  }

  @Test
  public void testReplaceDefault1() {
    Namespace replacement = new Namespace("https://ns.example", "");
    PrefixMapping mapping = PrefixMapping.noNamespace();
    mapping.replace(replacement);
    assertEquals(1, mapping.size());
    assertTrue(mapping.contains(replacement));
    assertEquals("https://ns.example", mapping.getUri(""));
    assertEquals("", mapping.getPrefix("https://ns.example"));
    assertNull(mapping.getPrefix(""));
  }

  @Test
  public void testReplaceDefault2() {
    Namespace namespace = new Namespace("https://default.example", "");
    Namespace replacement = new Namespace("https://ns.example", "");
    PrefixMapping mapping = new PrefixMapping();
    mapping.add(namespace);
    mapping.replace(replacement);
    assertEquals(2, mapping.size());
    assertFalse(mapping.contains(namespace));
    assertTrue(mapping.contains(replacement));
    assertEquals("https://ns.example", mapping.getUri(""));
    assertEquals("", mapping.getPrefix("https://ns.example"));
    assertNotNull(mapping.getPrefix("https://default.example"));
  }

  @Test
  public void testClear() {
    PrefixMapping mapping = new PrefixMapping();
    mapping.add("https://ns1.example", "ns1");
    mapping.add("https://ns2.example", "ns2");
    mapping.add("https://ns3.example", "ns3");
    assertFalse(mapping.isEmpty());
    assertEquals(3, mapping.size());
    mapping.clear();
    assertTrue(mapping.isEmpty());
    assertEquals(0, mapping.size());
    assertNull(mapping.getPrefix("ns1"));
    assertNull(mapping.getUri("https://ns1.example"));
  }

  @Test
  public void testMergeSame() {
    Namespace namespace1 = new Namespace("https://ns1.example", "ns1");
    PrefixMapping mapping1 = new PrefixMapping(namespace1);
    PrefixMapping mapping2 = new PrefixMapping(namespace1);
    PrefixMapping merged = PrefixMapping.merge(mapping1, mapping2);
    assertFalse(merged.isEmpty());
    assertEquals(1, merged.size());
    assertTrue(merged.contains(namespace1));
  }

  @Test
  public void testMerge1() {
    Namespace namespace1 = new Namespace("https://ns1.example", "ns1");
    Namespace namespace2 = new Namespace("https://ns2.example", "ns2");
    PrefixMapping mapping1 = new PrefixMapping(namespace1);
    PrefixMapping mapping2 = new PrefixMapping(namespace2);
    PrefixMapping merged = PrefixMapping.merge(mapping1, mapping2);
    assertFalse(merged.isEmpty());
    assertEquals(2, merged.size());
    assertTrue(merged.contains(namespace1));
    assertTrue(merged.contains(namespace2));
  }

  @Test
  public void testMerge2() {
    Namespace namespace1 = new Namespace("https://ns1.example", "ns1");
    PrefixMapping mapping1 = new PrefixMapping(namespace1);
    PrefixMapping mapping2 = new PrefixMapping(namespace1);
    PrefixMapping merged = PrefixMapping.merge(mapping1, mapping2);
    assertFalse(merged.isEmpty());
    assertEquals(1, merged.size());
    assertTrue(merged.contains(namespace1));
  }

}
