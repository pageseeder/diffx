package org.pageseeder.diffx.util;

import org.jetbrains.annotations.NotNull;
import org.pageseeder.diffx.token.StartElementToken;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.token.XMLTokenType;
import org.pageseeder.diffx.token.impl.IgnorableSpaceToken;
import org.pageseeder.diffx.token.impl.SpaceToken;
import org.pageseeder.diffx.token.impl.XMLStartElement;
import org.pageseeder.diffx.xml.Namespace;
import org.pageseeder.diffx.xml.Sequence;
import org.pageseeder.diffx.xml.SequenceProcessor;

import java.util.*;

/**
 * This class is an implementation of the {@link SequenceProcessor} interface
 * that processes XML sequences by removing ignorable whitespace based on specified rules.
 *
 * <p>This class allows the configuration of elements to be ignored during whitespace stripping.
 * It provides mechanisms to define elements whose whitespace should always be ignored, maybe ignored,
 * or never ignored, and processes XML tokens accordingly.
 *
 * <p>It relies on the {@link StripWhitespace} context to manage whitespace stripping, evaluating
 * each token and applying the stripping rules based on the context and token relationships.
 *
 * @author Christophe Lauret
 *
 * @version 1.2.0
 * @since 1.1.0
 */
public class ExtendedWhitespaceStripper implements SequenceProcessor {

  public enum StripWhitespace {
    ALWAYS, MAYBE, NEVER
  }

  private final Set<StartElementToken> alwaysIgnore = new HashSet<>();
  private final Set<StartElementToken> maybeIgnore = new HashSet<>();

  public void setAlwaysIgnore(String... names) {
    setAlwaysIgnore(Namespace.NO_NAMESPACE, names);
  }

  public void setMaybeIgnore(String... names) {
    setMaybeIgnore(Namespace.NO_NAMESPACE, names);
  }

  public void setAlwaysIgnore(Namespace ns, String... names) {
    this.alwaysIgnore.clear();
    this.alwaysIgnore.addAll(toSet(names, ns));
  }

  public void setMaybeIgnore(Namespace ns, String... names) {
    this.maybeIgnore.clear();
    this.maybeIgnore.addAll(toSet(names, ns));
  }

  public StripWhitespace forElement(StartElementToken start) {
    if (this.alwaysIgnore.contains(start)) return StripWhitespace.ALWAYS;
    if (this.maybeIgnore.contains(start)) return StripWhitespace.MAYBE;
    return StripWhitespace.NEVER;
  }

  /**
   * Removes ignorable whitespace from the given XML sequence based on the defined set of elements to ignore.
   *
   * <p>The method traverses through the sequence of XML tokens, checks the context of each token,
   * and filters out whitespace tokens as needed. Start and end elements are used to manage the context
   * stack, determining whether the current context is ignorable. Only the tokens that are not ignorable
   * in the given context are included in the new stripped sequence.
   *
   * @param sequence The XML sequence to process and strip of ignorable whitespace.
   * @return A new {@link Sequence} instance containing the filtered tokens without ignorable whitespace.
   */
  @Override
  public @NotNull Sequence process(@NotNull Sequence sequence) {
    return new Sequence(process(sequence.tokens()), sequence.getNamespaces());
  }

  /**
   * Filters a sequence of XML tokens to remove ignorable whitespace based on the context
   * and strip rules defined for certain XML elements.
   *
   * @param tokens The list of XML tokens to be filtered, including start elements, end elements,
   *               text tokens, and other token types.
   * @return A filtered list of XML tokens where ignorable whitespace has been excluded
   *         based on the context of each token.
   */
  @Override
  public @NotNull List<XMLToken> process(@NotNull List<XMLToken> tokens) {
    Deque<StripWhitespace> context = new ArrayDeque<>();
    List<XMLToken> out = new ArrayList<>(tokens.size());
    for (int i = 0; i < tokens.size(); i++) {
      XMLToken token = tokens.get(i);
      XMLTokenType type = token.getType();
      boolean include = true;
      if (type == XMLTokenType.START_ELEMENT) {
        StripWhitespace sc = forElement((StartElementToken)token);
        context.push(sc);
      } else if (type == XMLTokenType.END_ELEMENT) {
        context.pop();
      } else if (type == XMLTokenType.TEXT && isWhiteSpace(token)) {
        include = includeWhitespace(context, tokens, i);
      }

      // Include
      if (include) {
        out.add(token);
      }
    }
    return out;
  }

  /**
   * Determines whether whitespace should be included in a given context within a sequence of XML tokens.
   * The method evaluates the context stack and the subsequent XML token to make its determination.
   *
   * @param context A stack representing the current state of whitespace stripping rules.
   *                The stack contains {@link StripWhitespace} values to dictate how whitespace is handled
   *                within the current scope.
   * @param tokens A list of {@link XMLToken} objects representing the sequence of XML tokens being evaluated.
   * @param i The index of the current XML token in the tokens list.
   * @return true if whitespace should be included based on the context and subsequent XML tokens,
   *         false if whitespace should be excluded.
   */
  private boolean includeWhitespace(Deque<StripWhitespace> context, List<XMLToken> tokens, int i) {
    StripWhitespace stripContext = context.peek();
    if (stripContext == StripWhitespace.ALWAYS) {
      return false;
    } else if (stripContext == StripWhitespace.MAYBE) {
      XMLToken next = tokens.get(i+1);
      if (next.getType() == XMLTokenType.TEXT) {
        context.pop();
        context.push(StripWhitespace.NEVER);
      } else if (next.getType() == XMLTokenType.START_ELEMENT) {
        StripWhitespace sc = forElement((StartElementToken)next);
        if (sc == StripWhitespace.NEVER) {
          context.pop();
          context.push(StripWhitespace.NEVER);
        } else return false;
      } else if (next.getType() == XMLTokenType.END_ELEMENT) {
        return false;
      }
    }
    return true;
  }

  /**
   * Determines if the given XML token consists entirely of whitespace.
   *
   * @param token The XML token to evaluate for whitespace.
   * @return true if the token consists entirely of whitespace, false otherwise.
   */
  private boolean isWhiteSpace(XMLToken token) {
    if (token instanceof IgnorableSpaceToken || token instanceof SpaceToken) return true;
    String value = token.getValue();
    for (int i = 0; i < value.length(); i++) {
      if (!Character.isWhitespace(value.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  /**
   * Converts the specified array of element names into a set of {@link StartElementToken} instances.
   */
  private Set<StartElementToken> toSet(String[] names, Namespace ns) {
    HashSet<StartElementToken> elements = new HashSet<>(names.length);
    for (String name : names) {
      elements.add(new XMLStartElement(ns.getUri(), name));
    }
    return elements;
  }

}
