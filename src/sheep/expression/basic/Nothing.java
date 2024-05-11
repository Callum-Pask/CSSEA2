package sheep.expression.basic;

import sheep.expression.Expression;
import sheep.expression.TypeError;

import java.util.*;

/**
 * An empty expression.
 * @stage1
 */
public class Nothing extends Expression {
    /**
     * Dependencies of the empty expression.
     * Empty expressions have no dependencies.
     *
     * @return An empty set to represent no dependencies.
     */
    @Override
    public Set<String> dependencies() {
        return new HashSet<>();
    }

    /**
     * The result of evaluating this expression.
     * <p>
     * An empty expression cannot be further evaluated, therefore this method
     * will return itself.
     * @param state A mapping of references to the expression they hold.
     * @return Itself.
     */
    @Override
    public Expression value(Map<String, Expression> state) throws TypeError {
        return this;
    }

    /**
     * Evaluate the expression to a numeric value.
     * For empty expressions, a type error will always be thrown.
     * @return Nothing will be returned as a {@link TypeError} is always thrown.
     * @throws TypeError Will always be thrown by {@link Nothing}.
     */
    @Override
    public long value() throws TypeError {
        throw new TypeError();
    }

    /**
     * The string representation of an expression when rendered within a cell.
     * For empty expressions, this is the empty string.
     * <pre>
     * {@code
     * Nothing nothing = new Nothing();
     * nothing.render(); // ""
     * }</pre>
     * @return the string representation of the expression.
     */
    @Override
    public String render() {
        return "";
    }

    /**
     * String representation of the empty expression.
     * The result should be formatted as "NOTHING", e.g.
     * <pre>
     * {@code
     * Nothing nothing = new Nothing();
     * nothing.toString(); // "NOTHING"
     * }</pre>
     * @return String representation of the expression.
     */
    @Override
    public String toString() {
        return "NOTHING";
    }
}
