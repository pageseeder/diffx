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
package org.pageseeder.diffx.xml;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NamespaceSetTest {

  @Test
  public void testEmpty() {
    NamespaceSet namespaces = new NamespaceSet();
    assertTrue(namespaces.isEmpty());
    assertEquals(0, namespaces.size());
    assertFalse(namespaces.iterator().hasNext());
    assertNull(namespaces.getUri("ns"));
    assertNull(namespaces.getUri(""));
    assertNull(namespaces.getPrefix("https://ns.example"));
    assertNull(namespaces.getPrefix(""));
  }

  @Test
  public void testAddSingle() {
    NamespaceSet namespaces = new NamespaceSet();
    namespaces.add("https://ns.example", "ns");
    assertFalse(namespaces.isEmpty());
    assertEquals(1, namespaces.size());
    assertTrue(namespaces.iterator().hasNext());
    assertEquals(new Namespace("https://ns.example", "ns"), namespaces.iterator().next());
    // Match
    assertEquals("https://ns.example", namespaces.getUri("ns"));
    assertEquals("ns", namespaces.getPrefix("https://ns.example"));
    // No Match
    assertNull(namespaces.getUri("test"));
    assertNull(namespaces.getUri(""));
    assertNull(namespaces.getPrefix("https://test.example"));
    assertNull(namespaces.getPrefix(""));
  }

  @Test
  public void testAddSingle2() {
    NamespaceSet namespaces = new NamespaceSet();
    Namespace namespace = new Namespace("https://ns.example", "ns");
    boolean added = namespaces.add(namespace);
    assertTrue(added);
    assertFalse(namespaces.isEmpty());
    assertEquals(1, namespaces.size());
    assertTrue(namespaces.contains(namespace));
    assertTrue(namespaces.iterator().hasNext());
    assertEquals(namespace, namespaces.iterator().next());
    // Match
    assertEquals("https://ns.example", namespaces.getUri("ns"));
    assertEquals("ns", namespaces.getPrefix("https://ns.example"));
    // No match
    assertNull(namespaces.getUri("test"));
    assertNull(namespaces.getUri(""));
    assertNull(namespaces.getPrefix("https://test.example"));
    assertNull(namespaces.getPrefix(""));
  }

  @Test
  public void testAddMultiple() {
    NamespaceSet namespaces = new NamespaceSet();
    namespaces.add("https://ns1.example", "ns1");
    namespaces.add("https://ns2.example", "ns2");
    namespaces.add("https://ns3.example", "ns3");
    assertFalse(namespaces.isEmpty());
    assertEquals(3, namespaces.size());
    assertTrue(namespaces.iterator().hasNext());
    assertEquals("https://ns1.example", namespaces.getUri("ns1"));
    assertEquals("https://ns2.example", namespaces.getUri("ns2"));
    assertEquals("https://ns3.example", namespaces.getUri("ns3"));
    assertEquals("ns1", namespaces.getPrefix("https://ns1.example"));
    assertEquals("ns2", namespaces.getPrefix("https://ns2.example"));
    assertEquals("ns3", namespaces.getPrefix("https://ns3.example"));
    // No match
    assertNull(namespaces.getUri("test"));
    assertNull(namespaces.getUri(""));
    assertNull(namespaces.getPrefix("https://test.example"));
    assertNull(namespaces.getPrefix(""));
  }

  @Test
  public void testOverrideUri() {
    NamespaceSet namespaces = new NamespaceSet();
    Namespace namespace = new Namespace("https://ns.example", "ns");
    Namespace override = new Namespace("https://ns.example", "os");
    boolean added = namespaces.add(namespace);
    boolean overridden = namespaces.add(override);
    assertTrue(added);
    assertFalse(overridden);
    assertFalse(namespaces.isEmpty());
    assertEquals(1, namespaces.size());
    assertTrue(namespaces.contains(namespace));
    assertTrue(namespaces.iterator().hasNext());
    // Match
    assertEquals("https://ns.example", namespaces.getUri("ns"));
    assertEquals("ns", namespaces.getPrefix("https://ns.example"));
    // No match
    assertNull(namespaces.getUri("os"));
    assertNull(namespaces.getUri(""));
    assertNull(namespaces.getPrefix("https://test.example"));
    assertNull(namespaces.getPrefix(""));
  }

  @Test
  public void testOverridePrefix() {
    NamespaceSet namespaces = new NamespaceSet();
    Namespace namespace = new Namespace("https://ns.example", "ns");
    Namespace override = new Namespace("https://os.example", "ns");
    boolean added = namespaces.add(namespace);
    boolean overridden = namespaces.add(override);
    assertTrue(added);
    assertTrue(overridden);
    assertFalse(namespaces.isEmpty());
    assertEquals(2, namespaces.size());
    assertTrue(namespaces.contains(namespace));
    assertFalse(namespaces.contains(override));
    assertTrue(namespaces.iterator().hasNext());
    // Match
    assertEquals("https://ns.example", namespaces.getUri("ns"));
    assertEquals("ns", namespaces.getPrefix("https://ns.example"));
    assertNotNull(namespaces.getPrefix("https://os.example"));
    String newPrefix = namespaces.getPrefix("https://os.example");
    assertEquals("https://os.example", namespaces.getUri(newPrefix));
    // No match
    assertNull(namespaces.getUri("test"));
    assertNull(namespaces.getUri(""));
    assertNull(namespaces.getPrefix("https://test.example"));
    assertNull(namespaces.getPrefix(""));
  }

  @Test
  public void testOverrideDefaultPrefix() {
    NamespaceSet namespaces = new NamespaceSet();
    Namespace namespace = new Namespace("https://ns.example", "");
    Namespace override = new Namespace("https://os.example", "");
    namespaces.add(namespace);
    namespaces.add(override);
    assertFalse(namespaces.isEmpty());
    assertEquals(2, namespaces.size());
    assertTrue(namespaces.contains(namespace));
    assertFalse(namespaces.contains(override));
    assertTrue(namespaces.iterator().hasNext());
    // Match
    assertEquals("https://ns.example", namespaces.getUri(""));
    assertEquals("", namespaces.getPrefix("https://ns.example"));
    assertNotNull(namespaces.getPrefix("https://os.example"));
    String newPrefix = namespaces.getPrefix("https://os.example");
    assertEquals("https://os.example", namespaces.getUri(newPrefix));
    // No match
    assertNull(namespaces.getUri("test"));
    assertNull(namespaces.getPrefix("https://test.example"));
    assertNull(namespaces.getPrefix(""));
  }

  @Test
  public void testReplaceEmpty() {
    Namespace replacement = new Namespace("https://ns.example", "ns-bis");
    NamespaceSet namespaces = new NamespaceSet();
    namespaces.replace(replacement);
    assertEquals(1, namespaces.size());
    assertTrue(namespaces.contains(replacement));
    assertEquals("https://ns.example", namespaces.getUri("ns-bis"));
    assertEquals("ns-bis", namespaces.getPrefix("https://ns.example"));
  }

  @Test
  public void testReplaceUri() {
    Namespace namespace = new Namespace("https://ns.example", "ns");
    Namespace replacement = new Namespace("https://ns.example", "ns-bis");
    NamespaceSet namespaces = new NamespaceSet();
    namespaces.add(namespace);
    namespaces.replace(replacement);
    assertEquals(1, namespaces.size());
    assertFalse(namespaces.contains(namespace));
    assertTrue(namespaces.contains(replacement));
    assertEquals("https://ns.example", namespaces.getUri("ns-bis"));
    assertEquals("ns-bis", namespaces.getPrefix("https://ns.example"));
    assertNull(namespaces.getUri("ns"));
  }

  @Test
  public void testReplacePrefix() {
    Namespace namespace = new Namespace("https://os.example", "ns");
    Namespace replacement = new Namespace("https://ns.example", "ns");
    NamespaceSet namespaces = new NamespaceSet();
    namespaces.add(namespace);
    namespaces.replace(replacement);
    assertEquals(2, namespaces.size());
    assertFalse(namespaces.contains(namespace));
    assertTrue(namespaces.contains(replacement));
    assertEquals("https://ns.example", namespaces.getUri("ns"));
    assertEquals("ns", namespaces.getPrefix("https://ns.example"));
    assertNotNull(namespaces.getPrefix("https://os.example"));
  }

  @Test
  public void testReplaceDefault1() {
    Namespace replacement = new Namespace("https://ns.example", "");
    NamespaceSet namespaces = NamespaceSet.noNamespace();
    namespaces.replace(replacement);
    assertEquals(1, namespaces.size());
    assertTrue(namespaces.contains(replacement));
    assertEquals("https://ns.example", namespaces.getUri(""));
    assertEquals("", namespaces.getPrefix("https://ns.example"));
    assertNull(namespaces.getPrefix(""));
  }

  @Test
  public void testReplaceDefault2() {
    Namespace namespace = new Namespace("https://default.example", "");
    Namespace replacement = new Namespace("https://ns.example", "");
    NamespaceSet namespaces = new NamespaceSet();
    namespaces.add(namespace);
    namespaces.replace(replacement);
    assertEquals(2, namespaces.size());
    assertFalse(namespaces.contains(namespace));
    assertTrue(namespaces.contains(replacement));
    assertEquals("https://ns.example", namespaces.getUri(""));
    assertEquals("", namespaces.getPrefix("https://ns.example"));
    assertNotNull(namespaces.getPrefix("https://default.example"));
  }

  @Test
  public void testAddDefault3() {
    Namespace namespace = new Namespace("https://example.org", "");
    Namespace svg = new Namespace("http://www.w3.org/2000/svg", "");
    Namespace xlink = new Namespace("http://www.w3.org/1999/xlink", "");
    Namespace notSvg = new Namespace("http://example.org/notsvg", "svg");
    NamespaceSet namespaces = new NamespaceSet();
    namespaces.add(namespace);
    namespaces.add(svg);
    namespaces.add(xlink);
    namespaces.add(notSvg);
    assertEquals(4, namespaces.size());
    assertTrue(namespaces.contains(namespace));
    assertEquals("http://www.w3.org/2000/svg", namespaces.getUri("svg"));
    assertEquals("svg", namespaces.getPrefix("http://www.w3.org/2000/svg"));
    assertNotNull(namespaces.getPrefix("http://example.org/notsvg"));
  }

  @Test
  public void testClear() {
    NamespaceSet namespaces = new NamespaceSet();
    namespaces.add("https://ns1.example", "ns1");
    namespaces.add("https://ns2.example", "ns2");
    namespaces.add("https://ns3.example", "ns3");
    assertFalse(namespaces.isEmpty());
    assertEquals(3, namespaces.size());
    namespaces.clear();
    assertTrue(namespaces.isEmpty());
    assertEquals(0, namespaces.size());
    assertNull(namespaces.getPrefix("ns1"));
    assertNull(namespaces.getUri("https://ns1.example"));
  }

  @Test
  public void testMergeSame() {
    Namespace namespace1 = new Namespace("https://ns1.example", "ns1");
    NamespaceSet namespaces1 = new NamespaceSet(namespace1);
    NamespaceSet namespaces2 = new NamespaceSet(namespace1);
    NamespaceSet merged = NamespaceSet.merge(namespaces1, namespaces2);
    assertFalse(merged.isEmpty());
    assertEquals(1, merged.size());
    assertTrue(merged.contains(namespace1));
  }

  @Test
  public void testMerge1() {
    Namespace namespace1 = new Namespace("https://ns1.example", "ns1");
    Namespace namespace2 = new Namespace("https://ns2.example", "ns2");
    NamespaceSet namespaces1 = new NamespaceSet(namespace1);
    NamespaceSet namespaces2 = new NamespaceSet(namespace2);
    NamespaceSet merged = NamespaceSet.merge(namespaces1, namespaces2);
    assertFalse(merged.isEmpty());
    assertEquals(2, merged.size());
    assertTrue(merged.contains(namespace1));
    assertTrue(merged.contains(namespace2));
  }

  @Test
  public void testMerge2() {
    Namespace namespace1 = new Namespace("https://ns1.example", "ns1");
    NamespaceSet namespaces1 = new NamespaceSet(namespace1);
    NamespaceSet namespaces2 = new NamespaceSet(namespace1);
    NamespaceSet merged = NamespaceSet.merge(namespaces1, namespaces2);
    assertFalse(merged.isEmpty());
    assertEquals(1, merged.size());
    assertTrue(merged.contains(namespace1));
  }

}
