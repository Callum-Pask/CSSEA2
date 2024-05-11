package sheep.expression.arithmetic;

import sheep.expression.Expression;

/**
 * A times operation.
 * Times operations must have the operator name "*".
 * @stage2
 */
class Times extends Arithmetic {
    /**
     * Construct a new times expression.
     *
     * @param arguments A sequence of sub-expressions to perform the operation upon.
     * @requires arguments.length &gt; 0
     */
    public Times(Expression[] arguments) {
        super("*", arguments);
    }

    /**
     * Perform a times operation over the list of arguments.
     *
     * <pre>
     * {@code
     * Arithmetic times = new Times(new Expression[]{new Constant(12), new Constant(2), new Constant(5)});
     * times.perform(new long[]{12, 2, 5}); // 120
     * }</pre>
     *
     * @param arguments A list of numbers to perform the operation upon.
     * @return The result of times.
     */
    @Override
    protected long perform(long[] arguments) {
        long result = 1;
        for (long arg : arguments) {
            result *= arg;
        }
        return result;
    }
}
