package sheep.expression.arithmetic;

import sheep.expression.Expression;

/**
 * A minus operation.
 * Minus operations must have the operator name "-".
 * @stage2
 */
class Minus extends Arithmetic {
    /**
     * Construct a new minus expression.
     *
     * @param arguments A sequence of sub-expressions to perform the operation upon.
     * @requires arguments.length &gt; 0
     */
    public Minus(Expression[] arguments) {
        super("-", arguments);
    }

    /**
     * Perform a minus operation over the list of arguments.
     *
     * <pre>
     * {@code
     * Arithmetic minus = new Minus(new Expression[]{new Constant(12), new Constant(2), new Constant(5)});
     * minus.perform(new long[]{12, 2, 5}); // 5
     * }</pre>
     *
     * @param arguments A list of numbers to perform the operation upon.
     * @return The result of minus.
     */
    @Override
    protected long perform(long[] arguments) {
        long result = arguments[0];
        for (int i = 1; i < arguments.length; i++) {
            result -= arguments[i];
        }
        return result;
    }
}
