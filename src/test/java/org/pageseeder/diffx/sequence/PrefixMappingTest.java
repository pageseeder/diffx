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

import org.junit.Assert;
import org.junit.Test;

public class PrefixMappingTest {

  @Test
  public void testEmpty() {
    PrefixMapping mapping = new PrefixMapping();
    Assert.assertTrue(mapping.isEmpty());
    Assert.assertEquals(0, mapping.size());
    Assert.assertFalse(mapping.iterator().hasNext());
    Assert.assertNull(mapping.getUri("ns"));
    Assert.assertNull(mapping.getUri(""));
    Assert.assertNull(mapping.getPrefix("https://ns.example"));
    Assert.assertNull(mapping.getPrefix(""));
  }

  @Test
  public void testAddSingle() {
    PrefixMapping mapping = new PrefixMapping();
    mapping.add("https://ns.example", "ns");
    Assert.assertFalse(mapping.isEmpty());
    Assert.assertEquals(1, mapping.size());
    Assert.assertTrue(mapping.iterator().hasNext());
    Assert.assertEquals(new Namespace("https://ns.example", "ns"), mapping.iterator().next());
    // Match
    Assert.assertEquals("https://ns.example", mapping.getUri("ns"));
    Assert.assertEquals("ns", mapping.getPrefix("https://ns.example"));
    // No Match
    Assert.assertNull(mapping.getUri("test"));
    Assert.assertNull(mapping.getUri(""));
    Assert.assertNull(mapping.getPrefix("https://test.example"));
    Assert.assertNull(mapping.getPrefix(""));
  }

  @Test
  public void testAddSingle2() {
    PrefixMapping mapping = new PrefixMapping();
    Namespace namespace = new Namespace("https://ns.example", "ns");
    boolean added = mapping.add(namespace);
    Assert.assertTrue(added);
    Assert.assertFalse(mapping.isEmpty());
    Assert.assertEquals(1, mapping.size());
    Assert.assertTrue(mapping.contains(namespace));
    Assert.assertTrue(mapping.iterator().hasNext());
    Assert.assertEquals(namespace, mapping.iterator().next());
    // Match
    Assert.assertEquals("https://ns.example", mapping.getUri("ns"));
    Assert.assertEquals("ns", mapping.getPrefix("https://ns.example"));
    // No match
    Assert.assertNull(mapping.getUri("test"));
    Assert.assertNull(mapping.getUri(""));
    Assert.assertNull(mapping.getPrefix("https://test.example"));
    Assert.assertNull(mapping.getPrefix(""));
  }

  @Test
  public void testAddMultiple() {
    PrefixMapping mapping = new PrefixMapping();
    mapping.add("https://ns1.example", "ns1");
    mapping.add("https://ns2.example", "ns2");
    mapping.add("https://ns3.example", "ns3");
    Assert.assertFalse(mapping.isEmpty());
    Assert.assertEquals(3, mapping.size());
    Assert.assertTrue(mapping.iterator().hasNext());
    Assert.assertEquals("https://ns1.example", mapping.getUri("ns1"));
    Assert.assertEquals("https://ns2.example", mapping.getUri("ns2"));
    Assert.assertEquals("https://ns3.example", mapping.getUri("ns3"));
    Assert.assertEquals("ns1", mapping.getPrefix("https://ns1.example"));
    Assert.assertEquals("ns2", mapping.getPrefix("https://ns2.example"));
    Assert.assertEquals("ns3", mapping.getPrefix("https://ns3.example"));
    // No match
    Assert.assertNull(mapping.getUri("test"));
    Assert.assertNull(mapping.getUri(""));
    Assert.assertNull(mapping.getPrefix("https://test.example"));
    Assert.assertNull(mapping.getPrefix(""));
  }

  @Test
  public void testOverrideUri() {
    PrefixMapping mapping = new PrefixMapping();
    Namespace namespace = new Namespace("https://ns.example", "ns");
    Namespace override = new Namespace("https://ns.example", "os");
    boolean added = mapping.add(namespace);
    boolean overridden = mapping.add(override);
    Assert.assertTrue(added);
    Assert.assertFalse(overridden);
    Assert.assertFalse(mapping.isEmpty());
    Assert.assertEquals(1, mapping.size());
    Assert.assertTrue(mapping.contains(namespace));
    Assert.assertTrue(mapping.iterator().hasNext());
    // Match
    Assert.assertEquals("https://ns.example", mapping.getUri("ns"));
    Assert.assertEquals("ns", mapping.getPrefix("https://ns.example"));
    // No match
    Assert.assertNull(mapping.getUri("os"));
    Assert.assertNull(mapping.getUri(""));
    Assert.assertNull(mapping.getPrefix("https://test.example"));
    Assert.assertNull(mapping.getPrefix(""));
  }

