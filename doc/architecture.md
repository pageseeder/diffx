General architecture
===

Although Diffx is primarily designed for XML, you can use it to find differences or generate the shortest edit script
between list of any type.

To analyse the differences between two sources, Diffx does the following:

1. A `Loader` converts the sources (files, strings, objects, DOM, etc...) into a list of tokens.
2. An `Algorithm` takes two lists of tokens and reports differences and matches to the `Handler`
3. A `Handler` converts the result into the desired output, possibly transforming it in the process

A `Processor` has several built-in options to co

Loaders
---

Diffx provides several loaders for XML depending on your source:

- The `DOMLoader` converts the a DOM `Document` or `Node` object into a sequence
- The `SAXLoader` uses a SAX stream of events.
- The `XMLEventLoader` and `XMLStreamLoaders` is based on Stax.
- The `LineLoader` converts a document into lines.

When loading directly from a file or reader, the SAX and Stream loaders provides the best performance.

Loaders can split text nodes in a number of ways which will result in different sequences.

### Text granularity

The text granularity lets you choose how the 

1. *Text*: text nodes are left untouched
2. *Punctuation*: text nodes are split by punctuation marks (`,`, `;`, `.`, `?`, `!`)
3. *SpaceWord* (default): A space followed by a word, this is the most efficient way to report differences at word level
4. *Word*: split text nodes into spaces and words, this is a bit more precise when SpaceWord but requires twice the amount of tokens
5. *Character*: create a token per character, this provides the highest level of precision but does not scale well.

Algorithms
---

Algorithms solve the shortest edit script (SES) for transforming A into B. Most algorithm implementations do so by
solving the equivalent Longest Common Subsequence (LCS) problem.

Generic algorithms report matches and differences (insertions or deletions) on any type of token by only checking 
equality between tokens. There may be multiple possible LCS, but only one of them is reported.

XML-specific algorithms solve the SES problem taking into account XML specifics to ensure that the tokens are returned
as a well-formed XML sequence. XML-specific implementations only accept `XMLToken` implementations.

### XML-specific algorithms

In many situations, the XML-LCS is also a generic-LCS in which case producing the XML subsequence is simply an ordering
problem. However, in some case the generic algorithm is unable to produce an acceptable solution.

Consider the following example:

> A: `<p>A <b>black cat</b></p>`
> 
> B: `<p>A <b>black</b> <b>cat</b></p>`

The generic algorithm will produce the following solution with only two differences:

> `<p><b>black`<ins>`</b>`</ins> <ins>`<b>`</ins>`cat</b></p>`

But this cannot be used to produce a well-formed sequence.
A correct solution would have at least 4 differences:

> `<p><b>black`<del>` cat`</del>`</b>` <ins>`<b>cat</b>`</ins>`</p>`

### Namespace handling

To simplify namespace handling, diffx computes a global namespace context used by both sequences and only considers the
namespace URI for comparison. In most cases, the prefix does not matter and the same prefix mapping is used for both 
sequences. 

By computing a namespace context, diffx can generate an XML output that uses the same namespace prefixes used in the 
input sequences.

### Implementations

Diffx provides multiple implementations of generic and XML-specific algorithm.
Each implementation has different performance properties.

See [algorithms](algorithms.md) for details.

Tokens
---

From version 0.9, Diffx generic algorithms can report differences between any list of object as long as they implement
efficient `hashCode` and `equals` methods. So they can be used on lists of `String` directly.

Diffx does provide efficient token implementations for XML.


Handlers
---

Handlers provide simple callback method `handle(Operator, Token)` and can be used to generate an output or manipulate the
results.

See [Diff outputs](output.md)

### `CoalescingFilter`

When the several consecutive `TextToken` are found, the coalescing filter lets you report this as a single change
by merging them into a single token.

This filter also address issues when a insertions and deletions are interspersed but could be 
lumped together if they were reordered.

For example:

> A <ins>beautiful</ins> <del>pretty</del> <ins>grey</ins> <del>white</del> cat

Into 

> A <ins>beautiful grey</ins> <del>pretty white</del> cat


### `PostXMLFixer`

This filter attempts to fix sequences that would produce incorrect XML by reordering tokens based on whether they are
inserted, deleted or matching both sequences.

As a side effect, it also reorders tokens that have been inserted or deleted to be grouped together.

Only ordering issues can be fixed, that is when the XML-LCS matches the generic LCS.
It can correct:
- when an inserted or delete attribute is reported after a text token
- when end element tokens are not reported in the same order as the start token.

The post XML fixer can detect when the it is unable to produce a valid sequence.

Processors
---

Processors have built-in filters and common options to process the differences.

### Optimistic processor

When changes occur mostly in text nodes, the generic LCS is usually identical to the XML-LCS.

Since generic algorithms tend to return the same results as XML-specific more efficiently, the optimistic 
processor attempts to process XML using the fastest generic algorithm. If it is unable to produce a well-formed
XML sequence, it falls back on an XML-specific implementation. 
