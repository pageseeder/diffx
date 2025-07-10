package org.pageseeder.diffx.handler;

import org.jetbrains.annotations.NotNull;
import org.pageseeder.diffx.action.Operation;
import org.pageseeder.diffx.api.DiffHandler;
import org.pageseeder.diffx.api.Operator;
import org.pageseeder.diffx.token.XMLToken;
import org.pageseeder.diffx.token.XMLTokenType;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * A filter to ensure that all start and end XML element tokens are properly balanced in the input stream.
 *
 * <p>This filter checks whether every {@code START_ELEMENT} token has a matching {@code END_ELEMENT} token
 * with matching names and namespace URIs.
 *
 * <p>If an imbalance is detected (extra unmatched start or end elements, mismatched names/namespaces, or invalid order),
 * the filter will mark the stream as unbalanced and log warnings.
 *
 * <p>The {@code balanced} state can be checked after processing using the {@code isBalanced()} method.
 *
 * @author Christophe Lauret
 *
 * @since 1.2.2
 * @version 1.2.2
 */
public final class XMLBalanceCheckFilter extends DiffFilter<XMLToken> {

  /**
   * A stack to keep track of {@link Operation} instances representing {@code START_ELEMENT} tokens and their
   * corresponding {@code END_ELEMENT} tokens while processing an XML stream.
   */
  private final Deque<Operation<XMLToken>> stack = new ArrayDeque<>();

  /**
   * Stores a list of errors detected during the XML balancing process.
   */
  private List<String> errors = new ArrayList<>(1);

  public XMLBalanceCheckFilter(DiffHandler<XMLToken> handler) {
    super(handler);
  }

  @Override
  public void start() {
    this.errors = new ArrayList<>(0);
    this.stack.clear();
  }

  @Override
  public void handle(@NotNull Operator operator, @NotNull XMLToken token) {
    if (token.getType() == XMLTokenType.START_ELEMENT) {
      this.stack.push(new Operation<>(operator, token));
    } else if (token.getType() == XMLTokenType.END_ELEMENT) {
      Operation<XMLToken> start = this.stack.peek();
      if (start == null) {
        errors.add("Unexpected " + operator + token);
      } else {
        this.stack.pop();
        if (start.operator() != operator
          || !start.token().getName().equals(token.getName())
          || !start.token().getNamespaceURI().equals(token.getNamespaceURI())) {
          errors.add("Expected " + start.operator() + start.token() + ", but got " + operator + token);
        }
      }
    }
    this.target.handle(operator, token);
  }

  /**
   * Indicates whether the XML input stream processed by this filter is balanced.
   *
   * <p>A balanced XML input stream implies that every {@code START_ELEMENT} token
   * has a corresponding and valid {@code END_ELEMENT} token with matching names
   * and namespace URIs, and operator.
   *
   * @return true if the XML input stream is balanced, false otherwise
   */
  public boolean isBalanced() {
    return this.errors.isEmpty();
  }

  /**
   * Retrieves the list of errors identified during the XML balancing process.
   *
   * @return a list of strings representing the errors detected, or an empty list if no errors were found
   */
  public List<String> getErrors() {
    return this.errors;
  }

}