  @Test
  public void testOverridePrefix() {
    PrefixMapping mapping = new PrefixMapping();
    Namespace namespace = new Namespace("https://ns.example", "ns");
    Namespace override = new Namespace("https://os.example", "ns");
    boolean added = mapping.add(namespace);
    boolean overridden = mapping.add(override);
    Assert.assertTrue(added);
    Assert.assertTrue(overridden);
    Assert.assertFalse(mapping.isEmpty());
    Assert.assertEquals(2, mapping.size());
    Assert.assertTrue(mapping.contains(namespace));
    Assert.assertFalse(mapping.contains(override));
    Assert.assertTrue(mapping.iterator().hasNext());
    // Match
    Assert.assertEquals("https://ns.example", mapping.getUri("ns"));
    Assert.assertEquals("ns", mapping.getPrefix("https://ns.example"));
    Assert.assertNotNull(mapping.getPrefix("https://os.example"));
    String newPrefix = mapping.getPrefix("https://os.example");
    Assert.assertEquals("https://os.example", mapping.getUri(newPrefix));
    // No match
    Assert.assertNull(mapping.getUri("test"));
    Assert.assertNull(mapping.getUri(""));
    Assert.assertNull(mapping.getPrefix("https://test.example"));
    Assert.assertNull(mapping.getPrefix(""));
  }

  @Test
  public void testOverrideDefaultPrefix() {
    PrefixMapping mapping = new PrefixMapping();
    Namespace namespace = new Namespace("https://ns.example", "");
    Namespace override = new Namespace("https://os.example", "");
    mapping.add(namespace);
    mapping.add(override);
    Assert.assertFalse(mapping.isEmpty());
    Assert.assertEquals(2, mapping.size());
    Assert.assertTrue(mapping.contains(namespace));
    Assert.assertFalse(mapping.contains(override));
    Assert.assertTrue(mapping.iterator().hasNext());
    // Match
    Assert.assertEquals("https://ns.example", mapping.getUri(""));
    Assert.assertEquals("", mapping.getPrefix("https://ns.example"));
    Assert.assertNotNull(mapping.getPrefix("https://os.example"));
    String newPrefix = mapping.getPrefix("https://os.example");
    Assert.assertEquals("https://os.example", mapping.getUri(newPrefix));
    // No match
    Assert.assertNull(mapping.getUri("test"));
    Assert.assertNull(mapping.getPrefix("https://test.example"));
    Assert.assertNull(mapping.getPrefix(""));
  }

  @Test
  public void testClear() {
    PrefixMapping mapping = new PrefixMapping();
    mapping.add("https://ns1.example", "ns1");
    mapping.add("https://ns2.example", "ns2");
    mapping.add("https://ns3.example", "ns3");
    Assert.assertFalse(mapping.isEmpty());
    Assert.assertEquals(3, mapping.size());
    mapping.clear();
    Assert.assertTrue(mapping.isEmpty());
    Assert.assertEquals(0, mapping.size());
    Assert.assertNull(mapping.getPrefix("ns1"));
    Assert.assertNull(mapping.getUri("https://ns1.example"));
  }

  @Test
  public void testMergeSame() {
    Namespace namespace1 = new Namespace("https://ns1.example", "ns1");
    PrefixMapping mapping1 = new PrefixMapping(namespace1);
    PrefixMapping mapping2 = new PrefixMapping(namespace1);
    PrefixMapping merged = PrefixMapping.merge(mapping1, mapping2);
    Assert.assertFalse(merged.isEmpty());
    Assert.assertEquals(1, merged.size());
    Assert.assertTrue(merged.contains(namespace1));
  }

  @Test
  public void testMerge1() {
    Namespace namespace1 = new Namespace("https://ns1.example", "ns1");
    Namespace namespace2 = new Namespace("https://ns2.example", "ns2");
    PrefixMapping mapping1 = new PrefixMapping(namespace1);
    PrefixMapping mapping2 = new PrefixMapping(namespace2);
    PrefixMapping merged = PrefixMapping.merge(mapping1, mapping2);
    Assert.assertFalse(merged.isEmpty());
    Assert.assertEquals(2, merged.size());
    Assert.assertTrue(merged.contains(namespace1));
    Assert.assertTrue(merged.contains(namespace2));
  }

  @Test
  public void testMerge2() {
    Namespace namespace1 = new Namespace("https://ns1.example", "ns1");
    PrefixMapping mapping1 = new PrefixMapping(namespace1);
    PrefixMapping mapping2 = new PrefixMapping(namespace1);
    PrefixMapping merged = PrefixMapping.merge(mapping1, mapping2);
    Assert.assertFalse(merged.isEmpty());
    Assert.assertEquals(1, merged.size());
    Assert.assertTrue(merged.contains(namespace1));
  }

}
