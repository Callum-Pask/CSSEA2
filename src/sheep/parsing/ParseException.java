package sheep.parsing;

/**
 * Thrown if an expression cannot be parsed.
 * @stage1
 */
public class ParseException extends Exception {
    /**
     * Construct a new exception without any additional details.
     */
    public ParseException() {
        super();
    }

    /**
     * Construct a new exception with a description of the exception.
     * @param message The description of the exception.
     */
    public ParseException(String message) {
        super(message);
    }

    /**
     * Construct a new exception with another exception as the base cause.
     * @param base The exception that caused this exception to be thrown.
     */
    public ParseException(Exception base) {
        super(base);
    }


}