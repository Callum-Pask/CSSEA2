package sheep.expression.arithmetic;

import sheep.expression.Expression;

/**
 * A division operation.
 * Division operations must have the operator name "/".
 * @stage2
 */
class Divide extends Arithmetic {
    /**
     * Construct a new division expression.
     *
     * @param arguments A sequence of sub-expressions to perform the operation upon.
     * @requires arguments.length &gt; 0
     */
    public Divide(Expression[] arguments) {
        super("/", arguments);
    }

    /**
     * Perform integer division over the list of arguments.
     *
     * <pre>
     * {@code
     * Arithmetic divide = new Divide(new Expression[]{new Constant(64), new Constant(2), new Constant(8)});
     * divide.perform(new long[]{64, 2, 8}); // 4
     * }</pre>
     *
     * @param arguments A list of numbers to perform the operation upon.
     * @return The result of division.
     */
    @Override
    protected long perform(long[] arguments) {
        long result = arguments[0];
        for (int i = 1; i < arguments.length; i++) {
            result /= arguments[i];
        }
        return result;
    }
}
