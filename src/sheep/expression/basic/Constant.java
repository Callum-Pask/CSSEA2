package sheep.expression.basic;

import sheep.expression.Expression;

import java.util.*;

/**
 * A constant numeric value.
 * @stage1
 */
public class Constant extends Expression {
    private final long number;

    /**
     * Construct a new constant to represent the given number.
     * @param number The number to represent as an expression.
     */
    public Constant(long number) {
        this.number = number;
    }

    /**
     * Get the numeric value stored within the constant expression.
     * @return Value stored within the expression.
     */
    public long getValue() {
        return number;
    }

    /**
     * String representation of the constant.
     * The result should be formatted as "CONSTANT([number])", e.g.
     * <pre>
     * {@code
     * Constant four = new Constant(4L);
     * four.toString(); // "CONSTANT(4)"
     * }</pre>
     * @return String representation of the constant.
     */
    @Override
    public String toString() {
        return "CONSTANT(" + number + ")";
    }

    /**
     * Determine if two constants are equal.
     * <p>
     * Two constants are equal if;
     * <ul>
     *     <li>they are both instances of Constant, and</li>
     *     <li>they both store the same number.</li>
     * </ul>
     * <pre>
     * {@code
     * Constant four = new Constant(4L);
     * Constant five = new Constant(5L);
     * Constant another = new Constant(4L);
     * four.equals(five); // false
     * four.equals(another); // true
     * }</pre>
     * @param object Another object to compare against.
     * @return If the given object is equal to this object.
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Constant constant1 = (Constant) object;
        return number == constant1.number;
    }

    /**
     * A hashcode method that respects the {@link Constant#equals(Object)} method.
     * @return An appropriate hashcode value for this instance.
     */
    @Override
    public int hashCode() {
        return Objects.hash(number);
    }

    /**
     * Dependencies of the constant expression.
     * Constant expressions have no dependencies.
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
     * A constant cannot be further evaluated, therefore this method
     * will return itself.
     * @param state A mapping of references to the expression they hold.
     * @return Itself.
     */
    @Override
    public Expression value(Map<String, Expression> state) {
        return this;
    }

    /**
     * Evaluate the expression to a numeric value.
     * For constants, this is the long value stored.
     * <pre>
     * {@code
     * Constant four = new Constant(4L);
     * four.value(); // 4
     * }</pre>
     * @return A long that represents the numeric value of the expression.
     */
    @Override
    public long value() {
        return number;
    }

    /**
     * The string representation of an expression when rendered within a cell.
     * For constants, this is the number as a string.
     * <pre>
     * {@code
     * Constant four = new Constant(4L);
     * four.render(); // "4"
     * }</pre>
     * @return the string representation of the expression.
     */
    @Override
    public String render() {
        return "" + number;
    }
}
