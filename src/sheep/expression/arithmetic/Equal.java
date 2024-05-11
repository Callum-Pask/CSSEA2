package sheep.expression.arithmetic;

import sheep.expression.Expression;

/**
 * An equal to operation.
 * Equal operations must have the operator name "=".
 * @stage2
 */
class Equal extends Arithmetic {
    /**
     * Construct a new equal to expression.
     *
     * @param arguments A sequence of sub-expressions to perform the operation upon.
     * @requires arguments.length &gt; 0
     */
    public Equal(Expression[] arguments) {
        super("=", arguments);
    }

    /**
     * Perform an equal to operation over the list of arguments.
     * This method will return 1 if all arguments are equal, 0 otherwise.
     *
     * <pre>
     * {@code
     * Arithmetic equal = new Equal(new Expression[]{new Constant(2), new Constant(1), new Constant(2)});
     * equal.perform(new long[]{2, 1, 2}); // 0
     * equal = new Equal(new Expression[]{new Constant(2), new Constant(2), new Constant(2)});
     * equal.perform(new long[]{2, 2, 2}); // 1
     * }</pre>
     *
     * @param arguments A list of numbers to perform the operation upon.
     * @return 1 is all arguments are equal, 0 otherwise.
     */
    @Override
    protected long perform(long[] arguments) {
        for (int i = 1; i < arguments.length; i++) {
            if (arguments[i - 1] != arguments[i]) {
                return 0;
            }
        }
        return 1;
    }
}
