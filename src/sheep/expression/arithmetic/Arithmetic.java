package sheep.expression.arithmetic;

import sheep.expression.basic.Constant;
import sheep.expression.Expression;
import sheep.expression.TypeError;
import sheep.expression.basic.Nothing;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;


/**
 * An arithmetic expression.
 * Performs arithmetic operations on a sequence of sub-expressions.
 * @stage2
 */
public abstract class Arithmetic extends Expression {
    private final String operator;
    private final Expression[] arguments;

    /**
     * Construct a new arithmetic expression.
     *
     * @param operator The name of the arithmetic operation, e.g. plus.
     * @param arguments A sequence of sub-expressions to perform the operation upon.
     * @requires arguments.length &gt; 0
     */
    protected Arithmetic(String operator, Expression[] arguments) {
        this.operator = operator;
        this.arguments = arguments;
    }

    /**
     * Construct a new addition (plus) operation.
     *
     * @param arguments A sequence of sub-expressions to perform the operation upon.
     * @requires arguments.length &gt; 0
     * @return A plus expression.
     */
    public static Arithmetic plus(Expression[] arguments) {
        return new Plus(arguments);
    }

    /**
     * Construct a new subtraction (minus) operation.
     *
     * @param arguments A sequence of sub-expressions to perform the operation upon.
     * @requires arguments.length &gt; 0
     * @return A minus expression.
     */
    public static Arithmetic minus(Expression[] arguments) {
        return new Minus(arguments);
    }

    /**
     * Construct a new multiplication (times) operation.
     *
     * @param arguments A sequence of sub-expressions to perform the operation upon.
     * @requires arguments.length &gt; 0
     * @return A times expression.
     */
    public static Arithmetic times(Expression[] arguments) {
        return new Times(arguments);
    }

    /**
     * Construct a new division (divide) operation.
     *
     * @param arguments A sequence of sub-expressions to perform the operation upon.
     * @requires arguments.length &gt; 0
     * @return A divide expression.
     */
    public static Arithmetic divide(Expression[] arguments) {
        return new Divide(arguments);
    }

    /**
     * Construct a new less than (less) operation.
     *
     * @param arguments A sequence of sub-expressions to perform the operation upon.
     * @requires arguments.length &gt; 0
     * @return A less expression.
     */
    public static Arithmetic less(Expression[] arguments) {
        return new Less(arguments);
    }

    /**
     * Construct a new equal to (equal) operation.
     *
     * @param arguments A sequence of sub-expressions to perform the operation upon.
     * @requires arguments.length &gt; 0
     * @return A equal expression.
     */
    public static Arithmetic equal(Expression[] arguments) {
        return new Equal(arguments);
    }

    /**
     * Dependencies of the arithmetic expression.
     * The dependencies of an arithmetic expression are the union
     * of all sub-expressions.
     * <pre>
     * {@code
     * Arithmetic plus = Arithmetic.plus(new Expression[]{Arithmetic.minus(new Expression[]{new Reference("A1"), new Reference("A2")}), new Reference("B1")});
     * plus.dependencies() // {"A1", "A2", "B1"}
     * }</pre>
     *
     * @return A set containing the union of all sub-expression dependencies.
     */
    @Override
    public Set<String> dependencies() {
        Set<String> dependencies = new HashSet<>();
        for (Expression expression : arguments) {
            dependencies.addAll(expression.dependencies());
        }
        return dependencies;
    }

    /**
     * The result of evaluating this expression.
     * <p>
     * An arithmetic expression will evaluate to a {@link Constant} expression
     * that stores the result of performing the specific arithmetic operation.
     * <p>
     * During evaluation the arithmetic expression should evaluate each sub-expression
     * and convert the resulting values to numeric values to perform the operation.
     *
     * @param state A mapping of references to the expression they hold.
     * @return A constant expression of the result.
     * @throws TypeError If any of the sub-expressions cannot be converted to a numeric value.
     */
    @Override
    public Expression value(Map<String, Expression> state) throws TypeError {
        long[] values = new long[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            values[i] = arguments[i].value(state).value();
        }
        return new Constant(perform(values));
    }

    /**
     * Evaluate the expression to a numeric value.
     * For arithmetic expressions, a type error will always be thrown.
     * @return Nothing will be returned as a {@link TypeError} is always thrown.
     * @throws TypeError Will always be thrown by {@link Arithmetic}.
     */
    @Override
    public long value() throws TypeError {
        throw new TypeError();
    }

    /**
     * Perform the arithmetic operation over a list of numbers.
     * <p>
     * This is an abstract method that should be implemented by each subclass.
     * @param arguments A list of numbers to perform the operation upon.
     * @return The result of performing the arithmetic operation.
     */
    protected abstract long perform(long[] arguments);

    /**
     * The string representation of an expression when rendered within a cell.
     * For arithmetic, this is the sequence of sub-expressions joined
     * by the operator node.
     * <pre>
     * {@code
     * Arithmetic plus = Arithmetic.plus(new Expression[]{new Reference("A1"), new Reference("A2"), new Constant(4)});
     * plus.render(); // "A1 + A2 + 4"
     * }</pre>
     * @return the string representation of the expression.
     */
    @Override
    public String render() {
        return toString();
    }

    /**
     * The string representation of the expression.
     * For arithmetic, this is the sequence of sub-expressions joined
     * by the operator node.
     * <pre>
     * {@code
     * Arithmetic plus = Arithmetic.plus(new Expression[]{new Reference("A1"), new Reference("A2"), new Constant(4)});
     * plus.toString(); // "A1 + A2 + 4"
     * }</pre>
     * @return the string representation of the expression.
     */
    @Override
    public String toString() {
        StringJoiner builder = new StringJoiner(" " + operator + " ");
        for (Expression argument : arguments) {
            builder.add(argument.render());
        }
        return builder.toString();
    }
}
