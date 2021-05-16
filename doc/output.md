
Diff outputs
===

Diffx provides multiple output formats.

### Default XML output

The XML output is a well-formed XML output that can be used to display a consolidated 
view of changes between two XML documents. It resembles the input sequences and uses 
`<ins>` and `<del>` elements for changes in text content and namespaces for changes 
to XML elements and attributes.

The main limitation is that it does not include deleted attributes on a specific 
namespace, but since these are generally rare, this is usually considered acceptable 
in most situations. The complete XML output (described below) was designed to overcome
this limitation.


### Complete XML output

This XML output can be used to display a consolidated view of changes between two XML
documents.

It resembles the input sequences and uses `<ins>` and `<del>` elements for changes in
the input sequences.

It is *complete* in the sense that all changes all included in this format so both 
input sequences can be recomputed from this format. This makes it a good candidate
to display the consolidated and side-by-side view.

### Strict XML output

This output is similar to the default output but does not use namespaces for changes.

### Diff reporter

This XML output is designed for generating reports or being processed by XSLT.

```xml
<diff-report>
  <match type="start-element" name="p" class-name="XMLStartElement"/>
  <delete type="attribute" name="id" value="a" class-name="XMLAttribute"/>
  <insert type="attribute" name="class" value="test" class-name="XMLAttribute"/>
  <match type="text" class-name="WordToken">Hello</match>
  <match type="text" class-name="WordToken"> night</match>
  <match type="end-element" name="p" class-name="XMLEndElement"/>
</diff-report>
```
