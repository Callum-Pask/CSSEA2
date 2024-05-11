package sheep.expression.arithmetic;

import sheep.expression.Expression;

/**
 * A less than operation.
 * Less than operations must have the operator name "&lt;".
 * @stage2
 */
class Less extends Arithmetic {
    /**
     * Construct a new less than expression.
     *
     * @param arguments A sequence of sub-expressions to perform the operation upon.
     * @requires arguments.length &gt; 0
     */
    public Less(Expression[] arguments) {
        super("<", arguments);
    }

    /**
     * Perform a less than operation over the list of arguments.
     * This method will return 1 if all arguments are in strictly increasing order, 0 otherwise.
     *
     * <pre>
     * {@code
     * Arithmetic less = new Less(new Expression[]{new Constant(2), new Constant(1), new Constant(2)});
     * less.perform(new long[]{2, 1, 2}); // 0
     * less = new Less(new Expression[]{new Constant(0), new Constant(1), new Constant(2)});
     * less.perform(new long[]{0, 1, 2}); // 1
     * }</pre>
     *
     * @param arguments A list of numbers to perform the operation upon.
     * @return 1 is all arguments are in increasing order, 0 otherwise.
     */
    @Override
    protected long perform(long[] arguments) {
        for (int i = 1; i < arguments.length; i++) {
            if (arguments[i - 1] >= arguments[i]) {
                return 0;
            }
        }
        return 1;
    }
}
