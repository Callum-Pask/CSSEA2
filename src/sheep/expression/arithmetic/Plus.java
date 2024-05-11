package sheep.expression.arithmetic;

import sheep.expression.Expression;

/**
 * A plus operation.
 * Plus operations must have the operator name "+".
 * @stage2
 */
class Plus extends Arithmetic {
    /**
     * Construct a new plus expression.
     *
     * @param arguments A sequence of sub-expressions to perform the operation upon.
     * @requires arguments.length &gt; 0
     */
    public Plus(Expression[] arguments) {
        super("+", arguments);
    }

    /**
     * Perform a plus operation over the list of arguments.
     *
     * <pre>
     * {@code
     * Arithmetic plus = new Plus(new Expression[]{new Constant(12), new Constant(2), new Constant(5)});
     * plus.perform(new long[]{12, 2, 5}); // 19
     * }</pre>
     *
     * @param arguments A list of numbers to perform the operation upon.
     * @return The result of plus.
     */
    @Override
    protected long perform(long[] arguments) {
        long result = 0;
        for (long arg : arguments) {
            result += arg;
        }
        return result;
    }
}
