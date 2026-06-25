[![Maven Central](https://img.shields.io/maven-central/v/org.pageseeder.diffx/pso-diffx.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22org.pageseeder.diffx%22%20AND%20a:%22pso-diffx%22)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=pageseeder_diffx&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=pageseeder_diffx)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=pageseeder_diffx&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=pageseeder_diffx)
[![javadoc](https://javadoc.io/badge2/org.pageseeder.diffx/pso-diffx/javadoc.svg)](https://javadoc.io/doc/org.pageseeder.diffx/pso-diffx)

# Diff-X

A Java library for comparing XML documents by analysing sequences of XML tokens. Diff-X implements multiple
LCS/SES (Longest Common Subsequence / Shortest Edit Script) algorithms and provides XML-aware diffing that
guarantees well-formed output.

Diff-X targets **Java 11+** and is published on Maven Central as `org.pageseeder.diffx:pso-diffx`.

## Getting started

### Maven

```xml
<dependency>
  <groupId>org.pageseeder.diffx</groupId>
  <artifactId>pso-diffx</artifactId>
  <version>1.3.3</version>
</dependency>
```

### Gradle

```kotlin
implementation("org.pageseeder.diffx:pso-diffx:1.3.3")
```

## Packages

| Package | Purpose |
|---|---|
| `org.pageseeder.diffx.api` | Core interfaces: `Loader`, `DiffAlgorithm`, `DiffHandler`, `Operator`, `Token` |
| `org.pageseeder.diffx.config` | `DiffConfig` (immutable, fluent), `WhiteSpaceProcessing`, `TextGranularity` |
| `org.pageseeder.diffx.token` | `XMLToken` hierarchy: `StartElementToken`, `EndElementToken`, `AttributeToken`, `TextToken` |
| `org.pageseeder.diffx.load` | XML loaders: `SAXLoader`, `DOMLoader`, `XMLStreamLoader`, `XMLEventLoader`, `LineLoader` |
| `org.pageseeder.diffx.algorithm` | Algorithm implementations (Myers, Hirschberg, Kumar-Rangan, Wagner-Fischer, Wu, Patience, Histogram, etc.) |
| `org.pageseeder.diffx.core` | High-level processors: `DefaultXMLProcessor`, `OptimisticXMLProcessor`, `TextOnlyProcessor` |
| `org.pageseeder.diffx.handler` | `DiffHandler` implementations and filters: `CoalescingFilter`, `PostXMLFixer`, `MuxHandler` |
| `org.pageseeder.diffx.format` | `XMLDiffOutput` implementations that write XML-annotated diff output |
| `org.pageseeder.diffx.action` | `Operation`/`Action` types for buffering and replaying diff results |
| `org.pageseeder.diffx.similarity` | Similarity metrics (Cosine, Jaccard, Edit distance) built on top of the diff engine |
| `org.pageseeder.diffx.sequence` | `TokenListSlicer` for removing common prefix/suffix before diffing |
| `org.pageseeder.diffx.xml` | XML-specific utilities: `Namespace`, `NamespaceSet`, `Sequence` |

## API usage

The diff pipeline has three stages, each defined by an interface in `org.pageseeder.diffx.api`:

1. **`Loader<T>`** — converts a source (file, string, DOM node, etc.) into a `List<T>` of tokens
2. **`DiffAlgorithm<T>`** — computes the SES between two token lists and reports operations to a `DiffHandler<T>`
3. **`DiffHandler<T>`** — receives `handle(Operator, T)` callbacks and produces output

### Quick diff between two XML files

The simplest way to diff two XML files:

```java
import org.pageseeder.diffx.Main;

// Using Reader/Writer
Reader xmlA = new FileReader("old.xml");
Reader xmlB = new FileReader("new.xml");
Writer out = new FileWriter("diff.xml");
Main.diff(xmlA, xmlB, out);
```

### Diff with custom configuration

Use `DiffConfig` to control whitespace handling and text granularity:

```java
import org.pageseeder.diffx.config.*;
import org.pageseeder.diffx.load.SAXLoader;
import org.pageseeder.diffx.core.DefaultXMLProcessor;
import org.pageseeder.diffx.format.DefaultXMLDiffOutput;
import org.pageseeder.diffx.xml.*;

// Configure the diff
DiffConfig config = DiffConfig.getDefault()
    .whitespace(WhiteSpaceProcessing.IGNORE)
    .granularity(TextGranularity.WORD);

// Load XML sequences
SAXLoader loader = new SAXLoader();
loader.setConfig(config);
Sequence seqA = loader.load(new File("old.xml"));
Sequence seqB = loader.load(new File("new.xml"));

// Set up the output
Writer out = new FileWriter("diff.xml");
DefaultXMLDiffOutput output = new DefaultXMLDiffOutput(out);
output.setNamespaces(NamespaceSet.merge(seqA.getNamespaces(), seqB.getNamespaces()));

// Run the diff
DefaultXMLProcessor processor = new DefaultXMLProcessor();
processor.diff(seqA.tokens(), seqB.tokens(), output);
```

### Check XML equivalence

```java
boolean same = Main.equivalent(new File("a.xml"), new File("b.xml"));
```

### Diff DOM nodes

```java
import org.w3c.dom.Node;
import org.pageseeder.diffx.Main;

Main.diff(nodeA, nodeB, writer, config);
```

### Configuration options

`DiffConfig` is immutable — each setter returns a new instance:

| Method | Values | Default |
|---|---|---|
| `whitespace()` | `PRESERVE`, `COMPARE`, `IGNORE` | `COMPARE` |
| `granularity()` | `TEXT`, `PUNCTUATION`, `SPACE_WORD`, `WORD`, `CHARACTER` | `SPACE_WORD` |
| `noNamespaces()` | — | namespace-aware |
| `allowDoctypeDeclaration()` | `true`, `false` | `false` |

### Processors

| Processor | Use case |
|---|---|
| `DefaultXMLProcessor` | General-purpose XML diffing (recommended) |
| `OptimisticXMLProcessor` | Tries the fast generic path first, falls back to XML-aware algorithm if the result is not well-formed |
| `TextOnlyProcessor` | Plain text / line-by-line comparison |

### Output formats

| Format | Description |
|---|---|
| `DefaultXMLDiffOutput` | Annotated XML diff with `dfx` and `del` namespace prefixes |
| `CompleteXMLDiffOutput` | Annotates all tokens (including matches) |
| `StrictXMLDiffOutput` | Strict well-formed output |
| `XMLDiffReporter` | Reports differences as XML |

## Command-line usage

After building the project, you can compare two XML files from the command line:

```
java -jar pso-diffx-<version>.jar [options] xml_file1 xml_file2
```

Where `xml_file1` is the path to the new XML file and `xml_file2` is the path to the old XML file.

### Options

| Option | Values | Default | Description |
|---|---|---|---|
| `-o [output]` | filename | stdout | Output file |
| `-l [loader]` | `sax`, `dom`, `stream`, `stax` | `sax` | XML loader to use |
| `-p [processor]` | `optimistic`, `xml`, `text` | `optimistic` | Diff processor |
| `-f [format]` | `default`, `complete`, `strict`, `report` | `default` | Output format |
| `-w [whitespace]` | `preserve`, `compare`, `ignore` | `preserve` | Whitespace processing |
| `-g [granularity]` | `word`, `text`, `character` | `word` | Text granularity |
| `-profile` | — | — | Display profiling info |
| `-allowdoctype` | — | — | Allow DOCTYPE declarations (caution: XXE risk) |

## Using Diff-X as an XSLT extension

Diff-X provides static methods which can be used in XSLT as extension functions.

### Saxon

Ensure that **saxon-dom.jar** is included in your classpath as Diff-X will compare DOM nodes.

With Saxon-PE or Saxon-EE reflexive Java extension functions, declare the extension namespace as
the Java class name:

In your XSLT, declare the namespace as:

```xml
<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:diffx="org.pageseeder.diffx.Extension"
    extension-element-prefixes="diffx">
```

Then call Diff-X within your XSLT:

```xml
<xsl:copy-of select="diffx:diff(/node1/to/compare, /node2/to/compare, 'IGNORE', 'WORD')"/>
```

Saxon-HE no longer supports reflexive Java extension functions. With Saxon-HE 9.8 and later,
including Saxon-HE 12.x, register the Diff-X function explicitly with Saxon's s9api
`Processor.registerExtensionFunction()` API.

Parameters:
- `node1` — the first node to compare (Node)
- `node2` — the second node to compare (Node)
- `whitespace` — `COMPARE`, `PRESERVE`, or `IGNORE`
- `granularity` — `TEXT`, `PUNCTUATION`, `SPACE_WORD`, `WORD`, or `CHARACTER`

## Building

```bash
# Build
./gradlew build

# Run tests
./gradlew test

# Run a single test class
./gradlew test --tests "org.pageseeder.diffx.core.OptimisticXMLProcessorTest"

# Run JMH benchmarks
./gradlew jmh

# Generate JaCoCo coverage report
./gradlew jacocoTestReport

# Publish to local Maven repository
./gradlew publishToMavenLocal
```

## License

Apache License 2.0

## History

This is the official home of Diff-X. The project has moved over the years, from
[SourceForge](https://sourceforge.net/projects/diffx/) to
[Google Code](https://code.google.com/p/wo-diffx/) and finally to GitHub.
