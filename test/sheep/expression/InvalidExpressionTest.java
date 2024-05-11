package sheep.expression;

import org.junit.Test;

import static org.junit.Assert.*;

public class InvalidExpressionTest {
    public static final double testWeight = 2;

    /**
     * Assert that when InvalidExpression is thrown with no
     * argument, the message will be null.
     */
    @Test
    public void throwExceptionNoMessage() {
        String message = "";
        try {
            throw new InvalidExpression();
        } catch (InvalidExpression e) {
            message = e.getMessage();
        }
        assertNull("Message should be null when no message is given.",
                message);
    }

    /**
     * Assert that when InvalidExpression is thrown with a message,
     * calling getMessage will give that message.
     */
    @Test
    @Deprecated
    public void throwExceptionWithMessage() {
        String message = "";
        try {
            throw new InvalidExpression("Unable to create an expression");
        } catch (InvalidExpression e) {
            message = e.getMessage();
        }
        assertEquals("Exception gave the incorrect message.",
                "Unable to create an expression",
                message);
    }

    /**
     * Assert that when InvalidExpression is thrown with a root exception
     * as an argument, that getCause will return the root exception.
     */
    @Test
    @Deprecated
    public void throwExceptionWithCause() {
        Throwable actual = null;
        Exception expected = new InvalidExpression();
        try {
            throw new InvalidExpression(expected);
        } catch (InvalidExpression e) {
            actual = e.getCause();
        }
        assertEquals("Exception gave incorrect cause.",
                expected,
                actual);
    }
}
