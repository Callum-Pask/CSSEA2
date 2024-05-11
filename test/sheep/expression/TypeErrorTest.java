package sheep.expression;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TypeErrorTest {
    public static final double testWeight = 2;

    /**
     * Assert that when TypeError is thrown with no
     * argument, the message will be null.
     */
    @Test
    @Deprecated
    public void throwExceptionNoMessage() {
        String message = "";
        try {
            throw new TypeError();
        } catch (TypeError e) {
            message = e.getMessage();
        }
        assertNull("Message should be null when no message is given.",
                message);
    }

    /**
     * Assert that when TypeError is thrown with a message,
     * calling getMessage will give that message.
     */
    @Test
    public void throwExceptionWithMessage() {
        String message = "";
        try {
            throw new TypeError("Unable to create an expression");
        } catch (TypeError e) {
            message = e.getMessage();
        }
        assertEquals("Exception gave the incorrect message.",
                "Unable to create an expression",
                message);
    }

    /**
     * Assert that when TypeError is thrown with a root exception
     * as an argument, that getCause will return the root exception.
     */
    @Test
    public void throwExceptionWithCause() {
        Throwable actual = null;
        Exception expected = new InvalidExpression();
        try {
            throw new TypeError(expected);
        } catch (TypeError e) {
            actual = e.getCause();
        }
        assertEquals("Exception gave incorrect cause.",
                expected,
                actual);
    }
}
