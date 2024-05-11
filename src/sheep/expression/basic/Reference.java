package sheep.expression.basic;

import sheep.expression.Expression;
import sheep.expression.TypeError;
import sheep.sheets.CellLocation;

import java.util.*;

/**
 * A reference to a given identifier.
 * The identifier may be of another cell or a built-in.
 * @stage1
 */
public class Reference extends Expression {
    private final String identifier;

    /**
     * Construct a new reference to an identifier.
     * @requires identifier != ""
     * @requires identifier != null
     * @param identifier An identifier of a cell or a built-in.
     */
    public Reference(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Returns the identifier of the reference.
     * @return the identifier of the reference.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * String representation of the reference.
     * The result should be formatted as "REFERENCE([identifier])", e.g.
     * <pre>
     * {@code
     * Reference hello = new Reference("hello");
     * hello.toString(); // "REFERENCE(hello)"
     * }</pre>
     * @return String representation of the expression.
     */
    @Override
    public String toString() {
        return "REFERENCE(" + identifier + ")";
    }

    /**
     * Returns whether the expression is a reference.
     * For the reference expression, this must return true.
     * @return true
     */
    @Override
    public boolean isReference() {
        return true;
    }

    /**
     * If two instances of reference are equal to each other.
     * Equality is defined by having the same identifier.
     * @param obj another instance to compare against.
     * @return true if the other object is a reference
     *         with the same identifier.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Reference ref) {
            return identifier.equals(ref.identifier);
        }
        return false;
    }

    /**
     * A hashcode method that respects the {@link Reference#equals(Object)} method.
     * @return An appropriate hashcode value for this instance.
     */
    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    /**
     * Dependencies of the reference expression.
     * The dependencies of a reference are its identifier.
     *
     * @return A set containing the references' identifier.
     */
    @Override
    public Set<String> dependencies() {
        List<String> deps = new ArrayList<>();
        deps.add(identifier);
        return new HashSet<>(deps);
    }

    /**
     * The result of evaluating this expression.
     * <p>
     * If the given state does not have an entry for this reference's identifier,
     * return {@code this}.
     * Otherwise, return the result of calling {@link Expression#value(Map)} on the entry in the state.
     *
     * @param state A mapping of references to the expression they hold.
     * @return The result of evaluating this expression.
     */
    @Override
    public Expression value(Map<String, Expression> state) throws TypeError {
        if (state.containsKey(identifier)) {
            Expression value = state.get(identifier);
            return value.value(state);
        }
        return this;
    }

    /**
     * Evaluate the expression to a numeric value.
     * For references, a type error will always be thrown.
     * @return Nothing will be returned as a {@link TypeError} is always thrown.
     * @throws TypeError Will always be thrown by {@link Reference}.
     */
    @Override
    public long value() throws TypeError {
        throw new TypeError();
    }

    /**
     * The string representation of an expression when rendered within a cell.
     * For references, this is the referenced identifier.
     * <pre>
     * {@code
     * Reference hello = new Reference("hello");
     * hello.render(); // "hello"
     * }</pre>
     * @return the string representation of the expression.
     */
    @Override
    public String render() {
        return identifier;
    }
}
