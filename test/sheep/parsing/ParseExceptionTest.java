package sheep.parsing;

import org.junit.Test;
import sheep.expression.InvalidExpression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ParseExceptionTest {
    public static final double testWeight = 2;

    /**
     * Assert that when ParseException is thrown with no
     * argument, the message will be null.
     */
    @Test
    public void throwExceptionNoMessage() {
        String message = "";
        try {
            throw new ParseException();
        } catch (ParseException e) {
            message = e.getMessage();
        }
        assertNull("Message should be null when no message is given.",
                message);
    }

    /**
     * Assert that when ParseException is thrown with a message,
     * calling getMessage will give that message.
     */
    @Test
    @Deprecated
    public void throwExceptionWithMessage() {
        String message = "";
        try {
            throw new ParseException("Unable to create an expression");
        } catch (ParseException e) {
            message = e.getMessage();
        }
        assertEquals("Exception gave the incorrect message.",
                "Unable to create an expression",
                message);
    }

    /**
     * Assert that when ParseException is thrown with a root exception
     * as an argument, that getCause will return the root exception.
     */
    @Test
    @Deprecated
    public void throwExceptionWithCause() {
        Throwable actual = null;
        Exception expected = new InvalidExpression();
        try {
            throw new ParseException(expected);
        } catch (ParseException e) {
            actual = e.getCause();
        }
        assertEquals("Exception gave incorrect cause.",
                expected,
                actual);
    }
}
