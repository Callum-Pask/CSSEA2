package sheep.expression.arithmetic;

import org.junit.Test;
import sheep.expression.Expression;
import sheep.expression.TypeError;
import sheep.expression.basic.Constant;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LessTest {
    public static final double testWeight = 1.5;

    @Test
    public void testIdentityValue() throws TypeError {
        Arithmetic arith = new Less(new Expression[]{new Constant(20)});
        Expression result = arith.value(new HashMap<>());
        assertTrue("Result of evaluating less is not a constant",
                result instanceof Constant);
        assertEquals(1, ((Constant) result).getValue());
    }

    @Test
    public void testIdentityPerform() throws TypeError {
        Arithmetic arith = new Less(new Expression[]{new Constant(20)});
        long result = arith.perform(new long[]{20});
        assertEquals(1, result);
    }

    /**
     * Assert that a `Less` instance containing the constants 10 and 20
     * resolves (by calling `value(Map)`) to a `Constant` instance storing the value 1 (because 10 is less than 20).
     */
    @Test
    @Deprecated
    public void testTwoValueTrue() throws TypeError {
        Arithmetic arith = new Less(new Expression[]{new Constant(10), new Constant(20)});
        Expression result = arith.value(new HashMap<>());
        assertTrue("Result of evaluating less is not a constant",
                result instanceof Constant);
        assertEquals(1, ((Constant) result).getValue());
    }

    @Test
    public void testTwoValueFalse() throws TypeError {
        Arithmetic arith = new Less(new Expression[]{new Constant(20), new Constant(10)});
        Expression result = arith.value(new HashMap<>());
        assertTrue("Result of evaluating less is not a constant",
                result instanceof Constant);
        assertEquals(0, ((Constant) result).getValue());
    }

    @Test
    public void testTwoPerformFalse() throws TypeError {
        Arithmetic arith = new Less(new Expression[]{new Constant(20), new Constant(10)});
        long result = arith.perform(new long[]{20, 10});
        assertEquals(0, result);
    }

    @Test
    public void testTwoPerformTrue() throws TypeError {
        Arithmetic arith = new Less(new Expression[]{new Constant(10), new Constant(20)});
        long result = arith.perform(new long[]{10, 20});
        assertEquals(1, result);
    }

    /**
     * Assert that a `Less` instance containing the constants 20, 2, 3, and 5
     * resolves (by calling `value(Map)`) to a `Constant` instance storing the value 0.
     */
    @Test
    @Deprecated
    public void testNValueFalse() throws TypeError {
        Arithmetic arith = new Less(
                new Expression[]{new Constant(20), new Constant(2), new Constant(3), new Constant(5)}
        );
        Expression result = arith.value(new HashMap<>());
        assertTrue("Result of evaluating less is not a constant",
                result instanceof Constant);
        assertEquals(0, ((Constant) result).getValue());
    }

    @Test
    public void testNValueTrue() throws TypeError {
        Arithmetic arith = new Less(
                new Expression[]{new Constant(2), new Constant(3), new Constant(6), new Constant(8)}
        );
        Expression result = arith.value(new HashMap<>());
        assertTrue("Result of evaluating less is not a constant",
                result instanceof Constant);
        assertEquals(1, ((Constant) result).getValue());
    }

    @Test
    public void testNPerformFalse() throws TypeError {
        Arithmetic arith = new Less(
                new Expression[]{new Constant(20), new Constant(2), new Constant(2), new Constant(2)}
        );
        long result = arith.perform(new long[]{20, 2, 2, 2});
        assertEquals(0, result);
    }

    /**
     * Assert that a `Less` instance containing the constants 2, 3, 4, 6
     * resolves (by calling `perform`) to the value 1.
     */
    @Test
    @Deprecated
    public void testNPerformTrue() throws TypeError {
        Arithmetic arith = new Less(
                new Expression[]{new Constant(2), new Constant(3), new Constant(4), new Constant(6)}
        );
        long result = arith.perform(new long[]{2, 3, 4, 6});
        assertEquals(1, result);
    }
}
