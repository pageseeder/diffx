[ ![Download](https://api.bintray.com/packages/pageseeder/maven/diffx/images/download.svg) ](https://bintray.com/pageseeder/maven/diffx/_latestVersion)

# DiffX

A java API for comparing XML documents by analysing the sequence of XML events.

DiffX was designed only to handle small documents, but be

## Overview

To invoke Diff-x from the command-line to compare two file, you can simply use the `java -jar diffx-xx.jar` command 
followed by the options below:
```
  Main [options] xml_file1 xml_file2
  where
     xml_file1 = Path to the new XML file
     xml_file2 = Path to the old XML file
  options:
    -profile    Display profiling info
    -noslice    Do not use slicing
    -o [output] The output file
    -L [loader] Choose a specific loader
                sax* | dom | text
    -A [algo]   Choose a specific algorithm
                fitsy* | guano | fitopsy | kumar | wesyma
    -F [format] Choose a specific formatter
                smart* | basic | convenient | strict | short
    -W [wsp]    Define whitespace processing
                preserve* | compare | ignore
    -G [granul] Define text diffing granularity
                word* | text | character
    * indicates option used by default.
```

## Basic options

### `-o [output]` option

By default, Diff-X sends the diffing results to the standard output. 
Use this option to specify in which file the diffing result should be stored. `[output]` should be a filename.

### `-L [loader]` option

Choose a specific loader, to creates the sequence of events:

 * Use sax to use a SAX parser (faster, but will ignore XML comments)
 * Use dom to use the W3C DOM
 * Use text to perform a simple text comparison

### `-W [wsp]` option

Define whitespace processing.

 * preserve White spaces are preserved for formatting but ignored during diffing.
 * compare White spaces are preserved for formatting and white space differences are reported (slow)
 * ignore white spaces are completely ignored and the result may produce different white spaces (faster)

### `-G [granul]` option

Use this option to defines the granularity text diffing.

 * word reports differences at the word level
 * text reports differences at the text node level (faster)
 * character reports differences at the character level (slow)

## Advanced options

### `-profile` option

Displays some profiling information on the error output

### `-noslice` option

Prevents Diff-X from "slicing" the XML. Diff-X uses slicing to remove common subsequences at the beginning and at the end of the sequence of XML events.

### `-A [algo]` option

Choose a specific algorithm implementation to use. 
Use this option to test your own algorithm - these option values may change in the future. Must be one of `fitsy*` | `guano` | `fitopsy` | `kumar` | `wesyma`

### `-F [format]` option
Choose a specific formatter. Must be one of `smart*` | `basic` | `convenient` | `strict` | `short`

## Using Diff-X as an XSLT extension

Diff-X provides some static methods which can be used in XSLT as extension functions.

### Saxon

To use Diff-X as an XSLT extension, you need to ensure that **saxon-dom.jar** is included in your classpath as 
Diff-X will compare DOM nodes.

In your XSLT, declare the namespace as:

```
 <xsl:stylesheet version="2.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:diffx="java:com.topologi.diffx.Extension"
    extension-element-prefixes="diffx">
```

Diff-X can then be called within your XSLT as an extension function as:

```
diffx:diff(node1, node2, whitespace_processing, text_granularity)"/>
```

With:
 * `node1` the first node to compare (Node)
 * `node2` the first node to compare (Node)
 * whitespace processing to indicate how white spaces should be processed, valid options are `COMPARE`, `PRESERVE` or `IGNORE` (xs:string)
 * text granularity to indicate the level of precision in text comparison, valid options are `TEXT` or `WORD` (xs:string)

For example, to compare two nodes ignoring whitespaces and detecting text differences at the word level, use:

```
  <xsl:copy-of select="diffx:diff(/node1/to/compare, /node2/to/compare, 'IGNORE', 'WORD')"/>
```

For more info on how to use saxon extension function, visit Saxonica's website: 
http://www.saxonica.com/documentation9.1/extensibility/functions.html


## Older repositories

This is the official home of DiffX.

DiffX has moved over the years, after a short stint at https://sourceforge.net/projects/diffx/
We moved to https://code.google.com/p/wo-diffx/
