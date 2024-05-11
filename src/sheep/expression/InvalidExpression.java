package sheep.expression;

/**
 * Thrown if an error occur in the creation of an {@link Expression}
 * in an {@link ExpressionFactory}.
 * @hint Refer to lecture example of extending exceptions.
 * @stage1
 */
public class InvalidExpression extends Exception {
    /**
     * Construct a new exception without any additional details.
     */
    public InvalidExpression() {
        super();
    }

    /**
     * Construct a new exception with a description of the exception.
     * @param message The description of the exception.
     */
    public InvalidExpression(String message) {
        super(message);
    }

    /**
     * Construct a new exception with another exception as the base cause.
     * @param base The exception that caused this exception to be thrown.
     */
    public InvalidExpression(Exception base) {
        super(base);
    }
}
