package sheep.expression.arithmetic;

import org.junit.Test;
import sheep.expression.Expression;
import sheep.expression.TypeError;
import sheep.expression.basic.Constant;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PlusTest {
    public static final double testWeight = 1.5;

    @Test
    public void testIdentityValue() throws TypeError {
        Arithmetic arith = new Plus(new Expression[]{new Constant(20)});
        Expression result = arith.value(new HashMap<>());
        assertTrue("Result of evaluating plus is not a constant",
                result instanceof Constant);
        assertEquals(20, ((Constant) result).getValue());
    }

    @Test
    public void testIdentityPerform() throws TypeError {
        Arithmetic arith = new Plus(new Expression[]{new Constant(20)});
        long result = arith.perform(new long[]{20});
        assertEquals(20, result);
    }

    @Test
    public void testTwoValue() throws TypeError {
        Arithmetic arith = new Plus(new Expression[]{new Constant(20), new Constant(10)});
        Expression result = arith.value(new HashMap<>());
        assertTrue("Result of evaluating plus is not a constant",
                result instanceof Constant);
        assertEquals(30, ((Constant) result).getValue());
    }

    /**
     * Assert that a `Plus` instance containing the constants 20 and 10
     * resolves (by calling `perform`) to the value 30.
     */
    @Test
    @Deprecated
    public void testTwoPerform() throws TypeError {
        Arithmetic arith = new Plus(new Expression[]{new Constant(20), new Constant(10)});
        long result = arith.perform(new long[]{20, 10});
        assertEquals(30, result);
    }

    /**
     * Assert that a `Plus` instance containing the constants 20, 2, 2, and 2
     * resolves (by calling `value(Map)`) to a `Constant` instance storing the value 26.
     */
    @Test
    @Deprecated
    public void testNValue() throws TypeError {
        Arithmetic arith = new Plus(
                new Expression[]{new Constant(20), new Constant(2), new Constant(2), new Constant(2)}
        );
        Expression result = arith.value(new HashMap<>());
        assertTrue("Result of evaluating plus is not a constant",
                result instanceof Constant);
        assertEquals(26, ((Constant) result).getValue());
    }

    @Test
    public void testNPerform() throws TypeError {
        Arithmetic arith = new Plus(
                new Expression[]{new Constant(20), new Constant(2), new Constant(2), new Constant(2)}
        );
        long result = arith.perform(new long[]{20, 2, 2, 2});
        assertEquals(26, result);
    }
}
